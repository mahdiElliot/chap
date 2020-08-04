package com.example.chap.view.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.chap.R
import com.example.chap.internal.OnError
import com.example.chap.internal.SharedPref
import com.example.chap.internal.ViewModelsFactory
import com.example.chap.viewModel.CommentsFragmentViewModel
import com.example.chap.viewModel.MainActivityViewModel
import kotlinx.android.synthetic.main.fragment_comment_dialog.*
import kotlinx.android.synthetic.main.fragment_dialog_about_us.*
import kotlinx.android.synthetic.main.fragment_dialog_about_us.btn_cancel


class CommentDialogFragment : DialogFragment() {

    lateinit var viewModel: CommentsFragmentViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        dialog?.window?.requestFeature(Window.FEATURE_NO_TITLE)

        viewModel =
            ViewModelProviders.of(
                requireActivity(),
                ViewModelsFactory(SharedPref(requireContext()))
            )
                .get(CommentsFragmentViewModel::class.java)
        return inflater.inflate(R.layout.fragment_comment_dialog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        btn_cancel.setOnClickListener {
            this.dismiss()
        }

        btn_send.setOnClickListener {
            viewModel.sendComment("mahdi", content.text.toString(), object : OnError {
                override fun onError(errMsg: String?) {
                    Toast.makeText(context, errMsg, Toast.LENGTH_LONG).show()
                }
            })
            dismiss()
        }
    }

    override fun onStart() {
        dialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        super.onStart()
    }
}