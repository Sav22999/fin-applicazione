package com.saverio.finapp.api

import com.google.gson.annotations.SerializedName
import com.saverio.finapp.api.LastUpdateList

data class PostMessageResponseList(
    @SerializedName("code")
    val code: Int,
    @SerializedName("description")
    val description: String,
    @SerializedName("message_id")
    val message_id: String? = null
)