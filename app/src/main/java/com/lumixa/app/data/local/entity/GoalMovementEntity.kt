package com.lumixa.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

const val GOAL_MOVEMENT_WITHDRAWAL = "goal_withdrawal"

@Entity(tableName = "goal_movements")
data class GoalMovementEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val goalId: Int = 0,
    val userId: String = "",
    val amount: Double = 0.0,
    val description: String = "",
    val type: String = GOAL_MOVEMENT_WITHDRAWAL,
    val date: String = "",
    val time: String = ""
)
