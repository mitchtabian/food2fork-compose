package com.codingwithmitch.food2forkcompose.presentation

import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.ui.platform.AmbientContext
import androidx.compose.ui.platform.setContent
import androidx.compose.ui.viewinterop.viewModel
import androidx.hilt.navigation.HiltViewModelFactory
import androidx.navigation.NavType
import androidx.navigation.compose.*
import com.codingwithmitch.food2forkcompose.presentation.navigation.Screen
import com.codingwithmitch.food2forkcompose.presentation.ui.recipe.RecipeDetailScreen
import com.codingwithmitch.food2forkcompose.presentation.ui.recipe.RecipeViewModel
import com.codingwithmitch.food2forkcompose.presentation.ui.recipe_list.RecipeListScreen
import com.codingwithmitch.food2forkcompose.presentation.ui.recipe_list.RecipeListViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
@ExperimentalMaterialApi
@AndroidEntryPoint
class MainActivity : AppCompatActivity(){

  val TAG = "c-manager"
  val cm = this.getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
  val networkRequest = NetworkRequest.Builder()
    .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    .build()
  val networkCallback = object: ConnectivityManager.NetworkCallback() {

    // Called when the framework connects and has declared a new network ready for use.
    override fun onAvailable(network: Network) {
      super.onAvailable(network)
      Log.d(TAG, "onAvailable: ${network}")
    }

    // Called when a network disconnects or otherwise no longer satisfies this request or callback
    override fun onLost(network: Network) {
      super.onLost(network)
      Log.d(TAG, "onLost: ${network}")
    }
  }

  override fun onStart() {
    super.onStart()
    cm.registerNetworkCallback(networkRequest, networkCallback)
  }

  override fun onDestroy() {
    super.onDestroy()
    cm.unregisterNetworkCallback(networkCallback)
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContent {
      val navController = rememberNavController()
      NavHost(navController = navController, startDestination = Screen.RecipeList.route) {
        composable(route = Screen.RecipeList.route) { navBackStackEntry ->
          val factory = HiltViewModelFactory(AmbientContext.current, navBackStackEntry)
          val viewModel: RecipeListViewModel = viewModel("RecipeListViewModel", factory)
          RecipeListScreen(
            isDarkTheme = (application as BaseApplication).isDark.value,
            onToggleTheme = (application as BaseApplication)::toggleLightTheme,
            onNavigateToRecipeDetailScreen = navController::navigate,
            viewModel = viewModel,
          )
        }
        composable(
          route = Screen.RecipeDetail.route + "/{recipeId}",
          arguments = listOf(navArgument("recipeId") {
            type = NavType.IntType
          })
        ) { navBackStackEntry ->
          val factory = HiltViewModelFactory(AmbientContext.current, navBackStackEntry)
          val viewModel: RecipeViewModel = viewModel("RecipeDetailViewModel", factory)
          RecipeDetailScreen(
            isDarkTheme = (application as BaseApplication).isDark.value,
            recipeId = navBackStackEntry.arguments?.getInt("recipeId"),
            viewModel = viewModel,
          )
        }
      }
    }
  }


}














