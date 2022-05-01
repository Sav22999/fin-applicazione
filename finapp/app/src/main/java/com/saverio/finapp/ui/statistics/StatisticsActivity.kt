package com.saverio.finapp.ui.statistics

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isGone
import androidx.core.view.updateLayoutParams
import com.saverio.finapp.R
import com.saverio.finapp.db.DatabaseHandler

class StatisticsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_statistics)

        loadBar()

        val actionBar = getSupportActionBar()
        if (actionBar != null) {
            //show the back button in the action bar
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.title = ""
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        //add the back button event in the actionbar
        when (item.getItemId()) {
            android.R.id.home -> {
                onBackPressed()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        finish()
        super.onBackPressed()
    }

    fun loadBar() {
        val bar: CardView = findViewById(R.id.cardViewStatisticsBar)
        val correct: View = findViewById(R.id.statisticsBarCorrectAnswers)
        val skipped: View = findViewById(R.id.statisticsBarSkippedQuestions)
        val wrong: View = findViewById(R.id.statisticsBarWrongAnswers)

        val databaseHandler = DatabaseHandler(this)

        val noSimulation: TextView = findViewById(R.id.no_simulation_done_text)
        val showHideButton: TextView = findViewById(R.id.show_hide_details_statistics_text)

        val correctAnswers =
            databaseHandler.getWrongCorrectSkippedAnswersStatistics(filter = "c").size
        val skippedQuestions =
            databaseHandler.getWrongCorrectSkippedAnswersStatistics(filter = "s").size
        val wrongAnswers =
            databaseHandler.getWrongCorrectSkippedAnswersStatistics(filter = "w").size
        val totalAnswers = correctAnswers + skippedQuestions + wrongAnswers
        if (totalAnswers > 0) {
            val widthBar = (getScreenWidth() / 2) - 25

            val widthCorrect = (widthBar * correctAnswers) / totalAnswers
            val widthSkipped = (widthBar * skippedQuestions) / totalAnswers
            val widthWrong = (widthBar * wrongAnswers) / totalAnswers
            correct.updateLayoutParams { width = widthCorrect }
            skipped.updateLayoutParams { width = widthSkipped }
            //wrong.updateLayoutParams { width = widthWrong }

            correct.isGone = widthCorrect == 0
            skipped.isGone = widthSkipped == 0
            wrong.isGone = widthWrong == 0
            noSimulation.isGone = true
            showHideButton.isGone = false

            /*
            val percentCorrect = (100 * correctAnswers) / totalAnswers
            val percentSkipped = (100 * skippedQuestions) / totalAnswers
            val percentWrong = (100 * wrongAnswers) / totalAnswers
            */

            findViewById<TextView>(R.id.textViewTotalQuestionsStatistics).text = getString(
                R.string.total_questions_details_statistics_text, totalAnswers
            )
            findViewById<TextView>(R.id.textViewCorrectAnswersStatistics).text = getString(
                R.string.correct_answers_details_statistics_text, correctAnswers
            )
            findViewById<TextView>(R.id.textViewSkippedQuestionsStatistics).text = getString(
                R.string.skipped_questions_details_statistics_text, skippedQuestions
            )
            findViewById<TextView>(R.id.textViewWrongAnswersStatistics).text = getString(
                R.string.wrong_answers_details_statistics_text, wrongAnswers
            )

            showHideButton.setOnClickListener {
                if (findViewById<ConstraintLayout>(R.id.layoutConstraintDetailsStatistics).isGone) {
                    //show
                    findViewById<TextView>(R.id.show_hide_details_statistics_text).text =
                        getString(R.string.hide_details_text)
                    findViewById<ConstraintLayout>(R.id.layoutConstraintDetailsStatistics).isGone =
                        false
                } else {
                    //hide
                    findViewById<TextView>(R.id.show_hide_details_statistics_text).text =
                        getString(R.string.show_details_text)
                    findViewById<ConstraintLayout>(R.id.layoutConstraintDetailsStatistics).isGone =
                        true
                }
            }
        } else {
            noSimulation.isGone = false
            showHideButton.isGone = true
            findViewById<ConstraintLayout>(R.id.layoutConstraintDetailsStatistics).isGone = true
            findViewById<CardView>(R.id.cardViewStatisticsBar).isGone = true
            findViewById<ConstraintLayout>(R.id.constraintLayoutLegendStatistics).isGone = true
        }

        val cardViewStatisticsViewAllSimulations: CardView =
            findViewById(R.id.cardViewStatisticsViewAllSimulations)
        cardViewStatisticsViewAllSimulations.setOnClickListener {
            val intent = Intent(this, AllSimulations::class.java)
            startActivity(intent)
        }
    }

    fun getScreenWidth(): Int {
        val displayMetrics = DisplayMetrics()
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics)
        return displayMetrics.heightPixels
    }
}