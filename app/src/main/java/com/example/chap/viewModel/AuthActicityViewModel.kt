package com.example.chap.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.chap.internal.OnError
import com.example.chap.internal.SharedPref
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AuthActivityViewModel(sharedPref: SharedPref) : ViewModel() {

    val isUserAuthenticated = MutableLiveData<Boolean>()

    fun register(mobile: String, onError: OnError) {
        CoroutineScope(IO).launch {
            withContext(Dispatchers.Main) {
                isUserAuthenticated.value = true
            }
        }
    }

    fun login(username: String, pass: String, onError: OnError) {
        CoroutineScope(IO).launch {
            withContext(Dispatchers.Main) {
                isUserAuthenticated.value = true
            }
        }
    }
}