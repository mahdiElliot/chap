package com.example.chap.viewModel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.chap.internal.ApiService
import com.example.chap.internal.OnError
import com.example.chap.internal.SharedPref
import com.example.chap.model.Comment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class CommentsFragmentViewModel(sharedPref: SharedPref) : ViewModel() {
    private val apiService = ApiService.getInstance(sharedPref)
    val comments = MutableLiveData<ArrayList<Comment>>()
    val a = ArrayList<Comment>()

    fun getComments(onError: OnError) {
        CoroutineScope(IO).launch {
            val res = apiService.getComments(onError)
            if (res != null) {
                MainScope().launch {
                    comments.value = res
//                val c = Comment("1", "سینا سینایی", "سلام خوبی", "۹/۸/۹۹")
//                a.add(c)
//                a.add(c)
//                a.add(c)
//                comments.value = a
                }
            }
        }
    }

    fun sendComment(title: String, content: String, onError: OnError) {
        CoroutineScope(IO).launch {
            val res = apiService.addComment(title, content, onError)
//            a.add(Comment("2", name, content, "2/2/99"))
//            MainScope().launch {
//
//            }
        }
    }
}