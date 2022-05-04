package com.saverio.finapp

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.saverio.finapp.api.ApiClient
import com.saverio.finapp.api.PostResponseList
import com.saverio.finapp.api.chapters.ChaptersList
import com.saverio.finapp.api.news.NewsList
import com.saverio.finapp.api.quizzes.QuizzesList
import com.saverio.finapp.api.sections.SectionsList
import com.saverio.finapp.api.statistics.StatisticsList
import com.saverio.finapp.api.statistics.StatisticsPostList
import com.saverio.finapp.databinding.ActivityMainBinding
import com.saverio.finapp.db.*
import com.saverio.finapp.internet.NetworkConnection
import com.saverio.finapp.notification.NotificationReceiver
import com.saverio.finapp.ui.firstrun.FirstRunActivity
import com.saverio.finapp.ui.home.HomeViewModel
import com.saverio.finapp.ui.quiz.MistakesQuizActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import kotlin.collections.ArrayList


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    lateinit var currentFragment: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        navView.setupWithNavController(navController)

        val homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)

        checkFirstRunTutorial()

        /*
        val networkConnection = NetworkConnection(applicationContext)
        networkConnection.observe(this, Observer { isConnected ->
            if (isConnected) {
                //connected
            } else {
                //not connected
            }
        })
        */

        //set scheduled notifications
        scheduleNotifications()

        //check for new messages and eventually new notifications
        val notificationReceiver = NotificationReceiver()
        notificationReceiver.onReceive(this, Intent(this, NotificationReceiver::class.java))
    }

    fun scheduleNotifications() {
        try {
            val notificationIntent = Intent(this, NotificationReceiver::class.java)

            val pendingIntent =
                PendingIntent.getBroadcast(applicationContext, 0, notificationIntent, 0)
            val alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager
            alarmManager.setRepeating(
                AlarmManager.RTC,
                100,
                10000,
                pendingIntent
            )
        } catch (e: Exception) {
            //Exception
        }
    }

    fun allCheckes() {
        val networkConnection = NetworkConnection(applicationContext)
        networkConnection.observe(this, Observer { isConnected ->
            if (isConnected) {
                //connected
                checkChapters("")
                checkSections("", "")
                checkQuizzes("", "")
                checkNews("", "")
                checkStatistics()
            } else {
                //not connected
                //println("No connection available")
            }
        })
    }

    override fun onResume() {
        supportActionBar?.hide()
        super.onResume()
    }

    fun checkLogged(): Boolean {
        return (getVariable("userid") != "" && getVariable("userid") != null)
    }

    fun checkFirstRunTutorial() {
        if (getVariable(FIRST_RUN_APP, default = true)!!) {
            val intent = Intent(this, FirstRunActivity::class.java)
            startActivity(intent) //run FirstRunActivity
            finish() //close MainActivity
        }
    }

    fun setUseridLogged(userid: String) {
        setVariable("userid", userid)
    }

    fun getUserid(): String {
        return (if (checkLogged()) getVariable("userid")!! else "")
    }

    fun pushStatistics() {
        //push all statistics to server
        if (checkLogged()) {
            //logged
            val databaseHandler = DatabaseHandler(this)
            val getStatistics = databaseHandler.getStatistics() //all statistics

            getStatistics.forEach {
                val statisticsToSend = StatisticsPostList(
                    userid = getUserid(),
                    type = it.type,
                    datetime = it.datetime,
                    correct_answer = it.correct_answer!!,
                    user_answer = it.user_answer!!,
                    question_id = it.question_id,
                    milliseconds = it.milliseconds
                )
                sendStatistics(statisticsToSend)
            }
        }
    }

    fun checkStatistics() {
        //check new data from server
        if (checkLogged()) {
            //logged

            val call: Call<StatisticsList> =
                ApiClient.client.getStatisticsInfo(userid = getUserid())
            call.enqueue(object : Callback<StatisticsList> {

                override fun onResponse(
                    call: Call<StatisticsList>?,
                    response: Response<StatisticsList>?
                ) {
                    //println("Response:\n" + response!!.body()!!)

                    if (response!!.isSuccessful && response.body() != null) {
                        val responseList = response.body()!!

                        if (responseList != null) {
                            val databaseHandler = DatabaseHandler(this@MainActivity)

                            responseList.statistics!!.forEach {
                                val statistics = StatisticsModel(
                                    id = databaseHandler.getNewIdStatistics(),
                                    type = it.type,
                                    question_id = it.question_id,
                                    datetime = it.datetime,
                                    correct_answer = it.correct_answer,
                                    user_answer = it.user_answer,
                                    milliseconds = it.milliseconds
                                )
                                if (databaseHandler.checkStatistics(it.question_id, it.type)) {
                                    //update
                                    databaseHandler.updateStatistics(statistics)
                                } else {
                                    //insert
                                    databaseHandler.addStatistics(statistics)
                                }
                            }
                        }
                    }
                }

                override fun onFailure(call: Call<StatisticsList>?, t: Throwable?) {
                    //progerssProgressDialog.dismiss()
                    Log.v("Error", t.toString())
                }

            })
        }
    }

    fun sendStatistics(statistics: StatisticsPostList) {
        val call: Call<PostResponseList> =
            ApiClient.client.postStatisticsInfo(
                userid = statistics.userid,
                type = statistics.type,
                datetime = statistics.datetime,
                correct_answer = statistics.correct_answer,
                user_answer = statistics.user_answer,
                question_id = statistics.question_id,
                milliseconds = statistics.milliseconds
            )
        call.enqueue(object : Callback<PostResponseList> {

            override fun onResponse(
                call: Call<PostResponseList>?,
                response: Response<PostResponseList>?
            ) {
                //println("Response:\n" + response!!.body()!!)

                if (response!!.isSuccessful && response.body() != null) {
                    val responseList = response.body()!!

                    if (responseList.code == 200) {
                        //("${responseList.code} || ${responseList.description}")
                    } else {
                        Log.v("Error", responseList.description)
                    }
                }
            }

            override fun onFailure(call: Call<PostResponseList>?, t: Throwable?) {
                Log.v("Error", t.toString())
            }

        })
    }

    fun checkChapters(chapter: String = "") {
        val chaptersSaved = getChapters()

        val datetimeSaved = getVariable(DATETIME_CHAPTERS_PREF)
        if (datetimeSaved != null) {
            //println("Saved (Chapters): $datetimeSaved")
        } else {
            //println("Not yet saved")
        }

        val call: Call<ChaptersList> =
            ApiClient.client.getChaptersInfo(chapter = chapter)
        call.enqueue(object : Callback<ChaptersList> {

            override fun onResponse(
                call: Call<ChaptersList>?,
                response: Response<ChaptersList>?
            ) {
                //println("Response:\n" + response!!.body()!!)

                if (response!!.isSuccessful && response.body() != null) {
                    val chaptersList = response.body()!!

                    if (datetimeSaved == null || datetimeSaved != chaptersList.lastUpdate?.datetime) {
                        //Data are updated server-side, so I update data saved in the local database
                        //println("Updating")
                        deleteAllChapters()
                        setVariable(DATETIME_CHAPTERS_PREF, chaptersList.lastUpdate?.datetime)
                        chaptersList.chapters?.forEachIndexed { counter, it ->
                            add(ChaptersModel(it.chapter, it.title))
                        }
                        //println("Updated")
                    } else {
                        //println("Already updated")
                    }
                }
            }

            override fun onFailure(call: Call<ChaptersList>?, t: Throwable?) {
                //progerssProgressDialog.dismiss()
                Log.v("Error", t.toString())
            }

        })
    }

    fun checkSections(chapter: String = "", section: String = "") {
        val sectionsSaved = getSections()

        val datetimeSaved = getVariable(DATETIME_SECTIONS_PREF)
        if (datetimeSaved != null) {
            //println("Saved (Sections): $datetimeSaved")
        } else {
            //println("Not yet saved")
        }

        val call: Call<SectionsList> =
            ApiClient.client.getSectionsInfo(chapter = chapter, section = section)
        call.enqueue(object : Callback<SectionsList> {

            override fun onResponse(
                call: Call<SectionsList>?,
                response: Response<SectionsList>?
            ) {
                //println("Response:\n" + response!!.body()!!)

                if (response!!.isSuccessful && response.body() != null) {
                    val sectionsList = response.body()!!

                    if (datetimeSaved == null || datetimeSaved != sectionsList.lastUpdate?.datetime) {
                        //Data are updated server-side, so I update data saved in the local database
                        //println("Updating")
                        deleteAllSections()
                        setVariable(DATETIME_SECTIONS_PREF, sectionsList.lastUpdate?.datetime)
                        sectionsList.sections?.forEachIndexed { counter, it ->
                            add(
                                SectionsModel(
                                    it.id,
                                    it.chapter,
                                    it.title,
                                    it.author,
                                    it.text
                                )
                            )
                        }
                        //println("Updated")
                    } else {
                        //println("Already updated")
                    }
                }
            }

            override fun onFailure(call: Call<SectionsList>?, t: Throwable?) {
                //progerssProgressDialog.dismiss()
                Log.v("Error", t.toString())
                setVariable(DATETIME_SECTIONS_PREF, "")
            }

        })
    }

    fun checkQuizzes(chapter: String = "", quiz: String = "") {
        val quizzesSaved = getQuizzes()

        val datetimeSaved = getVariable(DATETIME_QUIZZES_PREF)
        if (datetimeSaved != null) {
            //println("Saved (Quizzes): $datetimeSaved")
        } else {
            //println("Not yet saved")
        }

        val call: Call<QuizzesList> =
            ApiClient.client.getQuizzesInfo(chapter = chapter, question = quiz)
        call.enqueue(object : Callback<QuizzesList> {

            override fun onResponse(
                call: Call<QuizzesList>?,
                response: Response<QuizzesList>?
            ) {
                //println("Response:\n" + response!!.body()!!)

                if (response!!.isSuccessful && response.body() != null) {
                    val quizzesList = response.body()!!

                    if (datetimeSaved == null || datetimeSaved != quizzesList.lastUpdate?.datetime) {
                        //First time download data from server
                        //println("Adding")
                        deleteAllQuizzes()
                        setVariable(DATETIME_QUIZZES_PREF, quizzesList.lastUpdate?.datetime)
                        quizzesList.questions?.forEach {
                            add(
                                QuizzesModel(
                                    it.id,
                                    it.chapter,
                                    it.section,
                                    it.question,
                                    it.A,
                                    it.B,
                                    it.C,
                                    it.D,
                                    it.correct
                                )
                            )
                        }
                        //println("Added")
                    } else {
                        //println("Already updated")
                    }
                }
            }

            override fun onFailure(call: Call<QuizzesList>?, t: Throwable?) {
                //progerssProgressDialog.dismiss()
                Log.v("Error", t.toString())
            }

        })
    }

    fun checkNews(type: String = "", limit: String = "") {
        val newsSaved = getNews()

        val datetimeSaved = getVariable(DATETIME_NEWS_PREF)
        if (datetimeSaved != null) {
            //println("Saved (News): $datetimeSaved")
        } else {
            //println("Not yet saved")
        }
        val homeViewModel =
            ViewModelProvider(this@MainActivity).get(HomeViewModel::class.java)

        homeViewModel.setLoading(load = true)
        //println("Started loading")

        val call: Call<NewsList> =
            ApiClient.client.getNewsInfo(type = type, limit = limit)
        call.enqueue(object : Callback<NewsList> {

            override fun onResponse(
                call: Call<NewsList>?,
                response: Response<NewsList>?
            ) {
                //println("Response:\n" + response!!.body()!!)
                homeViewModel.setLoading(load = false)
                //println("Loaded")
                if (response!!.isSuccessful && response.body() != null) {
                    val newsList = response.body()!!

                    if (datetimeSaved == null || datetimeSaved != newsList.lastUpdate?.datetime) {
                        //Data are updated server-side, so I update data saved in the local database
                        //println("Updating")
                        deleteAllNews()
                        setVariable(DATETIME_NEWS_PREF, newsList.lastUpdate?.datetime)

                        newsList.news?.forEachIndexed { counter, it ->
                            add(
                                NewsModel(
                                    it.id,
                                    it.type,
                                    it.date,
                                    it.title,
                                    it.image,
                                    it.link,
                                    it.text
                                )
                            )
                        }
                        //println("Updated")
                        homeViewModel.setNewsChanged(changed = true)
                        //println("Changing")
                    } else {
                        //println("Already updated")
                        homeViewModel.setNewsChanged(changed = false)
                        //println("(Not) changing")
                    }
                }
            }

            override fun onFailure(call: Call<NewsList>?, t: Throwable?) {
                //progerssProgressDialog.dismiss()
                Log.v("Error", t.toString())
                homeViewModel.setLoading(load = false)
                //println("Loaded")
            }

        })
    }

    private fun add(chapter: ChaptersModel) {
        //add || chapter
        val databaseHandler = DatabaseHandler(this)
        if (chapter.chapter != 0 && chapter.title != null) {
            val status = databaseHandler.addChapter(chapter)
            if (status > -1) {
                //println("${chapter.chapter}|${chapter.title} added correctly")
            } else {
                //println("${chapter.chapter}|${chapter.title} NOT added correctly")
            }
        }
    }

    private fun add(section: SectionsModel) {
        //add || section
        val databaseHandler = DatabaseHandler(this)
        if (section.section != "" && section.chapter != null && section.title != null && section.author != null && section.text != null) {
            val status = databaseHandler.addSection(section)
            if (status > -1) {
                //println("${section.section}|${section.chapter}|${section.author}|${section.title} added correctly")
            } else {
                //println("${section.section}|${section.title} NOT added correctly")
            }
        }
    }

    private fun add(quiz: QuizzesModel) {
        //add || quiz
        val databaseHandler = DatabaseHandler(this)
        if (quiz.id != 0 && quiz.chapter != null && quiz.section != null && quiz.question != null && quiz.A != null && quiz.B != null && quiz.C != null && quiz.D != null && quiz.correct != null) {
            val status = databaseHandler.addQuiz(quiz)
            if (status > -1) {
                //println("${quiz.id}|${quiz.question} added correctly")
            } else {
                //println("${quiz.id}|${quiz.question} NOT added correctly")
            }
        }
    }

    private fun add(news: NewsModel) {
        //add || news
        val databaseHandler = DatabaseHandler(this)
        if (news.id != 0 && news.type != -1 && news.text != "") {
            val status = databaseHandler.addNews(news)
            if (status > -1) {
                //println("${news.id}|${news.type}|${news.text} added correctly")
            } else {
                //println("${news.id}|${news.type}|${news.text} NOT added correctly")
            }
        }
    }

    fun deleteAllChapters() {
        //delete || chapter
        val databaseHandler = DatabaseHandler(this)
        databaseHandler.deleteAllStatistics()
        databaseHandler.close()
    }

    fun deleteAllSections() {
        //delete || sections
        val databaseHandler = DatabaseHandler(this)
        databaseHandler.deleteAllSections()
        databaseHandler.close()
    }

    fun deleteAllQuizzes() {
        //delete || quizzes
        val databaseHandler = DatabaseHandler(this)
        databaseHandler.deleteAllQuizzes()
        databaseHandler.close()
    }

    fun deleteAllNews() {
        //delete || news
        val databaseHandler = DatabaseHandler(this)
        databaseHandler.deleteAllNews()
        databaseHandler.close()
    }

    fun getChapters(): ArrayList<ChaptersModel> {
        //get all chapters
        val databaseHandler = DatabaseHandler(this)
        return databaseHandler.getChapters()
    }

    fun getSections(): ArrayList<SectionsModel> {
        //get all chapters
        val databaseHandler = DatabaseHandler(this)
        return databaseHandler.getSections()
    }

    fun getQuizzes(): ArrayList<QuizzesModel> {
        //get all chapters
        val databaseHandler = DatabaseHandler(this)
        return databaseHandler.getQuizzes()
    }

    fun getNews(): ArrayList<NewsModel> {
        //get all news
        val databaseHandler = DatabaseHandler(this)
        return databaseHandler.getNews()
    }

    fun checkNews(id: Int): Boolean {
        //get all news
        val databaseHandler = DatabaseHandler(this)
        return databaseHandler.checkNews(id = id)
    }

    private fun setVariable(variable: String, value: String?) {
        getSharedPreferences("QuizNuotoPreferences", Context.MODE_PRIVATE).edit()
            .putString(variable, value).apply()
    }

    private fun setVariable(variable: String, value: Boolean = false) {
        getSharedPreferences("QuizNuotoPreferences", Context.MODE_PRIVATE).edit()
            .putBoolean(variable, value).apply()
    }

    private fun getVariable(variable: String): String? {
        return getSharedPreferences(
            "QuizNuotoPreferences",
            Context.MODE_PRIVATE
        ).getString(variable, null)
    }

    private fun getVariable(variable: String, default: Boolean = false): Boolean? {
        return getSharedPreferences("QuizNuotoPreferences", Context.MODE_PRIVATE).getBoolean(
            variable,
            default
        )
    }

    companion object {
        const val DATETIME_CHAPTERS_PREF = "DATETIME_CHAPTERS_PREF"
        const val DATETIME_SECTIONS_PREF = "DATETIME_SECTIONS_PREF"
        const val DATETIME_QUIZZES_PREF = "DATETIME_QUIZZES_PREF"
        const val DATETIME_NEWS_PREF = "DATETIME_NEWS_PREF"
        const val FIRST_RUN_APP = "FIRST_RUN_APP"
    }
}