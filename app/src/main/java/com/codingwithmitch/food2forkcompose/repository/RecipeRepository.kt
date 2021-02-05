package com.codingwithmitch.food2forkcompose.repository

import com.codingwithmitch.food2forkcompose.domain.model.Recipe


interface RecipeRepository {

    suspend fun search(token: String, page: Int, query: String): List<Recipe>

    suspend fun get(token: String, id: Int): Recipe

}
