package com.codingwithmitch.food2forkcompose.presentation.ui

import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import com.codingwithmitch.food2forkcompose.presentation.MainActivity
import com.codingwithmitch.food2forkcompose.presentation.theme.AppTheme
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
@ExperimentalMaterialApi
class MainActivityTest {

  @get:Rule
  val composeTestRule = createAndroidComposeRule<MainActivity>()

  @Test
  fun SimpleTest(){
    composeTestRule.onNodeWithText("Search").assertIsDisplayed()
  }
}
















