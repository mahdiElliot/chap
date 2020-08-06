package com.example.chap.view.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.chap.R
import com.example.chap.internal.OnError
import com.example.chap.internal.SharedPref
import com.example.chap.internal.ViewModelsFactory
import com.example.chap.viewModel.EditFragmentViewModel
import kotlinx.android.synthetic.main.fragment_edit.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class EditFragment : Fragment() {

    lateinit var viewModel: EditFragmentViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        viewModel =
            ViewModelProviders.of(
                requireActivity(),
                ViewModelsFactory(SharedPref(requireContext()))
            )
                .get(EditFragmentViewModel::class.java)
        return inflater.inflate(R.layout.fragment_edit, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (SharedPref(requireContext()).hasToken())
            viewModel.getUser(object : OnError {
                override fun onError(errMsg: String?) {
                    Toast.makeText(context, R.string.error, Toast.LENGTH_SHORT).show()
                }
            })

        viewModel.user.observe(viewLifecycleOwner, Observer {
            et_username.setText(it.username)
            et_email.setText(it.email)
            et_mobile.setText(it.number)
        })

        btn_save.setOnClickListener {
            val builder1 = AlertDialog.Builder(requireContext())
            builder1.setMessage("اطلاعات جدید ذخیره شود؟")
            builder1.setCancelable(true)
            builder1.setPositiveButton(
                "بله"
            ) { _, _ ->
                CoroutineScope(Dispatchers.IO).launch {
                    val res = viewModel.editProfile(et_username.text.toString(),
                        et_email.text.toString(),
                        et_mobile.text.toString(),
                        object : OnError {
                            override fun onError(errMsg: String?) {
                                Toast.makeText(context, "خطا در ارسال اطلاعات", Toast.LENGTH_SHORT)
                                    .show()
                            }
                        })
                    if (res) {
                        MainScope().launch {
                            Toast.makeText(context, "اطلاعات ذخیره شد", Toast.LENGTH_SHORT).show()
                        }
                    }
                }

            }

            builder1.setNegativeButton(
                "خیر"
            ) { dialog, _ -> dialog.cancel() }

            val alert11 = builder1.create()
            alert11.show()
        }

        btn_change_pass.setOnClickListener {
            DialogChangePassFragment().show(requireActivity().supportFragmentManager, "changePass")
        }
    }
}