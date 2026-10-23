package com.challengeapp.data.local

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class FormDaoTest {

    private lateinit var database: AppDatabase
    private lateinit var dao: FormDao

    @Before
    fun setUp() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java,
        ).build()
        dao = database.formDao()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun observeFormReturnsItemsWithTheirResponses() = runTest {
        dao.replaceAll(
            items = listOf(page(1), choice(2, parentId = 1)),
            responses = listOf(ResponseEntity(20, questionId = 2, position = 0, label = "Yes", score = 1)),
        )

        val rows = dao.observeForm().first()

        assertEquals(listOf(1L, 2L), rows.map { it.item.id })
        assertEquals(listOf("Yes"), rows.single { it.item.id == 2L }.responses.map { it.label })
    }

    @Test
    fun replaceAllRemovesPreviousItemsAndResponses() = runTest {
        dao.replaceAll(
            items = listOf(page(1), choice(2, parentId = 1)),
            responses = listOf(ResponseEntity(20, questionId = 2, position = 0, label = "Yes", score = 1)),
        )

        dao.replaceAll(items = listOf(page(10)), responses = emptyList())

        val rows = dao.observeForm().first()
        assertEquals(listOf(10L), rows.map { it.item.id })
        assertEquals(emptyList<ResponseEntity>(), rows.single().responses)
    }

    private fun page(id: Long) = FormItemEntity(id, null, 0, "page", "Page $id", null, null, null)

    private fun choice(id: Long, parentId: Long) =
        FormItemEntity(id, parentId, 0, "choice", null, "Question $id", null, false)
}
