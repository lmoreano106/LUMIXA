package com.lumixa.app.data.provider

import android.content.Context
import androidx.room.Room
import com.lumixa.app.data.local.database.LumixaDatabase

object DatabaseProvider {

    private var database: LumixaDatabase? = null

    fun getDatabase(context: Context): LumixaDatabase {

        return database ?: synchronized(this) {

            val instance = Room.databaseBuilder(
                context.applicationContext,
                LumixaDatabase::class.java,
                "lumixa_database"
            )
                .fallbackToDestructiveMigration()
                .build()

            database = instance

            instance
        }
    }
}