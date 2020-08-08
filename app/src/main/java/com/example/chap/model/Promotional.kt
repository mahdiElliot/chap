package com.example.chap.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Promotional(
    val name: String,
    val url: String,
    var isChosen: Boolean = false
) : Parcelable