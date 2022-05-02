package com.saverio.finapp.api.users

import com.google.gson.annotations.SerializedName

data class UsersList(
    @SerializedName("code")
    val code: Int,
    @SerializedName("description")
    val description: String,
    @SerializedName("user_details")
    val user_details: UsersItemsList
)