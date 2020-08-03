package com.example.chap.application.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.chap.application.internal.OnError
import com.example.chap.application.internal.SharedPref
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class MainActivityViewModel(sharedPref: SharedPref) : ViewModel() {

    var currentFragment = MutableLiveData<Int>().apply { value = 0 }
    var loggedOut = MutableLiveData<Boolean>().apply { value = false }

    fun logout(
        onError: OnError
    ) {
        CoroutineScope(Dispatchers.IO).launch {
//            val res = apiService.logout(onError)
            MainScope().launch {
//                loggedOut.value=res
                loggedOut.value = true
            }
        }

    }
}