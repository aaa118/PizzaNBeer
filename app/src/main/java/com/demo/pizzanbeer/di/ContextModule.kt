package com.demo.pizzanbeer.di

import android.content.Context
import dagger.Module
import dagger.Provides
import java.util.logging.Logger

@Module
class ContextModule(private val context: Context) {

    @Provides
    fun provideContext(): Context {
        return context
    }

    @Provides
    fun provideRepoLogger(): Logger {
        return Logger.getLogger("PizzaNBeerRepository")

    }
}