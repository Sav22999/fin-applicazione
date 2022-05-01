package com.saverio.finapp.api

import com.google.gson.annotations.SerializedName
import com.saverio.finapp.api.chapters.ChaptersList
import com.saverio.finapp.api.news.NewsList
import com.saverio.finapp.api.quizzes.QuizzesList
import com.saverio.finapp.api.sections.SectionsList
import com.saverio.finapp.api.statistics.StatisticsList
import com.saverio.finapp.api.statistics.StatisticsPostList
import com.saverio.finapp.db.StatisticsModel
import retrofit2.Call
import retrofit2.http.*

interface ApiInterface {
    @GET("chapters/get")
    fun getChaptersInfo(
        @Query("chapter") chapter: String = ""
    ): Call<ChaptersList>

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

    @GET("news/get")
    fun getNewsInfo(
        @Query("type") type: String = "",
        @Query("limit") limit: String = ""
    ): Call<NewsList>

    @GET("statistics/get")
    fun getStatisticsInfo(
        @Query("userid") userid: String = ""
    ): Call<StatisticsList>

    /*
    //My server isn't able to collect POST request
    @Headers("Content-Type: application/json;charset=UTF-8")
    @POST("statistics/post")
    fun postStatisticsInfo(
        @Body post: StatisticsPostList
    ): Call<PostResponseList>
    */

    //TODO: remove all "postSomething" methods with "GET", because they should use "POST"
    @GET("statistics/post")
    fun postStatisticsInfo(
        @Query("userid") userid: String,
        @Query("type") type: Int,
        @Query("datetime") datetime: String,
        @Query("correct_answer") correct_answer: String,
        @Query("user_answer") user_answer: String = "",
        @Query("question_id") question_id: Int,
        @Query("milliseconds") milliseconds: Int = 0
    ): Call<PostResponseList>
}