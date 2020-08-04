package com.example.chap.view.fragment

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.chap.R
import com.example.chap.adapter.CommentsRecyclerViewAdapter
import com.example.chap.internal.OnError
import com.example.chap.internal.SharedPref
import com.example.chap.internal.ViewModelsFactory
import com.example.chap.model.Comment
import com.example.chap.view.activity.MainActivity
import com.example.chap.viewModel.CommentsFragmentViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.fragment_comments.*

class CommentsFragment : Fragment() {

    lateinit var recyclerViewAdapter: CommentsRecyclerViewAdapter
    lateinit var bottom_nav: BottomNavigationView
    lateinit var iv_content: ImageView
    lateinit var btn_back: ImageView
    lateinit var layout_basket: LinearLayout
    lateinit var tv_address_title: TextView

    private lateinit var foundComments: ArrayList<Comment>

    lateinit var act: MainActivity

    lateinit var commentsViewModel: CommentsFragmentViewModel

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

        recyclerViewAdapter =
            CommentsRecyclerViewAdapter(
                object : CommentsRecyclerViewAdapter.Interaction {
                    override fun onItemSelected(position: Int, item: Comment) {

                    }
                }
            )
        rv_comments.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            adapter = this@CommentsFragment.recyclerViewAdapter
        }

        commentsViewModel.comments.observe(viewLifecycleOwner, Observer {
            recyclerViewAdapter.submitList(it)
            progressbar.visibility = View.GONE
            btn_retry.visibility = View.GONE
        })

        Log.i("fuck", (commentsViewModel.comments.value == null).toString())

        progressbar.visibility = View.VISIBLE

        if (commentsViewModel.comments.value == null)
            commentsViewModel.getCommnets(object : OnError {
                override fun onError(errMsg: String?) {
                    progressbar.visibility = View.GONE
                    btn_retry.visibility = View.VISIBLE
                }
            })

        btn_retry.setOnClickListener {
            progressbar.visibility = View.VISIBLE
            btn_retry.visibility = View.GONE
            commentsViewModel.getCommnets(object : OnError {
                override fun onError(errMsg: String?) {
                    progressbar.visibility = View.GONE
                    btn_retry.visibility = View.VISIBLE
                }
            })
        }

    }
}