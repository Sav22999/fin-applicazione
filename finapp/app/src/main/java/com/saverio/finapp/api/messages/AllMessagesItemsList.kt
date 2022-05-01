package com.saverio.finapp.api.messages

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.saverio.finapp.api.LastUpdateList

data class AllMessagesItemsList(
    @SerializedName("id")
    val id: Int,
    @SerializedName("reply_to")
    val reply_to: Int = -1,
    @SerializedName("username")
    val username: String,
    @SerializedName("email")
    val email: String,
    @SerializedName("section")
    val section: String,
    @SerializedName("text")
    val text: String,
    @SerializedName("datetime")
    val datetime: String
)