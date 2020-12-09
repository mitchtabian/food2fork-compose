package com.codingwithmitch.food2forkcompose.cache.model

import android.util.Log
import com.codingwithmitch.food2forkcompose.cache.exceptions.RecipeCacheException
import com.codingwithmitch.food2forkcompose.domain.model.Recipe
import com.codingwithmitch.food2forkcompose.util.EntityMapper
import com.codingwithmitch.food2forkcompose.util.TAG
import java.lang.StringBuilder

class RecipeCacheMapper : EntityMapper<RecipeCacheEntity, Recipe>{

    override fun mapFromEntity(entity: RecipeCacheEntity): Recipe {
        return Recipe(
            id = entity.id,
            title = entity.title,
            featuredImage = entity.featuredImage,
            rating = entity.rating,
            publisher = entity.publisher,
            sourceUrl = entity.sourceUrl,
            description = entity.description,
            cookingInstructions = entity.cookingInstructions,
            ingredients = convertIngredientsToList(entity.ingredients),
            dateAdded = entity.dateAdded,
            dateUpdated = entity.dateUpdated,
        )
    }

    override fun mapToEntity(domainModel: Recipe): RecipeCacheEntity {
        if(domainModel.id == null){
            throw RecipeCacheException("Recipe id must not be null")
        }
        if(domainModel.title == null){
            throw RecipeCacheException("Recipe title must not be null")
        }
        return RecipeCacheEntity(
            id = domainModel.id!!,
            title = domainModel.title!!,
            featuredImage = domainModel.featuredImage,
            rating = domainModel.rating,
            publisher = domainModel.publisher,
            sourceUrl = domainModel.sourceUrl,
            description = domainModel.description,
            cookingInstructions = domainModel.cookingInstructions,
            ingredients = convertIngredientListToString(domainModel.ingredients?: listOf()),
            dateAdded = domainModel.dateAdded,
            dateUpdated = domainModel.dateUpdated,
        )
    }

    /**
     * "Carrot, potato, Chicken, ..."
     */
    private fun convertIngredientListToString(ingredients: List<String>): String {
        val ingredientsString: StringBuilder = StringBuilder()
        for(ingredient in ingredients){
            ingredientsString.append("$ingredient,")
        }
        return ingredientsString.toString()
    }

    private fun convertIngredientsToList(ingredientsString: String?): List<String>{
        val list: ArrayList<String> = ArrayList()
        ingredientsString?.let {
            for(ingredient in it.split(",")){
                list.add(ingredient)
            }
        }
        return list
    }

    fun fromEntityList(initial: List<RecipeCacheEntity>): List<Recipe>{
        return initial.map { mapFromEntity(it) }
    }

    fun toEntityList(initial: List<Recipe>): List<RecipeCacheEntity>{
        return initial.map { mapToEntity(it) }
    }
}




















