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
import com.saverio.finapp.db.QuizzesModel
import com.saverio.finapp.db.SectionsModel
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
        checkSections("", "")
        checkQuizzes("", "")
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
            ApiClient.getClient.getChaptersInfo(chapter = chapter)
        call.enqueue(object : Callback<ChaptersList> {

            override fun onResponse(
                call: Call<ChaptersList>?,
                response: Response<ChaptersList>?
            ) {
                //println("Response:\n" + response!!.body()!!)

                if (response!!.isSuccessful && response.body() != null) {
                    val chaptersList = response.body()!!

                    if (chaptersSaved.isEmpty()) {
                        //First time download data from server
                        //println("Adding")
                        setVariable(DATETIME_CHAPTERS_PREF, chaptersList.lastUpdate?.datetime)
                        chaptersList.chapters?.forEach {
                            add(ChaptersModel(it.chapter, it.title))
                        }
                        //println("Added")
                    } else {
                        if (datetimeSaved == null || datetimeSaved != chaptersList.lastUpdate?.datetime) {
                            //Data are updated server-side, so I update data saved in the local database
                            //println("Updating")
                            setVariable(DATETIME_CHAPTERS_PREF, chaptersList.lastUpdate?.datetime)

                            var counter = 0
                            chaptersList.chapters?.forEach {
                                if (chaptersSaved[counter].title != it.title) {
                                    update(ChaptersModel(it.chapter, it.title))
                                }
                                counter++
                            }
                            //println("Updated")
                        } else {
                            //println("Already updated")
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

    fun checkSections(chapter: String = "", section: String = "") {
        val sectionsSaved = getSections()

        val datetimeSaved = getVariable(DATETIME_SECTIONS_PREF)
        if (datetimeSaved != null) {
            //println("Saved (Sections): $datetimeSaved")
        } else {
            //println("Not yet saved")
        }

        val call: Call<SectionsList> =
            ApiClient.getClient.getSectionsInfo(chapter = chapter, section = section)
        call.enqueue(object : Callback<SectionsList> {

            override fun onResponse(
                call: Call<SectionsList>?,
                response: Response<SectionsList>?
            ) {
                //println("Response:\n" + response!!.body()!!)

                if (response!!.isSuccessful && response.body() != null) {
                    val sectionsList = response.body()!!

                    if (sectionsSaved.isEmpty()) {
                        //First time download data from server
                        //println("Adding")
                        setVariable(DATETIME_SECTIONS_PREF, sectionsList.lastUpdate?.datetime)
                        sectionsList.sections?.forEach {
                            add(SectionsModel(it.id, it.chapter, it.title, it.author, it.text))
                        }
                        //println("Added")
                    } else {
                        if (datetimeSaved == null || datetimeSaved != sectionsList.lastUpdate?.datetime) {
                            //Data are updated server-side, so I update data saved in the local database
                            //println("Updating")
                            setVariable(DATETIME_SECTIONS_PREF, sectionsList.lastUpdate?.datetime)

                            var counter = 0
                            sectionsList.sections?.forEach {
                                if (sectionsSaved[counter].chapter != it.chapter || sectionsSaved[counter].title != it.title || sectionsSaved[counter].author != it.author || sectionsSaved[counter].text != it.text) {
                                    update(
                                        SectionsModel(
                                            it.id,
                                            it.chapter,
                                            it.title,
                                            it.author,
                                            it.text
                                        )
                                    )
                                }
                                counter++
                            }
                            //println("Updated")
                        } else {
                            //println("Already updated")
                        }
                    }
                }
            }

            override fun onFailure(call: Call<SectionsList>?, t: Throwable?) {
                //progerssProgressDialog.dismiss()
                Log.v("Error", t.toString())
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
            ApiClient.getClient.getQuizzesInfo(chapter = chapter, question = quiz)
        call.enqueue(object : Callback<QuizzesList> {

            override fun onResponse(
                call: Call<QuizzesList>?,
                response: Response<QuizzesList>?
            ) {
                //println("Response:\n" + response!!.body()!!)

                if (response!!.isSuccessful && response.body() != null) {
                    val quizzesList = response.body()!!

                    if (quizzesSaved.isEmpty()) {
                        //First time download data from server
                        //println("Adding")
                        setVariable(DATETIME_QUIZZES_PREF, quizzesList.lastUpdate?.datetime)
                        quizzesList.questions?.forEach {
                            add(
                                QuizzesModel(
                                    it.id,
                                    it.chapter,
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
                        if (datetimeSaved == null || datetimeSaved != quizzesList.lastUpdate?.datetime) {
                            //Data are updated server-side, so I update data saved in the local database
                            //println("Updating")
                            setVariable(DATETIME_QUIZZES_PREF, quizzesList.lastUpdate?.datetime)

                            var counter = 0
                            quizzesList.questions?.forEach {
                                if (quizzesSaved[counter].chapter != it.chapter || quizzesSaved[counter].question != it.question || quizzesSaved[counter].A != it.A || quizzesSaved[counter].B != it.B || quizzesSaved[counter].C != it.C || quizzesSaved[counter].D != it.D || quizzesSaved[counter].correct != it.correct) {
                                    update(
                                        QuizzesModel(
                                            it.id,
                                            it.chapter,
                                            it.question,
                                            it.A,
                                            it.B,
                                            it.C,
                                            it.D,
                                            it.correct
                                        )
                                    )
                                }
                                counter++
                            }
                            //println("Updated")
                        } else {
                            //println("Already updated")
                        }
                    }
                }
            }

            override fun onFailure(call: Call<QuizzesList>?, t: Throwable?) {
                //progerssProgressDialog.dismiss()
                Log.v("Error", t.toString())
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
        if (quiz.id != 0 && quiz.chapter != null && quiz.question != null && quiz.A != null && quiz.B != null && quiz.C != null && quiz.D != null && quiz.correct != null) {
            val status = databaseHandler.addQuiz(quiz)
            if (status > -1) {
                //println("${quiz.id}|${quiz.question} added correctly")
            } else {
                //println("${quiz.id}|${quiz.question} NOT added correctly")
            }
        }
    }

    private fun update(chapter: ChaptersModel) {
        //update || chapter
        val databaseHandler = DatabaseHandler(this)
        if (chapter.chapter != 0 && chapter.title != null) {
            val oldChapter = databaseHandler.getChapter(chapter = chapter.chapter)
            val status = databaseHandler.updateChapter(chapter)
            if (status > -1) {
                //println("${oldChapter.chapter}|${oldChapter.title} updated correctly with ${chapter.chapter}|${chapter.title}")
            } else {
                //println("${chapter.chapter}|${chapter.title} NOT updated correctly")
            }
        }
    }

    private fun update(section: SectionsModel) {
        //update || section
        val databaseHandler = DatabaseHandler(this)
        if (section.section != "" && section.chapter != null && section.title != null && section.author != null && section.text != null) {
            val oldSection = databaseHandler.getSection(section = section.section)
            val status = databaseHandler.updateSection(section)
            if (status > -1) {
                //println("${oldSection.section}|${oldSection.chapter}|${oldSection.title} updated correctly with ${section.section}|${section.chapter}|${section.title}")
            } else {
                //println("${section.section}|${section.chapter}|${section.title} NOT updated correctly")
            }
        }
    }

    private fun update(quiz: QuizzesModel) {
        //update || quiz
        val databaseHandler = DatabaseHandler(this)
        if (quiz.id != 0 && quiz.chapter != null && quiz.question != null && quiz.A != null && quiz.B != null && quiz.C != null && quiz.D != null && quiz.correct != null) {
            val oldQuiz = databaseHandler.getQuiz(id = quiz.id)
            val status = databaseHandler.updateQuiz(quiz)
            if (status > -1) {
                //println("${oldQuiz.id}|${oldQuiz.chapter}|${oldQuiz.question} updated correctly with ${quiz.id}|${quiz.chapter}|${quiz.question}")
            } else {
                //println("${quiz.id}|${quiz.chapter}|${quiz.question} NOT updated correctly")
            }
        }
    }

    fun delete(chapter: ChaptersModel) {
        //delete || chapter
        val databaseHandler = DatabaseHandler(this)
        if (chapter.chapter != 0) {
            val status = databaseHandler.deleteChapter(chapter)
            if (status > -1) {
                //println("${chapter.chapter} deleted correctly")
            } else {
                //println("${chapter.chapter} NOT deleted correctly")
            }
        }
    }

    fun delete(section: SectionsModel) {
        //delete || section
        val databaseHandler = DatabaseHandler(this)
        if (section.section != "") {
            val status = databaseHandler.deleteSection(section)
            if (status > -1) {
                //println("${section.section} deleted correctly")
            } else {
                //println("${section.section} NOT deleted correctly")
            }
        }
    }

    fun delete(quiz: QuizzesModel) {
        //delete || quiz
        val databaseHandler = DatabaseHandler(this)
        if (quiz.id != 0) {
            val status = databaseHandler.deleteQuiz(quiz)
            if (status > -1) {
                //println("${quiz.id} deleted correctly")
            } else {
                //println("${quiz.id} NOT deleted correctly")
            }
        }
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