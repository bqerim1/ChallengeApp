package com.challengeapp.domain.repository

import com.challengeapp.domain.model.Page
import com.challengeapp.domain.model.RefreshError
import kotlinx.coroutines.flow.Flow

interface FormRepository {
    fun observePages(): Flow<List<Page>>

    // Returns null when the saved form was updated.
    suspend fun refresh(): RefreshError?
}
