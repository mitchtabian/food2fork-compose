package com.codingwithmitch.food2forkcompose.cache.model

import com.codingwithmitch.food2forkcompose.domain.model.Recipe
import com.codingwithmitch.food2forkcompose.domain.util.DomainMapper
import com.codingwithmitch.food2forkcompose.util.DateUtils

class RecipeEntityMapper : DomainMapper<RecipeEntity, Recipe> {

    override fun mapToDomainModel(model: RecipeEntity): Recipe {
        return Recipe(
            id = model.id,
            title = model.title,
            featuredImage = model.featuredImage,
            rating = model.rating,
            publisher = model.publisher,
            sourceUrl = model.sourceUrl,
            ingredients = convertIngredientsToList(model.ingredients),
            dateAdded = DateUtils.longToDate(model.dateAdded),
            dateUpdated = DateUtils.longToDate(model.dateUpdated),
        )
    }


    override fun mapFromDomainModel(domainModel: Recipe): RecipeEntity {
        return RecipeEntity(
            id = domainModel.id,
            title = domainModel.title,
            featuredImage = domainModel.featuredImage,
            rating = domainModel.rating,
            publisher = domainModel.publisher,
            sourceUrl = domainModel.sourceUrl,
            ingredients = convertIngredientListToString(domainModel.ingredients),
            dateAdded = DateUtils.dateToLong(domainModel.dateAdded),
            dateUpdated = DateUtils.dateToLong(domainModel.dateUpdated),
            dateCached = DateUtils.dateToLong(DateUtils.createTimestamp())
        )
    }

    /**
     * "Carrot, potato, Chicken, ..."
     */
    private fun convertIngredientListToString(ingredients: List<String>): String {
        val ingredientsString = StringBuilder()
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