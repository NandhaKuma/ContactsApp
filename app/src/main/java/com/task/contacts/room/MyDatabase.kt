package com.task.contacts.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [PhoneSevicesTable::class], version = 1, exportSchema = false)
abstract class MyDatabase : RoomDatabase() {
    abstract fun myDao() : MyDao
    companion object {
        @Volatile
        private var instance: MyDatabase?=null
        fun getChatDatabase(context: Context) : MyDatabase {
            if (instance != null) return instance!!
            instance = Room.databaseBuilder(context, MyDatabase::class.java, "CONTACT_DATABASE").fallbackToDestructiveMigration().build()
            return instance!!
        }
    }

}