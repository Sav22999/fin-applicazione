package com.saverio.finapp.ui.quiz

import android.content.DialogInterface
import android.content.Intent
import android.content.res.ColorStateList
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.DisplayMetrics
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isGone
import androidx.core.view.updateLayoutParams
import com.saverio.finapp.R
import com.saverio.finapp.db.DatabaseHandler
import com.saverio.finapp.db.SimulationQuizzesModel
import com.saverio.finapp.db.StatisticsModel
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

    var timePassed: Int = 0
    var simulationDatetime: String = ""
    val MAX_TIME: Int = 7200 //2 hours in seconds (2*60*60)=7200
    var timeStopped = true
    var timeFinished = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_simulation_quiz)

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

        start(
            chapterId = questionsSimulation[0].chapter!!,
            number = 1,
            questionId = questionsSimulation[0].id
        )

        startTime()

        val actionBar = getSupportActionBar()
        if (actionBar != null) {
            //show the back button in the action bar
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.title = ""
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
                Handler().postDelayed({ incrementTime() }, 1000)//TODO change to 1000 after tests
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
            timeText.text =
                getString(R.string.time_residual_text).replace("{{time}}", getTimeFormatted())
            setProgressBar()
        }
    }

    fun stopTime() {
        timeStopped = true
    }

    fun getTimeFormatted(): String {
        val time = MAX_TIME - timePassed
        var timeToReturn: String = ""
        if (time < 60) {
            timeToReturn = "00:00:${getValueWithZero(time)}"
        } else if (time < (60 * 60)) {
            timeToReturn =
                "00:${getValueWithZero(time / 60)}:${getValueWithZero(time % 60)}}"
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

    fun setProgressBar() {
        val time = timePassed
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

        questionNumber.text =
            getString(R.string.question_number_text).replace("{{n}}", number.toString())
                .replace("{{tot}}", total.toString())

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
        questionsSimulation.forEach {
            val databaseHandler = DatabaseHandler(this)
            val statistics = StatisticsModel(
                id = databaseHandler.getNewIdStatistics(),
                type = 1,
                datetime = simulationDatetime,
                question_id = it.id,
                correct_answer = it.correct,
                user_answer = it.user_answer
            )
            databaseHandler.addStatistics(statistics)
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
        super.onResume()
    }
}