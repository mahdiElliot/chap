package com.example.chap.internal

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.chap.viewModel.AuthActivityViewModel
import com.example.chap.viewModel.CommentsFragmentViewModel
import com.example.chap.viewModel.EditFragmentViewModel
import com.example.chap.viewModel.MainActivityViewModel

@Suppress("UNCHECKED_CAST")
class ViewModelsFactory(private val sharedPref: SharedPref) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(CommentsFragmentViewModel::class.java) -> CommentsFragmentViewModel(
                sharedPref
            ) as T
            modelClass.isAssignableFrom(EditFragmentViewModel::class.java) -> EditFragmentViewModel(
                sharedPref
            ) as T
            modelClass.isAssignableFrom(MainActivityViewModel::class.java) -> MainActivityViewModel(
                sharedPref
            ) as T
            modelClass.isAssignableFrom(AuthActivityViewModel::class.java) -> AuthActivityViewModel(
                sharedPref
            ) as T
//            modelClass.isAssignableFrom(FavouriteActivityViewModel::class.java) -> FavouriteActivityViewModel(
//                sharedPref
//            ) as T
            else -> throw IllegalArgumentException("ViewModel Not Found")
        }
    }
}