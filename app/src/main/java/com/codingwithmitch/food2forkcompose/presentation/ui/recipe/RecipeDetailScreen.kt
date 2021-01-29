package com.codingwithmitch.food2forkcompose.presentation.ui.recipe

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Scaffold
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.AmbientContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.viewModel
import androidx.hilt.navigation.HiltViewModelFactory
import androidx.navigation.NavBackStackEntry
import com.codingwithmitch.food2forkcompose.presentation.components.IMAGE_HEIGHT
import com.codingwithmitch.food2forkcompose.presentation.components.InvalidRecipe
import com.codingwithmitch.food2forkcompose.presentation.components.LoadingRecipeShimmer
import com.codingwithmitch.food2forkcompose.presentation.components.RecipeView
import com.codingwithmitch.food2forkcompose.presentation.theme.AppTheme
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch


@ExperimentalMaterialApi
@ExperimentalCoroutinesApi
@Composable
fun RecipeDetailScreen(
    isDarkTheme: Boolean,
    recipeId: Int?,
    navBackStackEntry: NavBackStackEntry,
    onNavigateBack: () -> Unit,
){
    if (recipeId == null){
        InvalidRecipe(onNavigateBack = { onNavigateBack()})
    }else{
        val factory = HiltViewModelFactory(AmbientContext.current, navBackStackEntry)
        val viewModel: RecipeDetailViewModel = viewModel("RecipeDetailViewModel", factory)

        // fire a one-off event to get the recipe from api
        val onLoad = viewModel.onLoad.value
        if(!onLoad){
            viewModel.onLoad.value = true
            viewModel.onTriggerEvent(RecipeEvent.GetRecipeEvent(recipeId))
        }

        val loading = viewModel.loading.value

        val messageQueue = viewModel.messageQueue.value

        val recipe = viewModel.recipe.value

        val scaffoldState = rememberScaffoldState()

        AppTheme(
            displayProgressBar = loading,
            scaffoldState = scaffoldState,
            darkTheme = isDarkTheme,
            messageQueue = messageQueue,
            onDismiss = { viewModel.removeHeadMessage() },
        ) {
            Scaffold(
                scaffoldState = scaffoldState,
                snackbarHost = {
                    scaffoldState.snackbarHostState
                }
            ) {
                Box(
                    modifier = Modifier.fillMaxSize()
                ) {
                    if (loading && recipe == null) {
                        LoadingRecipeShimmer(imageHeight = IMAGE_HEIGHT.dp)
                    }
                    else if(!loading && recipe == null && onLoad){
                        InvalidRecipe(onNavigateBack = {onNavigateBack()})
                    }
                    else {
                        recipe?.let {
                            if (it.id == 1) { // force an error to demo snackbar
                                viewModel.snackbarController.getScope().launch {
                                    viewModel.snackbarController.showSnackbar(
                                        scaffoldState = scaffoldState,
                                        message = "An error occurred with this recipe",
                                        actionLabel = "Ok"
                                    )
                                }
                            } else {
                                RecipeView(
                                    recipe = it,
                                )
                            }
                        }
                    }
                }
            }
        }

    }
}