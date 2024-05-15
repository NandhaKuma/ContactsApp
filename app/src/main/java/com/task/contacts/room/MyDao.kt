package com.task.contacts.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface MyDao {

    @Query("Select * from PhoneServices")
    fun getPhoneServicesTable(): List<PhoneSevicesTable>


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertPhoneServices(details: PhoneSevicesTable)


    @Query("DELETE FROM PhoneServices")
    fun DropPhoneServices()



}