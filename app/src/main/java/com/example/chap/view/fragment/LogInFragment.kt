package com.example.chap.view.fragment

import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.os.SystemClock
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.chap.R
import com.example.chap.internal.OnError
import com.example.chap.internal.SharedPref
import com.example.chap.internal.ViewModelsFactory
import com.example.chap.view.activity.MainActivity
import com.example.chap.viewModel.AuthActivityViewModel


class LogInFragment : Fragment() {


    lateinit var authActivityViewModel: AuthActivityViewModel


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        //screen only portrait
//        this.activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR
//        this.retainInstance = true

        authActivityViewModel =
            ViewModelProviders.of(
                requireActivity(),
                ViewModelsFactory(SharedPref(requireContext()))
            )
                .get(AuthActivityViewModel::class.java)
        return inflater.inflate(R.layout.fragment_login_in, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val et_email_username = view.findViewById<EditText>(R.id.et_email_username)
        val et_pass = view.findViewById<EditText>(R.id.et_pass)
        val tv_register = view.findViewById<TextView>(R.id.tv_register)
        val btn_login = view.findViewById<Button>(R.id.btn_login)
        val back = view.findViewById<ImageView>(R.id.back)

        back.setOnClickListener {
            requireActivity().onBackPressed()
        }

        tv_register.setOnClickListener {
            requireActivity().onBackPressed()
        }

        et_email_username.setText(authActivityViewModel.username2.value)
        et_pass.setText(authActivityViewModel.password2.value)

        et_email_username.doOnTextChanged { text, start, before, count ->
            authActivityViewModel.username2.value = text.toString()
        }

        et_pass.doOnTextChanged { text, start, before, count ->
            authActivityViewModel.password2.value = text.toString()
        }

        btn_login.setOnClickListener {
            if (et_email_username.text.toString().isEmpty())
                et_email_username.error = getString(R.string.invalid_email)
            else if (et_pass.text.toString().length < 6)
                et_pass.error = getString(R.string.invalid_pass)
            else {
//                authActivityViewModel.login(et_email_username.text.toString(),
//                    et_pass.text.toString(),
//                    object : OnError {
//                        override fun onError(errMsg: String?) {
//                            Toast.makeText(requireContext(), errMsg, Toast.LENGTH_SHORT).show()
//                        }
//                    })

                authActivityViewModel.login(et_email_username.text.toString(),
                    et_pass.text.toString(),
                    object : OnError {
                        override fun onError(errMsg: String?) {
                            Toast.makeText(requireContext(), errMsg, Toast.LENGTH_SHORT).show()
                        }
                    })

                authActivityViewModel.isUserAuthenticated.observe(viewLifecycleOwner, Observer {
                    if (it) {
                        activity?.startActivity(Intent(activity, MainActivity::class.java))
                        activity?.finish()
                        btn_login.isEnabled = false
                    }
                })
            }
        }

    }

}