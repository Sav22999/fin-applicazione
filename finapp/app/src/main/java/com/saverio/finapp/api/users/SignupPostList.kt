package com.saverio.finapp.api.users

import com.google.gson.annotations.SerializedName
import retrofit2.http.Query

data class SignupPostList(
    @SerializedName("username")
    val username: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("password")
    val password: String = "",
    @SerializedName("surname")
    val surname: String,
    @SerializedName("email")
    val email: String = "",
    @SerializedName("born")
    val born: String
)