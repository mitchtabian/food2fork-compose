package com.codingwithmitch.food2forkcompose.presentation.ui.recipe_list

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.codingwithmitch.food2forkcompose.domain.model.Recipe
import com.codingwithmitch.food2forkcompose.interactors.recipe_list.RestoreRecipes
import com.codingwithmitch.food2forkcompose.interactors.recipe_list.SearchRecipes
import com.codingwithmitch.food2forkcompose.presentation.ui.recipe_list.RecipeListEvent.*
import com.codingwithmitch.food2forkcompose.util.TAG
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.withContext
import javax.inject.Named

const val PAGE_SIZE = 30

const val STATE_KEY_PAGE = "recipe.state.page.key"
const val STATE_KEY_QUERY = "recipe.state.query.key"
const val STATE_KEY_LIST_POSITION = "recipe.state.query.list_position"
const val STATE_KEY_SELECTED_CATEGORY = "recipe.state.query.selected_category"

@ExperimentalCoroutinesApi
class RecipeListViewModel
@ViewModelInject
constructor(
    private val searchRecipe: SearchRecipes,
    private val restoreRecipes: RestoreRecipes,
    private @Named("auth_token") val token: String,
    @Assisted private val savedStateHandle: SavedStateHandle,
): ViewModel(){

    val recipes: MutableState<List<Recipe>> = mutableStateOf(ArrayList())

    val loading = mutableStateOf(false)

    // Pagination starts at '1' (-1 = exhausted?)
    val page = mutableStateOf(1)

    val query = mutableStateOf("")

    val selectedCategory: MutableState<FoodCategory?> = mutableStateOf(null)

    var categoryScrollPosition: Float = 0f

    var recipeListScrollPosition = 0

    init {
        savedStateHandle.get<Int>(STATE_KEY_PAGE)?.let { p ->
            Log.d(TAG, "restoring page: ${p}")
            setPage(p)
        }
        savedStateHandle.get<String>(STATE_KEY_QUERY)?.let { q ->
            setQuery(q)
        }
        savedStateHandle.get<Int>(STATE_KEY_LIST_POSITION)?.let { p ->
            Log.d(TAG, "restoring scroll position: ${p}")
            setListScrollPosition(p)
        }
        savedStateHandle.get<FoodCategory>(STATE_KEY_SELECTED_CATEGORY)?.let { c ->
            setSelectedCategory(c)
        }

        // Were they doing something before the process died?
        if(recipeListScrollPosition != 0){
            onTriggerEvent(RestoreStateEvent())
        }
        else{
            onTriggerEvent(NewSearchEvent())
        }

    }

    fun onTriggerEvent(event: RecipeListEvent){
        try {
            when(event){
                is NewSearchEvent -> {
                    newSearch()
                }
                is NextPageEvent -> {
                    nextPage()
                }
                is RestoreStateEvent -> {
                    restoreState()
                }
            }
        }catch (e: Exception){
            Log.e(TAG, "launchJob: Exception: ${e}, ${e.cause}")
            e.printStackTrace()
        }
        finally {
            Log.d(TAG, "launchJob: finally called.")
        }
    }

    private fun restoreState(){
        restoreRecipes.execute(page = page.value, query = query.value ).onEach { dataState ->
            withContext(Main){
                loading.value = dataState.loading

                dataState.data?.let { list ->
                    recipes.value = list
                }

                dataState.error?.let { error ->
                    Log.e(TAG, "restoreState: ${error}")
                    // TODO("handle errors...")
                }
            }
        }.launchIn(viewModelScope)
    }

    private fun newSearch(){
        Log.d(TAG, "newSearch: query: ${query.value}")
        // New search. Reset the state
        resetSearchState()

        searchRecipe.execute(token = token, page = page.value, query = query.value ).onEach { dataState ->
            withContext(Main){
                loading.value = dataState.loading

                dataState.data?.let { list ->
                    recipes.value = list
                }

                dataState.error?.let { error ->
                    Log.e(TAG, "newSearch: ${error}")
                    // TODO("handle errors...")
                }
            }
        }.launchIn(viewModelScope)

    }

    private fun nextPage(){
        incrementPage()
        Log.d(TAG, "nextPage: triggered: ${page.value}")

        if(page.value > 1){
            searchRecipe.execute(token = token, page = page.value, query = query.value ).onEach { dataState ->
                withContext(Main){
                    loading.value = dataState.loading

                    dataState.data?.let { list ->
                        appendRecipes(list)
                    }
                }
            }.launchIn(viewModelScope)
        }
    }

    fun onChangeRecipeScrollPosition(position: Int){
        setListScrollPosition(position)
    }

    /**
     * Called when a new search is executed.
     */
    private fun resetSearchState(){
        recipes.value = listOf()
        setPage(1)
        setListScrollPosition(0)
        if(selectedCategory.value?.value != query.value) clearSelectedCategory()
    }

    /**
     * Append new recipes to the current list of recipes
     */
    private fun appendRecipes(recipes: List<Recipe>){
        val current = this.recipes.value
        val new = listOf(current, recipes).flatten()
        this.recipes.value = new
    }

    private fun incrementPage(){
        setPage(page.value + 1)
    }

    /**
     * Keep track of what the user has searched
     */
    fun onQueryChanged(query: String){
        setQuery(query)
    }

    private fun clearSelectedCategory(){
        setSelectedCategory(null)
    }

    fun onSelectedCategoryChanged(category: String){
        val newCategory = getFoodCategory(category)
        setSelectedCategory(newCategory)
        onQueryChanged(category)
    }


    fun onChangeCategoryScrollPosition(position: Float){
        categoryScrollPosition = position
    }

    private fun setListScrollPosition(position: Int){
        recipeListScrollPosition = position
        savedStateHandle.set(STATE_KEY_LIST_POSITION, position)
    }

    private fun setPage(page: Int){
        this.page.value = page
        savedStateHandle.set(STATE_KEY_PAGE, page)
    }

    private fun setSelectedCategory(category: FoodCategory?){
        selectedCategory.value = category
        savedStateHandle.set(STATE_KEY_SELECTED_CATEGORY, category)
    }

    private fun setQuery(query: String){
        this.query.value = query
        savedStateHandle.set(STATE_KEY_QUERY, query)
    }
}



























