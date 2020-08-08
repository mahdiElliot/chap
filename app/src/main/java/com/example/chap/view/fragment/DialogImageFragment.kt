package com.example.chap.view.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.fragment.app.DialogFragment
import com.example.chap.R
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_dialog_image.*


class DialogImageFragment : DialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        dialog?.window?.requestFeature(Window.FEATURE_NO_TITLE)
        return inflater.inflate(R.layout.fragment_dialog_image, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        btn_cancel.setOnClickListener {
            this.dismiss()
        }

        Picasso.get().load(this.tag).into(iv_design)

    }

    override fun onStart() {
        dialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            850
        )
        super.onStart()
    }
}