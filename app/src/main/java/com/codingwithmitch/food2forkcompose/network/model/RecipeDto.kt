package com.codingwithmitch.food2forkcompose.network.model

import com.google.gson.annotations.SerializedName

data class RecipeDto(

        @SerializedName("pk")
        var pk: Int,

        @SerializedName("title")
        var title: String,

        @SerializedName("publisher")
        var publisher: String,

        @SerializedName("featured_image")
        var featuredImage: String,

        @SerializedName("rating")
        var rating: Int = 0,

        @SerializedName("source_url")
        var sourceUrl: String,

        @SerializedName("ingredients")
        var ingredients: List<String> = emptyList(),

        @SerializedName("long_date_added")
        var longDateAdded: Long,

        @SerializedName("long_date_updated")
        var longDateUpdated: Long,
)












