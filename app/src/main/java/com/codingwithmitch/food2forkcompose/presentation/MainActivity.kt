package com.codingwithmitch.food2forkcompose.presentation

import android.os.Bundle
import android.util.Log
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.HiltViewModelFactory
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.*
import com.codingwithmitch.food2forkcompose.datastore.SettingsDataStore
import com.codingwithmitch.food2forkcompose.network.model.KotlinxRecipeDto
import com.codingwithmitch.food2forkcompose.network.model.RecipeDto
import com.codingwithmitch.food2forkcompose.presentation.navigation.Screen
import com.codingwithmitch.food2forkcompose.presentation.ui.recipe.RecipeDetailScreen
import com.codingwithmitch.food2forkcompose.presentation.ui.recipe.RecipeViewModel
import com.codingwithmitch.food2forkcompose.presentation.ui.recipe_list.RecipeListScreen
import com.codingwithmitch.food2forkcompose.presentation.ui.recipe_list.RecipeListViewModel
import com.codingwithmitch.food2forkcompose.presentation.util.ConnectivityManager
import dagger.hilt.android.AndroidEntryPoint
import io.ktor.client.*
import io.ktor.client.features.json.*
import io.ktor.client.features.json.serializer.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.util.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import javax.inject.Inject

@ExperimentalCoroutinesApi
@ExperimentalMaterialApi
@AndroidEntryPoint
class MainActivity : AppCompatActivity(){

  private val TAG: String = "AppDebug"

  @Inject
  lateinit var connectivityManager: ConnectivityManager

  @Inject
  lateinit var settingsDataStore: SettingsDataStore

  lateinit var httpClient: HttpClient

  override fun onStart() {
    super.onStart()
    connectivityManager.registerConnectionObserver(this)
  }

  override fun onDestroy() {
    super.onDestroy()
    connectivityManager.unregisterConnectionObserver(this)
  }

  @ExperimentalComposeUiApi
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)


    CoroutineScope(IO).launch {

      httpClient = HttpClient(){
        install(JsonFeature){
          serializer = KotlinxSerializer(kotlinx.serialization.json.Json {
            ignoreUnknownKeys = true // if the server sends extra fields, ignore them
          })
        }
      }
      val response = httpClient.get<KotlinxRecipeDto>("https://food2fork.ca/api/recipe/get/?id=1551"){
        headers {
          append("Authorization", "Token 9c8b06d329136da358c2d00e76946b0111ce2c48")
        }
      }
      Log.d(TAG, "ktor data: ${response}")

      // The method close signals to stop executing new requests. It wouldn't block and allows all current requests to finish successfully and release resources
      httpClient.close()
    }


    setContent {
      val navController = rememberNavController()
      NavHost(navController = navController, startDestination = Screen.RecipeList.route) {
        composable(route = Screen.RecipeList.route) { navBackStackEntry ->
          val factory = HiltViewModelFactory(LocalContext.current, navBackStackEntry)
          val viewModel: RecipeListViewModel = viewModel("RecipeListViewModel", factory)
          RecipeListScreen(
            isDarkTheme = settingsDataStore.isDark.value,
            isNetworkAvailable = connectivityManager.isNetworkAvailable.value,
            onToggleTheme = settingsDataStore::toggleTheme,
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
          val factory = HiltViewModelFactory(LocalContext.current, navBackStackEntry)
          val viewModel: RecipeViewModel = viewModel("RecipeDetailViewModel", factory)
          RecipeDetailScreen(
            isDarkTheme = settingsDataStore.isDark.value,
            isNetworkAvailable = connectivityManager.isNetworkAvailable.value,
            recipeId = navBackStackEntry.arguments?.getInt("recipeId"),
            viewModel = viewModel,
          )
        }
      }
    }
  }


}














