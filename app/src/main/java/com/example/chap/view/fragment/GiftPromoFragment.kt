package com.example.chap.view.fragment

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.chap.R
import com.example.chap.adapter.GiftPromoRecyclerViewAdapter
import com.example.chap.internal.OnError
import com.example.chap.internal.SharedPref
import com.example.chap.internal.ViewModelsFactory
import com.example.chap.model.Gift
import com.example.chap.viewModel.GiftPromoFragmentViewModel
import kotlinx.android.synthetic.main.fragment_cp_pb_form.*
import kotlinx.android.synthetic.main.fragment_gift_promo.*
import kotlinx.android.synthetic.main.fragment_gift_promo.btn_continue
import kotlinx.android.synthetic.main.fragment_gift_promo.btn_upload
import kotlinx.android.synthetic.main.fragment_gift_promo.et_description
import kotlinx.android.synthetic.main.fragment_gift_promo.iv_tick
import org.apache.commons.io.FileUtils
import java.io.File


class GiftPromoFragment : Fragment() {

    lateinit var viewModel: GiftPromoFragmentViewModel
    lateinit var recyclerViewAdapter: GiftPromoRecyclerViewAdapter

    private var tempF: File? = null
    private var tempF2: File? = null

    private var form = -1

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        viewModel =
            ViewModelProviders.of(
                requireActivity(),
                ViewModelsFactory(SharedPref(requireContext()))
            ).get(GiftPromoFragmentViewModel::class.java)

        return inflater.inflate(R.layout.fragment_gift_promo, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        iv_tick.visibility = if (tempF == null) View.GONE else View.VISIBLE

        var url = ""
        recyclerViewAdapter =
            GiftPromoRecyclerViewAdapter(object : GiftPromoRecyclerViewAdapter.Interaction {
                override fun onItemSelected(position: Int, item: Gift) {
                    url = if (viewModel.positionChecked.value == -1) "" else item.url
                }
            }, viewModel, requireActivity().supportFragmentManager)

        rv_design.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = this@GiftPromoFragment.recyclerViewAdapter
        }

        form = requireArguments().getInt("form2")

        if (form == 1) {
            viewModel.getGifts(object : OnError {
                override fun onError(errMsg: String?) {

                }
            })
            viewModel.gifts.observe(viewLifecycleOwner, Observer {
                recyclerViewAdapter.submitList(it)
            })
        } else if (form == 2) {
            viewModel.getPromos(object : OnError {
                override fun onError(errMsg: String?) {

                }
            })

            viewModel.promos.observe(viewLifecycleOwner, Observer {
                recyclerViewAdapter.submitList(it)
            })
        }



        tempF = viewModel.file1.value
        tempF2 = viewModel.file2.value

        et_description.setText(viewModel.description.value)

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

        et_description.doOnTextChanged { text, start, before, count ->
            viewModel.description.value = text.toString()
        }


        val navController = Navigation.findNavController(view)
        btn_continue.setOnClickListener {
            if (tempF == null && url == "") {
                Toast.makeText(context, "لطفا فایل یا طرحی را انتخاب کنید", Toast.LENGTH_LONG)
                    .show()
            } else {
                val sharedP =
                    requireActivity().getSharedPreferences("forms", Context.MODE_PRIVATE)
                val editor = sharedP.edit()
                editor.putInt("form", 2)
                editor.putString("url", url)
                editor.putString("file1", tempF?.absolutePath)
                editor.putString("file2", tempF2?.absolutePath)
                editor.putString("desc", et_description.text.toString())
                editor.apply()
                navController.navigate(R.id.action_giftPromoFragment_to_addressListFragment)
            }
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 111 && resultCode == Activity.RESULT_OK) {
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