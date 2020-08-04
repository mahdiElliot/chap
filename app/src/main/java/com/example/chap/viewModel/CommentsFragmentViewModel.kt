package com.example.chap.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.chap.internal.OnError
import com.example.chap.internal.SharedPref
import com.example.chap.model.Comment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class CommentsFragmentViewModel(sharedPref: SharedPref) : ViewModel() {
    val comments = MutableLiveData<List<Comment>>()

    fun getCommnets(onError: OnError) {
        CoroutineScope(Dispatchers.IO).launch {
//            val res = apiService.getComments(onError)
//            if (res != null) {
            MainScope().launch {
//                    comments.value = res
                comments.value = listOf(
                    Comment("1", "سینا سینایی", "سلام خوبی", "۹/۸/۹۹"),
                    Comment("1", "سینا سینایی", "سلام خوبی", "۹/۸/۹۹"),
                    Comment("1", "سینا سینایی", "سلام خوبی", "۹/۸/۹۹")
                )
//                }
            }
        }
    }
}