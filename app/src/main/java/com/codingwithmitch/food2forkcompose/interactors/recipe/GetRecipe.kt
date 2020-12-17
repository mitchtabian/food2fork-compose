package com.codingwithmitch.food2forkcompose.interactors.recipe

import com.codingwithmitch.food2forkcompose.cache.RecipeDao
import com.codingwithmitch.food2forkcompose.cache.exceptions.RecipeCacheException
import com.codingwithmitch.food2forkcompose.cache.model.RecipeEntityMapper
import com.codingwithmitch.food2forkcompose.domain.data.DataState
import com.codingwithmitch.food2forkcompose.domain.model.Recipe
import com.codingwithmitch.food2forkcompose.network.RecipeService
import com.codingwithmitch.food2forkcompose.network.model.RecipeDtoMapper
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.lang.Exception

/**
 * Retrieve a recipe from the cache given it's unique id.
 */
class GetRecipe (
    private val recipeDao: RecipeDao,
    private val entityMapper: RecipeEntityMapper,
    private val recipeService: RecipeService,
    private val recipeDtoMapper: RecipeDtoMapper,
){

    fun execute(
        recipeId: Int,
        token: String,
    ): Flow<DataState<Recipe>> = flow {

        try {
            emit(DataState.loading())

            // just to show loading, cache is fast
            delay(1000)

            var recipe = getRecipeFromCache(recipeId = recipeId)

            if(recipe != null){
                emit(DataState.success(recipe))
            }
            // if the recipe is null, it means it was not in the cache for some reason. So get from network.
            else{

                // insert into cache
                recipeDao.insertRecipe(
                    entityMapper.mapFromDomainModel( // domain -> entity
                        getRecipeFromNetwork(token, recipeId) // dto -> domain
                    )
                )

                // get from cache
                recipe = getRecipeFromCache(recipeId = recipeId)

                // emit and finish
                if(recipe != null){
                    emit(DataState.success(recipe))
                }
                else{
                    throw RecipeCacheException("Unable to get recipe from the cache.")
                }
            }

        }catch (e: Exception){
            emit(DataState.error<Recipe>(e.message?: "Unknown Error"))
        }
    }

    private suspend fun getRecipeFromCache(recipeId: Int): Recipe? {
        return recipeDao.getRecipeById(recipeId)?.let { recipeEntity ->
            entityMapper.mapToDomainModel(recipeEntity)
        }
    }

    private suspend fun getRecipeFromNetwork(token: String, recipeId: Int): Recipe {
        return recipeDtoMapper.mapToDomainModel(recipeService.get(token, recipeId))
    }
}
































