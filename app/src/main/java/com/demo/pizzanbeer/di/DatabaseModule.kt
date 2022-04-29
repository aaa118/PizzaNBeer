package com.demo.pizzanbeer.di

import android.content.Context
import androidx.room.Room
import com.demo.pizzanbeer.db.AppDatabase
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class DatabaseModule {

    @Provides
    fun provideDatabase(context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            DB_NAME
        ).fallbackToDestructiveMigration().build()
    }

    companion object {
        const val DB_NAME = "businesses_db"
    }
}