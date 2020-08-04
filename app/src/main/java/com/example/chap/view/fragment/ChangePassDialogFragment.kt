package com.example.chap.view.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProviders
import com.example.chap.R
import com.example.chap.internal.OnError
import com.example.chap.internal.SharedPref
import com.example.chap.internal.ViewModelsFactory
import com.example.chap.model.User
import com.example.chap.viewModel.CommentsFragmentViewModel
import com.example.chap.viewModel.EditFragmentViewModel
import kotlinx.android.synthetic.main.fragment_change_pass_dialog.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class ChangePassDialogFragment : DialogFragment() {

    lateinit var viewModel: EditFragmentViewModel

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
                .get(EditFragmentViewModel::class.java)
        return inflater.inflate(R.layout.fragment_change_pass_dialog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        btn_cancel.setOnClickListener {
            this.dismiss()
        }

        btn_send.setOnClickListener {
            val pass = et_change_pass.text.toString()
            val pass2 = et_change_pass_repeat.text.toString()
            if (pass == pass2) {
                CoroutineScope(Dispatchers.IO).launch {
                    val res = viewModel.editPass(pass, object : OnError {
                        override fun onError(errMsg: String?) {
                            Toast.makeText(context, "خطا در ارسال اطلاعات", Toast.LENGTH_SHORT)
                                .show()
                        }
                    })
                    if (res) {
                        MainScope().launch {
                            Toast.makeText(context, "اطلاعات ذخیره شد", Toast.LENGTH_SHORT).show()
                            dismiss()
                        }
                    }
                }
            } else
                et_change_pass_repeat.error = getString(R.string.notEqual_pass)
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