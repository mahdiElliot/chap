package com.example.chap.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.chap.internal.OnError
import com.example.chap.internal.SharedPref
import com.example.chap.model.DateTime
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import java.io.File

class CpPbFormFragmentViewModel(sharedPref: SharedPref) : ViewModel() {

    val times = MutableLiveData<ArrayList<DateTime>>()
    val positionChecked = MutableLiveData<Int>().apply { value = -1 }
    val switch = MutableLiveData<Boolean>().apply { value = true }
    val description = MutableLiveData<String>()
    val file1 = MutableLiveData<File>()
    val file2 = MutableLiveData<File>()

    fun getTimes(onError: OnError) {
        CoroutineScope(IO).launch {
//            val res = apiService.getTimes(onError)
//            if (res != null) {
            MainScope().launch {
//                    times.value = res
                times.value = arrayListOf(
                    DateTime("18:30", "12/9/99"),
                    DateTime("18:30", "12/9/99"),
                    DateTime("18:30", "12/9/99"),
                    DateTime("18:30", "12/9/99"),
                    DateTime("18:30", "12/9/99"),
                    DateTime("18:30", "12/9/99"),
                    DateTime("18:30", "12/9/99")
                )
            }
//            }
        }
    }

}