package com.codingwithmitch.food2forkcompose.presentation

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.ui.platform.AmbientContext
import androidx.compose.ui.platform.setContent
import androidx.compose.ui.viewinterop.viewModel
import androidx.hilt.navigation.HiltViewModelFactory
import androidx.navigation.NavType
import androidx.navigation.compose.*
import com.codingwithmitch.food2forkcompose.datastore.SettingsDataStore
import com.codingwithmitch.food2forkcompose.presentation.navigation.Screen.RecipeDetail
import com.codingwithmitch.food2forkcompose.presentation.navigation.Screen.RecipeList
import com.codingwithmitch.food2forkcompose.presentation.ui.recipe.RecipeDetailScreen
import com.codingwithmitch.food2forkcompose.presentation.ui.recipe.RecipeDetailViewModel
import com.codingwithmitch.food2forkcompose.presentation.ui.recipe_list.RecipeListScreen
import com.codingwithmitch.food2forkcompose.presentation.ui.recipe_list.RecipeListViewModel
import com.codingwithmitch.food2forkcompose.presentation.util.ConnectivityManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import javax.inject.Inject

@ExperimentalCoroutinesApi
@ExperimentalMaterialApi
@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

  @Inject
  lateinit var connectivityManager: ConnectivityManager

  @Inject
  lateinit var settingsDataStore: SettingsDataStore

  override fun onStart() {
    super.onStart()
    connectivityManager.registerConnectionObserver(this)
  }

  override fun onDestroy() {
    super.onDestroy()
    connectivityManager.unregisterConnectionObserver(this)
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContent {
      val navController = rememberNavController()
      NavHost(navController = navController, startDestination = RecipeList.route) {
        composable(route = RecipeList.route) { navBackStackEntry ->
          val factory = HiltViewModelFactory(AmbientContext.current, navBackStackEntry)
          val viewModel: RecipeListViewModel = viewModel("RecipeListViewModel", factory)
          RecipeListScreen(
            isDarkTheme = settingsDataStore.isDark.value,
            isNetworkAvailable = connectivityManager.isNetworkAvailable.value,
            onNavigateToRecipeDetailScreen = navController::navigate,
            onToggleTheme = settingsDataStore::toggleTheme,
            viewModel = viewModel,
          )
        }
        composable(
          route = RecipeDetail.route + "/{recipeId}",
          arguments = listOf(navArgument("recipeId") {
            type = NavType.IntType
          })
        ) { navBackStackEntry ->
          val factory = HiltViewModelFactory(AmbientContext.current, navBackStackEntry)
          val viewModel: RecipeDetailViewModel = viewModel("RecipeDetailViewModel", factory)
          RecipeDetailScreen(
            isDarkTheme = settingsDataStore.isDark.value,
            isNetworkAvailable = connectivityManager.isNetworkAvailable.value,
            recipeId = navBackStackEntry.arguments?.getInt("recipeId"),
            viewModel = viewModel,
            onNavigateBack = navController::popBackStack
          )
        }
      }
    }
  }
}


