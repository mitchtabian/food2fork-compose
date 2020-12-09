package com.codingwithmitch.food2forkcompose.interactors

import com.codingwithmitch.food2forkcompose.cache.RecipeDao
import com.codingwithmitch.food2forkcompose.cache.exceptions.RecipeCacheException
import com.codingwithmitch.food2forkcompose.cache.model.RecipeCacheMapper
import com.codingwithmitch.food2forkcompose.domain.data.DataState
import com.codingwithmitch.food2forkcompose.domain.model.Recipe
import com.codingwithmitch.food2forkcompose.network.RecipeService
import com.codingwithmitch.food2forkcompose.network.model.RecipeNetworkMapper
import com.codingwithmitch.food2forkcompose.util.RECIPE_PAGINATION_PAGE_SIZE
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.lang.Exception

class SearchRecipe(
    private val recipeDao: RecipeDao,
    private val recipeService: RecipeService,
    private val cacheMapper: RecipeCacheMapper,
    private val networkMapper: RecipeNetworkMapper,
) {

    suspend fun execute(
        token: String,
        page: Int,
        query: String
    ): Flow<DataState<List<Recipe>>> = flow {

        try {
            emit(DataState.loading())

            // just to show pagination, api is fast
            delay(1000)

            throw RecipeCacheException("Oopsie poopsie")

            // search network
            val networkResult = recipeService.search(
                    token = token,
                    page = page,
                    query = query,
            )

            // Convert: NetworkRecipeEntity -> Recipe -> RecipeCacheEntity
            val recipes = networkMapper.fromEntityList(
                    networkResult.recipes
            )
            val entities = cacheMapper.toEntityList(recipes)

            // insert into cache
            recipeDao.insertRecipes(entities)

            // query the cache
            val cacheResult = if (query.isBlank()){
                recipeDao.getAllRecipes(
                        pageSize = RECIPE_PAGINATION_PAGE_SIZE,
                        page = page
                )
            }
            else{
                recipeDao.searchRecipes(
                        query = query,
                        pageSize = RECIPE_PAGINATION_PAGE_SIZE,
                        page = page
                )
            }

            // emit List<Recipe> from cache
            val list = cacheMapper.fromEntityList(cacheResult)
            emit(DataState.success(list))
        }catch (e: Exception){
            emit(DataState.error<List<Recipe>>(e.message?: "Unknown Error"))
        }

    }
}




























