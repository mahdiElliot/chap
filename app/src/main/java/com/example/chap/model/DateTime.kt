package com.example.chap.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class DateTime(
    val hour: String,
    val date: String,
    var isChosen: Boolean = false
) : Parcelable