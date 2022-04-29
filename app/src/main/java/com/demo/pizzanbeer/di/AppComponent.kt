package com.demo.pizzanbeer.di

import com.demo.pizzanbeer.ui.ListFragment
import com.demo.pizzanbeer.ui.MainActivity
import dagger.Component
import javax.inject.Singleton

@Component(modules = [ContextModule::class, NetworkModule::class, DatabaseModule::class])
@Singleton
interface AppComponent {

    fun inject(mainActivity: MainActivity)
    fun inject(listFragment: ListFragment)
}