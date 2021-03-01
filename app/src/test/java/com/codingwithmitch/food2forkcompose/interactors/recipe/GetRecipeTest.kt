package com.codingwithmitch.food2forkcompose.interactors.recipe

import com.codingwithmitch.food2fork.network.RecipeService
import com.codingwithmitch.food2forkcompose.cache.AppDatabaseFake
import com.codingwithmitch.food2forkcompose.cache.RecipeDaoFake
import com.codingwithmitch.food2forkcompose.cache.model.RecipeEntityMapper
import com.codingwithmitch.food2forkcompose.domain.model.Recipe
import com.codingwithmitch.food2forkcompose.interactors.recipe_list.SearchRecipes
import com.codingwithmitch.food2forkcompose.network.data.MockWebServerResponses
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

class GetRecipeTest {

  private val appDatabase = AppDatabaseFake()
  private lateinit var mockWebServer: MockWebServer
  private lateinit var baseUrl: HttpUrl
  private val DUMMY_TOKEN = "gg335v5453453" // can be anything
  private val DUMMY_QUERY = "This doesn't matter" // can be anything

  // system in test
  private lateinit var getRecipe: GetRecipe
  private val RECIPE_ID = 1551

  // Dependencies
  private lateinit var searchRecipes: SearchRecipes
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

    searchRecipes = SearchRecipes(
      recipeDao = recipeDao,
      recipeService = recipeService,
      entityMapper = entityMapper,
      dtoMapper = dtoMapper
    )

    // instantiate the system in test
    getRecipe = GetRecipe(
      recipeDao = recipeDao,
      entityMapper = entityMapper,
      recipeService = recipeService,
      recipeDtoMapper = dtoMapper,
    )
  }

  /**
   * 1. Get some recipes from the network and insert into cache
   * 2. Try to retrieve recipes by their specific recipe id
   */
  @Test
  fun getRecipesFromNetwork_getRecipeById(): Unit = runBlocking {
    // condition the response
    mockWebServer.enqueue(
      MockResponse()
        .setResponseCode(HttpURLConnection.HTTP_OK)
        .setBody(MockWebServerResponses.recipeListResponse)
    )

    // confirm the cache is empty to start
    assert(recipeDao.getAllRecipes(1, 30).isEmpty())

    // get recipes from network and insert into cache
    val searchResult = searchRecipes.execute(DUMMY_TOKEN, 1, DUMMY_QUERY, true).toList()

    // confirm the cache is no longer empty
    assert(recipeDao.getAllRecipes(1, 30).isNotEmpty())

    // run use case
    val recipeAsFlow = getRecipe.execute(RECIPE_ID, DUMMY_TOKEN, true).toList()

    // first emission should be `loading`
    assert(recipeAsFlow[0].loading)

    // second emission should be the recipe
    val recipe = recipeAsFlow[1].data
    assert(recipe?.id == RECIPE_ID)

    // confirm it is actually a Recipe object
    assert(recipe is Recipe)

    // 'loading' should be false now
    assert(!recipeAsFlow[1].loading)
  }


  /**
   * 1. Try to get a recipe that does not exist in the cache
   * Result should be:
   * 1. Recipe is retrieved from network and inserted into cache
   * 2. Recipe is returned as flow from cache
   */
  @Test
  fun attemptGetNullRecipeFromCache_getRecipeById(): Unit = runBlocking {
    // condition the response
    mockWebServer.enqueue(
      MockResponse()
        .setResponseCode(HttpURLConnection.HTTP_OK)
        .setBody(MockWebServerResponses.recipeWithId1551)
    )

    // confirm the cache is empty to start
    assert(recipeDao.getAllRecipes(1, 30).isEmpty())

    // run use case
    val recipeAsFlow = getRecipe.execute(RECIPE_ID, DUMMY_TOKEN, true).toList()

    // first emission should be `loading`
    assert(recipeAsFlow[0].loading)

    // second emission should be the recipe
    val recipe = recipeAsFlow[1].data
    assert(recipe?.id == RECIPE_ID)

    // confirm the recipe is in the cache now
    assert(recipeDao.getRecipeById(RECIPE_ID)?.id == RECIPE_ID)

    // confirm it is actually a Recipe object
    assert(recipe is Recipe)

    // 'loading' should be false now
    assert(!recipeAsFlow[1].loading)
  }

  @AfterEach
  fun tearDown() {
    mockWebServer.shutdown()
  }
}