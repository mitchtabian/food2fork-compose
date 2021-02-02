package com.codingwithmitch.food2forkcompose.presentation.ui.recipe_list

import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Text
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.navigation.NavBackStackEntry
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
@ExperimentalMaterialApi
class RecipeScreenTest {

  @get:Rule
  val composeTestRule = createComposeRule()

  @Test
  fun SomeTest(){
    composeTestRule.setContent {
      RecipeListScreen(
        isDarkTheme = false,
        onToggleTheme = {  },
        navBackStackEntry = ,
        onNavigateToRecipeDetailScreen = {  })
    }


  }

}











