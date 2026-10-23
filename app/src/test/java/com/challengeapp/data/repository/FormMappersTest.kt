package com.challengeapp.data.repository

import com.challengeapp.data.local.FormItemWithResponses
import com.challengeapp.data.remote.ItemDto
import com.challengeapp.domain.model.ChoiceQuestion
import com.challengeapp.domain.model.ImageQuestion
import com.challengeapp.domain.model.Response
import com.challengeapp.domain.model.Section
import com.challengeapp.domain.model.TextQuestion
import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class FormMappersTest {

    private val dtos = loadSampleForm()

    @Test
    fun `toEntities flattens the tree with parent ids and positions`() {
        val entities = dtos.toEntities()

        assertEquals((1L..14L).toList(), entities.items.map { it.id })
        val subsection = entities.items.first { it.id == 7L }
        assertEquals(5L, subsection.parentId)
        assertEquals(1, subsection.position)
        assertNull(entities.items.first { it.id == 10L }.parentId)
    }

    @Test
    fun `toEntities keeps responses with their question`() {
        val responses = dtos.toEntities().responses

        assertEquals(listOf(1011L, 1012L, 1013L, 1021L, 1022L, 1023L), responses.map { it.id })
        assertEquals(13L, responses.first().questionId)
        assertNull(responses.first { it.id == 1013L }.score)
    }

    @Test
    fun `toPages rebuilds the original hierarchy`() {
        val pages = dtos.toEntities().toRows().toPages()

        assertEquals(listOf("Main Page", "Second Page"), pages.map { it.title })
        val chapter1 = pages[0].items[1] as Section
        val subsection = chapter1.items[1] as Section
        assertEquals("Subsection 1.1", subsection.title)
        assertEquals(TextQuestion(8, "This is a subsection under Chapter 1."), subsection.items[0])
        assertEquals("Chapter 1 Image", (subsection.items[1] as ImageQuestion).title)
    }

    @Test
    fun `toPages maps choice questions with ordered responses`() {
        val chapter2 = dtos.toEntities().toRows().toPages()[1].items[0] as Section

        val single = chapter2.items[1] as ChoiceQuestion
        val multi = chapter2.items[2] as ChoiceQuestion
        assertEquals(false, single.multipleSelection)
        assertEquals(true, multi.multipleSelection)
        assertEquals(Response(1013, "Not applicable", null), single.responses.last())
    }

    @Test
    fun `toPages skips unknown item types`() {
        val form = listOf(
            ItemDto(id = 1, type = "page", title = "Page", items = listOf(
                ItemDto(id = 2, type = "video"),
                ItemDto(id = 3, type = "text", content = "Hello"),
            )),
        )

        val page = form.toEntities().toRows().toPages().single()

        assertEquals(listOf(TextQuestion(3, "Hello")), page.items)
    }
}

internal fun loadSampleForm(): List<ItemDto> {
    val json = requireNotNull(FormMappersTest::class.java.classLoader?.getResource("form.json")).readText()
    return Json.decodeFromString(json)
}

// Same shape Room returns for the observeForm query.
internal fun FormEntities.toRows(): List<FormItemWithResponses> =
    items.map { item -> FormItemWithResponses(item, responses.filter { it.questionId == item.id }) }
