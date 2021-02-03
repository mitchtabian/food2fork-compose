package com.codingwithmitch.food2forkcompose.presentation.util

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkRequest
import androidx.compose.runtime.mutableStateOf
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ConnectivityManager
@Inject
constructor(
  application: Application,
) {

  private val cm = application.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
  private val builder: NetworkRequest.Builder = NetworkRequest.Builder()

  // observe this in ui
  val isNetworkAvailable = mutableStateOf(false)

  init {
    cm.registerNetworkCallback(
      builder.build(),
      object : ConnectivityManager.NetworkCallback() {
        override fun onAvailable(network: Network) {
          isNetworkAvailable.value = true
        }

        override fun onLost(network: Network) {
          isNetworkAvailable.value = false
        }
      })
  }
}











