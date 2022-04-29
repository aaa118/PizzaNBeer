package com.demo.pizzanbeer.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.demo.pizzanbeer.model.Businesses
import com.demo.pizzanbeer.model.YelpResponse
import kotlinx.coroutines.flow.Flow

@Dao
interface BusinessesDao {

    @Query("SELECT * FROM businesses  ORDER BY distance ASC")
    fun getAll(): Flow<List<Businesses>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(businesses: List<Businesses>)

//    @Query("SELECT * FROM user WHERE uid IN (:userIds)")
//    fun loadAllByIds(userIds: IntArray): List<User>
//
//    @Query("SELECT * FROM user WHERE first_name LIKE :first AND " +
//            "last_name LIKE :last LIMIT 1")
//    fun findByName(first: String, last: String): User
}