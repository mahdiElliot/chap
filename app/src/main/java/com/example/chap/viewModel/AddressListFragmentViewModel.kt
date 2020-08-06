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
            if (adrs.isEmpty())
                adrs = db.getAll()
            Log.i("fuck", adrs.toString())
            MainScope().launch {
                addresses.value = adrs
            }

        }
    }

    fun saveAddress(address: Address, onError: OnError) {
        CoroutineScope(IO).launch {
            if (db.save(address))
                adrs.add(address)
            MainScope().launch {
                addresses.value = adrs
            }
        }
    }

    fun deleteAddress(address: Address, onError: OnError): Boolean {
        val d = db.deleteAddress(address.lat, address.lng, address.phone)
//        CoroutineScope(IO).launch {
        if (!d)
            onError.onError("not deleted")
        else {
            adrs.remove(address)
            addresses.value = adrs
        }
//            MainScope().launch {

        return d

//            }
//        }

        return d
    }

}