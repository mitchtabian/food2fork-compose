package com.codingwithmitch.food2forkcompose.cache.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.codingwithmitch.food2forkcompose.cache.RecipeDao
import com.codingwithmitch.food2forkcompose.cache.model.RecipeEntity

@Database(entities = [RecipeEntity::class ], version = 1)
abstract class AppDatabase: RoomDatabase() {

    abstract fun recipeDao(): RecipeDao

    companion object{
        val DATABASE_NAME: String = "recipe_db"
    }


}