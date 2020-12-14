package com.codingwithmitch.food2forkcompose.di

import com.codingwithmitch.food2forkcompose.cache.RecipeDao
import com.codingwithmitch.food2forkcompose.cache.model.RecipeEntityMapper
import com.codingwithmitch.food2forkcompose.interactors.SearchRecipe
import com.codingwithmitch.food2forkcompose.network.RecipeService
import com.codingwithmitch.food2forkcompose.network.model.RecipeDtoMapper
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
object InteractorsModule {

    @Singleton
    @Provides
    fun provideSearchRecipe(
        recipeService: RecipeService,
        recipeDao: RecipeDao,
        recipeEntityMapper: RecipeEntityMapper,
        recipeDtoMapper: RecipeDtoMapper
    ): SearchRecipe{
        return SearchRecipe(
            recipeService = recipeService,
            recipeDao = recipeDao,
            entityMapper = recipeEntityMapper,
            dtoMapper = recipeDtoMapper,
        )
    }
}















