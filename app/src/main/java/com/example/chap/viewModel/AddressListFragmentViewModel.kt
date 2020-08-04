package com.example.chap.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.chap.internal.SharedPref
import com.example.chap.model.Address

class AddressListFragmentViewModel(sharedPref: SharedPref) : ViewModel() {

    val addresses = MutableLiveData<ArrayList<Address>>()

}