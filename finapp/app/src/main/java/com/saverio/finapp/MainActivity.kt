package com.saverio.finapp

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.saverio.finapp.api.ApiClient
import com.saverio.finapp.api.chapters.ChaptersList
import com.saverio.finapp.api.quizzes.QuizzesList
import com.saverio.finapp.api.sections.SectionsList
import com.saverio.finapp.databinding.ActivityMainBinding
import com.saverio.finapp.db.ChaptersModel
import com.saverio.finapp.db.DatabaseHandler
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home,
                R.id.navigation_theory,
                R.id.navigation_quiz,
                R.id.navigation_account,
                R.id.navigation_settings
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        checkChapters("")
        //getSections()
        //getQuizzes()
    }

    fun checkChapters(chapter: String = "") {
        val chaptersSaved = getChapters()

        val datetimeSaved = getVariable(DATETIME_CHAPTERS_PREF)
        if (datetimeSaved != null) {
            println("Saved: $datetimeSaved")
        } else {
            println("Not yet saved")
        }

        val call: Call<ChaptersList> =
            ApiClient.getClient.getChaptersInfo(chapter = chapter)
        call.enqueue(object : Callback<ChaptersList> {

            override fun onResponse(
                call: Call<ChaptersList>?,
                response: Response<ChaptersList>?
            ) {
                println("Response:\n" + response!!.body()!!)

                if (response.isSuccessful && response.body() != null) {
                    val chaptersList = response.body()!!

                    if (chaptersSaved.isEmpty()) {
                        //First time download data from server
                        println("Adding")
                        setVariable(DATETIME_CHAPTERS_PREF, chaptersList.lastUpdate?.datetime)
                        chaptersList.chapters?.forEach {
                            add(ChaptersModel(it.chapter, it.title))
                        }
                        println("Added")
                    } else {
                        if (datetimeSaved == null || datetimeSaved != chaptersList.lastUpdate?.datetime) {
                            //Data are updated server-side, so I update data saved in the local database
                            println("Updating")
                            setVariable(DATETIME_CHAPTERS_PREF, chaptersList.lastUpdate?.datetime)

                            var counter = 0
                            chaptersList.chapters?.forEach {
                                if (chaptersSaved[counter].title != it.title) {
                                    update(ChaptersModel(it.chapter, it.title))
                                }
                                counter++
                            }
                            println("Updated")
                        } else {
                            println("Already updated")
                        }
                    }
                }
            }

            override fun onFailure(call: Call<ChaptersList>?, t: Throwable?) {
                //progerssProgressDialog.dismiss()
                Log.v("Error", t.toString())
            }

        })
    }

    private fun getSections() {
        var sectionsList = SectionsList()
        val call: Call<SectionsList> = ApiClient.getClient.getSectionsInfo()
        call.enqueue(object : Callback<SectionsList> {

            override fun onResponse(
                call: Call<SectionsList>?,
                response: Response<SectionsList>?
            ) {
                //sectionsList.addAll(response!!.body()!!)
                /*progerssProgressDialog.dismiss()
                recyclerView.adapter.notifyDataSetChanged()*/
            }

            override fun onFailure(call: Call<SectionsList>?, t: Throwable?) {
                //progerssProgressDialog.dismiss()
            }

        })
    }

    private fun getQuizzes() {
        var quizzesList = QuizzesList()
        val call: Call<QuizzesList> = ApiClient.getClient.getQuizzesInfo()
        call.enqueue(object : Callback<QuizzesList> {

            override fun onResponse(
                call: Call<QuizzesList>?,
                response: Response<QuizzesList>?
            ) {
                //quizzesList.addAll(response!!.body()!!)
                /*progerssProgressDialog.dismiss()
                recyclerView.adapter.notifyDataSetChanged()*/
            }

            override fun onFailure(call: Call<QuizzesList>?, t: Throwable?) {
                //progerssProgressDialog.dismiss()
            }
        })
    }

    private fun add(chapter: ChaptersModel) {
        val databaseHandler = DatabaseHandler(this)
        if (chapter.chapter != 0 && chapter.title != "") {
            val status = databaseHandler.addChapter(chapter)
            if (status > -1) {
                println("${chapter.chapter}|${chapter.title} added correctly")
            } else {
                println("${chapter.chapter}|${chapter.title} NOT added correctly")
            }
        }
    }

    private fun update(chapter: ChaptersModel) {
        val databaseHandler = DatabaseHandler(this)
        if (chapter.chapter != 0 && chapter.title != "") {
            val oldChapter = databaseHandler.getChapter(chapter = chapter.chapter)
            val status = databaseHandler.updateChapter(chapter)
            if (status > -1) {
                println("${oldChapter.chapter}|${oldChapter.title} updated correctly with ${chapter.chapter}|${chapter.title}")
            } else {
                println("${chapter.chapter}|${chapter.title} NOT updated correctly")
            }
        }
    }

    fun getChapters(): ArrayList<ChaptersModel> {
        val databaseHandler = DatabaseHandler(this)
        return databaseHandler.getChapters()
    }

    fun delete(chapter: ChaptersModel) {
        val databaseHandler = DatabaseHandler(this)
        if (chapter.chapter != 0 && chapter.title != "") {
            val status = databaseHandler.deleteChapter(chapter)
            if (status > -1) {
                println("${chapter.chapter} deleted correctly")
            } else {
                println("${chapter.chapter} NOT deleted correctly")
            }
        }
    }

    private fun setVariable(variable: String, value: String?) {
        getPreferences(MODE_PRIVATE).edit().putString(variable, value).apply()
    }

    private fun getVariable(variable: String): String? {
        return getPreferences(MODE_PRIVATE).getString(variable, null)
    }

    companion object {
        const val DATETIME_CHAPTERS_PREF = "DATETIME_CHAPTERS_PREF"
        const val DATETIME_SECTIONS_PREF = "DATETIME_SECTIONS_PREF"
        const val DATETIME_QUIZZES_PREF = "DATETIME_QUIZZES_PREF"
    }
}