package com.demo.pizzanbeer.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.demo.pizzanbeer.model.Businesses

@Database(entities = [Businesses::class], version = 1, exportSchema = false)
@androidx.room.TypeConverters(TypeConverters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun businessesDao(): BusinessesDao
}