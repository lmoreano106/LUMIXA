package com.lumixa.app.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.lumixa.app.data.local.dao.ExpenseDao
import com.lumixa.app.data.local.dao.GoalDao
import com.lumixa.app.data.local.dao.IncomeDao
import com.lumixa.app.data.local.dao.SavingsDao
import com.lumixa.app.data.local.entity.ExpenseEntity
import com.lumixa.app.data.local.entity.GoalEntity
import com.lumixa.app.data.local.entity.IncomeEntity
import com.lumixa.app.data.local.entity.SavingsEntity

@Database(
    entities = [
        ExpenseEntity::class,
        IncomeEntity::class,
        GoalEntity::class,
        SavingsEntity::class
    ],

    version = 5,

    exportSchema = false
)

abstract class LumixaDatabase : RoomDatabase() {

    abstract fun expenseDao(): ExpenseDao

    abstract fun incomeDao(): IncomeDao

    abstract fun goalDao(): GoalDao

    abstract fun savingsDao(): SavingsDao
}