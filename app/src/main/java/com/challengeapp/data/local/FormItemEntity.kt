package com.challengeapp.data.local

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "form_item",
    foreignKeys = [
        ForeignKey(
            entity = FormItemEntity::class,
            parentColumns = ["id"],
            childColumns = ["parentId"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [Index("parentId")],
)
data class FormItemEntity(
    @PrimaryKey val id: Long,
    val parentId: Long?,
    val position: Int,
    val type: String,
    val title: String?,
    val content: String?,
    val src: String?,
    val multipleSelection: Boolean?,
)
