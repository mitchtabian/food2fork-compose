package com.codingwithmitch.food2forkcompose.presentation.ui.recipe_list

import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Scaffold
import androidx.compose.material.ScaffoldState
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.AmbientContext
import androidx.compose.ui.viewinterop.viewModel
import androidx.hilt.navigation.HiltViewModelFactory
import androidx.navigation.NavBackStackEntry
import com.codingwithmitch.food2forkcompose.domain.model.Recipe
import com.codingwithmitch.food2forkcompose.presentation.components.GenericDialogInfo
import com.codingwithmitch.food2forkcompose.presentation.components.RecipeList
import com.codingwithmitch.food2forkcompose.presentation.components.SearchAppBar
import com.codingwithmitch.food2forkcompose.presentation.theme.AppTheme
import com.codingwithmitch.mvvmrecipeapp.presentation.components.util.SnackbarController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import java.util.*

@ExperimentalMaterialApi
@ExperimentalCoroutinesApi
@Composable
fun RecipeListScreen(
    isDarkTheme: Boolean,
    isNetworkAvailable: Boolean,
    onToggleTheme: () -> Unit,
    onNavigateToRecipeDetailScreen: (String) -> Unit,
    viewModel: RecipeListViewModel,
){
    val recipes = viewModel.recipes.value

    val query = viewModel.query.value

    val selectedCategory = viewModel.selectedCategory.value

    val loading = viewModel.loading.value

    val page = viewModel.page.value

    val messageQueue = viewModel.messageQueue.value

    val scaffoldState = rememberScaffoldState()

    AppTheme(
        displayProgressBar = loading,
        scaffoldState = scaffoldState,
        darkTheme = isDarkTheme,
        isNetworkAvailable = isNetworkAvailable,
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
                    onToggleTheme = onToggleTheme
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
                onNavigateToRecipeDetailScreen = onNavigateToRecipeDetailScreen
            )
        }
    }
}