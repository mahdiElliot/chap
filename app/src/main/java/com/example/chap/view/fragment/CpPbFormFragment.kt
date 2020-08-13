package com.example.chap.view.fragment

import android.app.Activity.RESULT_OK
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast

import androidx.appcompat.widget.Toolbar
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.chap.R
import com.example.chap.adapter.TimeRecyclerViewAdapter
import com.example.chap.internal.*
import com.example.chap.model.DateTime
import com.example.chap.viewModel.CpPbFormFragmentViewModel
import com.google.android.material.appbar.AppBarLayout
import com.squareup.picasso.Picasso
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_cp_pb_form.*
import org.apache.commons.io.FileUtils
import java.io.File
import java.io.FileDescriptor
import java.lang.Exception


class CpPbFormFragment : Fragment() {


    lateinit var appBarLayout: AppBarLayout
    lateinit var toolbar: Toolbar
    lateinit var btn_back: ImageView
    lateinit var iv_basket: ImageView

    lateinit var recyclerViewAdapter: TimeRecyclerViewAdapter

    lateinit var viewModel: CpPbFormFragmentViewModel

    private var tempF: File? = null
    private var tempF2: File? = null

    private var form = -1

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        appBarLayout = requireActivity().findViewById(R.id.appBarLayout)
        toolbar = requireActivity().findViewById(R.id.toolbar)
        btn_back = requireActivity().findViewById(R.id.btn_back)
        iv_basket = requireActivity().findViewById(R.id.iv_basket)

        viewModel =
            ViewModelProviders.of(
                requireActivity(),
                ViewModelsFactory(SharedPref(requireContext()))
            ).get(CpPbFormFragmentViewModel::class.java)

        return inflater.inflate(R.layout.fragment_cp_pb_form, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        form = requireArguments().getInt("form")

        var time = ""
        if (form == 1) {
            frame_time.visibility = View.GONE
            layout_upload.visibility = View.VISIBLE
            iv_tick.visibility = if (tempF == null) View.GONE else View.VISIBLE
        } else if (form == 2) {
            frame_time.visibility = View.VISIBLE
            layout_upload.visibility = View.GONE

            //recyclerView
            recyclerViewAdapter =
                TimeRecyclerViewAdapter(object : TimeRecyclerViewAdapter.Interaction {
                    override fun onItemSelected(position: Int, item: DateTime) {
                        time = if (viewModel.positionChecked.value == -1)
                            ""
                        else "${item.date}   ${item.hour}"
                    }
                }, viewModel)

            rv_time.apply {
                layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
                adapter = this@CpPbFormFragment.recyclerViewAdapter
            }

            viewModel.getTimes(object : OnError {
                override fun onError(errMsg: String?) {
                    Toast.makeText(context, "خطا در دریافت", Toast.LENGTH_LONG).show()
                }
            })

            viewModel.times.observe(viewLifecycleOwner, Observer {
                recyclerViewAdapter.submitList(it)
            })
        }

//        showDialog(form)

        tempF = viewModel.file1.value
        tempF2 = viewModel.file2.value

        switch_type.setOnClickListener { switch() }


        viewModel.switch.observe(viewLifecycleOwner, Observer {
            switch_type.isChecked = it

            if (switch_type.isChecked) {
                toolbar.setBackgroundColor(resources.getColor(R.color.colorPrimaryDark))
                appBarLayout.setBackgroundColor(resources.getColor(R.color.colorPrimaryDark))
                btn_back.setImageResource(R.drawable.ic_back)
                iv_basket.setImageResource(R.drawable.ic_basket)
                btn_upload.setCardBackgroundColor(resources.getColor(R.color.colorPrimaryDark))
                switch_type.trackDrawable = resources.getDrawable(R.drawable.switch_background)
            } else {
                toolbar.setBackgroundColor(resources.getColor(R.color.darkGrey))
                appBarLayout.setBackgroundColor(resources.getColor(R.color.darkGrey))
                btn_back.setImageResource(R.drawable.ic_back_white)
                iv_basket.setImageResource(R.drawable.ic_basket_white)
                btn_upload.setCardBackgroundColor(resources.getColor(R.color.darkGrey))
                switch_type.trackDrawable = resources.getDrawable(R.drawable.switch_background2)
            }
        })



        btn_upload.setOnClickListener {
            val mimeTypes =
                arrayOf("application/pdf", "application/jpg", "application/png", "image/*")

            val intent = Intent().setAction(Intent.ACTION_GET_CONTENT)
//            intent.addCategory(Intent.CATEGORY_OPENABLE)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                intent.type = "*/*"
                intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes)
            } else {
                var mimeStr = ""
                for (mime in mimeTypes)
                    mimeStr += "$mime|"

                intent.type = mimeStr.substring(0, mimeStr.length - 1)
            }
            intent.flags =
                Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION

            startActivityForResult(Intent.createChooser(intent, "select a file"), 111)

        }

        val navController = Navigation.findNavController(view)
        btn_continue.setOnClickListener {
            if (form == 1 && tempF == null)
                Toast.makeText(context, "لطفا فایلی را انتخاب کنید", Toast.LENGTH_LONG).show()
            else if (form == 2 && time == "")
                Toast.makeText(context, "لطفا زمانی را انتخاب کنید", Toast.LENGTH_LONG).show()
            else {
                val sharedP =
                    requireActivity().getSharedPreferences("forms", Context.MODE_PRIVATE)
                val editor = sharedP.edit()
                editor.putInt("form", 1)
                editor.putString("time", time)
                editor.putString("file1", tempF?.absolutePath)
                editor.putString("file2", tempF2?.absolutePath)
                editor.putBoolean("switch", switch_type.isChecked)
                editor.putString("desc", et_description.text.toString())
                editor.apply()
                navController.navigate(R.id.action_cpPbFormFragment_to_addressListFragment)
            }
//                Mailer.sendMail("mahdimn2011@yahoo.com",
//                    "subject test", "text test", tempF.absolutePath)
//                    .subscribeOn(Schedulers.io())
//                    .observeOn(AndroidSchedulers.mainThread())
//                    .subscribe ({
//                        Toast.makeText(context, "mail sent", Toast.LENGTH_LONG).show()
//                    },
//                {
//                    Log.i("didNot", "did not")
//                })
        }


        et_description.setText(viewModel.description.value)

        et_description.doOnTextChanged { text, start, before, count ->
            viewModel.description.value = text.toString()
        }

    }

    private fun switch() {
        viewModel.switch.value = switch_type.isChecked

        if (switch_type.isChecked) {
            toolbar.setBackgroundColor(resources.getColor(R.color.colorPrimaryDark))
            appBarLayout.setBackgroundColor(resources.getColor(R.color.colorPrimaryDark))
            btn_back.setImageResource(R.drawable.ic_back)
            iv_basket.setImageResource(R.drawable.ic_basket)
            if (form == 1)
                btn_upload.setBackgroundColor(resources.getColor(R.color.colorPrimaryDark))
            switch_type.trackDrawable = resources.getDrawable(R.drawable.switch_background)
        } else {
            toolbar.setBackgroundColor(resources.getColor(R.color.darkGrey))
            appBarLayout.setBackgroundColor(resources.getColor(R.color.darkGrey))
            btn_back.setImageResource(R.drawable.ic_back_white)
            iv_basket.setImageResource(R.drawable.ic_basket_white)
            if (form == 1)
                btn_upload.setBackgroundColor(resources.getColor(R.color.darkGrey))
            switch_type.trackDrawable = resources.getDrawable(R.drawable.switch_background2)
        }

        if (form == 2)
            recyclerViewAdapter.notifyDataSetChanged()
    }

//    private fun showDialog(form: Int) {
//        if (form != 1 && viewModel.first.value!!) {
//            val alertDialog: AlertDialog? = requireActivity().let {
//                val builder = AlertDialog.Builder(it)
//                builder.apply {
//                    setPositiveButton("تایید", { dialog, id ->
//                    })
//                }
//
//                builder.setMessage(" اگر نیاز به کپی کتاب یا جزوه ای دارید که باید به صورت فیزیکی تحویل دهید، زمانی را جهت تحویل انتخاب کنید")
//                builder.create()
//            }
//
//            alertDialog?.show()
//
//            viewModel.first.value = false
//        }
//    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 111 && resultCode == RESULT_OK) {
            val selectedFile = data?.data
            val f = File(selectedFile.toString())
            val inp = requireActivity().contentResolver.openInputStream(selectedFile!!)

            tempF =
                if (f.name.contains("image") || f.name.contains("png") || f.name.contains("jpg")) {
                    File.createTempFile("file", ".png")

                } else if (f.name.contains("pdf")) {
                    File.createTempFile("file", ".pdf")
                } else {
                    tempF2 = File.createTempFile("file", ".png")
                    FileUtils.copyInputStreamToFile(inp, tempF2)
                    viewModel.file2.value = tempF2
                    File.createTempFile("", ".pdf")
                }

            FileUtils.copyInputStreamToFile(inp, tempF)
            viewModel.file1.value = tempF
            if (form == 1)
                iv_tick.visibility = View.VISIBLE
        }
    }


}