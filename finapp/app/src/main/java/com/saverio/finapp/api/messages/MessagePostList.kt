package com.saverio.finapp.api.messages

import com.google.gson.annotations.SerializedName

data class MessagePostList(
    @SerializedName("userid")
    val userid: String,
    @SerializedName("reply_to")
    val reply_to: Int = -1,
    @SerializedName("section")
    val section: String,
    @SerializedName("text")
    val text: String
)