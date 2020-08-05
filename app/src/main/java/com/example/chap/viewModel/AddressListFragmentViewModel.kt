package com.example.chap.viewModel

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.chap.internal.DbAddressHelper
import com.example.chap.internal.OnError
import com.example.chap.internal.SharedPref
import com.example.chap.model.Address
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class AddressListFragmentViewModel(sharedPref: SharedPref, context: Context) : ViewModel() {

    val addresses = MutableLiveData<ArrayList<Address>>()
    private var adrs = ArrayList<Address>()
    val db = DbAddressHelper(context)

    fun getAddresses(onError: OnError) {
        CoroutineScope(IO).launch {
            adrs = db.getAll()

            if (adrs.isNotEmpty()) {
                MainScope().launch {
                    addresses.value = adrs
                }
            }
        }
    }

    fun saveAddress(address: Address, onError: OnError) {
        CoroutineScope(IO).launch {
            adrs.add(address)
            db.save(address)
            MainScope().launch {
                addresses.value = adrs
            }
        }
    }

}