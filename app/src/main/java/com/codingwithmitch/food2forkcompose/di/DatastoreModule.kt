package com.codingwithmitch.food2forkcompose.di

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.createDataStore
import com.codingwithmitch.food2forkcompose.presentation.BaseApplication
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object DatastoreModule {

  @Singleton
  @Provides
  fun provideDatastore(app: BaseApplication): DataStore<Preferences> {
    return app.createDataStore(
      name = "settings"
    )
  }
}










