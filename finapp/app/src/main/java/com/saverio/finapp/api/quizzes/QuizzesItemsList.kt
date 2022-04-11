package com.saverio.finapp.api.quizzes

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.saverio.finapp.api.LastUpdateList

data class QuizzesItemsList(
    @SerializedName("id")
    val id: Int = 0,
    @SerializedName("chapter")
    val chapter: Int = 0,
    @SerializedName("section")
    val section: String = "",
    @SerializedName("question")
    val question: String = "",
    @SerializedName("A")
    val A: String = "",
    @SerializedName("B")
    val B: String = "",
    @SerializedName("C")
    val C: String = "",
    @SerializedName("D")
    val D: String = "",
    @SerializedName("correct")
    val correct: String = ""
)