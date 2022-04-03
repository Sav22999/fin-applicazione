package com.saverio.finapp

import android.os.Bundle
import android.util.Log
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.saverio.finapp.api.ApiClient
import com.saverio.finapp.api.chapters.*
import com.saverio.finapp.api.quizzes.QuizzesList
import com.saverio.finapp.api.sections.SectionsList
import com.saverio.finapp.databinding.ActivityMainBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    public var chaptersList = ChaptersList()
    public var sectionsList = SectionsList()
    public var quizzesList = QuizzesList()

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

        getChapters("")
        //getSections()
        //getQuizzes()
    }

    public fun getChapters(chapter: String = "") {
        val call: Call<ChaptersList> =
            ApiClient.getClient.getChaptersInfo(chapter = chapter)
        call.enqueue(object : Callback<ChaptersList> {

            override fun onResponse(
                call: Call<ChaptersList>?,
                response: Response<ChaptersList>?
            ) {
                println("Response:\n" + response!!.body()!!)

                if (response != null && response.isSuccessful && response.body() != null) {
                    val data = response.body()
                    println(data!!.lastUpdate!!.datetime)
                    data!!.chapters!!.forEach {
                        println(it.title)
                    }
                    /*progerssProgressDialog.dismiss()
                    recyclerView.adapter.notifyDataSetChanged()*/
                    //println("Set!")
                }
            }

            override fun onFailure(call: Call<ChaptersList>?, t: Throwable?) {
                //progerssProgressDialog.dismiss()
                Log.v(
                    "Error",
                    t.toString() + "\n----------------\n" + call.toString() + "\n----------------\n"
                )
            }

        })
    }

    private fun getSections() {
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
}