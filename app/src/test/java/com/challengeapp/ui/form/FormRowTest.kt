package com.challengeapp.ui.form

import com.challengeapp.domain.model.Page
import com.challengeapp.domain.model.Section
import com.challengeapp.domain.model.TextQuestion
import org.junit.Assert.assertEquals
import org.junit.Test

class FormRowTest {

    @Test
    fun `toRows lists items in order with their nesting depth`() {
        val pages = listOf(
            Page(
                1, "Page", listOf(
                    Section(
                        2, "Section", listOf(
                            Section(3, "Nested", listOf(TextQuestion(4, "Deep"))),
                        ),
                    ),
                    TextQuestion(5, "Shallow"),
                ),
            ),
            Page(6, "Second", emptyList()),
        )

        val rows = pages.toRows()

        assertEquals(listOf(1L, 2L, 3L, 4L, 5L, 6L), rows.map { it.item.id })
        assertEquals(listOf(0, 1, 2, 3, 1, 0), rows.map { it.depth })
    }
}
