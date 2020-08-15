package com.example.chap.view.fragment

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProviders
import com.example.chap.R
import com.example.chap.internal.*
import com.example.chap.model.Address
import com.example.chap.viewModel.AddressListFragmentViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_edit_address.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import java.io.File


class EditAddressFragment : Fragment() {

    lateinit var viewModel: AddressListFragmentViewModel
    lateinit var t: CountDownTimer

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        viewModel =
            ViewModelProviders.of(
                requireActivity(),
                ViewModelsFactory(SharedPref(requireContext()))
            ).get(AddressListFragmentViewModel::class.java)

        return inflater.inflate(R.layout.fragment_edit_address, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val address = requireArguments().getString("address")
        val lat = requireArguments().getDouble("lat")
        val lng = requireArguments().getDouble("lng")
        val phone = requireArguments().getString("phone")

        et_address.setText(address)
        et_number.setText(phone)

        btn_save.isClickable = true

        btn_save.setOnClickListener {
//            btn_save.isClickable = false
            val txt = et_address.text.toString()
            val number = et_number.text.toString()
            if (txt.isEmpty())
                Toast.makeText(context, "فیلد آدرس نباید خالی باشد", Toast.LENGTH_LONG).show()
            else if (number.isEmpty() || number.length != 11)
                Toast.makeText(context, "شماره تلفن باید ۱۱ رقم باشد", Toast.LENGTH_LONG).show()
            else {
                val sharedP = requireActivity().getSharedPreferences("forms", Context.MODE_PRIVATE)
                val isForm = sharedP.getInt("form", 0)
                if (isForm != 0) {
                    val builder = AlertDialog.Builder(context)
                    builder.setMessage("درصورت اطمینان از صحت اطلاعات درخواست شما ثبت خواهد شد‌")
                    builder.setCancelable(true)
                    builder.setPositiveButton("بله") { _, _ ->
                        progressbar.visibility = View.VISIBLE

                        CoroutineScope(IO).launch {
                            if (save(lat, lng, txt, number)) {
                                if (isForm == 1)
                                    send(sharedP, txt, number)
                                else
                                    send2(sharedP, txt, number)

                                setTimer()
                            } else
                                showDialog("متاسفانه درخواست ثبت نشد. دوباره سعی کنید")
                        }

                    }
                    builder.setNegativeButton("خیر") { dialog, _ -> dialog.cancel() }
                    builder.create().show()

                } else {
                    CoroutineScope(IO).launch {
                        if (save(lat, lng, txt, number)) {
                            requireActivity().onBackPressed()
                            requireActivity().onBackPressed()
                        }
                    }
                }

            }
        }
    }

    private suspend fun save(lat: Double, lng: Double, address: String, number: String): Boolean {
        val shouldAdd = requireArguments().getBoolean("add", false)
        val adrs = Address(lat.toString(), lng.toString(), address, number)
        var isSaved = false
        if (!shouldAdd) {
            isSaved = viewModel.editAddress(adrs, object : OnError {
                override fun onError(errMsg: String?) {
                    isSaved = false
                    btn_save.isClickable = true
                }
            })
        } else {
            isSaved = viewModel.saveAddress(adrs, object : OnError {
                override fun onError(errMsg: String?) {
                    Log.i("not saved", "not saved")
                    isSaved = false
                    btn_save.isClickable = true
                }
            })
        }
        return isSaved
    }

    @SuppressLint("CheckResult")
    private fun send(sharedP: SharedPreferences, address: String, number: String) {
        val time = sharedP.getString("time", "")
        val file1 = sharedP.getString("file1", "")
        val file2 = sharedP.getString("file2", "")
        val colorful = sharedP.getBoolean("switch", true)
        val description = sharedP.getString("desc", "")

        Mailer.sendMail(
            Config.RECEIVER,
            colorful,
            number,
            address,
            time,
            file1,
            file2,
            description
        )
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                t.cancel()
                if (progressbar != null)
                    progressbar.visibility = View.GONE

                showDialog(" درخواست شماثبت شد. درباره هزینه و سایر جزئیات به زودی به شما اطلاع داده خواهد شد")
                val editor = sharedP.edit()
                editor?.putInt("form", 0)
                editor?.apply()
                requireActivity().onBackPressed()
                requireActivity().onBackPressed()
                requireActivity().onBackPressed()
                requireActivity().onBackPressed()

                btn_save.isClickable = true
            },
                {
                    t.cancel()
                    if (progressbar != null)
                        progressbar.visibility = View.GONE
                    Log.i("didNot", "did not")

                    showDialog("متاسفانه درخواست ثبت نشد. دوباره سعی کنید")
                    btn_save.isClickable = true

                })
    }


    @SuppressLint("CheckResult")
    private fun send2(sharedP: SharedPreferences, address: String, number: String) {
        val url = sharedP.getString("url", "")
        val file1 = sharedP.getString("file1", "")
        val file2 = sharedP.getString("file2", "")
        val description = sharedP.getString("desc", "")

        Mailer.sendMail2(
            Config.RECEIVER,
            number,
            address,
            url,
            file1,
            file2,
            description
        )
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                t.cancel()
                if (progressbar != null)
                    progressbar.visibility = View.GONE

                showDialog(" درخواست شماثبت شد. درباره هزینه و سایر جزئیات به زودی به شما اطلاع داده خواهد شد")
                val editor = sharedP.edit()
                editor?.putInt("form", 0)
                editor?.apply()
                requireActivity().onBackPressed()
                requireActivity().onBackPressed()
                requireActivity().onBackPressed()
                requireActivity().onBackPressed()

                btn_save.isClickable = true
            },
                {
                    t.cancel()
                    if (progressbar != null)
                        progressbar.visibility = View.GONE
                    Log.i("didNot", "did not")

                    showDialog("متاسفانه درخواست ثبت نشد. دوباره سعی کنید")
                    btn_save.isClickable = true

                })
    }

    private fun showDialog(text: String) {
        val alertDialog: AlertDialog? = requireActivity().let {
            val builder = AlertDialog.Builder(it)
            builder.apply {
                setPositiveButton("تایید", { dialog, id ->
                })
            }

            builder.setMessage(text)
            builder.create()
        }

        alertDialog?.show()
    }

    private fun setTimer() {
        //40 seconds
        t = object : CountDownTimer(30000, 1000) {
            override fun onTick(millisUntilFinished: Long) {}
            override fun onFinish() {
                progressbar.visibility = View.GONE
                btn_save.isClickable = true
            }
        }.start()
    }
}
