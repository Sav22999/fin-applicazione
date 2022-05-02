package com.saverio.finapp.api.users

import com.google.gson.annotations.SerializedName

data class ResetPasswordPostList(
    @SerializedName("username_or_email")
    val username_or_email: String,
    @SerializedName("password")
    val password: String
)