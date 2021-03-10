package com.codingwithmitch.food2forkcompose.network.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class KotlinxRecipeDto(

        @SerialName("pk")
        val pk: Int,

        @SerialName("title")
        val title: String,

        @SerialName("publisher")
        val publisher: String,

        @SerialName("featured_image")
        val featuredImage: String,

        @SerialName("rating")
        val rating: Int = 0,

        @SerialName("source_url")
        val sourceUrl: String? = null,

        @SerialName("ingredients")
        val ingredients: List<String> = emptyList(),

        @SerialName("long_date_added")
        val longDateAdded: Long,

        @SerialName("long_date_updated")
        val longDateUpdated: Long,
)












