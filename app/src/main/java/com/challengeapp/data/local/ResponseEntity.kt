package com.challengeapp.data.local

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "response",
    foreignKeys = [
        ForeignKey(
            entity = FormItemEntity::class,
            parentColumns = ["id"],
            childColumns = ["questionId"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [Index("questionId")],
)
data class ResponseEntity(
    @PrimaryKey val id: Long,
    val questionId: Long,
    val position: Int,
    val label: String,
    val score: Int?,
)
