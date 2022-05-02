package com.demo.pizzanbeer.repo

import androidx.annotation.WorkerThread
import com.amazon.jadzia.monitor.NetworkConnectivityMonitor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import javax.net.ssl.SSLHandshakeException

class NetworkRepo {
    private val monitor: NetworkConnectivityMonitor by provide()

    protected suspend inline fun safeNetworkRequest(crossinline block: suspend () -> Unit) =
        safeNetworkRequest(Unit, block)

    /**
     * This method wraps the network request and does not fire it if the network is not currently available.
     * [UnknownHostException] still has a chance of occurring if the request was already fired but
     * the network has gone down in the middle of it. To prevent this, we ignore the possible exception
     * we would catch in this case.
     */
    @Suppress("NON_PUBLIC_CALL_FROM_PUBLIC_INLINE")
    protected suspend inline fun <T> safeNetworkRequest(defaultResult: T, crossinline block: suspend () -> T): T {
        return when (monitor.isNetworkAvailable) {
            true -> {
                try {
                    withContext(Dispatchers.IO) { block() }
                } catch (e: UnknownHostException) {
                    logError(e) { "Unknown host exception performing network request" }
                    postToast(ToastAlertMessage(R.string.error_general_network_request_exception))
                    defaultResult
                } catch (e: SSLHandshakeException) {
                    // This often happens if the internet is available but the NTP time sync hasn't
                    // occurred yet.  This causes the SSL handshake to fail due to the certificate
                    // not being within the validity period.  However, this exception can also occur
                    // for other reasons, like if the certificate is legitimately expired.
                    val rootCause = ExceptionUtils.getRootCause(e)
                    logError(e) { "Handshake exception performing network request rootCause=$rootCause" }
                    postToast(ToastAlertMessage(R.string.error_general_network_request_exception))
                    defaultResult
                } catch (e: SocketTimeoutException) {
                    logError(e) { "SocketTimeout performing network request" }
                    postToast(ToastAlertMessage(R.string.error_general_network_request_exception))
                    defaultResult
                } catch (e: Exception) {
                    analyticsPublisher.record(
                        analyticsPublisher.createEvent(APP_ENCOUNTERED_GENERAL_NETWORK_REQUEST_EXCEPTION)
                    )
                    logError(e) { "Encountered unexpected exception" }
                    postToast(ToastAlertMessage(R.string.error_general_network_request_exception))
                    defaultResult
                }
            }
            false -> {
                postNoInternetAvailableToast()
                defaultResult
            }
        }
    }

    @Suppress("NON_PUBLIC_CALL_FROM_PUBLIC_INLINE")
    @WorkerThread
    protected inline fun <T> safeBlockingNetworkRequest(defaultResult: T, crossinline block: () -> T): T {
        return when (monitor.isNetworkAvailable) {
            true -> {
                try {
                    block()
                } catch (e: UnknownHostException) {
                    logError(e) { "[safeNetworkRequest]" }
                    postToast(ToastAlertMessage(R.string.error_general_network_request_exception))
                    defaultResult
                } catch (e: SSLHandshakeException) {
                    // This often happens if the internet is available but the NTP time sync hasn't
                    // occurred yet.  This causes the SSL handshake to fail due to the certificate
                    // not being within the validity period.  However, this exception can also occur
                    // for other reasons, like if the certificate is legitimately expired.
                    val rootCause = ExceptionUtils.getRootCause(e)
                    logError(e) { "Handshake exception performing network request rootCause=$rootCause" }
                    postToast(ToastAlertMessage(R.string.error_general_network_request_exception))
                    defaultResult
                } catch (e: SocketTimeoutException) {
                    logError(e) { "SocketTimeout performing network request" }
                    postToast(ToastAlertMessage(R.string.error_general_network_request_exception))
                    defaultResult
                } catch (e: Exception) {
                    logError(e) { "Encountered unexpected exception" }
                    postToast(ToastAlertMessage(R.string.error_general_network_request_exception))
                    defaultResult
                }
            }
            false -> {
                postNoInternetAvailableToast()
                defaultResult
            }
        }
    }

}