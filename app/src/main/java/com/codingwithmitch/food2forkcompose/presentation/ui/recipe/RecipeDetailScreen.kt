package com.codingwithmitch.food2forkcompose.presentation.ui.recipe

import android.util.Log
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import com.codingwithmitch.food2forkcompose.util.TAG
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalMaterialApi
@ExperimentalCoroutinesApi
@Composable
fun RecipeDetailScreen(
  isDarkTheme: Boolean,
  recipeId: Int?,
  viewModel: RecipeViewModel,
){
  Log.d(TAG, "RecipeDetailScreen: ${viewModel}")
  Text(
    "Recipe id: ${recipeId}",
    style = MaterialTheme.typography.h2
  )

//  val loading = viewModel.loading.value
//
//  val recipe = viewModel.recipe.value
//
//  val scaffoldState = rememberScaffoldState()
//
//  AppTheme(
//    displayProgressBar = loading,
//    scaffoldState = scaffoldState,
//    darkTheme = isDarkTheme,
//  ){
//    Scaffold(
//      scaffoldState = scaffoldState,
//      snackbarHost = {
//        scaffoldState.snackbarHostState
//      }
//    ) {
//      Box (
//        modifier = Modifier.fillMaxSize()
//      ){
//        if (loading && recipe == null) {
//          LoadingRecipeShimmer(imageHeight = IMAGE_HEIGHT.dp)
//        }
//        else if(!loading && recipe == null && onLoad){
//          TODO("Show Invalid Recipe")
//        }
//        else {
//          recipe?.let {RecipeView(recipe = it) }
//        }
//      }
//    }
//  }
}