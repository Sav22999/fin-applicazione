package com.saverio.finapp.ui.quiz

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.res.ColorStateList
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.DisplayMetrics
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.*
import androidx.core.widget.NestedScrollView
import com.saverio.finapp.MainActivity
import com.saverio.finapp.MainActivity.Companion.FIRST_RUN_SIMULATION
import com.saverio.finapp.MainActivity.Companion.PREFERENCES_NAME
import com.saverio.finapp.R
import com.saverio.finapp.api.ApiClient
import com.saverio.finapp.api.PostResponseList
import com.saverio.finapp.api.statistics.StatisticsPostList
import com.saverio.finapp.db.DatabaseHandler
import com.saverio.finapp.db.SimulationQuizzesModel
import com.saverio.finapp.db.StatisticsModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class SimulationQuizActivity : AppCompatActivity() {
    var questionsLayout = arrayOf<ConstraintLayout>()
    var questionsImage = arrayOf<ImageView>()
    var questions = arrayOf<TextView>()

    val correctListLettToNum = mapOf("A" to 0, "B" to 1, "C" to 2, "D" to 3)
    val correctListNumToLett = mapOf(0 to "A", 1 to "B", 2 to "C", 3 to "D")

    var questionsSimulation = ArrayList<SimulationQuizzesModel>()

    var simulationDatetime: String = ""
    val MAX_TIME: Int = 7200 //2 hours in seconds (2*60*60)=7200
    var timePassed: Int = 0
    var timeStopped = true
    var timeFinished = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_simulation_quiz)

        checkFirstRun()

        val actionBar = getSupportActionBar()
        if (actionBar != null) {
            //show the back button in the action bar
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.title = ""
        }
    }

    fun firstRun(number: Int) {
        val buttonNext: Button = findViewById(R.id.buttonNextFirstRunSimulation)
        val viewFocus: View = findViewById(R.id.viewFirstRunSimulation)
        val text: TextView = findViewById(R.id.textViewFirstRunSimulation)
        buttonNext.setOnClickListener { firstRun(number + 1) }
        val buttonSkip: TextView = findViewById(R.id.skipFirstRunSimulation)
        buttonSkip.setOnClickListener {
            setVariable(FIRST_RUN_SIMULATION, false)
            checkFirstRun()
        }

        val textViewTimePassed: TextView = findViewById(R.id.textViewTimePassed)
        textViewTimePassed.isGone = false
        textViewTimePassed.text =
            getString(R.string.time_spent_details_results_text, getTimeFormatted(passed = 787))
        setProgressBar(passed = 787)

        val textViewQuestionNumber: TextView = findViewById(R.id.textViewQuestionNumberSimulation)
        textViewQuestionNumber.isGone = false
        textViewQuestionNumber.text = getString(
            R.string.question_number_text,
            getString(R.string.question_number_first_run_quiz).toInt(),
            getString(R.string.question_number_total_first_run_simulation).toInt()
        )

        val constraintLayoutSimulationActivity: ConstraintLayout =
            findViewById(R.id.constraintLayoutSimulationActivity)

        var screenWidth = constraintLayoutSimulationActivity.width
        var screenHeight = constraintLayoutSimulationActivity.height

        when (number) {
            1 -> {
                viewFocus.isGone = true
                text.text = getString(R.string.message1_first_run_simulation)
            }
            2 -> {
                //reset from before status
                viewFocus.isGone = false

                //set new status
                setViewFirstRun(
                    viewFocus,
                    textViewQuestionNumber.width,
                    textViewQuestionNumber.height,
                    textViewQuestionNumber.x,
                    textViewQuestionNumber.y
                )
                text.text = getString(R.string.message3_first_run_simulation)
            }
            3 -> {
                //reset from before status
                //nothing

                //set new status
                setViewFirstRun(
                    viewFocus,
                    textViewTimePassed.width,
                    textViewTimePassed.height,
                    textViewTimePassed.x,
                    textViewTimePassed.y
                )
                text.text = getString(R.string.message2_first_run_simulation)
            }
            4 -> {
                //reset from before status
                val buttonFinishSimulation: Button = findViewById(R.id.buttonFinishSimulation)
                buttonFinishSimulation.isGone = true

                //set new status
                viewFocus.isGone = true
                text.text = getString(R.string.message4_first_run_simulation)
            }
            5 -> {
                //reset from before/after status
                val buttonFinishSimulation: Button = findViewById(R.id.buttonFinishSimulation)
                buttonFinishSimulation.isGone = false
                viewFocus.isGone = false

                //set new status
                val buttonExitTestSimulation: Button = findViewById(R.id.buttonExitTestSimulation)
                val constraintLayoutButtonsSimulation: ConstraintLayout =
                    findViewById(R.id.constraintLayoutButtonsSimulation)
                setViewFirstRun(
                    viewFocus,
                    buttonExitTestSimulation.width,
                    buttonExitTestSimulation.height,
                    buttonExitTestSimulation.x,
                    buttonExitTestSimulation.y + constraintLayoutButtonsSimulation.y
                )
                text.text = getString(R.string.message5_first_run_simulation)
            }
            6 -> {
                //reset from before status
                viewFocus.isGone = false

                //set new status
                val buttonFinishSimulation: Button = findViewById(R.id.buttonFinishSimulation)
                buttonFinishSimulation.isGone = false
                val constraintLayoutNavigationButtonsSimulation: ConstraintLayout =
                    findViewById(R.id.constraintLayoutNavigationButtonsSimulation)
                setViewFirstRun(
                    viewFocus,
                    buttonFinishSimulation.width,
                    buttonFinishSimulation.height,
                    screenWidth - (buttonFinishSimulation.width + constraintLayoutNavigationButtonsSimulation.marginStart + buttonFinishSimulation.marginEnd + (buttonFinishSimulation.paddingStart / 3) * 2).toFloat(),
                    screenHeight - (buttonFinishSimulation.height + constraintLayoutNavigationButtonsSimulation.marginBottom + (buttonFinishSimulation.paddingStart / 3) * 2).toFloat()
                )
                text.text = getString(R.string.message6_first_run_simulation)
            }
            7 -> {
                //reset from before status
                //nothing

                //set new status
                text.text = getString(R.string.message7_first_run_simulation)
                viewFocus.isGone = true
            }
            else -> {
                //finish
                setVariable(FIRST_RUN_SIMULATION, false)
                val constraintLayoutFirstRunSimulationActivity: ConstraintLayout =
                    findViewById(R.id.constraintLayoutFirstRunSimulation)
                constraintLayoutFirstRunSimulationActivity.isGone = true
                startSimulation()
            }
        }
    }

    fun setViewFirstRun(viewFocus: View, width: Int, height: Int, x: Float, y: Float) {
        viewFocus.isGone = false
        Handler().postDelayed({
            viewFocus.animate().x(x - viewFocus.paddingStart).setDuration(500)
            viewFocus.animate().y(y - viewFocus.paddingTop).setDuration(500)
            viewFocus.layoutParams =
                ViewGroup.LayoutParams(
                    width + (viewFocus.paddingStart * 2),
                    height + (viewFocus.paddingTop * 2)
                )
        }, 200)
    }

    fun checkFirstRun() {
        val constraintLayoutFirstRunSimulationActivity: ConstraintLayout =
            findViewById(R.id.constraintLayoutFirstRunSimulation)
        if (getVariable(FIRST_RUN_SIMULATION, default = true)!!) {
            //it's the first time (Simulation)
            constraintLayoutFirstRunSimulationActivity.isGone = false

            firstRun(number = 1)
        } else {
            //it's not the first time (Simulation)
            constraintLayoutFirstRunSimulationActivity.isGone = true
            startSimulation()
        }
    }

    fun startSimulation() {
        questionsLayout = arrayOf(
            findViewById(R.id.constraintLayoutQuestion1Simulation),
            findViewById(R.id.constraintLayoutQuestion2Simulation),
            findViewById(R.id.constraintLayoutQuestion3Simulation),
            findViewById(R.id.constraintLayoutQuestion4Simulation)
        )

        questionsImage = arrayOf(
            findViewById(R.id.imageViewQuestion1Simulation),
            findViewById(R.id.imageViewQuestion2Simulation),
            findViewById(R.id.imageViewQuestion3Simulation),
            findViewById(R.id.imageViewQuestion4Simulation)
        )

        questions = arrayOf(
            findViewById(R.id.textViewQuestion1Simulation),
            findViewById(R.id.textViewQuestion2Simulation),
            findViewById(R.id.textViewQuestion3Simulation),
            findViewById(R.id.textViewQuestion4Simulation)
        )

        simulationDatetime = now()

        val databaseHandler = DatabaseHandler(this)
        val questionsTemp = databaseHandler.getQuizzesRandom(limit = 50)
        databaseHandler.close()
        if (questionsTemp.size > 0) {
            questionsTemp.forEach {
                questionsSimulation.add(
                    SimulationQuizzesModel(
                        id = it.id,
                        chapter = it.chapter!!,
                        section = it.section!!,
                        question = it.question!!,
                        A = it.A!!,
                        B = it.B!!,
                        C = it.C!!,
                        D = it.D!!,
                        correct = it.correct!!,
                        user_answer = ""
                    )
                )
            }

            val buttonFinishSimulation: Button = findViewById(R.id.buttonFinishSimulation)
            buttonFinishSimulation.isGone = true

            start(
                chapterId = questionsSimulation[0].chapter,
                number = 1,
                questionId = questionsSimulation[0].id
            )

            startTime()
        }
    }

    fun start(chapterId: Int, number: Int, questionId: Int) {
        val getQuiz = questionsSimulation[number - 1]
        setupQuiz(
            chapter = chapterId,
            getQuiz = getQuiz,
            number = number,
            total = questionsSimulation.size
        )
    }

    fun startTime() {
        timeStopped = false
        incrementTime()
    }

    fun incrementTime() {
        val timeText: TextView = findViewById(R.id.textViewTimePassed)
        if (!timeStopped) {
            if (timePassed < (MAX_TIME) && !timeFinished) {

                timePassed++
                Handler().postDelayed({ incrementTime() }, 1000)
            } else {
                stopTime()
                timeFinished = true
                val builder: AlertDialog.Builder = AlertDialog.Builder(this)
                builder.setTitle(R.string.time_over_alert_dialog_title)
                builder.setMessage(R.string.time_over_simulation_alert_dialog_text)
                builder.setCancelable(false)//if tap out of the alert dialog, it doesn't disappear
                builder.setPositiveButton(
                    R.string.see_results_alert_dialog_button,
                    DialogInterface.OnClickListener { dialog, which ->
                        seeResults()
                    })
                builder.create().show() //create and show the alert dialog
            }
            timeText.text = getString(R.string.time_residual_text, getTimeFormatted())
            setProgressBar()
        }
    }

    fun stopTime() {
        timeStopped = true
    }

    fun getTimeFormatted(passed: Int = timePassed): String {
        val time = MAX_TIME - passed
        var timeToReturn: String = ""
        if (time < 60) {
            timeToReturn = "00:00:${getValueWithZero(time)}"
        } else if (time < (60 * 60)) {
            timeToReturn =
                "00:${getValueWithZero(time / 60)}:${getValueWithZero(time % 60)}"
        } else {
            timeToReturn =
                "${getValueWithZero(time / (60 * 60))}:${getValueWithZero((time - 60 * 60) / 60)}:${
                    getValueWithZero(
                        time % 60
                    )
                }"
        }
        return timeToReturn
    }

    fun getValueWithZero(value: Int): String {
        return if (value < 10) "0$value" else value.toString()
    }

    fun setProgressBar(passed: Int = timePassed) {
        val time = passed
        val passedProgress: View = findViewById(R.id.progressBarSimulationPassed)
        val residualProgress: View = findViewById(R.id.progressBarSimulationResidual)
        passedProgress.isGone = false
        residualProgress.isGone = false
        //if the width is "0dp" android treats it as "match_constraint"
        if (time == 0) passedProgress.isGone = true
        else if (time == MAX_TIME) residualProgress.isGone = true

        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        val DISPLAY_WIDTH = displayMetrics.widthPixels
        val NEW_WIDTH = (time * DISPLAY_WIDTH) / MAX_TIME
        if (NEW_WIDTH != 0) {
            passedProgress.updateLayoutParams {
                width = NEW_WIDTH
            }
        } else {
            passedProgress.isGone = true
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
        if (getVariable(FIRST_RUN_SIMULATION, default = false) == false) {
            val builder: AlertDialog.Builder = AlertDialog.Builder(this)
            builder.setTitle(R.string.closing_alert_dialog_title)
            builder.setMessage(R.string.sure_to_close_and_cancel_simulation_alert_dialog_text)
            builder.setPositiveButton(
                R.string.im_sure_alert_dialog_button,
                DialogInterface.OnClickListener { dialog, which ->
                    finish()
                    super.onBackPressed()
                })
            builder.setNegativeButton(
                R.string.cancel_alert_dialog_button,
                DialogInterface.OnClickListener { dialog, which ->
                    startTime()
                    dialog.dismiss()
                })
            stopTime()
            builder.create().show() //create and show the alert dialog
        } else {
            finish()
            super.onBackPressed()
        }
    }

    fun setupQuiz(
        chapter: Int? = null,
        getQuiz: SimulationQuizzesModel,
        number: Int,
        total: Int
    ) {
        val questionNumber: TextView = findViewById(R.id.textViewQuestionNumberSimulation)
        //val questionTime: TextView = findViewById(R.id.textViewTimePassed)
        val question: TextView = findViewById(R.id.textViewQuestionQuestionSimulation)

        val exitTestButton: Button = findViewById(R.id.buttonExitTestSimulation)

        val buttonBack: Button = findViewById(R.id.buttonGoBackSimulation)
        val buttonForward: Button = findViewById(R.id.buttonGoForwardSimulation)
        val buttonFinish: Button = findViewById(R.id.buttonFinishSimulation)

        buttonBack.setOnClickListener {
            val getQuiz = questionsSimulation[number - 2]
            resetQuestionsLayout(chapter!!, number - 1, questionId = getQuiz.id)
        }

        buttonFinish.setOnClickListener {
            val builder: AlertDialog.Builder = AlertDialog.Builder(this)
            builder.setTitle(R.string.closing_alert_dialog_title)
            builder.setMessage(R.string.sure_to_finish_simulation_alert_dialog_text)
            builder.setPositiveButton(
                R.string.im_sure_alert_dialog_button,
                DialogInterface.OnClickListener { dialog, which ->
                    seeResults()
                })
            builder.setNegativeButton(
                R.string.cancel_alert_dialog_button,
                DialogInterface.OnClickListener { dialog, which ->
                    startTime()
                    dialog.dismiss()
                })
            stopTime()
            builder.create().show() //create and show the alert dialog
        }

        buttonForward.setOnClickListener {
            val getQuiz = questionsSimulation[number]
            resetQuestionsLayout(chapter!!, number + 1, questionId = getQuiz.id)
        }

        if (number == 1) buttonBack.isGone = true
        else buttonBack.isGone = false

        if (number == total) {
            buttonForward.isGone = true
            buttonFinish.isGone = false
        } else {
            buttonForward.isGone = false
            buttonFinish.isGone = true
        }

        questionNumber.text = getString(R.string.question_number_text, number, total)

        exitTestButton.setOnClickListener {
            onBackPressed()
        }

        question.text = getQuiz.question
        questions[0].text = getQuiz.A
        questions[1].text = getQuiz.B
        questions[2].text = getQuiz.C
        questions[3].text = getQuiz.D

        if (questionsSimulation[number - 1].user_answer != "")
            selectOption(
                number = number - 1,
                index = correctListLettToNum[questionsSimulation[number - 1].user_answer]!!,
                force = true
            )

        questionsLayout.forEachIndexed { index, it ->
            it.setOnClickListener {
                selectOption(number - 1, index)
            }
        }
    }

    fun seeResults() {
        val milliseconds = timePassed
        questionsSimulation.forEach {
            val databaseHandler = DatabaseHandler(this)
            val statistics = StatisticsModel(
                id = databaseHandler.getNewIdStatistics(),
                type = 1,
                datetime = simulationDatetime,
                question_id = it.id,
                correct_answer = it.correct,
                user_answer = it.user_answer,
                milliseconds = milliseconds
            )
            databaseHandler.addStatistics(statistics)
            databaseHandler.close()
            sendStatistics(statistics)
        }

        val intent = Intent(this, ResultsSimulationQuizActivity::class.java)
        intent.putExtra("datetime", simulationDatetime)
        startActivity(intent)

        finish()
    }

    fun selectOption(number: Int, index: Int, force: Boolean = false) {
        if (!timeFinished) {
            questionsImage[0].setBackgroundResource(R.drawable.ic_checkbox)
            questionsImage[1].setBackgroundResource(R.drawable.ic_checkbox)
            questionsImage[2].setBackgroundResource(R.drawable.ic_checkbox)
            questionsImage[3].setBackgroundResource(R.drawable.ic_checkbox)

            if (correctListNumToLett[index] != questionsSimulation[number].user_answer || force) {
                questionsImage[index].setBackgroundResource(R.drawable.ic_checkbox_checked)
                questionsSimulation[number].user_answer = correctListNumToLett[index]!!
            } else {
                questionsSimulation[number].user_answer = ""
            }
        }
    }

    fun resetQuestionsLayout(chapterId: Int, number: Int, questionId: Int) {
        val question: TextView = findViewById(R.id.textViewQuestionQuestionSimulation)

        questionsLayout.forEach {
            it.isEnabled = true
        }
        questionsImage.forEach {
            it.setBackgroundResource(R.drawable.ic_checkbox)
            it.backgroundTintList = question.textColors
            it.imageTintList = question.textColors
        }
        questions.forEach {
            it.setTextColor(question.textColors)
        }

        start(chapterId, number = number, questionId)
    }

    fun now(): String {
        return SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
    }

    override fun onResume() {
        //checkFirstRun()
        super.onResume()
    }

    fun sendStatistics(statistics: StatisticsModel) {
        if (checkLogged()) {
            //logged
            //println("Prepare to sending")
            val statisticsToSend = StatisticsPostList(
                userid = getUserid(),
                type = statistics.type,
                datetime = statistics.datetime,
                correct_answer = statistics.correct_answer!!,
                user_answer = statistics.user_answer!!,
                question_id = statistics.question_id,
                milliseconds = statistics.milliseconds
            )
            sendStatistics(statisticsToSend)
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
                        //println("${responseList.code} || ${responseList.description}")
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

    fun checkLogged(): Boolean {
        return (getVariable("userid") != "" && getVariable("userid") != null)
    }

    fun getUserid(): String {
        return (if (checkLogged()) getVariable("userid")!! else "")
    }

    private fun setVariable(variable: String, value: String?) {
        getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE).edit()
            .putString(variable, value).apply()
    }

    private fun getVariable(variable: String): String? {
        return getSharedPreferences(
            PREFERENCES_NAME,
            Context.MODE_PRIVATE
        ).getString(variable, null)
    }

    fun getVariable(variable: String, default: Boolean = false): Boolean? {
        return getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE).getBoolean(
            variable,
            default
        )
    }

    fun setVariable(variable: String, value: Boolean = false) {
        getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE).edit()
            .putBoolean(variable, value).apply()
    }
}