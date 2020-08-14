package com.example.chap.view.fragment

import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.example.chap.R
import com.example.chap.internal.OnError
import com.example.chap.internal.SharedPref
import com.example.chap.internal.ViewModelsFactory
import com.example.chap.model.User
import com.example.chap.view.activity.MainActivity
import com.example.chap.viewModel.AuthActivityViewModel
import kotlinx.android.synthetic.main.fragment_edit_address.*
import java.util.regex.Pattern


class RegisterFragment : Fragment() {

    val VALID_EMAIL_ADDRESS_REGEX =
        Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE)

    lateinit var navController: NavController

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


        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_register, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = Navigation.findNavController(view)
        val et_username = view.findViewById<EditText>(R.id.et_username)
        val et_name_familyName = view.findViewById<EditText>(R.id.et_name_familyName)
        val et_email = view.findViewById<EditText>(R.id.et_email)
        val et_mobile = view.findViewById<EditText>(R.id.et_mobile)
        val et_pass = view.findViewById<EditText>(R.id.et_pass)
        val btn_register = view.findViewById<Button>(R.id.btn_register)
        val tv_signin = view.findViewById<TextView>(R.id.tv_signin)

        et_username.setText(authActivityViewModel.username.value)
        et_email.setText(authActivityViewModel.email.value)
        et_mobile.setText(authActivityViewModel.mobile.value)
        et_name_familyName.setText(authActivityViewModel.first_last_name.value)
        et_pass.setText(authActivityViewModel.password.value)

        et_username.doOnTextChanged { text, start, before, count ->
            authActivityViewModel.username.value = text.toString()
        }

        et_email.doOnTextChanged { text, start, before, count ->
            authActivityViewModel.email.value = text.toString()
        }

        et_name_familyName.doOnTextChanged { text, start, before, count ->
            authActivityViewModel.first_last_name.value = text.toString()
        }

        et_mobile.doOnTextChanged { text, start, before, count ->
            authActivityViewModel.mobile.value = text.toString()
        }

        et_pass.doOnTextChanged { text, start, before, count ->
            authActivityViewModel.password.value = text.toString()
        }

        btn_register.setOnClickListener {
            if (et_username.text.toString().isEmpty())
                et_username.error = getString(R.string.invalid_username)
            else if (et_name_familyName.text.toString().isEmpty())
                et_name_familyName.error = getString(R.string.invalid_name_family)
            else {
                val matcher = VALID_EMAIL_ADDRESS_REGEX.matcher(et_email.text.toString())
                if (et_email.text.toString().isNotEmpty() && !matcher.find())
                    et_email.error = getString(R.string.invalid_email)
                else if (et_mobile.text.toString()
                        .isEmpty() || et_mobile.text.toString().length != 11
                )
                    et_mobile.error = getString(R.string.invalid_mobile)
                else if (et_pass.text.toString().length < 6)
                    et_pass.error = getString(R.string.invalid_pass)
                else {
                    val user = User(
                        et_username.text.toString(),
                        et_name_familyName.text.toString().trim(),
                        et_email.text.toString(),
                        et_mobile.text.toString(),
                        et_pass.text.toString()
                    )
                    authActivityViewModel.register(user, object : OnError {
                        override fun onError(errMsg: String?) {
                            Toast.makeText(requireContext(), errMsg, Toast.LENGTH_SHORT).show()
                        }
                    })

                    authActivityViewModel.isUserAuthenticated.observe(viewLifecycleOwner, Observer {
                        if (it) {
                            activity?.startActivity(Intent(activity, MainActivity::class.java))
                            activity?.finish()
                            btn_register.isEnabled = false
                        }

                    })

                }
            }
        }

        tv_signin.setOnClickListener {

        navController.navigate(R.id.action_registerFragment_to_signInFragment)
        }
    }
}