package com.saverio.finapp.api.users

import com.google.gson.annotations.SerializedName

data class LoginPostList(
    @SerializedName("username_or_email")
    val username_or_email: String,
    @SerializedName("password")
    val password: String
)