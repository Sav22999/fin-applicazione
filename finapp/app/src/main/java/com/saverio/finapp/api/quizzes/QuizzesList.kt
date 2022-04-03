package com.saverio.finapp.api.quizzes

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.saverio.finapp.api.LastUpdateList

data class QuizzesList(
    @SerializedName("last-update")
    val lastUpdate: LastUpdateList? = null,
    @SerializedName("questions")
    val questions: List<QuizzesItemsList>? = null
)