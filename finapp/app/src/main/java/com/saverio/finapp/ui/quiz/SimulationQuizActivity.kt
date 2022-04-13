package com.saverio.finapp.ui.quiz

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
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isGone
import androidx.core.view.updateLayoutParams
import com.saverio.finapp.R
import com.saverio.finapp.db.DatabaseHandler
import com.saverio.finapp.db.QuizzesModel
import com.saverio.finapp.db.StatisticsModel
import com.saverio.finapp.ui.theory.SectionActivity
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class SimulationQuizActivity : AppCompatActivity() {
    var selectedQuestion: Int = -1

    var questionsLayout = arrayOf<ConstraintLayout>()
    var questionsImage = arrayOf<ImageView>()
    var questions = arrayOf<TextView>()

    val correctListLettToNum = mapOf("A" to 0, "B" to 1, "C" to 2, "D" to 3)
    val correctListNumToLett = mapOf(0 to "A", 1 to "B", 2 to "C", 3 to "D")

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

        //start(chapterId!!, number = questionNumber, questionId)

        startTime()

        val actionBar = getSupportActionBar()
        if (actionBar != null) {
            //show the back button in the action bar
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.title = ""
        }
    }

    fun start(chapterId: Int, number: Int, questionId: Int) {
        val databaseHandler = DatabaseHandler(this)
        val getQuiz = databaseHandler.getQuiz(id = questionId)
        setupQuiz(
            chapter = chapterId,
            getQuiz = getQuiz,
            number = number,
            total = databaseHandler.getQuizzes(chapter = chapterId).size
        )
        if (databaseHandler.checkStatistics(question = questionId, type = 0)) {
            val statistics = databaseHandler.getStatistics(question_id = questionId, type = 0)[0]
            selectedQuestion = correctListLettToNum[statistics.user_answer]!!
            findViewById<Button>(R.id.buttonCheck).performClick()
        }
    }

    fun startTime() {
        timeStopped = false
        println("Time started")
        incrementTime()
    }

    fun incrementTime() {
        val timeText: TextView = findViewById(R.id.textViewTimePassed)
        if (timePassed < (MAX_TIME) && !timeFinished) {
            if (!timeStopped) {
                timePassed++
                println("Time incremented")
            }
            Handler().postDelayed({ incrementTime() }, 1)//todo change to 1000 after tests
        } else {
            println("Time finished")
            timeFinished = true
        }
        println("Time passed: $timePassed")
        timeText.text =
            getString(R.string.time_residual_text).replace("{{time}}", getTimeFormatted())
        setProgressBar()
    }

    fun stopTime() {
        timeStopped = true
        println("Time stopped")
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
                }}"
        }
        println(time.toString())
        return timeToReturn
    }

    fun getValueWithZero(value: Int): String {
        return if (value < 10) "0$value" else value.toString()
    }

    fun setProgressBar() {
        val time = timePassed
        val passedProgress: View = findViewById(R.id.progressBarSimulationGreen)
        val residualProgress: View = findViewById(R.id.progressBarSimulationRed)
        passedProgress.isGone = false
        residualProgress.isGone = false
        //if the width is "0dp" android treats it as "match_constraint"
        if (time == 0) passedProgress.isGone = true
        else if (time == MAX_TIME) residualProgress.isGone = true

        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        val DISPLAY_WIDTH = displayMetrics.widthPixels
        val NEW_WIDTH = (time * DISPLAY_WIDTH) / MAX_TIME
        passedProgress.updateLayoutParams {
            width = NEW_WIDTH
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

    fun setupQuiz(
        chapter: Int? = null,
        getQuiz: QuizzesModel,
        number: Int,
        total: Int
    ) {
        val questionNumber: TextView = findViewById(R.id.textViewQuestionNumber)
        val questionTime: TextView = findViewById(R.id.textViewTimeUsed)
        val question: TextView = findViewById(R.id.textViewQuestionQuestion)

        val theoryButton: Button = findViewById(R.id.buttonTheory)
        val exitTestButton: Button = findViewById(R.id.buttonExitTest)

        val buttonBack: Button = findViewById(R.id.buttonGoBack)
        val buttonForward: Button = findViewById(R.id.buttonGoForward)
        val buttonCheck: Button = findViewById(R.id.buttonCheck)

        buttonBack.setOnClickListener {
            val databaseHandler = DatabaseHandler(this)
            val getQuiz =
                databaseHandler.getQuiz(id = databaseHandler.getQuizzes(chapter = chapter)[number - 2].id)
            resetQuestionsLayout(chapter!!, number - 1, questionId = getQuiz.id)
        }

        buttonCheck.setOnClickListener {
            if (selectedQuestion != -1) {
                buttonCheck.isGone = true
                if (number == total) buttonForward.isGone = true
                else buttonForward.isGone = false

                checkOption(
                    selected = selectedQuestion,
                    correct = correctListLettToNum[getQuiz.correct]!!,
                    questionId = getQuiz.id,
                    update = true
                )
            }
        }

        buttonForward.setOnClickListener {
            val databaseHandler = DatabaseHandler(this)
            val getQuiz =
                databaseHandler.getQuiz(id = databaseHandler.getQuizzes(chapter = chapter)[number].id)
            resetQuestionsLayout(chapter!!, number + 1, questionId = getQuiz.id)
        }

        if (number == 1) buttonBack.isGone = true
        else buttonBack.isGone = false

        buttonCheck.isGone = false

        questionNumber.text =
            getString(R.string.question_number_chapter_text).replace("{{n}}", number.toString())
                .replace("{{tot}}", total.toString())
                .replace("{{chapter}}", getQuiz.chapter.toString())

        theoryButton.setOnClickListener {
            val intent = Intent(this, SectionActivity::class.java)
            intent.putExtra("chapter_id", getQuiz.chapter)
            intent.putExtra("section_id", getQuiz.section)
            this.startActivity(intent)
        }
        exitTestButton.setOnClickListener {
            onBackPressed()
        }

        question.text = getQuiz.question
        questions[0].text = getQuiz.A
        questions[1].text = getQuiz.B
        questions[2].text = getQuiz.C
        questions[3].text = getQuiz.D

        if (selectedQuestion != -1) selectOption(index = selectedQuestion)

        questionsLayout.forEachIndexed { index, it ->
            it.setOnClickListener {
                selectOption(index)
            }
        }
    }

    fun selectOption(index: Int) {
        questionsImage[0].setBackgroundResource(R.drawable.ic_checkbox)
        questionsImage[1].setBackgroundResource(R.drawable.ic_checkbox)
        questionsImage[2].setBackgroundResource(R.drawable.ic_checkbox)
        questionsImage[3].setBackgroundResource(R.drawable.ic_checkbox)

        questionsImage[index].setBackgroundResource(R.drawable.ic_checkbox_checked)
        selectedQuestion = index
    }

    fun checkOption(correct: Int, selected: Int, questionId: Int, update: Boolean = false) {
        questionsImage[0].setBackgroundResource(R.drawable.ic_checkbox)
        questionsImage[1].setBackgroundResource(R.drawable.ic_checkbox)
        questionsImage[2].setBackgroundResource(R.drawable.ic_checkbox)
        questionsImage[3].setBackgroundResource(R.drawable.ic_checkbox)

        questionsImage[selected].setBackgroundResource(R.drawable.ic_checkbox_checked)
        questionsImage[selected].backgroundTintList =
            ColorStateList.valueOf(resources.getColor(R.color.colorTimeRed))
        questions[selected].setTextColor(resources.getColor(R.color.colorTimeRed))

        questionsImage[correct].setBackgroundResource(R.drawable.ic_checkbox_checked)
        questionsImage[correct].backgroundTintList =
            ColorStateList.valueOf(resources.getColor(R.color.colorTimeGreen))
        questions[correct].setTextColor(resources.getColor(R.color.colorTimeGreen))

        questionsLayout.forEachIndexed { index, it ->
            it.isEnabled = false
        }

        if (update) {
            val databaseHandler = DatabaseHandler(this)
            val statistics = StatisticsModel(
                id = databaseHandler.getNewIdStatistics(),
                type = 0,
                datetime = now(),
                question_id = questionId,
                correct_answer = correctListNumToLett[correct],
                user_answer = correctListNumToLett[selected]
            )
            if (!databaseHandler.checkStatistics(question = questionId, type = 0))
                databaseHandler.addStatistics(statistics)
            else {
                statistics.id =
                    databaseHandler.getStatistics(question_id = questionId, type = 0)[0].id
                databaseHandler.updateStatistics(statistics)
            }
        }
    }

    fun resetQuestionsLayout(chapterId: Int, number: Int, questionId: Int) {
        val question: TextView = findViewById(R.id.textViewQuestionQuestion)

        selectedQuestion = -1

        questionsLayout.forEach {
            it.isEnabled = true
        }
        questionsImage.forEach {
            it.setBackgroundResource(R.drawable.ic_checkbox)
            it.backgroundTintList = question.textColors
        }
        questions.forEach {
            it.setTextColor(question.textColors)
        }

        start(chapterId!!, number = number, questionId)
    }

    fun now(): String {
        return SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
    }

    override fun onResume() {
        super.onResume()
    }
}