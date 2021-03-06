package com.codingwithmitch.food2forkcompose.presentation.ui.recipe

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Scaffold
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.codingwithmitch.food2forkcompose.presentation.components.IMAGE_HEIGHT
import com.codingwithmitch.food2forkcompose.presentation.components.LoadingRecipeShimmer
import com.codingwithmitch.food2forkcompose.presentation.components.RecipeView
import com.codingwithmitch.food2forkcompose.presentation.theme.AppTheme
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalComposeUiApi
@ExperimentalMaterialApi
@ExperimentalCoroutinesApi
@Composable
fun RecipeDetailScreen(
  isDarkTheme: Boolean,
  isNetworkAvailable: Boolean,
  recipeId: Int?,
  viewModel: RecipeViewModel,
){
  if (recipeId == null){
    TODO("Show Invalid Recipe")
  }else {
    // fire a one-off event to get the recipe from api
    val onLoad = viewModel.onLoad.value
    if (!onLoad) {
      viewModel.onLoad.value = true
      viewModel.onTriggerEvent(RecipeEvent.GetRecipeEvent(recipeId))
    }

    val loading = viewModel.loading.value

    val recipe = viewModel.recipe.value

    val dialogQueue = viewModel.dialogQueue

    val scaffoldState = rememberScaffoldState()

    AppTheme(
      displayProgressBar = loading,
      scaffoldState = scaffoldState,
      darkTheme = isDarkTheme,
      isNetworkAvailable = isNetworkAvailable,
      dialogQueue = dialogQueue.queue.value,
    ){
      Scaffold(
        scaffoldState = scaffoldState,
        snackbarHost = {
          scaffoldState.snackbarHostState
        }
      ) {
        Box (
          modifier = Modifier.fillMaxSize()
        ){
          if (loading && recipe == null) {
            LoadingRecipeShimmer(imageHeight = IMAGE_HEIGHT.dp)
          }
          else if(!loading && recipe == null && onLoad){
            TODO("Show Invalid Recipe")
          }
          else {
            recipe?.let {RecipeView(recipe = it) }
          }
        }
      }
    }
  }
}