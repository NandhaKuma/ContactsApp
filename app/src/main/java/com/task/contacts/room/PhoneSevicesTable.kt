package com.task.contacts.room

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName


@Entity(tableName = "PhoneServices")
class PhoneSevicesTable(
    @PrimaryKey
    @SerializedName("ids")
    var ids: Int?=0,
    @SerializedName("cell")
    var cell: String?=null,
    @SerializedName("email")
    var email: String?=null,
    @SerializedName("gender")
    var gender: String?=null,
    @SerializedName("id")
    var id: String?=null,
    @SerializedName("name")
    var name: String?=null,
    @SerializedName("phone")
    var phone: String?=null,
    @SerializedName("picture")
    var picture: String?=null
)