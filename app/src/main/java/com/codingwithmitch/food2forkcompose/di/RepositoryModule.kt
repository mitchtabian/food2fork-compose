package com.codingwithmitch.food2forkcompose.di

import com.codingwithmitch.food2forkcompose.network.RecipeService
import com.codingwithmitch.food2forkcompose.network.model.RecipeMapper
import com.codingwithmitch.food2forkcompose.repository.RecipeRepository
import com.codingwithmitch.food2forkcompose.repository.RecipeRepository_Impl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
object RepositoryModule {

    @Singleton
    @Provides
    fun provideRecipeRepository(
        recipeService: RecipeService,
        recipeMapper: RecipeMapper,
    ): RecipeRepository{
        return RecipeRepository_Impl(
            recipeService = recipeService,
            mapper = recipeMapper
        )
    }
}












