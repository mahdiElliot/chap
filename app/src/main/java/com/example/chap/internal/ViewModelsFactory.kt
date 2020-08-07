package com.example.chap.internal

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.chap.viewModel.*

@Suppress("UNCHECKED_CAST")
class ViewModelsFactory(private val sharedPref: SharedPref) :
    ViewModelProvider.Factory {
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
            modelClass.isAssignableFrom(AddressListFragmentViewModel::class.java) -> AddressListFragmentViewModel(
                sharedPref
            ) as T
            modelClass.isAssignableFrom(CpPbFormFragmentViewModel::class.java) -> CpPbFormFragmentViewModel(
                sharedPref
            ) as T
            modelClass.isAssignableFrom(GiftPromoFragmentViewModel::class.java) -> GiftPromoFragmentViewModel(
                sharedPref
            ) as T
            modelClass.isAssignableFrom(MapFragmentViewModel::class.java) -> MapFragmentViewModel(
                sharedPref
            ) as T
            else -> throw IllegalArgumentException("ViewModel Not Found")
        }
    }
}