package com.saverio.finapp.api

import com.saverio.finapp.api.chapters.ChaptersList
import com.saverio.finapp.api.quizzes.QuizzesList
import com.saverio.finapp.api.sections.SectionsList
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiInterface {
    @GET("chapters/get")
    fun getChaptersInfo(@Query("chapter") chapter: String = ""): Call<ChaptersList>

    @GET("sections/get")
    fun getSectionsInfo(
        @Query("chapter") chapter: String = "",
        @Query("section") section: String = ""
    ): Call<SectionsList>

    @GET("quizzes/get")
    fun getQuizzesInfo(
        @Query("chapter") chapter: String = "",
        @Query("question") question: String = ""
    ): Call<QuizzesList>
}