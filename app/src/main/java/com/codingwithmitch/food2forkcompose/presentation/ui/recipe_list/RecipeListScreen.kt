package com.codingwithmitch.food2forkcompose.presentation.ui.recipe_list

import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Scaffold
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.AmbientContext
import androidx.compose.ui.viewinterop.viewModel
import androidx.hilt.navigation.HiltViewModelFactory
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import com.codingwithmitch.food2forkcompose.presentation.components.RecipeList
import com.codingwithmitch.food2forkcompose.presentation.components.SearchAppBar
import com.codingwithmitch.food2forkcompose.presentation.theme.AppTheme
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch

@ExperimentalMaterialApi
@ExperimentalCoroutinesApi
@Composable
fun RecipeListScreen(
    navController: NavController,
    isDarkTheme: Boolean,
    onToggleTheme: () -> Unit,
    navBackStackEntry: NavBackStackEntry,
){
    val factory = HiltViewModelFactory(AmbientContext.current, navBackStackEntry)
    val viewModel: RecipeListViewModel = viewModel("RecipeListViewModel", factory)

    val recipes = viewModel.recipes.value

    val query = viewModel.query.value

    val selectedCategory = viewModel.selectedCategory.value

    val categoryScrollPosition = viewModel.categoryScrollPosition

    val loading = viewModel.loading.value

    val page = viewModel.page.value

    val messageQueue = viewModel.messageQueue.value

    val scaffoldState = rememberScaffoldState()

    AppTheme(
        displayProgressBar = loading,
        scaffoldState = scaffoldState,
        darkTheme = isDarkTheme,
        messageQueue = messageQueue,
        onDismiss = viewModel::removeHeadMessage,
    ) {
        Scaffold(
            topBar = {
                SearchAppBar(
                    query = query,
                    onQueryChanged = viewModel::onQueryChanged,
                    onExecuteSearch = {
                        if (viewModel.selectedCategory.value?.value == "Milk") {
                            viewModel.snackbarController.getScope().launch {
                                viewModel.snackbarController.showSnackbar(
                                    scaffoldState = scaffoldState,
                                    message = "Invalid category: MILK",
                                    actionLabel = "Hide"
                                )
                            }
                        } else {
                            viewModel.onTriggerEvent(RecipeListEvent.NewSearchEvent)
                        }
                    },
                    categories = getAllFoodCategories(),
                    selectedCategory = selectedCategory,
                    onSelectedCategoryChanged = viewModel::onSelectedCategoryChanged,
                    scrollPosition = categoryScrollPosition,
                    onChangeScrollPosition = viewModel::onChangeCategoryScrollPosition,
                    onToggleTheme = {
                        onToggleTheme()
                    }
                )
            },
            scaffoldState = scaffoldState,
            snackbarHost = {
                scaffoldState.snackbarHostState
            },
        ) {
            RecipeList(
                loading = loading,
                recipes = recipes,
                onChangeScrollPosition = viewModel::onChangeRecipeScrollPosition,
                page = page,
                onTriggerNextPage = { viewModel.onTriggerEvent(RecipeListEvent.NextPageEvent) },
                navController = navController,
            )
        }
    }
}