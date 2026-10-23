package com.challengeapp.data.repository

import com.challengeapp.data.local.FormDao
import com.challengeapp.data.remote.FormApi
import com.challengeapp.data.remote.isRetryable
import com.challengeapp.data.remote.toRefreshError
import com.challengeapp.domain.model.Page
import com.challengeapp.domain.model.RefreshError
import com.challengeapp.domain.repository.FormRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class FormRepositoryImpl @Inject constructor(
    private val api: FormApi,
    private val dao: FormDao,
) : FormRepository {

    override fun observePages(): Flow<List<Page>> = dao.observeForm().map { it.toPages() }

    override suspend fun refresh(): RefreshError? {
        var retryDelayMs = INITIAL_RETRY_DELAY_MS
        var attempt = 1
        while (true) {
            val error = fetchAndStore() ?: return null
            if (attempt == MAX_ATTEMPTS || !error.isRetryable()) return error.toRefreshError()
            delay(retryDelayMs)
            retryDelayMs *= 2
            attempt++
        }
    }

    private suspend fun fetchAndStore(): Exception? = try {
        val entities = api.getForm().toEntities()
        dao.replaceAll(entities.items, entities.responses)
        null
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        e
    }

    private companion object {
        const val MAX_ATTEMPTS = 3
        const val INITIAL_RETRY_DELAY_MS = 1_000L
    }
}
