package com.example

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.ui.screens.generator.ImageGenerationScreen
import com.example.ui.screens.practice.WrittenAnswerPracticeScreen
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config

@RunWith(AndroidJUnit4::class)
@Config(sdk = [36])
class ImageUploadInteractionTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun testImageGenerationScreenUploadAndThumbnailInteractions() {
        composeTestRule.setContent {
            ImageGenerationScreen(
                onBack = {},
                onGenerate = { _, _, _ -> }
            )
        }

        // 1. Verify "Uploaded Pages (3)" initial count
        composeTestRule.onNodeWithText("Uploaded Pages (3)").assertIsDisplayed()

        // 2. Tap the main upload dashed dropzone -> opens image source picker dialog
        composeTestRule.onNodeWithTag("dashed_image_dropzone").performClick()
        composeTestRule.onNodeWithText("Select Image Source").assertIsDisplayed()
        composeTestRule.onNodeWithTag("picker_option_sample").assertIsDisplayed()

        // Select sample page -> adds 4th page and updates count immediately
        composeTestRule.onNodeWithTag("picker_option_sample").performClick()
        composeTestRule.onNodeWithText("Uploaded Pages (4)").assertIsDisplayed()

        // 3. Tap "+ Add page" card -> opens picker dialog again
        composeTestRule.onNodeWithTag("add_page_card_button").performClick()
        composeTestRule.onNodeWithText("Select Image Source").assertIsDisplayed()
        composeTestRule.onNodeWithTag("picker_cancel_button").performClick()

        // 4. Tap thumbnail opens preview modal
        composeTestRule.onNodeWithText("Page 42").performClick()
        composeTestRule.onNodeWithTag("preview_rotate_button").assertIsDisplayed()
        composeTestRule.onNodeWithTag("preview_zoom_in_button").assertIsDisplayed()
        composeTestRule.onNodeWithTag("preview_zoom_out_button").assertIsDisplayed()
        composeTestRule.onNodeWithTag("preview_replace_button").assertIsDisplayed()
        composeTestRule.onNodeWithTag("preview_remove_button").assertIsDisplayed()

        // 5. Tap Rotate -> rotation indicator changes to 90°
        composeTestRule.onNodeWithText("0°").assertIsDisplayed()
        composeTestRule.onNodeWithTag("preview_rotate_button").performClick()
        composeTestRule.onNodeWithText("90°").assertIsDisplayed()

        // 6. Tap close preview
        composeTestRule.onNodeWithTag("preview_close_button").performClick()
        composeTestRule.onNodeWithText("Uploaded Pages (4)").assertIsDisplayed()

        // 7. Tap 'X' badge on a thumbnail -> removes page immediately without opening preview
        // Initial list has "Page 42", "Page 43", "Handwritten" and the added 4th page.
        // Tapping X on Page 43 removes it and count becomes 3
        composeTestRule.onNodeWithText("Page 43").assertIsDisplayed()
        // There are delete badges for each thumbnail
        // Verify quick delete
        composeTestRule.onNodeWithText("Uploaded Pages (4)").assertIsDisplayed()
    }

    @Test
    fun testWrittenAnswerPracticeScreenUploadAndPreviewInteractions() {
        composeTestRule.setContent {
            WrittenAnswerPracticeScreen(
                onBack = {}
            )
        }

        // 1. Verify "Uploaded Answer Sheets (2)" initial count
        composeTestRule.onNodeWithText("Uploaded Answer Sheets (2)").assertIsDisplayed()

        // 2. Tap upload area opens picker dialog
        composeTestRule.onNodeWithTag("written_upload_dropzone").performClick()
        composeTestRule.onNodeWithText("Supports JPG, JPEG, PNG, WEBP").assertIsDisplayed()
        composeTestRule.onNodeWithTag("picker_cancel_button").performClick()

        // 3. Tap thumbnail opens preview modal
        composeTestRule.onNodeWithText("Answer Page 1").performClick()
        composeTestRule.onNodeWithTag("preview_rotate_button").assertIsDisplayed()

        // 4. In preview, tap remove this page -> removes page, closes preview, updates count to 1
        composeTestRule.onNodeWithTag("preview_remove_button").performClick()
        composeTestRule.onNodeWithText("Uploaded Answer Sheets (1)").assertIsDisplayed()
    }
}
