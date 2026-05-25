package com.lumixa.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "savings")
data class SavingsEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val amount: Double = 0.0,

    val date: String = "",

    val userId: String = ""
)