package com.example.chap.view.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.chap.R
import com.example.chap.adapter.AddressRecyclerViewAdapter
import com.example.chap.internal.DbAddressHelper
import com.example.chap.internal.OnError
import com.example.chap.internal.SharedPref
import com.example.chap.internal.ViewModelsFactory
import com.example.chap.model.Address
import com.example.chap.view.activity.MainActivity
import com.example.chap.viewModel.AddressListFragmentViewModel
import kotlinx.android.synthetic.main.fragment_address_list.*

class AddressListFragment : Fragment() {

    lateinit var viewModel: AddressListFragmentViewModel
    lateinit var recyclerViewAdapter: AddressRecyclerViewAdapter


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


        return inflater.inflate(R.layout.fragment_address_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        val db = DbAddressHelper(context)
//        db.deleteAll()
        val navController = Navigation.findNavController(view)
        recyclerViewAdapter =
            AddressRecyclerViewAdapter(object : AddressRecyclerViewAdapter.Interaction {
                override fun onItemSelected(position: Int, item: Address) {
                    val bundle = bundleOf(
                        "lng" to item.lng,
                        "lat" to item.lat,
                        "address" to item.address,
                        "phone" to item.phone
                    )
                    navController.navigate(R.id.action_addressListFragment_to_mapFragment, bundle)
                }
            }, viewModel)

        rv_address.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            adapter = this@AddressListFragment.recyclerViewAdapter
        }

        viewModel.getAddresses(object : OnError {
            override fun onError(errMsg: String?) {
                Toast.makeText(context, "آدرسی وجود ندارد", Toast.LENGTH_LONG).show()
            }
        })

        viewModel.addresses.observe(viewLifecycleOwner, Observer {
            recyclerViewAdapter.submitList(it)
        })

        btn_add_address.setOnClickListener {
//            val a = Address(33f,33f, "this is address")
//            viewModel.saveAddress(a, object: OnError{
//                override fun onError(errMsg: String?) {
//                    Toast.makeText(context, errMsg, Toast.LENGTH_LONG).show()
//                }
//            })
//            recyclerViewAdapter.notifyDataSetChanged()
            val bundle = bundleOf("phone" to "09159880549")
            navController.navigate(R.id.action_addressListFragment_to_mapFragment, bundle)
        }

    }

}