package com.example.chap.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Comment(
    val id: String,
    val name: String,
    val content: String,
    val date: String
) : Parcelable