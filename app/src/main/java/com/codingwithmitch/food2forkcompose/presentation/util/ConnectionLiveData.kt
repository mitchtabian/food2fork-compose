package com.codingwithmitch.food2forkcompose.presentation.util

import android.content.Context
import android.content.Context.CONNECTIVITY_SERVICE
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities.NET_CAPABILITY_INTERNET
import android.net.NetworkRequest
import android.util.Log
import androidx.lifecycle.LiveData

/**
 * Saved all available networks with an internet connection to a set (@validNetworks).
 * As long as the size of the set > 0, this LiveData emits true.
 *
 * Inspired by:
 * https://github.com/AlexSheva-mason/Rick-Morty-Database/blob/master/app/src/main/java/com/shevaalex/android/rickmortydatabase/utils/networking/ConnectionLiveData.kt
 */
class ConnectionLiveData(context: Context) : LiveData<Boolean>() {

  private val TAG = "C-Manager"
  private lateinit var networkCallback: ConnectivityManager.NetworkCallback
  private val cm = context.getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
  private val validNetworks: MutableSet<Network> = HashSet()

  private fun checkValidNetworks(){
    postValue(validNetworks.size > 0)
  }

  override fun onActive() {
    networkCallback = createNetworkCallback()
    val networkRequest = NetworkRequest.Builder()
      .addCapability(NET_CAPABILITY_INTERNET)
      .build()
    cm.registerNetworkCallback(networkRequest, networkCallback)
  }

  override fun onInactive() {
    cm.unregisterNetworkCallback(networkCallback)
  }

  private fun createNetworkCallback() = object : ConnectivityManager.NetworkCallback() {

    /*
      Called when a network is detected. If that network has internet, save it in the Set.
     */
    override fun onAvailable(network: Network) {
      Log.d(TAG, "onAvailable: ${network}")
      val networkCapabilities = cm.getNetworkCapabilities(network)
      val isInternet = networkCapabilities?.hasCapability(NET_CAPABILITY_INTERNET)
      Log.d(TAG, "onAvailable: ${network}, $isInternet")
      if(isInternet ==  true){
        validNetworks.add(network)
      }
      checkValidNetworks()
    }

    /*
      If the callback was registered with registerNetworkCallback() it will be called for each network which no longer satisfies the criteria of the callback.
      Source: https://developer.android.com/reference/android/net/ConnectivityManager.NetworkCallback#onLost(android.net.Network)
     */
    override fun onLost(network: Network) {
      Log.d(TAG, "onLost: ${network}")
      validNetworks.remove(network)
      checkValidNetworks()
    }

  }

}