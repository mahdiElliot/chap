package com.example.chap.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.chap.internal.ApiService
import com.example.chap.internal.OnError
import com.example.chap.internal.SharedPref
import com.example.chap.model.Gift
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import java.io.File

class GiftPromoFragmentViewModel(sharedPref: SharedPref) : ViewModel() {
    private val apiService = ApiService.getInstance(sharedPref)
    val gifts = MutableLiveData<ArrayList<Gift>>()
    val promos = MutableLiveData<ArrayList<Gift>>()
    val file1 = MutableLiveData<File>()
    val file2 = MutableLiveData<File>()
    val description = MutableLiveData<String>()
    val positionChecked = MutableLiveData<Int>().apply { value = -1 }

    fun getGifts(onError: OnError) {
        CoroutineScope(Dispatchers.IO).launch {
            val res = apiService.getGifts(onError)
            if (res != null) {
                MainScope().launch {
                    gifts.value = res
                }
            }
        }
    }

    fun getPromos(onError: OnError) {
        CoroutineScope(Dispatchers.IO).launch {
            val res = apiService.getPromos(onError)
            if (res != null) {
                MainScope().launch {
                    promos.value = res

                }
            }
        }
    }
}