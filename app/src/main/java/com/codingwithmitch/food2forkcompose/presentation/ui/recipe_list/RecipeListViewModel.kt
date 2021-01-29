package com.codingwithmitch.food2forkcompose.presentation.ui.recipe_list

import android.util.Log
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.codingwithmitch.food2forkcompose.domain.model.Recipe
import com.codingwithmitch.food2forkcompose.interactors.recipe_list.RestoreRecipes
import com.codingwithmitch.food2forkcompose.interactors.recipe_list.SearchRecipes
import com.codingwithmitch.food2forkcompose.presentation.components.GenericDialogInfo
import com.codingwithmitch.food2forkcompose.presentation.components.NegativeAction
import com.codingwithmitch.food2forkcompose.presentation.components.PositiveAction
import com.codingwithmitch.food2forkcompose.presentation.ui.recipe_list.RecipeListEvent.*
import com.codingwithmitch.food2forkcompose.util.TAG
import com.codingwithmitch.mvvmrecipeapp.presentation.components.util.SnackbarController
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import java.util.*
import javax.inject.Inject
import javax.inject.Named

const val PAGE_SIZE = 30

const val STATE_KEY_PAGE = "recipe.state.page.key"
const val STATE_KEY_QUERY = "recipe.state.query.key"
const val STATE_KEY_LIST_POSITION = "recipe.state.query.list_position"
const val STATE_KEY_SELECTED_CATEGORY = "recipe.state.query.selected_category"

@ExperimentalMaterialApi
@ExperimentalCoroutinesApi
@HiltViewModel
class RecipeListViewModel
@Inject
constructor(
    private val searchRecipe: SearchRecipes,
    private val restoreRecipes: RestoreRecipes,
    private @Named("auth_token") val token: String,
    private val savedStateHandle: SavedStateHandle,
) : ViewModel() {

  val recipes: MutableState<List<Recipe>> = mutableStateOf(ArrayList())

  val loading = mutableStateOf(false)

  val snackbarController = SnackbarController(viewModelScope)

  // Pagination starts at '1' (-1 = exhausted?)
  val page = mutableStateOf(1)

  val query = mutableStateOf("")

  val selectedCategory: MutableState<FoodCategory?> = mutableStateOf(null)

  var categoryScrollPosition: Float = 0f

  var recipeListScrollPosition = 0

  // Deque for "First-In-First-Out" behavior (Deque = double-ended queue)
  val messageStack: MutableState<Queue<GenericDialogInfo>> = mutableStateOf(LinkedList())

  fun removeOldestMessage(){
    if (messageStack.value.isNotEmpty()) {
      val update = messageStack.value
      update.remove() // remove first (oldest message)
      messageStack.value = ArrayDeque() // force recompose (bug?)
      messageStack.value = update
    }
  }

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
    if (recipeListScrollPosition != 0) {
      onTriggerEvent(RestoreStateEvent)
    } else {
      onTriggerEvent(NewSearchEvent)
    }
  }

  fun onTriggerEvent(event: RecipeListEvent) {
    try {
      when (event) {
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
    } catch (e: Exception) {
      Log.e(TAG, "launchJob: Exception: ${e}, ${e.cause}")
      e.printStackTrace()
    }
  }

  private fun restoreState() {
    restoreRecipes.execute(page = page.value, query = query.value).onEach { dataState ->
      loading.value = dataState.loading

      dataState.data?.let { list ->
        recipes.value = list
      }

      dataState.error?.let { error ->
        Log.e(TAG, "restoreState: ${error}")
        // TODO("handle errors...")
      }
    }.launchIn(viewModelScope)
  }

  private fun newSearch() {
    Log.d(TAG, "newSearch: query: ${query.value}")
    // New search. Reset the state
    resetSearchState()

    searchRecipe.execute(token = token, page = page.value, query = query.value).onEach { dataState ->
      loading.value = dataState.loading

      dataState.data?.let { list ->
        recipes.value = list
      }

      dataState.error?.let { error ->
        Log.e(TAG, "newSearch: ${error}")
        messageStack.value.offer(
            GenericDialogInfo.Builder(
                title = "An Error Occurred",
                onDismiss = {removeOldestMessage()}
            )
                .description(error)
                .positive(
                    PositiveAction(
                        positiveBtnTxt = "Ok",
                        onPositiveAction = { removeOldestMessage() },
                    )
                )
                .build()
        )
        messageStack.value.offer(
            GenericDialogInfo.Builder(
                title = "An Error Occurred",
                onDismiss = {removeOldestMessage()}
            )
                .description("It's all fucked up Rick.")
                .positive(
                    PositiveAction(
                        positiveBtnTxt = "Ok",
                        onPositiveAction = {removeOldestMessage()},
                    )
                )
                .build()
        )
        messageStack.value.offer(
            GenericDialogInfo.Builder(
                title = "An Error Occurred",
                onDismiss = {removeOldestMessage()}
            )
                .description("Another One.")
                .positive(
                    PositiveAction(
                        positiveBtnTxt = "Ok",
                        onPositiveAction = {removeOldestMessage()},
                    )
                )
                .negative(
                    NegativeAction(
                        negativeBtnTxt = "Cancel",
                        onNegativeAction = {removeOldestMessage()}
                    )
                )
                .build()
        )
      }
    }.launchIn(viewModelScope)

  }

  private fun nextPage() {
    if ((recipeListScrollPosition + 1) >= (page.value * PAGE_SIZE)) {
      incrementPage()
      Log.d(TAG, "nextPage: triggered: ${page.value}")

      if (page.value > 1) {
        searchRecipe.execute(token = token, page = page.value, query = query.value).onEach { dataState ->
          loading.value = dataState.loading

          dataState.data?.let { list ->
            appendRecipes(list)
          }

          dataState.error?.let { error ->
            Log.e(TAG, "nextPage: ${error}")
            // TODO("handle errors...")
          }
        }.launchIn(viewModelScope)
      }
    }
  }

  fun onChangeRecipeScrollPosition(position: Int) {
    setListScrollPosition(position)
  }

  /**
   * Called when a new search is executed.
   */
  private fun resetSearchState() {
    recipes.value = listOf()
    setPage(1)
    setListScrollPosition(0)
    if (selectedCategory.value?.value != query.value) clearSelectedCategory()
  }

  /**
   * Append new recipes to the current list of recipes
   */
  private fun appendRecipes(recipes: List<Recipe>) {
    val current = this.recipes.value
    val new = listOf(current, recipes).flatten()
    this.recipes.value = new
  }

  private fun incrementPage() {
    setPage(page.value + 1)
  }

  /**
   * Keep track of what the user has searched
   */
  fun onQueryChanged(query: String) {
    setQuery(query)
  }

  private fun clearSelectedCategory() {
    setSelectedCategory(null)
  }

  fun onSelectedCategoryChanged(category: String) {
    val newCategory = getFoodCategory(category)
    setSelectedCategory(newCategory)
    onQueryChanged(category)
  }


  fun onChangeCategoryScrollPosition(position: Float) {
    categoryScrollPosition = position
  }

  private fun setListScrollPosition(position: Int) {
    recipeListScrollPosition = position
    savedStateHandle.set(STATE_KEY_LIST_POSITION, position)
  }

  private fun setPage(page: Int) {
    this.page.value = page
    savedStateHandle.set(STATE_KEY_PAGE, page)
  }

  private fun setSelectedCategory(category: FoodCategory?) {
    selectedCategory.value = category
    savedStateHandle.set(STATE_KEY_SELECTED_CATEGORY, category)
  }

  private fun setQuery(query: String) {
    this.query.value = query
    savedStateHandle.set(STATE_KEY_QUERY, query)
  }
}



























