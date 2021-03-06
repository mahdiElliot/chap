package com.example.chap.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.chap.internal.ApiService
import com.example.chap.internal.OnError
import com.example.chap.internal.SharedPref
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class MainActivityViewModel(sharedPref: SharedPref) : ViewModel() {
    private val apiService = ApiService.getInstance(sharedPref)
    var loggedOut = MutableLiveData<Boolean>().apply { value = false }

    fun logout(
        onError: OnError
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            val res = apiService.logout(onError)
            MainScope().launch {
                loggedOut.value = res
            }
        }

    }
}