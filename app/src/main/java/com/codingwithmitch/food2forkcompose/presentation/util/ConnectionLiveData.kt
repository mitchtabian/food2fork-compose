package com.codingwithmitch.food2forkcompose.presentation.util

import android.content.Context
import android.content.Context.CONNECTIVITY_SERVICE
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkCapabilities.NET_CAPABILITY_INTERNET
import android.net.NetworkCapabilities.NET_CAPABILITY_VALIDATED
import android.os.Build
import androidx.lifecycle.LiveData

/**
 * Author:
 * https://github.com/AlexSheva-mason/Rick-Morty-Database/blob/master/app/src/main/java/com/shevaalex/android/rickmortydatabase/utils/networking/ConnectionLiveData.kt
 */
class ConnectionLiveData(private val context: Context) : LiveData<Boolean>() {

  private lateinit var networkCallback: ConnectivityManager.NetworkCallback

  override fun onActive() {
    val cm = context.getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
    networkCallback = createNetworkCallback()
    cm.registerDefaultNetworkCallback(networkCallback)
  }

  override fun onInactive() {
    val cm = context.getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
    cm.unregisterNetworkCallback(networkCallback)
  }

  private fun createNetworkCallback() = object : ConnectivityManager.NetworkCallback() {

    override fun onAvailable(network: Network) {
      /* only starting from version Build.VERSION_CODES.O onAvailable() will always immediately
       be followed by a call to onCapabilitiesChanged.
       On versions below Build.VERSION_CODES.O when app is started with internet connection
       nothing apart from onAvailable() is being called, thus we need to pass postValue(true)
       here (although in some cases it could be false positive)
       */
      if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
        postValue(true)
      }
    }

    override fun onCapabilitiesChanged(
      network: Network,
      networkCapabilities: NetworkCapabilities
    ) {
      val isInternet = networkCapabilities.hasCapability(NET_CAPABILITY_INTERNET)
      val isValidated = networkCapabilities.hasCapability(NET_CAPABILITY_VALIDATED)
      postValue(isInternet && isValidated)
    }

    override fun onLost(network: Network) {
      postValue(false)
    }

  }

}