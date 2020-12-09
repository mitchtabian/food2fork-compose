package com.codingwithmitch.food2forkcompose.cache

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.codingwithmitch.food2forkcompose.cache.model.RecipeCacheEntity
import com.codingwithmitch.food2forkcompose.util.RECIPE_PAGINATION_PAGE_SIZE

@Dao
interface RecipeDao {

    @Insert
    suspend fun insertRecipe(recipe: RecipeCacheEntity): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertRecipes(recipes: List<RecipeCacheEntity>): LongArray

    @Query("SELECT * FROM recipes WHERE id = :id")
    suspend fun getRecipeById(id: Int): RecipeCacheEntity?

    @Query("DELETE FROM recipes WHERE id IN (:ids)")
    suspend fun deleteRecipes(ids: List<Int>): Int

    @Query("DELETE FROM recipes")
    suspend fun deleteAllRecipes()

    @Query("DELETE FROM recipes WHERE id = :primaryKey")
    suspend fun deleteRecipe(primaryKey: Int): Int

    @Query("""
        SELECT * FROM recipes 
        WHERE title LIKE :query
        OR description LIKE :query  
        OR ingredients LIKE :query  
        ORDER BY date_updated DESC LIMIT (:page * :pageSize)
        """)
    suspend fun searchRecipes(
        query: String,
        page: Int,
        pageSize: Int = RECIPE_PAGINATION_PAGE_SIZE
    ): List<RecipeCacheEntity>

    @Query("""
        SELECT * FROM recipes 
        ORDER BY date_updated DESC LIMIT (:page * :pageSize)
    """)
    suspend fun getAllRecipes(
        page: Int,
        pageSize: Int = RECIPE_PAGINATION_PAGE_SIZE
    ): List<RecipeCacheEntity>

}



















