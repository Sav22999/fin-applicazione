package com.saverio.finapp.api.users

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.saverio.finapp.api.LastUpdateList

data class UsersItemsList(
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