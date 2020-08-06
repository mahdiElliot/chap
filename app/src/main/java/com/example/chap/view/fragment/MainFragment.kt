package com.example.chap.view.fragment

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.cardview.widget.CardView
import androidx.core.os.bundleOf
import androidx.navigation.Navigation
import com.example.chap.R
import com.example.chap.view.activity.MainActivity
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.bottomnavigation.BottomNavigationView


class MainFragment : Fragment(), View.OnClickListener {


    lateinit var bottom_nav: BottomNavigationView
    lateinit var iv_content: ImageView
    lateinit var btn_back: ImageView
    lateinit var layout_basket: LinearLayout
    lateinit var tv_address_title: TextView
    lateinit var appBarLayout: AppBarLayout
    lateinit var toolbar: Toolbar

    lateinit var act: MainActivity

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
//        this.activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR
//        this.retainInstance = true

        bottom_nav = requireActivity().findViewById(R.id.bottom_nav)
        iv_content = requireActivity().findViewById(R.id.iv_content)
        btn_back = requireActivity().findViewById(R.id.btn_back)
        layout_basket = requireActivity().findViewById(R.id.layout_basket)
        tv_address_title = requireActivity().findViewById(R.id.tv_address_title)
        appBarLayout = requireActivity().findViewById(R.id.appBarLayout)
        toolbar = requireActivity().findViewById(R.id.toolbar)

        bottom_nav.visibility = View.VISIBLE
        iv_content.visibility = View.VISIBLE
        btn_back.visibility = View.GONE
        tv_address_title.visibility = View.GONE
        (layout_basket.layoutParams as Toolbar.LayoutParams).gravity = Gravity.LEFT

        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val cv_print = view.findViewById<CardView>(R.id.cv_print)
        val cv_copy = view.findViewById<CardView>(R.id.cv_copy)
        val cv_gift = view.findViewById<CardView>(R.id.cv_gift)
        val cv_promotional = view.findViewById<CardView>(R.id.cv_promotional)
        val btn_addresses = view.findViewById<Button>(R.id.btn_addresses)

        act = activity as MainActivity
        act.currentFragment = 0
        act.navController = Navigation.findNavController(view)

        toolbar.setBackgroundColor(resources.getColor(R.color.colorPrimaryDark))
        appBarLayout.setBackgroundColor(resources.getColor(R.color.colorPrimaryDark))

        cv_print.setOnClickListener(this)
        cv_copy.setOnClickListener(this)
        cv_gift.setOnClickListener(this)
        cv_promotional.setOnClickListener(this)
        btn_addresses.setOnClickListener(this)

    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.cv_print -> {
                val bundle = bundleOf("form" to 1)
                act.navController.navigate(R.id.action_mainFragment_to_cpPbFormFragment, bundle)
            }
            R.id.cv_copy -> {
                val bundle = bundleOf("form" to 2)
                act.navController.navigate(R.id.action_mainFragment_to_cpPbFormFragment, bundle)
            }
            R.id.cv_gift -> {
                val bundle = bundleOf("form2" to 1)
                act.navController.navigate(R.id.action_mainFragment_to_giftPromoFragment, bundle)
            }
            R.id.cv_promotional -> {
                val bundle = bundleOf("form2" to 2)
                act.navController.navigate(R.id.action_mainFragment_to_giftPromoFragment, bundle)
            }
            R.id.btn_addresses -> {
                act.navController.navigate(R.id.action_mainFragment_to_addressListFragment)
                tv_address_title.visibility = View.VISIBLE
            }
        }

        bottom_nav.visibility = View.GONE
        iv_content.visibility = View.GONE
        btn_back.visibility = View.VISIBLE
        (layout_basket.layoutParams as Toolbar.LayoutParams).gravity = Gravity.RIGHT
    }
}