package com.example.chap.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.chap.internal.OnError
import com.example.chap.internal.SharedPref
import com.example.chap.model.User
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class EditFragmentViewModel(sharedPref: SharedPref) : ViewModel() {

    var user = MutableLiveData<User>()

    fun getUser(onError: OnError) {
        CoroutineScope(Dispatchers.IO).launch {
//            val res = apiService.getProfile(onError)
            MainScope().launch {
//                user.value = res
            }
        }
    }

    fun editPass(pass: String, onError: OnError): Boolean {
        return true
        //  return apiService.editPass(pass, onError)
    }

    fun editProfile(username: String, email: String, number: String, onError: OnError): Boolean {
        return true
        // return apiService.editProfile(username, email, onError)
    }
}