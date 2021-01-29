package com.codingwithmitch.food2forkcompose.presentation.ui.recipe

import android.util.Log
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.codingwithmitch.food2forkcompose.domain.model.Recipe
import com.codingwithmitch.food2forkcompose.interactors.recipe.GetRecipe
import com.codingwithmitch.food2forkcompose.presentation.components.GenericDialogInfo
import com.codingwithmitch.food2forkcompose.presentation.components.PositiveAction
import com.codingwithmitch.food2forkcompose.util.TAG
import com.codingwithmitch.mvvmrecipeapp.presentation.components.util.SnackbarController
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import java.util.*
import javax.inject.Inject
import javax.inject.Named

const val STATE_KEY_RECIPE = "recipe.state.recipe.key"

@ExperimentalMaterialApi
@ExperimentalCoroutinesApi
@HiltViewModel
class RecipeDetailViewModel
@Inject
constructor(
    private val getRecipe: GetRecipe,
    private @Named("auth_token") val token: String,
    private val state: SavedStateHandle,
): ViewModel(){

    val recipe: MutableState<Recipe?> = mutableStateOf(null)

    val onLoad: MutableState<Boolean> = mutableStateOf(false)

    val loading = mutableStateOf(false)

    val snackbarController = SnackbarController(viewModelScope)

    // Queue for "First-In-First-Out" behavior
    val messageQueue: MutableState<Queue<GenericDialogInfo>> = mutableStateOf(LinkedList())

    fun removeHeadMessage(){
        if (messageQueue.value.isNotEmpty()) {
            val update = messageQueue.value
            update.remove() // remove first (oldest message)
            messageQueue.value = ArrayDeque() // force recompose (bug?)
            messageQueue.value = update
        }
    }

    fun appendErrorMessage(title: String, description: String){
        messageQueue.value.offer(
            GenericDialogInfo.Builder(
                title = title,
                onDismiss = {removeHeadMessage()}
            )
                .description(description)
                .positive(
                    PositiveAction(
                        positiveBtnTxt = "Ok",
                        onPositiveAction = { removeHeadMessage() },
                    )
                )
                .build()
        )
    }

    init {
        // restore or init for first time
        state.get<Int>(STATE_KEY_RECIPE)?.let{ recipeId ->
            onTriggerEvent(RecipeEvent.GetRecipeEvent(recipeId))
        }
    }

    fun onTriggerEvent(event: RecipeEvent){
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

    private fun getRecipe(id: Int){
        getRecipe.execute(id, token).onEach { dataState ->
            loading.value = dataState.loading

            dataState.data?.let { data ->
                recipe.value = data
                state.set(STATE_KEY_RECIPE, data.id)
            }

            dataState.error?.let { error ->
                Log.e(TAG, "getRecipe: ${error}")
                appendErrorMessage("An Error Occurred", error)
            }
        }.launchIn(viewModelScope)
    }
}




















