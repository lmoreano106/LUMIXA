package com.lumixa.app.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.lumixa.app.data.local.dao.ExpenseDao
import com.lumixa.app.data.local.entity.ExpenseEntity

@Database(
    entities = [ExpenseEntity::class],
    version = 1,
    exportSchema = false
)
abstract class LumixaDatabase : RoomDatabase() {

    abstract fun expenseDao(): ExpenseDao
}