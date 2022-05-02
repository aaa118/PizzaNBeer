/*
 * Copyright 2020 Amazon.com, Inc. or its affiliates. All Rights Reserved
 */

package com.amazon.jadzia.monitor

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.net.ConnectivityManager.NetworkCallback
import android.net.LinkProperties
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkCapabilities.NET_CAPABILITY_VALIDATED
import android.net.wifi.WifiManager
import androidx.annotation.AnyThread
import androidx.annotation.VisibleForTesting
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.amazon.jadzia.BroadcastReceiverRegistrar
import com.amazon.jadzia.api.component.provide
import com.amazon.jadzia.interstitial.AlertManager
import com.amazon.jadzia.monitor.NetworkConnectivityState.Available
import com.amazon.jadzia.monitor.NetworkConnectivityState.Lost
import com.amazon.jadzia.monitor.NetworkConnectivityState.Unavailable
import com.amazon.jadzia.util.isConnectedToNetwork
import com.amazon.jadzia.util.logDebug
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

/**
 * [NetworkConnectivityMonitor] listens to changes in network connectivity
 * and notifies its consumers about the change.
 * The primary events that the monitor publishes are
 * when a network is available, lost or switched.
 *
 * FIXME: JADZIASW-7228: Consolidate ConnectivityAwareViewModel and NetworkConnectivityMonitor.
 */
class NetworkConnectivityMonitor : NetworkCallback(), CoroutineScope by MainScope() {

    private val registrar: BroadcastReceiverRegistrar by provide()
    private val wifiManager: WifiManager by provide()
    private val connectivityManager: ConnectivityManager by provide()
    private val alertManager: AlertManager by provide()

    private val _isWifiEnabled: MutableLiveData<Boolean> = MutableLiveData()
    val isWifiEnabled: LiveData<Boolean> = _isWifiEnabled

    private val _showConnectivityBanner = MutableLiveData<Boolean>()
    val showConnectivityBanner: LiveData<Boolean> = _showConnectivityBanner

    /**
     * Returns whether the device is connected to a network.  It does *not* consider whether the
     * network connection has access to the internet.
     *
     * For example, if the device is connected to a router but the router does not have a WAN connection,
     * this property would still return "true"
     */
    @get:AnyThread
    val isNetworkAvailable
        get() = connectivityManager.isConnectedToNetwork()

    /**
     * Returns whether the device is connected to a network and the connection has access to the
     * internet, by checking the NET_CAPABILITY_VALIDATED capability on the active network.
     */
    val isNetworkAvailableAndValidated
        get() = currentNetworkState().isConnected()

    /**
     * TOOD: JADZIASW-14684 Replace with StateFlow once the project is upgraded to Kotlin Coroutines 1.4.
     */
    private val networkConnectivityStateChannel = ConflatedBroadcastChannel<NetworkConnectivityState>()
    val networkConnectivityState = networkConnectivityStateChannel.asFlow().distinctUntilChanged()

    private val wifiStateReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val wifiEnabled = wifiManager.isWifiEnabled
            _isWifiEnabled.postValue(wifiEnabled)
            _showConnectivityBanner.postValue(wifiEnabled)
        }
    }

    /**
     * Initialize [NetworkConnectivityMonitor] by registering it as a
     * default callback for network state changes and wifi updates
     * and send default values to the consumers
     */
    init {
        registrar.registerReceiver(wifiStateReceiver, IntentFilter(WifiManager.WIFI_STATE_CHANGED_ACTION))
        connectivityManager.registerDefaultNetworkCallback(this)
        wifiStateReceiver.onReceive(null, null)
    }

    /**
     * This is called when the device connects to a network or switches between networks.
     * It is important to note that
     * for devices that have both cellular(mobile) and wifi capabilities,
     * the callback is not invoked on every transition.
     * For example, the callback is invoked in case of the following transitions:
     * 1. No Network -> Wifi
     * 2. No Network -> Mobile
     * 3. Mobile -> Wifi + Mobile
     * 4. Wifi + Mobile -> Mobile
     * 5. Wifi -> (Different) Wifi
     *
     * Whereas the callback is NOT invoked in case of the following transitions since the device
     * continues to use wifi
     * as its active network:
     * 1. Wifi + Mobile -> Wifi
     * 2. Wifi -> Wifi + Mobile
     */
    override fun onAvailable(network: Network) {
        launch {
            val state = currentNetworkState()
            logDebug { "Send event for onAvailable $state" }
            networkConnectivityStateChannel.send(state)
            alertManager.hideToastType(TOAST_TYPE_NO_INTERNET_AVAILABLE)
        }
    }

    override fun onCapabilitiesChanged(network: Network, networkCapabilities: NetworkCapabilities) {
        launch {
            val state = currentNetworkState()
            logDebug { "Send event for onCapabilitiesChanged $state $network $networkCapabilities" }
            networkConnectivityStateChannel.send(state)
        }
    }

    override fun onLinkPropertiesChanged(network: Network, linkProperties: LinkProperties) {
        launch {
            val state = currentNetworkState()
            logDebug { "Send event for onLinkPropertiesChanged $state $network $linkProperties" }
            networkConnectivityStateChannel.send(state)
        }
    }

    /**
     * This is called when the device loses network connectivity.
     * It is also invoked when the device switches Wifi to
     * Mobile to indicate a lost event for Wifi followed by a available event for Mobile.
     */
    override fun onLost(network: Network) {
        launch {
            logDebug { "Send event for onLost" }
            networkConnectivityStateChannel.send(Lost)
        }
    }

    override fun onUnavailable() {
        super.onUnavailable()
        launch {
            logDebug { "Send event for onUnavailable" }
            networkConnectivityStateChannel.send(Unavailable)
        }
    }

    @VisibleForTesting
    internal fun currentNetworkState() =
            when (isNetworkAvailable) {
                true -> Available(isInternetConnected())
                false -> Lost
            }

    private fun isInternetConnected(): Boolean {
        val networkCapabilities = connectivityManager.activeNetwork ?: return false
        val activeNetwork = connectivityManager.getNetworkCapabilities(networkCapabilities) ?: return false

        val network = when {
            activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
            else -> false
        }

        val validated = activeNetwork.hasCapability(NET_CAPABILITY_VALIDATED)

        val isInternetConnectionValidated = network && validated

        _showConnectivityBanner.postValue(isInternetConnectionValidated)

        return isInternetConnectionValidated
    }

    /**
     * Stop listening for networking events.
     * Cleans up the monitor by closing the producer channel.
     * The lifecycle of this monitor is tied to the lifecycle of
     * the app and hence the clean up should not be called by the consumers
     *
     * This utility method exists to facilitate unit testing
     * as we cant really use it in reality this as this
     * object is a [SINGLETON] during the app scope
     * and we never unsubscribe from it.
     */
    @VisibleForTesting
    internal fun destroy() {
        connectivityManager.unregisterNetworkCallback(this)
        registrar.unregisterReceiver(wifiStateReceiver)
        networkConnectivityStateChannel.close()
        cancel()
    }

    companion object {
        const val TOAST_TYPE_NO_INTERNET_AVAILABLE = "no_internet_available"
    }
}

/**
 * Network connectivity states corresponding to the change in the connectivity status of the device.
 */
sealed class NetworkConnectivityState {
    data class Available(val isInternet: Boolean) : NetworkConnectivityState()
    object Lost : NetworkConnectivityState()
    object Unavailable : NetworkConnectivityState()
}

fun NetworkConnectivityState.isConnected(): Boolean = this is Available && this.isInternet

fun NetworkConnectivityState.isNotConnected(): Boolean = !isConnected()

/**
 * Given a [Flow] of [NetworkConnectivityState] this function will wait for the first
 * instance of an available network connection.
 */
suspend fun Flow<NetworkConnectivityState>.waitForConnectivity() {
    this
        .filter { it is Available && it.isInternet }
        .first()
}
