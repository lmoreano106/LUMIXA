package com.lumixa.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "goals")
data class GoalEntity(

    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val userId: String,

    val name: String,

    val targetAmount: Double,

    val savedAmount: Double,

    val targetDate: String
)