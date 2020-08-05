package com.example.chap.view.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView

import androidx.appcompat.widget.Toolbar
import com.example.chap.R
import com.google.android.material.appbar.AppBarLayout
import kotlinx.android.synthetic.main.fragment_cp_pb_form.*


class CpPbFormFragment : Fragment() {


    lateinit var appBarLayout: AppBarLayout
    lateinit var toolbar: Toolbar
    lateinit var btn_back: ImageView
    lateinit var iv_basket: ImageView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        appBarLayout = requireActivity().findViewById(R.id.appBarLayout)
        toolbar = requireActivity().findViewById(R.id.toolbar)
        btn_back = requireActivity().findViewById(R.id.btn_back)
        iv_basket = requireActivity().findViewById(R.id.iv_basket)

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
            } else {
                toolbar.setBackgroundColor(resources.getColor(R.color.darkGrey))
                appBarLayout.setBackgroundColor(resources.getColor(R.color.darkGrey))
                btn_back.setImageResource(R.drawable.ic_back_white)
                iv_basket.setImageResource(R.drawable.ic_basket_white)
                btn_upload.setBackgroundColor(resources.getColor(R.color.darkGrey))
            }
        }


    }
}