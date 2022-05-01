package com.saverio.finapp.api

import com.google.gson.annotations.SerializedName
import com.saverio.finapp.api.LastUpdateList

data class PostResponseList(
    @SerializedName("code")
    val code: Int,
    @SerializedName("description")
    val description: String,
    @SerializedName("userid") //this is only for "login", which returns also the userid
    val userid: String? = null
)