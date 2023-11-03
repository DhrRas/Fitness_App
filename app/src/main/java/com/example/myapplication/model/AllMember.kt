package com.example.myapplication.model

import android.os.Parcel
import android.os.Parcelable
import android.os.Parcelable.Creator

data class AllMember(
    val id: String = "",
    val firstName: String = "",
    val lastName: String = "",
    val gender: String = "",
    val age: String = "",
    val weight: String = "",
    val mobile: String = "",
    val address: String = "",
    val dateOfJoining: String = "",
    val memberShip: String = "",
    val expiryDate: String = "",
    val discount: String = "",
    val total: String = "",
    val image: String = ""
)


