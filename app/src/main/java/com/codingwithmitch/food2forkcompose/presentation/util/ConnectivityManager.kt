package com.codingwithmitch.food2forkcompose.presentation.util

import android.app.Application
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LifecycleOwner
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ConnectivityManager
@Inject
constructor(
  application: Application,
) {
  private val connectionLiveData = ConnectionLiveData(application)

  // observe this in ui
  val isNetworkAvailable = mutableStateOf(false)

  fun registerConnectionObserver(lifecycleOwner: LifecycleOwner){
    connectionLiveData.observe(lifecycleOwner, { isConnected ->
      isConnected?.let { isNetworkAvailable.value = it }
    })
  }

  fun unregisterConnectionObserver(lifecycleOwner: LifecycleOwner){
    connectionLiveData.removeObservers(lifecycleOwner)
  }
}