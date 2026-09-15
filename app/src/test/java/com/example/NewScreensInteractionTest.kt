package com.example

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import com.example.ui.screens.admin.AdminPanelScreen
import com.example.ui.screens.bank.QuestionBankScreen
import com.example.ui.screens.challenge.DailyChallengeScreen
import com.example.ui.screens.gamification.AchievementsScreen
import com.example.ui.screens.notes.AINotesGeneratorScreen
import com.example.ui.screens.revision.QuickRevisionScreen
import com.example.ui.theme.AIMCQTeacherTheme
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class NewScreensInteractionTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun testAINotesGeneratorScreenRendersAndSelectsNoteType() {
        var backClicked = false
        composeTestRule.setContent {
            AIMCQTeacherTheme(darkTheme = false) {
                AINotesGeneratorScreen(onBack = { backClicked = true })
            }
        }

        composeTestRule.onNodeWithText("AI Notes Generator").assertIsDisplayed()
        composeTestRule.onNodeWithTag("note_type_short_notes").assertExists()
        composeTestRule.onNodeWithTag("note_type_detailed_notes").assertExists()
        composeTestRule.onNodeWithTag("generate_notes_sticky_button").assertIsDisplayed()

        // Select detailed notes
        composeTestRule.onNodeWithTag("note_type_detailed_notes").performClick()

        // Test back button
        composeTestRule.onNodeWithTag("notes_back_button").performClick()
        assertTrue(backClicked)
    }

    @Test
    fun testQuickRevisionScreenRendersAndTogglesChecklist() {
        var backClicked = false
        composeTestRule.setContent {
            AIMCQTeacherTheme(darkTheme = false) {
                QuickRevisionScreen(onBack = { backClicked = true })
            }
        }

        composeTestRule.onNodeWithText("Quick Revision").assertIsDisplayed()
        composeTestRule.onNodeWithTag("checklist_item_concepts").assertExists()
        composeTestRule.onNodeWithTag("checklist_item_formulas").assertExists()
        composeTestRule.onNodeWithTag("generate_revision_sticky_button").assertIsDisplayed()

        // Toggle a checklist item
        composeTestRule.onNodeWithTag("checklist_item_concepts").performClick()

        // Back button
        composeTestRule.onNodeWithTag("revision_back_button").performClick()
        assertTrue(backClicked)
    }

    @Test
    fun testDailyChallengeScreenRendersStreakAndStartButton() {
        var startClicked = false
        composeTestRule.setContent {
            AIMCQTeacherTheme(darkTheme = false) {
                DailyChallengeScreen(
                    onBack = {},
                    onStartChallenge = { startClicked = true }
                )
            }
        }

        composeTestRule.onNodeWithText("Daily Challenge").assertIsDisplayed()
        composeTestRule.onNodeWithTag("today_challenge_feature_card").assertIsDisplayed()
        composeTestRule.onNodeWithText("10 Questions").assertIsDisplayed()
        composeTestRule.onNodeWithTag("your_streak_card").assertExists()
        composeTestRule.onNodeWithTag("streak_marker_Mon").assertExists()

        // Click Start Now
        composeTestRule.onNodeWithTag("daily_challenge_start_button").performClick()
        assertTrue(startClicked)
    }

    @Test
    fun testQuestionBankScreenRendersTabsAndSavedRows() {
        var backClicked = false
        composeTestRule.setContent {
            AIMCQTeacherTheme(darkTheme = false) {
                QuestionBankScreen(onBack = { backClicked = true })
            }
        }

        composeTestRule.onNodeWithText("Question Bank").assertIsDisplayed()
        composeTestRule.onNodeWithTag("tab_saved_questions").assertIsDisplayed()
        composeTestRule.onNodeWithTag("tab_saved_tests").assertIsDisplayed()
        composeTestRule.onNodeWithTag("bank_search_bar").assertIsDisplayed()

        // Switch to Saved Tests tab
        composeTestRule.onNodeWithTag("tab_saved_tests").performClick()
        composeTestRule.onNodeWithText("NEET Full Length Mock #4").assertIsDisplayed()

        // Back button
        composeTestRule.onNodeWithTag("bank_back_button").performClick()
        assertTrue(backClicked)
    }

    @Test
    fun testAchievementsScreenRendersBadgesAndStats() {
        var backClicked = false
        composeTestRule.setContent {
            AIMCQTeacherTheme(darkTheme = false) {
                AchievementsScreen(
                    userName = "Aarav",
                    onBack = { backClicked = true }
                )
            }
        }

        composeTestRule.onNodeWithText("Gamification & Achievements").assertIsDisplayed()
        composeTestRule.onNodeWithTag("badge_card_solved_count").assertExists()
        composeTestRule.onNodeWithTag("badge_card_streak").assertExists()
        composeTestRule.onNodeWithTag("motivational_mascot_banner").assertExists()

        // Switch to Stats tab
        composeTestRule.onNodeWithTag("tab_stats").performClick()
        composeTestRule.onNodeWithText("Total Solved").assertExists()

        // Back button
        composeTestRule.onNodeWithTag("achievements_back_button").performClick()
        assertTrue(backClicked)
    }

    @Test
    fun testAdminPanelScreenRendersSidebarAndKpiCards() {
        var logoutClicked = false
        composeTestRule.setContent {
            AIMCQTeacherTheme(darkTheme = false) {
                AdminPanelScreen(onBackToApp = { logoutClicked = true })
            }
        }

        composeTestRule.onNodeWithText("Admin Console").assertIsDisplayed()
        composeTestRule.onNodeWithTag("admin_nav_dashboard").assertIsDisplayed()
        composeTestRule.onNodeWithTag("admin_nav_users").assertIsDisplayed()
        composeTestRule.onNodeWithText("Admin Dashboard").assertIsDisplayed()
        composeTestRule.onNodeWithTag("kpi_card_total_users").assertExists()
        composeTestRule.onNodeWithTag("kpi_card_total_questions").assertExists()
        composeTestRule.onNodeWithTag("user_activity_chart_card").assertExists()
        composeTestRule.onNodeWithTag("recent_users_table_card").assertExists()

        // Click logout / back to app
        composeTestRule.onNodeWithTag("admin_logout_button").performClick()
        assertTrue(logoutClicked)
    }
}
