package com.saverio.finapp.ui.quiz

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isGone
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.saverio.finapp.R
import com.saverio.finapp.db.DatabaseHandler
import com.saverio.finapp.db.StatisticsModel

class ResultsSimulationQuizActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_results_simulation_quiz)

        val bundle = intent.extras

        var datetimeSimulation = ""

        val MAX_TIME: Int = 7200 //2 hours in seconds (2*60*60)=7200

        if (bundle != null) {
            datetimeSimulation = bundle.getString("datetime")!!
        }

        if (datetimeSimulation != "") {
            val databaseHandler = DatabaseHandler(this)
            val getStatistics = databaseHandler.getStatistics(
                type = 1,
                datetime = datetimeSimulation
            )
            setupRecyclerView(clear = true, search = false, getStatistics = getStatistics)
            val testPassedOrNot: TextView = findViewById(R.id.test_passed_or_not_text)
            val getWrongAnswersStatistics =
                databaseHandler.getWrongCorrectSkippedAnswersStatistics(datetimeSimulation, "w")
            val getSkippedAnswersStatistics =
                databaseHandler.getWrongCorrectSkippedAnswersStatistics(datetimeSimulation, "s")
            val getCorrectAnswersStatistics =
                databaseHandler.getWrongCorrectSkippedAnswersStatistics(datetimeSimulation, "c")
            val getWrongAndSkippedAnswersStatistics =
                databaseHandler.getWrongCorrectSkippedAnswersStatistics(datetimeSimulation, "ws")
            if (getWrongAndSkippedAnswersStatistics.size < 5) {
                testPassedOrNot.text = getString(R.string.exam_passed_text)
                testPassedOrNot.setTextColor(resources.getColor(R.color.colorWhite))
                testPassedOrNot.setBackgroundColor(resources.getColor(R.color.colorTimeGreen))
            } else {
                testPassedOrNot.text = getString(R.string.exam_not_passed_text)
                testPassedOrNot.setTextColor(resources.getColor(R.color.colorWhite))
                testPassedOrNot.setBackgroundColor(resources.getColor(R.color.colorTimeRed))
            }
            testPassedOrNot.isGone = false

            val showHideDetailsResults: TextView = findViewById(R.id.show_hide_details_results_text)
            val layoutDetails: ConstraintLayout = findViewById(R.id.layoutConstraintDetailsResults)
            showHideDetailsResults.setOnClickListener {
                if (layoutDetails.isGone) {
                    layoutDetails.isGone = false
                    showHideDetailsResults.text = getString(R.string.hide_details_results_text)
                } else {
                    layoutDetails.isGone = true
                    showHideDetailsResults.text = getString(R.string.show_details_results_text)
                }
            }

            val timeSpent: TextView = findViewById(R.id.textViewTimeUsedResults)
            val timeResidual: TextView = findViewById(R.id.textViewTimeResidualResults)
            val timeEstimated: TextView = findViewById(R.id.textViewEstimatedTimeResults)
            val totalQuestions: TextView = findViewById(R.id.textViewTotalQuestionsResults)
            val correctAnswers: TextView = findViewById(R.id.textViewCorrectAnswersResults)
            val wrongAnswers: TextView = findViewById(R.id.textViewWrongAnswersResults)
            val skippedQuestions: TextView = findViewById(R.id.textViewSkippedQuestionsResults)
            val datetime: TextView = findViewById(R.id.textViewDateResults)

            timeSpent.text = getString(
                R.string.time_spent_details_results_text,
                getTimeFormatted(getStatistics[0].milliseconds)
            )
            timeResidual.text = getString(
                R.string.time_residual_details_results_text,
                getTimeFormatted(MAX_TIME - getStatistics[0].milliseconds)
            )
            timeEstimated.text = getString(
                R.string.estimated_time_per_question_details_results_text,
                getTimeFormattedSingle(getStatistics[0].milliseconds / getStatistics.size)
            )
            totalQuestions.text =
                getString(R.string.total_questions_details_results_text, getStatistics.size)
            correctAnswers.text = getString(
                R.string.correct_answers_details_results_text,
                getCorrectAnswersStatistics.size
            )
            wrongAnswers.text = getString(
                R.string.wrong_answers_details_results_text,
                getWrongAnswersStatistics.size
            )
            skippedQuestions.text = getString(
                R.string.skipped_questions_details_results_text,
                getSkippedAnswersStatistics.size
            )
            datetime.text = getString(R.string.datetime_details_results_text, datetimeSimulation)
        } else {
            val noResults: TextView = findViewById(R.id.no_results_available_text)
            noResults.text = getString(R.string.results_error_text)
            noResults.visibility = View.VISIBLE
            val testPassedOrNot: TextView = findViewById(R.id.test_passed_or_not_text)
            testPassedOrNot.isGone = true
        }

        val actionBar = getSupportActionBar()
        if (actionBar != null) {
            //show the back button in the action bar
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.title = getString(R.string.title_simulation_results)
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

    fun setupRecyclerView(
        clear: Boolean = false,
        search: Boolean = false,
        getStatistics: ArrayList<StatisticsModel>
    ) {
        val resultsItemsList: RecyclerView = findViewById(R.id.simulation_results_items_list)
        val noResults: TextView = findViewById(R.id.no_results_available_text)

        noResults.text = getString(R.string.no_results_available_text)

        if (clear) resultsItemsList.adapter = null

        if (getStatistics.size > 0) {
            noResults.visibility = View.GONE
            resultsItemsList.visibility = View.VISIBLE

            resultsItemsList.layoutManager = LinearLayoutManager(this)
            //binding.newsItemsList.setHasFixedSize(true)
            val itemAdapter = ResultSimulationItemAdapter(this, getStatistics)
            resultsItemsList.adapter = itemAdapter
        } else {
            noResults.visibility = View.VISIBLE
            resultsItemsList.visibility = View.GONE
        }
    }

    fun getTimeFormatted(time: Int): String {
        var timeToReturn: String = ""
        if (time < 60) {
            //seconds
            timeToReturn = "00:00:${getValueWithZero(time)}"
        } else if (time < (60 * 60)) {
            //minutes and seconds
            timeToReturn =
                "00:${getValueWithZero(time / 60)}:${getValueWithZero(time % 60)}"
        } else {
            //hours, minutes and seconds
            timeToReturn =
                "${getValueWithZero(time / (60 * 60))}:${getValueWithZero((time - 60 * 60) / 60)}:${
                    getValueWithZero(
                        time % 60
                    )
                }"
        }
        return timeToReturn
    }

    fun getTimeFormattedSingle(time: Int): String {
        var timeToReturn: String = ""
        if (time < 60) {
            //seconds
            timeToReturn = resources.getQuantityString(R.plurals.seconds_text, time, time)
        } else {
            //minutes
            timeToReturn = resources.getQuantityString(R.plurals.minutes_text, time / 60, time / 60)
        }
        return timeToReturn
    }

    fun getValueWithZero(value: Int): String {
        return if (value < 10) "0$value" else value.toString()
    }
}