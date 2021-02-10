package com.codingwithmitch.food2forkcompose.cache

import androidx.room.Dao
import androidx.room.Insert
import com.codingwithmitch.food2forkcompose.cache.model.RecipeEntity

@Dao
interface RecipeDao {

  @Insert
  suspend fun insertRecipe(recipe: RecipeEntity): Long


}