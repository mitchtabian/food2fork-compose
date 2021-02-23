package com.codingwithmitch.food2forkcompose.interactors.recipe_list

import com.codingwithmitch.food2fork.network.RecipeService
import com.codingwithmitch.food2forkcompose.cache.AppDatabaseFake
import com.codingwithmitch.food2forkcompose.cache.RecipeDaoFake
import com.codingwithmitch.food2forkcompose.cache.model.RecipeEntityMapper
import com.codingwithmitch.food2forkcompose.domain.model.Recipe
import com.codingwithmitch.food2forkcompose.network.data.MockWebServerResponses.recipeListResponse
import com.codingwithmitch.food2forkcompose.network.model.RecipeDtoMapper
import com.google.gson.GsonBuilder
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import okhttp3.HttpUrl
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.net.HttpURLConnection

class SearchRecipesTest {

  private val appDatabase = AppDatabaseFake()
  private lateinit var mockWebServer: MockWebServer
  private lateinit var baseUrl: HttpUrl
  private val DUMMY_TOKEN = "gg335v5453453" // can be anything
  private val DUMMY_QUERY = "This doesn't matter" // can be anything

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

  /**
   * 1. Are the recipes retrieved from the network?
   * 2. Are the recipes inserted into the cache?
   * 3. Are the recipes then emitted as a flow from the cache?
   */
  @Test
  fun getRecipesFromNetwork_emitRecipesFromCache(): Unit = runBlocking {

    // condition the response
    mockWebServer.enqueue(
      MockResponse()
        .setResponseCode(HttpURLConnection.HTTP_OK)
        .setBody(recipeListResponse)
    )

    // confirm the cache is empty to start
    assert(recipeDao.getAllRecipes(1, 30).isEmpty())

    val flowItems = searchRecipes.execute(DUMMY_TOKEN, 1, DUMMY_QUERY, true).toList()

    // confirm the cache is no longer empty
    assert(recipeDao.getAllRecipes(1, 30).isNotEmpty())

    // first emission should be `loading`
    assert(flowItems[0].loading)

    // Second emission should be the list of recipes
    val recipes = flowItems[1].data
    assert(recipes?.size?: 0 > 0)

    // confirm they are actually Recipe objects
    assert(recipes?.get(index = 0) is Recipe)

    assert(!flowItems[1].loading) // loading should be false now
  }


  /**
   * Simulate a bad request
   */
  @Test
  fun getRecipesFromNetwork_emitHttpError(): Unit = runBlocking {

    // condition the response
    mockWebServer.enqueue(
      MockResponse()
        .setResponseCode(HttpURLConnection.HTTP_BAD_REQUEST)
        .setBody("{}")
    )

    val flowItems = searchRecipes.execute(DUMMY_TOKEN, 1, DUMMY_QUERY, true).toList()

    // first emission should be `loading`
    assert(flowItems[0].loading)

    // Second emission should be the exception
    val error = flowItems[1].error
    assert(error != null)

    assert(!flowItems[1].loading) // loading should be false now
  }

  @AfterEach
  fun tearDown() {
    mockWebServer.shutdown()
  }
}










