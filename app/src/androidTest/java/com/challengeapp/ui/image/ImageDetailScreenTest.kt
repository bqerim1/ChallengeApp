package com.challengeapp.ui.image

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.challengeapp.ui.theme.ChallengeAppTheme
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ImageDetailScreenTest {

    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun showsTitleAndGoesBack() {
        var wentBack = false
        composeRule.setContent {
            ChallengeAppTheme {
                ImageDetailScreen(title = "Chapter 1 Image", src = "https://example.com/ios.png", onBack = { wentBack = true })
            }
        }

        composeRule.onNodeWithText("Chapter 1 Image").assertIsDisplayed()
        composeRule.onNodeWithContentDescription("Back").performClick()

        assertTrue(wentBack)
    }
}
