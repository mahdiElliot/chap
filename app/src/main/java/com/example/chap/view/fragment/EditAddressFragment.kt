package com.example.chap.view.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProviders
import com.example.chap.R
import com.example.chap.internal.DbAddressHelper
import com.example.chap.internal.OnError
import com.example.chap.internal.SharedPref
import com.example.chap.internal.ViewModelsFactory
import com.example.chap.model.Address
import com.example.chap.viewModel.AddressListFragmentViewModel
import kotlinx.android.synthetic.main.fragment_edit_address.*


class EditAddressFragment : Fragment() {

    lateinit var viewModel: AddressListFragmentViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        viewModel =
            ViewModelProviders.of(
                requireActivity(),
                ViewModelsFactory(SharedPref(requireContext()), context)
            ).get(AddressListFragmentViewModel::class.java)

        return inflater.inflate(R.layout.fragment_edit_address, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val address = requireArguments().getString("address")
        val lat = requireArguments().getFloat("lat")
        val lng = requireArguments().getFloat("lng")
        val phone = requireArguments().getString("phone")

        et_address.setText(address)
        et_number.setText(phone)

        btn_save.setOnClickListener {
            val txt = et_address.text.toString()
            val number = et_number.text.toString()
            if (txt.isEmpty())
                Toast.makeText(context, "فیلد آدرس نباید خالی باشد", Toast.LENGTH_LONG).show()
            else if (number.isEmpty() || number.length != 11)
                Toast.makeText(context, "شماره تلفن باید ۱۱ رقم باشد", Toast.LENGTH_LONG).show()
            else {
                val adrs = Address(lat, lng, txt, number)
                viewModel.saveAddress(adrs, object : OnError {
                    override fun onError(errMsg: String?) {
                        Log.i("not saved", "not saved")
                    }
                })
                requireActivity().onBackPressed()
                requireActivity().onBackPressed()
            }
        }
    }
}