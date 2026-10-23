package com.challengeapp.ui.form

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.challengeapp.domain.model.ChoiceQuestion
import com.challengeapp.domain.model.ImageQuestion
import com.challengeapp.domain.model.Page
import com.challengeapp.domain.model.Response
import com.challengeapp.ui.theme.ChallengeAppTheme
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class FormContentTest {

    @get:Rule
    val composeRule = createComposeRule()

    private val image = ImageQuestion(2, "Welcome Image", "https://example.com/image.png")
    private val choice = ChoiceQuestion(3, "Pick one", false, listOf(Response(30, "Yes", 1)))
    private val rows = listOf(Page(1, "Main Page", listOf(image, choice))).toRows()

    @Test
    fun tappingAnImageOpensIt() {
        var opened: ImageQuestion? = null
        setContent(onImageClick = { opened = it })

        composeRule.onNodeWithContentDescription("Welcome Image").performClick()

        assertEquals(image, opened)
    }

    @Test
    fun tappingAResponseReportsQuestionAndResponse() {
        var tapped: Pair<Long, Long>? = null
        setContent(onResponseClick = { question, responseId -> tapped = question.id to responseId })

        composeRule.onNodeWithText("Yes").performClick()

        assertEquals(3L to 30L, tapped)
    }

    private fun setContent(
        onResponseClick: (ChoiceQuestion, Long) -> Unit = { _, _ -> },
        onImageClick: (ImageQuestion) -> Unit = {},
    ) {
        composeRule.setContent {
            ChallengeAppTheme {
                FormContent(FormUiState(rows = rows), onResponseClick, onImageClick)
            }
        }
    }
}
