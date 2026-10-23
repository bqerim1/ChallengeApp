package com.challengeapp.data.repository

import com.challengeapp.data.local.FormDao
import com.challengeapp.data.local.FormItemEntity
import com.challengeapp.data.local.FormItemWithResponses
import com.challengeapp.data.local.ResponseEntity
import com.challengeapp.data.remote.FormApi
import com.challengeapp.data.remote.ItemDto
import com.challengeapp.domain.model.RefreshError
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.SerializationException
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException

class FormRepositoryImplTest {

    private val api = FakeFormApi()
    private val dao = FakeFormDao()
    private val repository = FormRepositoryImpl(api, dao)

    @Test
    fun `refresh stores the fetched form`() = runTest {
        api.results += Result.success(loadSampleForm())

        val error = repository.refresh()

        assertNull(error)
        assertEquals(listOf("Main Page", "Second Page"), repository.observePages().first().map { it.title })
    }

    @Test
    fun `refresh failure keeps the cached form`() = runTest {
        api.results += Result.success(loadSampleForm())
        repository.refresh()
        repeat(3) { api.results += Result.failure(IOException("offline")) }

        val error = repository.refresh()

        assertEquals(RefreshError.Offline, error)
        assertEquals(2, repository.observePages().first().size)
    }

    @Test
    fun `refresh retries a network error and succeeds`() = runTest {
        api.results += Result.failure(IOException("timeout"))
        api.results += Result.success(loadSampleForm())

        assertNull(repository.refresh())
        assertEquals(2, api.calls)
    }

    @Test
    fun `refresh gives up after three attempts`() = runTest {
        repeat(5) { api.results += Result.failure(IOException("offline")) }

        assertEquals(RefreshError.Offline, repository.refresh())
        assertEquals(3, api.calls)
    }

    @Test
    fun `refresh retries a server error and reports it as unavailable`() = runTest {
        repeat(3) { api.results += Result.failure(httpError(503)) }

        assertEquals(RefreshError.Unavailable, repository.refresh())
        assertEquals(3, api.calls)
    }

    @Test
    fun `refresh does not retry a client error or a malformed body`() = runTest {
        api.results += Result.failure(httpError(404))
        assertEquals(RefreshError.Unavailable, repository.refresh())
        assertEquals(1, api.calls)

        api.results += Result.failure(SerializationException("bad json"))
        assertEquals(RefreshError.Unavailable, repository.refresh())
        assertEquals(2, api.calls)
    }

    private fun httpError(code: Int) = HttpException(Response.error<Any>(code, "".toResponseBody()))
}

private class FakeFormApi : FormApi {
    val results = ArrayDeque<Result<List<ItemDto>>>()
    var calls = 0

    override suspend fun getForm(): List<ItemDto> {
        calls++
        return results.removeFirst().getOrThrow()
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
