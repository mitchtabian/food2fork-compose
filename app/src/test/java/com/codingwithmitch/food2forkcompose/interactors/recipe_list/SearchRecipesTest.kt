package com.codingwithmitch.food2forkcompose.interactors.recipe_list

import com.codingwithmitch.food2fork.network.RecipeService
import com.codingwithmitch.food2forkcompose.cache.AppDatabaseFake
import com.codingwithmitch.food2forkcompose.cache.RecipeDaoFake
import com.codingwithmitch.food2forkcompose.cache.model.RecipeEntityMapper
import com.codingwithmitch.food2forkcompose.network.data.MockWebServerResponses.recipeListResponse
import com.codingwithmitch.food2forkcompose.network.model.RecipeDtoMapper
import com.google.gson.GsonBuilder
import okhttp3.HttpUrl
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.net.HttpURLConnection

class SearchRecipesTest {

  private val appDatabase = AppDatabaseFake()
  private lateinit var mockWebServer: MockWebServer
  private lateinit var baseUrl: HttpUrl

  // system in test
  private lateinit var searchRecipes: SearchRecipes

  // Dependencies
  private lateinit var recipeService: RecipeService
  private lateinit var recipeDao: RecipeDaoFake
  private val dtoMapper = RecipeDtoMapper()
  private val entityMapper = RecipeEntityMapper()

  @BeforeEach
  fun setup() {
    mockWebServer = MockWebServer()
    mockWebServer.start()
    baseUrl = mockWebServer.url("/api/recipe/")
    recipeService = Retrofit.Builder()
      .baseUrl(baseUrl)
      .addConverterFactory(GsonConverterFactory.create(GsonBuilder().create()))
      .build()
      .create(RecipeService::class.java)

    recipeDao = RecipeDaoFake(appDatabaseFake = appDatabase)

    // instantiate the system in test
    searchRecipes = SearchRecipes(
      recipeDao = recipeDao,
      recipeService = recipeService,
      entityMapper = entityMapper,
      dtoMapper = dtoMapper
    )
  }

  @Test
  fun mockWebServerSetup(){
    // condition the response
    mockWebServer.enqueue(
      MockResponse()
        .setResponseCode(HttpURLConnection.HTTP_OK)
        .setBody(recipeListResponse)
    )
  }
}










