package com.example.chap.view.fragment

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast

import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.chap.R
import com.example.chap.adapter.TimeRecyclerViewAdapter
import com.example.chap.internal.OnError
import com.example.chap.internal.SharedPref
import com.example.chap.internal.ViewModelsFactory
import com.example.chap.model.DateTime
import com.example.chap.viewModel.CpPbFormFragmentViewModel
import com.google.android.material.appbar.AppBarLayout
import kotlinx.android.synthetic.main.fragment_cp_pb_form.*
import java.lang.Exception


class CpPbFormFragment : Fragment() {


    lateinit var appBarLayout: AppBarLayout
    lateinit var toolbar: Toolbar
    lateinit var btn_back: ImageView
    lateinit var iv_basket: ImageView

    lateinit var recyclerViewAdapter: TimeRecyclerViewAdapter

    lateinit var viewModel: CpPbFormFragmentViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        appBarLayout = requireActivity().findViewById(R.id.appBarLayout)
        toolbar = requireActivity().findViewById(R.id.toolbar)
        btn_back = requireActivity().findViewById(R.id.btn_back)
        iv_basket = requireActivity().findViewById(R.id.iv_basket)

        viewModel =
            ViewModelProviders.of(
                requireActivity(),
                ViewModelsFactory(SharedPref(requireContext()), context)
            ).get(CpPbFormFragmentViewModel::class.java)

        return inflater.inflate(R.layout.fragment_cp_pb_form, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val form = requireArguments().getInt("form")

        frame_time.visibility = if (form == 1) View.GONE else View.VISIBLE

        switch_type.setOnClickListener {
            if (switch_type.isChecked) {
                toolbar.setBackgroundColor(resources.getColor(R.color.colorPrimaryDark))
                appBarLayout.setBackgroundColor(resources.getColor(R.color.colorPrimaryDark))
                btn_back.setImageResource(R.drawable.ic_back)
                iv_basket.setImageResource(R.drawable.ic_basket)
                btn_upload.setBackgroundColor(resources.getColor(R.color.colorPrimaryDark))
                switch_type.setTrackResource(R.drawable.switch_background)
            } else {
                toolbar.setBackgroundColor(resources.getColor(R.color.darkGrey))
                appBarLayout.setBackgroundColor(resources.getColor(R.color.darkGrey))
                btn_back.setImageResource(R.drawable.ic_back_white)
                iv_basket.setImageResource(R.drawable.ic_basket_white)
                btn_upload.setBackgroundColor(resources.getColor(R.color.darkGrey))
                switch_type.setTrackResource(R.drawable.switch_background2)
            }
        }


        recyclerViewAdapter =
            TimeRecyclerViewAdapter(object : TimeRecyclerViewAdapter.Interaction {
                override fun onItemSelected(position: Int, item: DateTime) {

                }
            })

        rv_time.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            adapter = this@CpPbFormFragment.recyclerViewAdapter
        }

        viewModel.getTimes(object : OnError {
            override fun onError(errMsg: String?) {

            }
        })

        viewModel.times.observe(viewLifecycleOwner, Observer {
            recyclerViewAdapter.submitList(it)
        })

        btn_upload.setOnClickListener {
            val intent = Intent().setType("*/*").setAction(Intent.ACTION_GET_CONTENT)

            startActivityForResult(Intent.createChooser(intent, "select a file"), 111)
        }

        btn_continue.setOnClickListener {

        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 111 && resultCode == RESULT_OK) {
            val selectedFile = data?.data
            tv_upload.text = selectedFile.toString()
        }
    }


}