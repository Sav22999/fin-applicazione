package com.saverio.finapp.api.messages

import com.google.gson.annotations.SerializedName

data class MessagePostList(
    @SerializedName("userid")
    val userid: String,
    @SerializedName("username")
    val username: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("surname")
    val surname: String,
    @SerializedName("email")
    val email: String = "",
    @SerializedName("born")
    val born: String,
    @SerializedName("created")
    val created: String
)