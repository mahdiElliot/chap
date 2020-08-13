package com.example.chap.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.chap.internal.ApiService
import com.example.chap.internal.OnError
import com.example.chap.internal.SharedPref
import com.example.chap.model.User
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AuthActivityViewModel(sharedPref: SharedPref) : ViewModel() {

    private val apiService = ApiService.getInstance(sharedPref)
    val isUserAuthenticated = MutableLiveData<Boolean>()
    val username = MutableLiveData<String>()
    val username2 = MutableLiveData<String>()
    val first_last_name = MutableLiveData<String>()
    val email = MutableLiveData<String>()
    val mobile = MutableLiveData<String>()
    val password = MutableLiveData<String>()
    val password2 = MutableLiveData<String>()

    fun register(user: User, onError: OnError) {
        CoroutineScope(IO).launch {
            val registerd = apiService.register(user, onError)
            withContext(Dispatchers.Main) {
                isUserAuthenticated.value = registerd
            }
        }
    }

    fun login(username: String, pass: String, onError: OnError) {
        CoroutineScope(IO).launch {
            val loggedIn = apiService.login(username, pass, onError)
            withContext(Dispatchers.Main) {
                isUserAuthenticated.value = loggedIn
            }
        }
    }
}