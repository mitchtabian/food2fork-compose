package com.codingwithmitch.food2forkcompose.presentation.ui.recipe

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.codingwithmitch.food2forkcompose.domain.model.Recipe
import com.codingwithmitch.food2forkcompose.interactors.recipe.GetRecipe
import com.codingwithmitch.food2forkcompose.presentation.ui.util.DialogQueue
import com.codingwithmitch.food2forkcompose.presentation.util.ConnectivityManager
import com.codingwithmitch.food2forkcompose.util.TAG
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Named

const val STATE_KEY_RECIPE = "recipe.state.recipe.key"

@ExperimentalCoroutinesApi
@HiltViewModel
class RecipeViewModel
@Inject
constructor(
    private val getRecipe: GetRecipe,
    private val connectivityManager: ConnectivityManager,
    private @Named("auth_token") val token: String,
    private val state: SavedStateHandle,
): ViewModel(){

    val recipe: MutableState<Recipe?> = mutableStateOf(null)

    val loading = mutableStateOf(false)

    val onLoad: MutableState<Boolean> = mutableStateOf(false)

    val dialogQueue = DialogQueue()

    init {
        // restore if process dies
        state.get<Int>(STATE_KEY_RECIPE)?.let{ recipeId ->
            onTriggerEvent(RecipeEvent.GetRecipeEvent(recipeId))
        }
    }

    fun onTriggerEvent(event: RecipeEvent){
        viewModelScope.launch {
            try {
                when(event){
                    is RecipeEvent.GetRecipeEvent -> {
                        if(recipe.value == null){
                            getRecipe(event.id)
                        }
                    }
                }
            }catch (e: Exception){
                Log.e(TAG, "launchJob: Exception: ${e}, ${e.cause}")
                e.printStackTrace()
            }
        }
    }

    private fun getRecipe(id: Int){
        getRecipe.execute(id, token, connectivityManager.isNetworkAvailable.value).onEach { dataState ->
            loading.value = dataState.loading

            dataState.data?.let { data ->
                recipe.value = data
                state.set(STATE_KEY_RECIPE, data.id)
            }

            dataState.error?.let { error ->
                Log.e(TAG, "getRecipe: ${error}")
                dialogQueue.appendErrorMessage("An Error Occurred", error)
            }
        }.launchIn(viewModelScope)
    }
}












