package com.example.chap.view.fragment

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast

import androidx.appcompat.widget.Toolbar
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
                ViewModelsFactory(SharedPref(requireContext()), context)
            ).get(CpPbFormFragmentViewModel::class.java)

        return inflater.inflate(R.layout.fragment_cp_pb_form, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        val form = requireArguments().getInt("form")

        frame_time.visibility = if (form == 1) View.GONE else View.VISIBLE


        if (tempF2 == null) {
            tv_upload.setTextColor(resources.getColor(R.color.black))
//            tv_upload.textSize = resources.getDimension(R.dimen.text_size1)
        } else {
            tv_upload.setTextColor(resources.getColor(R.color.colorPrimaryDark))
//            tv_upload.textSize = resources.getDimension(R.dimen.text_size2)
        }

        switch_type.setOnClickListener { switch() }


        if (form != 1) {
            recyclerViewAdapter =
                TimeRecyclerViewAdapter(object : TimeRecyclerViewAdapter.Interaction {
                    override fun onItemSelected(position: Int, item: DateTime) {

                    }
                }, viewModel)

            rv_time.apply {
                layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
                adapter = this@CpPbFormFragment.recyclerViewAdapter
            }

            viewModel.getTimes(object : OnError {
                override fun onError(errMsg: String?) {

                }
            })

            viewModel.times.observe(viewLifecycleOwner, Observer {
                recyclerViewAdapter.submitList(it)
            })
        }


        viewModel.switch.observe(viewLifecycleOwner, Observer {
            switch_type.isChecked = it

            if (switch_type.isChecked) {
                toolbar.setBackgroundColor(resources.getColor(R.color.colorPrimaryDark))
                appBarLayout.setBackgroundColor(resources.getColor(R.color.colorPrimaryDark))
                btn_back.setImageResource(R.drawable.ic_back)
                iv_basket.setImageResource(R.drawable.ic_basket)
                btn_upload.setBackgroundColor(resources.getColor(R.color.colorPrimaryDark))
                switch_type.trackDrawable = resources.getDrawable(R.drawable.switch_background)
            } else {
                toolbar.setBackgroundColor(resources.getColor(R.color.darkGrey))
                appBarLayout.setBackgroundColor(resources.getColor(R.color.darkGrey))
                btn_back.setImageResource(R.drawable.ic_back_white)
                iv_basket.setImageResource(R.drawable.ic_basket_white)
                btn_upload.setBackgroundColor(resources.getColor(R.color.darkGrey))
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
//                intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes)
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

        btn_continue.setOnClickListener {
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

    }

    private fun switch() {
        viewModel.switch.value = switch_type.isChecked

        if (switch_type.isChecked) {
            toolbar.setBackgroundColor(resources.getColor(R.color.colorPrimaryDark))
            appBarLayout.setBackgroundColor(resources.getColor(R.color.colorPrimaryDark))
            btn_back.setImageResource(R.drawable.ic_back)
            iv_basket.setImageResource(R.drawable.ic_basket)
            btn_upload.setBackgroundColor(resources.getColor(R.color.colorPrimaryDark))
            switch_type.trackDrawable = resources.getDrawable(R.drawable.switch_background)
        } else {
            toolbar.setBackgroundColor(resources.getColor(R.color.darkGrey))
            appBarLayout.setBackgroundColor(resources.getColor(R.color.darkGrey))
            btn_back.setImageResource(R.drawable.ic_back_white)
            iv_basket.setImageResource(R.drawable.ic_basket_white)
            btn_upload.setBackgroundColor(resources.getColor(R.color.darkGrey))
            switch_type.trackDrawable = resources.getDrawable(R.drawable.switch_background2)
        }

        if (frame_time.visibility == View.VISIBLE)
            recyclerViewAdapter.notifyDataSetChanged()
    }

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
                    File.createTempFile("", ".pdf")
                }

            FileUtils.copyInputStreamToFile(inp, tempF)
            tv_upload.setTextColor(resources.getColor(R.color.colorPrimaryDark))
            tv_upload.textSize = resources.getDimension(R.dimen.text_size2)
        }
    }


}