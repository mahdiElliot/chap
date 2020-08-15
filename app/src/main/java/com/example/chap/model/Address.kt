package com.example.chap.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Address(
    val lat: String,
    val lng: String,
    val address: String,
    val phone: String
) : Parcelable