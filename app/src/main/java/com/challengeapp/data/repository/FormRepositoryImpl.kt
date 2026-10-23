package com.challengeapp.data.repository

import com.challengeapp.data.local.FormDao
import com.challengeapp.data.remote.FormApi
import com.challengeapp.domain.model.Page
import com.challengeapp.domain.repository.FormRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class FormRepositoryImpl @Inject constructor(
    private val api: FormApi,
    private val dao: FormDao,
) : FormRepository {

    override fun observePages(): Flow<List<Page>> = dao.observeForm().map { it.toPages() }

    override suspend fun refresh(): Result<Unit> = try {
        val entities = api.getForm().toEntities()
        dao.replaceAll(entities.items, entities.responses)
        Result.success(Unit)
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        Result.failure(e)
    }
}
