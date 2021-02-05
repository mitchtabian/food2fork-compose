package com.codingwithmitch.food2forkcompose.network.response

import com.codingwithmitch.food2forkcompose.network.model.RecipeDto
import com.google.gson.annotations.SerializedName

data class RecipeSearchResponse(

        @SerializedName("count")
        var count: Int,

        @SerializedName("results")
        var recipes: List<RecipeDto>,
)