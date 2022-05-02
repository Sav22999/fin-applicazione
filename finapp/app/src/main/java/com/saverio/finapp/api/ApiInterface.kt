package com.saverio.finapp.api

import com.google.gson.annotations.SerializedName
import com.saverio.finapp.api.chapters.ChaptersList
import com.saverio.finapp.api.messages.AllMessagesList
import com.saverio.finapp.api.messages.MessagePostList
import com.saverio.finapp.api.messages.MessagesSectionsList
import com.saverio.finapp.api.news.NewsList
import com.saverio.finapp.api.quizzes.QuizzesList
import com.saverio.finapp.api.sections.SectionsList
import com.saverio.finapp.api.statistics.StatisticsList
import com.saverio.finapp.api.statistics.StatisticsPostList
import com.saverio.finapp.api.users.UsersItemsList
import com.saverio.finapp.api.users.UsersList
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

    @Headers("Content-Type: application/json;charset=UTF-8")
    @POST("users/signup")
    fun signupUserInfo(
        @Body post: SignupPostList
    ): Call<PostResponseList>

    @Headers("Content-Type: application/json;charset=UTF-8")
    @POST("users/login")
    fun loginUserInfo(
        @Body post: LoginPostList
    ): Call<PostResponseList>

    @Headers("Content-Type: application/json;charset=UTF-8")
    @POST("users/reset-password")
    fun resetPasswordUserInfo(
        @Body post: ResetPasswordPostList
    ): Call<PostResponseList>

    @Headers("Content-Type: application/json;charset=UTF-8")
    @POST("messages/post")
    fun postMessageInfo(
        @Body post: MessagePostList
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

    @GET("users/signup")
    fun signupUserInfo(
        @Query("username") username: String,
        @Query("name") name: String,
        @Query("surname") surname: String,
        @Query("email") email: String = "",
        @Query("password") password: String = "",
        @Query("born") born: String
    ): Call<PostResponseList>

    @GET("users/login")
    fun loginUserInfo(
        @Query("username_or_email") username_or_email: String,
        @Query("password") password: String
    ): Call<PostResponseList>

    @GET("users/reset-password")
    fun resetPasswordUserInfo(
        @Query("username_or_email") username_or_email: String,
        @Query("password") password: String
    ): Call<PostResponseList>

    @GET("users/get")
    fun getUserDetailsInfo(
        @Query("userid") userid: String
    ): Call<UsersList>

    @GET("messages/post")
    fun postMessageInfo(
        @Query("userid") userid: String,
        @Query("reply_to") reply_to: Int = -1,
        @Query("section") section: String,
        @Query("text") text: String
    ): Call<PostMessageResponseList>

    @GET("messages/get")
    fun getAllMessagesInfo(
        @Query("userid") userid: String
    ): Call<AllMessagesList>

    @GET("messages/section/get")
    fun getUserMessagesSectionsInfo(
        @Query("userid") userid: String
    ): Call<MessagesSectionsList>
}