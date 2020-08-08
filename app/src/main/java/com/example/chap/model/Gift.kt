package com.example.chap.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Gift(
    val name: String,
    val url: String,
    var isChosen: Boolean = false
) : Parcelable