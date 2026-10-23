package com.challengeapp.domain.repository

import com.challengeapp.domain.model.Page
import kotlinx.coroutines.flow.Flow

interface FormRepository {
    fun observePages(): Flow<List<Page>>

    suspend fun refresh(): Result<Unit>
}
