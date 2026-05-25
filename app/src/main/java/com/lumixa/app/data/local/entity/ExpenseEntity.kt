package com.lumixa.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "expenses")
data class ExpenseEntity(

    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val amount: Double = 0.0,

    val category: String = "",

    val description: String = "",

    val date: String = "",

    val time: String = "",

    val dayOfWeek: Int = 0,

    val userId: String = ""
)