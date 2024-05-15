package com.task.contacts.model

import com.google.gson.annotations.SerializedName

data class ContactsResponse(
    @SerializedName("results")
    val results: List<Result>,
    @SerializedName("info")
    val info: Info?

) {
    data class Info(
        @SerializedName("page")
        val page: Int?=0,
        @SerializedName("results")
        val results: Int?=0,
        @SerializedName("seed")
        val seed: String?=null,
        @SerializedName("version")
        val version: String?=null
    )

    data class Result(
        @SerializedName("cell")
        val cell: String?=null,
        @SerializedName("email")
        val email: String?=null,
        @SerializedName("gender")
        val gender: String?=null,
        @SerializedName("id")
        val id: Id?=null,
        @SerializedName("name")
        val name: Name?=null,
        @SerializedName("phone")
        val phone: String?=null,
        @SerializedName("picture")
        val picture: Picture?=null
    ) {
        data class Id(
            @SerializedName("name")
            val name: String?=null,
            @SerializedName("value")
            val value: String?=null
        )

        data class Name(
            @SerializedName("first")
            val first: String?=null,
            @SerializedName("last")
            val last: String?=null,
            @SerializedName("title")
            val title: String?=null
        )
        data class Picture(
            @SerializedName("large")
            val large: String?=null,
            @SerializedName("medium")
            val medium: String?=null,
            @SerializedName("thumbnail")
            val thumbnail: String?=null
        )
    }
}