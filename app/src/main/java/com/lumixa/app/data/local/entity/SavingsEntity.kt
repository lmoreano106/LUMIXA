package com.lumixa.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "savings")
data class SavingsEntity(

    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val userId: String,

    val amount: Double,

    val date: String
)