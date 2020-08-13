package com.example.chap.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.chap.internal.ApiService
import com.example.chap.internal.OnError
import com.example.chap.internal.SharedPref
import com.example.chap.model.User
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class EditFragmentViewModel(sharedPref: SharedPref) : ViewModel() {
    private val apiService = ApiService.getInstance(sharedPref)
    var user = MutableLiveData<User>()

    fun getUser(onError: OnError) {
        CoroutineScope(Dispatchers.IO).launch {
            val res = apiService.getProfile(onError)
            if (res != null) {
                MainScope().launch {
                    user.value = res
                }
            }
        }
    }

    suspend fun editPass(pass: String, onError: OnError): Boolean {
        return apiService.editPass(pass, onError)
    }

    suspend fun editProfile(user: User, onError: OnError): Boolean {
        return apiService.editProfile(user, onError)
    }
}