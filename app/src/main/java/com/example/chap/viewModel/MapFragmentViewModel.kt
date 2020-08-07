package com.example.chap.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.chap.internal.SharedPref

class MapFragmentViewModel(sharedPref: SharedPref) : ViewModel() {
    val latitude = MutableLiveData<Float>().apply { value = -86f }
    val longitude = MutableLiveData<Float>().apply { value = -186f }
}