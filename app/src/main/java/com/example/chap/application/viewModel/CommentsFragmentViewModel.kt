package com.example.chap.application.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.chap.application.internal.OnError
import com.example.chap.application.internal.SharedPref
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class CommentsFragmentViewModel(sharedPref: SharedPref) : ViewModel() {
    val comments = MutableLiveData<ArrayList<String>>()

    fun getCommnets(onError: OnError) {
        CoroutineScope(Dispatchers.IO).launch {
//            val res = apiService.getComments(onError)
//            if (res != null) {
            MainScope().launch {
//                    comments.value = res
//                }
            }
        }
    }
}