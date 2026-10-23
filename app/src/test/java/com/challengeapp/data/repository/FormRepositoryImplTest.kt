package com.challengeapp.data.repository

import com.challengeapp.data.local.FormDao
import com.challengeapp.data.local.FormItemEntity
import com.challengeapp.data.local.FormItemWithResponses
import com.challengeapp.data.local.ResponseEntity
import com.challengeapp.data.remote.FormApi
import com.challengeapp.data.remote.ItemDto
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.IOException

class FormRepositoryImplTest {

    private val api = FakeFormApi()
    private val dao = FakeFormDao()
    private val repository = FormRepositoryImpl(api, dao)

    @Test
    fun `refresh stores the fetched form`() = runTest {
        api.form = loadSampleForm()

        val result = repository.refresh()

        assertTrue(result.isSuccess)
        assertEquals(listOf("Main Page", "Second Page"), repository.observePages().first().map { it.title })
    }

    @Test
    fun `refresh failure keeps the cached form`() = runTest {
        api.form = loadSampleForm()
        repository.refresh()
        api.error = IOException("offline")

        val result = repository.refresh()

        assertTrue(result.exceptionOrNull() is IOException)
        assertEquals(2, repository.observePages().first().size)
    }
}

private class FakeFormApi : FormApi {
    var form: List<ItemDto> = emptyList()
    var error: Exception? = null

    override suspend fun getForm(): List<ItemDto> {
        error?.let { throw it }
        return form
    }
}

private class FakeFormDao : FormDao {
    private val rows = MutableStateFlow<List<FormItemWithResponses>>(emptyList())

    override fun observeForm(): Flow<List<FormItemWithResponses>> = rows

    override suspend fun insertItems(items: List<FormItemEntity>) = error("unused")

    override suspend fun insertResponses(responses: List<ResponseEntity>) = error("unused")

    override suspend fun deleteAll() = error("unused")

    override suspend fun replaceAll(items: List<FormItemEntity>, responses: List<ResponseEntity>) {
        rows.value = FormEntities(items, responses).toRows()
    }
}
