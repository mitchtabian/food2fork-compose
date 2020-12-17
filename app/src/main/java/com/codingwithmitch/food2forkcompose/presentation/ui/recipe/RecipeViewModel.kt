package com.codingwithmitch.food2forkcompose.presentation.ui.recipe

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.codingwithmitch.food2forkcompose.domain.model.Recipe
import kotlinx.coroutines.ExperimentalCoroutinesApi
import javax.inject.Named

const val STATE_KEY_RECIPE = "recipe.state.recipe.key"

@ExperimentalCoroutinesApi
class RecipeViewModel
@ViewModelInject
constructor(
    private @Named("auth_token") val token: String,
    @Assisted private val state: SavedStateHandle,
): ViewModel(){

    val recipe: MutableState<Recipe?> = mutableStateOf(null)

    val loading = mutableStateOf(false)

    init {

        // restore if process dies
        state.get<Recipe>(STATE_KEY_RECIPE)?.let{ recipe ->
            this.recipe.value = recipe
        }
    }

    fun onTriggerEvent(event: RecipeEvent){
//        viewModelScope.launch {
//            try {
//                when(event){
//                    is GetRecipeEvent -> {
//                        if(recipe.value == null){
//                            getRecipe(event.id)
//                        }
//                    }
//                }
//            }catch (e: Exception){
//                Log.e(TAG, "launchJob: Exception: ${e}, ${e.cause}")
//                e.printStackTrace()
//            }
//            finally {
//                Log.d(TAG, "launchJob: finally called.")
//                loading.value = false
//            }
//        }
    }

//    private suspend fun getRecipe(id: Int){
//        loading.value = true
//
//        // simulate a delay to show loading
//        delay(1000)
//
//        val recipe = recipeRepository.get(token=token, id=id)
//        this.recipe.value = recipe
//
//        state.set(STATE_KEY_RECIPE, recipe)
//    }
}




















