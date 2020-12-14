package com.codingwithmitch.food2forkcompose.cache.model

import com.codingwithmitch.food2forkcompose.cache.exceptions.RecipeCacheException
import com.codingwithmitch.food2forkcompose.domain.model.Recipe
import com.codingwithmitch.food2forkcompose.util.DomainMapper
import java.lang.StringBuilder

class RecipeEntityMapper : DomainMapper<RecipeEntity, Recipe>{

    override fun mapToDomainModel(model: RecipeEntity): Recipe {
        return Recipe(
            id = model.id,
            title = model.title,
            featuredImage = model.featuredImage,
            rating = model.rating,
            publisher = model.publisher,
            sourceUrl = model.sourceUrl,
            description = model.description,
            cookingInstructions = model.cookingInstructions,
            ingredients = convertIngredientsToList(model.ingredients),
            dateAdded = model.dateAdded,
            dateUpdated = model.dateUpdated,
        )
    }


    override fun mapFromDomainModel(domainModel: Recipe): RecipeEntity {
        return RecipeEntity(
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

    fun fromEntityList(initial: List<RecipeEntity>): List<Recipe>{
        return initial.map { mapToDomainModel(it) }
    }

    fun toEntityList(initial: List<Recipe>): List<RecipeEntity>{
        return initial.map { mapFromDomainModel(it) }
    }
}




















