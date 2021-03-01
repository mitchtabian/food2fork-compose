package com.codingwithmitch.food2forkcompose.interactors.recipe_list

import com.codingwithmitch.food2forkcompose.cache.RecipeDao
import com.codingwithmitch.food2forkcompose.cache.model.RecipeEntityMapper
import com.codingwithmitch.food2forkcompose.domain.data.DataState
import com.codingwithmitch.food2forkcompose.domain.model.Recipe
import com.codingwithmitch.food2forkcompose.util.RECIPE_PAGINATION_PAGE_SIZE
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

/**
 * Restore a list of recipes after process death.
 */
class RestoreRecipes(
  private val recipeDao: RecipeDao,
  private val entityMapper: RecipeEntityMapper,
) {
  fun execute(
    page: Int,
    query: String
  ): Flow<DataState<List<Recipe>>> = flow {
    try {
      emit(DataState.loading())

      // just to show pagination, api is fast
      delay(1000)

      // query the cache
      val cacheResult = if (query.isBlank()){
        recipeDao.restoreAllRecipes(
          pageSize = RECIPE_PAGINATION_PAGE_SIZE,
          page = page
        )
      }
      else{
        recipeDao.restoreRecipes(
          query = query,
          pageSize = RECIPE_PAGINATION_PAGE_SIZE,
          page = page
        )
      }

      // emit List<Recipe> from cache
      val list = entityMapper.fromEntityList(cacheResult)
      emit(DataState.success(list))

    }catch (e: Exception){
      emit(DataState.error<List<Recipe>>(e.message?: "Unknown Error"))
    }
  }
}




