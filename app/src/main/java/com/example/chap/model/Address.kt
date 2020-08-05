package com.example.chap.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Address(
    val lat: Float,
    val lng: Float,
    val address: String
) : Parcelable