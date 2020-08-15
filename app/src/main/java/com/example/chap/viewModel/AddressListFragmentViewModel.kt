package com.example.chap.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.chap.internal.ApiService
import com.example.chap.internal.OnError
import com.example.chap.internal.SharedPref
import com.example.chap.model.Address
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class AddressListFragmentViewModel(sharedPref: SharedPref) : ViewModel() {
    private val apiService = ApiService.getInstance(sharedPref)
    val addresses = MutableLiveData<ArrayList<Address>>()
    private var adrs = ArrayList<Address>()
    val number = MutableLiveData<String>()

    fun getAddresses(onError: OnError) {
        CoroutineScope(IO).launch {
            val res = apiService.getAddresses(onError)
            if (res != null)
                MainScope().launch {
                    addresses.value = res
                }

        }
    }

    suspend fun saveAddress(address: Address, onError: OnError): Boolean {
////        CoroutineScope(IO).launch {
//        if (db.save(address))
//            adrs.add(address)
////            MainScope().launch {
//        addresses.value = adrs
////            }
////        }

        return apiService.addAddress(address, onError)
//            if (res) {
//                MainScope().launch {
//                    addresses.value.add(address)
//                }
//            }

    }

    suspend fun deleteAddress(address: Address, onError: OnError): Boolean {
//        val d = db.deleteAddress(address.lat, address.lng, address.phone)
////        CoroutineScope(IO).launch {
//        if (!d)
//            onError.onError("not deleted")
//        else {
//            adrs.remove(address)
//            addresses.value = adrs
//        }
////            MainScope().launch {
//
//        return d

//            }
//        }

        return apiService.deleteAddress(address, onError)
    }

    suspend fun editAddress(address: Address, onError: OnError): Boolean {
        return apiService.editAddress(address, onError)
    }

    fun getNumber(onError: OnError) {
        CoroutineScope(Dispatchers.IO).launch {
            val res = apiService.getProfile(onError)
            if (res != null) {
                MainScope().launch {
                    number.value = res.number
                }
            }
        }
    }
}