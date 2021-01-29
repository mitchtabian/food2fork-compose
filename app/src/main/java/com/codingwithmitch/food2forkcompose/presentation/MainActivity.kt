package com.codingwithmitch.food2forkcompose.presentation

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Scaffold
import androidx.compose.ui.platform.setContent
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navArgument
import androidx.navigation.compose.rememberNavController
import com.codingwithmitch.food2forkcompose.presentation.navigation.Screen.RecipeDetail
import com.codingwithmitch.food2forkcompose.presentation.navigation.Screen.RecipeList
import com.codingwithmitch.food2forkcompose.presentation.ui.recipe.RecipeDetailScreen
import com.codingwithmitch.food2forkcompose.presentation.ui.recipe_list.RecipeListScreen
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
@ExperimentalMaterialApi
@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController()
            Scaffold {
                NavHost(navController = navController, startDestination = RecipeList.route) {
                    composable(route = RecipeList.route) { navBackStackEntry ->
                        RecipeListScreen(
                            navController = navController,
                            isDarkTheme = (application as BaseApplication).isDark.value,
                            onToggleTheme = (application as BaseApplication)::toggleLightTheme,
                            navBackStackEntry = navBackStackEntry,
                        )
                    }
                    composable(
                        route = RecipeDetail.route + "/{recipeId}",
                        arguments = listOf(navArgument("recipeId") {
                            type = NavType.IntType
                        })
                    ) { navBackStackEntry ->
                        RecipeDetailScreen(
                            navController = navController,
                            isDarkTheme = (application as BaseApplication).isDark.value,
                            recipeId = navBackStackEntry.arguments?.getInt("recipeId"),
                            navBackStackEntry = navBackStackEntry,
                        )
                    }
                }
            }

        }
    }
}


