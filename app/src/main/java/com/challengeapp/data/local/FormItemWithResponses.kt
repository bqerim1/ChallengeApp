package com.challengeapp.data.local

import androidx.room.Embedded
import androidx.room.Relation

data class FormItemWithResponses(
    @Embedded val item: FormItemEntity,
    @Relation(parentColumn = "id", entityColumn = "questionId")
    val responses: List<ResponseEntity>,
)
