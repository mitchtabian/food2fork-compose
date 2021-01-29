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
){
    if (recipeId == null){
        TODO("show error 'invalid recipe id?...")
    }else{
        val factory = HiltViewModelFactory(AmbientContext.current, navBackStackEntry)
        val viewModel: RecipeDetailViewModel = viewModel("RecipeDetailViewModel", factory)

        if(viewModel.recipe.value == null){
            viewModel.onTriggerEvent(RecipeEvent.GetRecipeEvent(recipeId))
        }

        val loading = viewModel.loading.value

        val recipe = viewModel.recipe.value

        val scaffoldState = rememberScaffoldState()

        AppTheme(
            displayProgressBar = loading,
            scaffoldState = scaffoldState,
            darkTheme = isDarkTheme,
            onDismiss = { },
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
                    if (loading && recipe == null) LoadingRecipeShimmer(imageHeight = IMAGE_HEIGHT.dp)
                    else recipe?.let {
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