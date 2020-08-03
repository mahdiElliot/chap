package com.example.chap.application.view.fragment

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.Navigation
import com.example.chap.R
import com.example.chap.application.internal.SharedPref
import com.example.chap.application.internal.ViewModelsFactory
import com.example.chap.application.view.activity.MainActivity
import com.example.chap.application.viewModel.CommentsFragmentViewModel
import com.example.chap.application.viewModel.MainActivityViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView

class CommentsFragment : Fragment() {

    lateinit var bottom_nav: BottomNavigationView
    lateinit var iv_content: ImageView
    lateinit var btn_back: ImageView
    lateinit var layout_basket: LinearLayout
    lateinit var tv_address_title: TextView

    private lateinit var foundComments: ArrayList<String>

    lateinit var act: MainActivity

    lateinit var commentsViewModel: CommentsFragmentViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        this.activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR
        this.retainInstance = true

        bottom_nav = requireActivity().findViewById(R.id.bottom_nav)
        iv_content = requireActivity().findViewById(R.id.iv_content)
        btn_back = requireActivity().findViewById(R.id.btn_back)
        layout_basket = requireActivity().findViewById(R.id.layout_basket)
        tv_address_title = requireActivity().findViewById(R.id.tv_address_title)

        bottom_nav.visibility = View.VISIBLE
        iv_content.visibility = View.VISIBLE
        btn_back.visibility = View.GONE
        tv_address_title.visibility = View.GONE
        (layout_basket.layoutParams as Toolbar.LayoutParams).gravity = Gravity.LEFT

        commentsViewModel =
            ViewModelProviders.of(
                requireActivity(),
                ViewModelsFactory(SharedPref(requireContext()))
            )
                .get(CommentsFragmentViewModel::class.java)
        return inflater.inflate(R.layout.fragment_comments, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        act = activity as MainActivity
        act.currentFragment = 1
        act.navController2 = Navigation.findNavController(view)

        foundComments = ArrayList()

    }
}