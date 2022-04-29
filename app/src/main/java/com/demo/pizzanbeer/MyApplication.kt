package com.demo.pizzanbeer

import android.app.Application
import com.demo.pizzanbeer.di.*

class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        appComponent = DaggerAppComponent.builder()
            .contextModule(ContextModule(this))
            .networkModule(NetworkModule())
            .databaseModule(DatabaseModule())
            .build()
    }

    companion object {
        lateinit var appComponent: AppComponent
    }
}