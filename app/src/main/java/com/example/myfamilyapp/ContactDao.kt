package com.example.myfamilyapp

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface ContactDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(contactModel : ContactModel) // this function will only work with coroutines due to use of suspend keyword

    //if we want to use live data we have to remove suspend keyword

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllContacts(contactModelList : List<ContactModel>)

    @Query("Select * from contactmodel")
    fun getAllContacts() : LiveData<List<ContactModel>>

}