package com.saverio.finapp.ui.quiz

import android.content.DialogInterface
import android.content.Intent
import android.content.res.ColorStateList
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.MenuItem
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isGone
import com.saverio.finapp.R
import com.saverio.finapp.db.DatabaseHandler
import com.saverio.finapp.db.QuizzesModel
import com.saverio.finapp.db.StatisticsModel
import com.saverio.finapp.ui.theory.SectionActivity
import org.w3c.dom.Text
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class QuestionsQuizActivity : AppCompatActivity() {
    var selectedQuestion: Int = -1
    var currentIterator: Int = 0

    var questionsLayout = arrayOf<ConstraintLayout>()
    var questionsImage = arrayOf<ImageView>()
    var questions = arrayOf<TextView>()

    val correctListLettToNum = mapOf("A" to 0, "B" to 1, "C" to 2, "D" to 3)
    val correctListNumToLett = mapOf(0 to "A", 1 to "B", 2 to "C", 3 to "D")

    var timePassed: Int = 0
    var timeStopped = true
    var alreadyAnswered = false
    var lastQuestionIdUsed: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_questions_quiz)

        val bundle = intent.extras

        questionsLayout = arrayOf(
            findViewById(R.id.constraintLayoutQuestion1),
            findViewById(R.id.constraintLayoutQuestion2),
            findViewById(R.id.constraintLayoutQuestion3),
            findViewById(R.id.constraintLayoutQuestion4)
        )

        questionsImage = arrayOf(
            findViewById(R.id.imageViewQuestion1),
            findViewById(R.id.imageViewQuestion2),
            findViewById(R.id.imageViewQuestion3),
            findViewById(R.id.imageViewQuestion4)
        )

        questions = arrayOf(
            findViewById(R.id.textViewQuestion1),
            findViewById(R.id.textViewQuestion2),
            findViewById(R.id.textViewQuestion3),
            findViewById(R.id.textViewQuestion4)
        )

        var chapterId: Int? = null
        var questionId: Int = 0
        var questionNumber: Int = 0

        if (bundle != null) {
            chapterId = bundle.getInt("chapter_id")
            questionId = bundle.getInt("question_id")
            questionNumber = bundle.getInt("question_number")
            selectedQuestion = bundle.getInt("selected_question")
        }
        if (questionNumber == -1) questionNumber = 1

        start(chapterId!!, number = questionNumber, questionId)

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
            if (statistics.user_answer != "") {
                //already answered, so load the old value
                alreadyAnswered = true
                selectedQuestion = correctListLettToNum[statistics.user_answer]!!
                checkOption(
                    correct = correctListLettToNum[statistics.correct_answer]!!,
                    selected = selectedQuestion,
                    questionId = questionId,
                    update = false
                )
            }
            timePassed = statistics.milliseconds
        }
        databaseHandler.close()
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
        lastQuestionIdUsed = getQuiz.id
        setTimeToShow(lastQuestionIdUsed)

        val questionNumber: TextView = findViewById(R.id.textViewQuestionNumber)
        val question: TextView = findViewById(R.id.textViewQuestionQuestion)

        val theoryButton: Button = findViewById(R.id.buttonTheory)
        val exitTestButton: Button = findViewById(R.id.buttonExitTest)

        val buttonBack: Button = findViewById(R.id.buttonGoBack)
        val buttonForward: Button = findViewById(R.id.buttonGoForward)
        val buttonCheck: Button = findViewById(R.id.buttonCheck)

        buttonBack.setOnClickListener {
            //back button
            val databaseHandler = DatabaseHandler(this)
            val getQuiz =
                databaseHandler.getQuiz(id = databaseHandler.getQuizzes(chapter = chapter)[number - 2].id)
            resetTime(getQuiz.id)
            resetQuestionsLayout(chapter!!, number - 1, questionId = getQuiz.id)
        }

        buttonCheck.setOnClickListener {
            //check button
            stopTime()
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
            //forward button
            val databaseHandler = DatabaseHandler(this)
            val getQuiz =
                databaseHandler.getQuiz(id = databaseHandler.getQuizzes(chapter = chapter)[number].id)
            resetTime(getQuiz.id)
            resetQuestionsLayout(chapter!!, number + 1, questionId = getQuiz.id)
        }

        if (number == 1) buttonBack.isGone = true
        else buttonBack.isGone = false

        if (number == total) buttonForward.isGone = true
        else buttonForward.isGone = false

        questionNumber.text =
            getString(R.string.question_number_chapter_text, number, total, getQuiz.chapter)

        theoryButton.setOnClickListener {
            stopTime()
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

        val buttonCheck: Button = findViewById(R.id.buttonCheck)
        if (selectedQuestion != index) {
            questionsImage[index].setBackgroundResource(R.drawable.ic_checkbox_checked)
            selectedQuestion = index
            buttonCheck.isGone = false
        } else {
            selectedQuestion = -1
            buttonCheck.isGone = true
        }
    }

    fun checkOption(correct: Int, selected: Int, questionId: Int, update: Boolean = false) {
        stopTime()
        questionsImage[0].setBackgroundResource(R.drawable.ic_checkbox)
        questionsImage[1].setBackgroundResource(R.drawable.ic_checkbox)
        questionsImage[2].setBackgroundResource(R.drawable.ic_checkbox)
        questionsImage[3].setBackgroundResource(R.drawable.ic_checkbox)

        questionsImage[selected].setBackgroundResource(R.drawable.ic_checkbox_checked)
        questionsImage[selected].backgroundTintList =
            ColorStateList.valueOf(resources.getColor(R.color.colorTimeGreen))
        questionsImage[selected].imageTintList =
            ColorStateList.valueOf(resources.getColor(R.color.colorTimeGreen))
        questions[selected].setTextColor(resources.getColor(R.color.colorTimeGreen))
        if (correct != selected) {
            questionsImage[correct].setBackgroundResource(R.drawable.ic_checkbox_checked)
            questionsImage[correct].backgroundTintList =
                ColorStateList.valueOf(resources.getColor(R.color.colorTimeRed))
            questionsImage[correct].imageTintList =
                ColorStateList.valueOf(resources.getColor(R.color.colorTimeRed))
            questions[correct].setTextColor(resources.getColor(R.color.colorTimeRed))
        }

        questionsLayout.forEachIndexed { index, it ->
            it.isEnabled = false
        }

        if (update) {
            //insert in database
            val databaseHandler = DatabaseHandler(this)
            val statistics = StatisticsModel(
                id = databaseHandler.getNewIdStatistics(),
                type = 0,
                datetime = now(),
                question_id = questionId,
                correct_answer = correctListNumToLett[correct],
                user_answer = correctListNumToLett[selected],
                milliseconds = timePassed //TODO implement timer (for single question)
            )
            if (!databaseHandler.checkStatistics(question = questionId, type = 0)) {
                //no present || add
                databaseHandler.addStatistics(statistics)
            } else {
                //already present || update
                statistics.id =
                    databaseHandler.getStatistics(question_id = questionId, type = 0)[0].id
                databaseHandler.updateStatistics(statistics)
            }
            databaseHandler.close()
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
            it.imageTintList = question.textColors
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
        if (!alreadyAnswered) startTime(lastQuestionIdUsed)
        super.onResume()
    }

    fun resetTime(currentQuestionId: Int) {
        currentIterator++
        alreadyAnswered = false
        startTime(currentQuestionId)
    }

    fun startTime(currentQuestionId: Int) {
        if (currentQuestionId != -1) {
            timeStopped = false
            incrementTime(currentIterator, currentQuestionId)
        }
    }

    fun incrementTime(iterator: Int, currentQuestionId: Int) {
        //this to avoid more async process ("delayed") increment the same variable
        val databaseHandler = DatabaseHandler(this)
        if (currentIterator == iterator && currentQuestionId != -1) {
            setTimeToShow(currentQuestionId)
            if (databaseHandler.checkStatistics(question = currentQuestionId, type = 0)) {
                val statistics =
                    databaseHandler.getStatistics(question_id = currentQuestionId, type = 0)[0]
                if (statistics.user_answer == "") {
                    //already present and not already answered || update
                    timePassed++
                    statistics.milliseconds = timePassed

                    if (!timeStopped) {
                        Handler().postDelayed({
                            incrementTime(iterator, currentQuestionId)
                        }, 1000)
                    }

                    databaseHandler.updateStatistics(statistics)
                }
            } else {
                //no present || add
                val getQuiz = databaseHandler.getQuiz(currentQuestionId)
                val statistics = StatisticsModel(
                    id = databaseHandler.getNewIdStatistics(),
                    type = 0,
                    datetime = now(),
                    question_id = currentQuestionId,
                    correct_answer = getQuiz.correct,
                    user_answer = "",
                    milliseconds = timePassed //TODO implement timer (for single question)
                )
                databaseHandler.addStatistics(statistics)
            }
        }
        databaseHandler.close()
    }

    fun setTimeToShow(currentQuestionId: Int) {
        val databaseHandler = DatabaseHandler(this)
        if (databaseHandler.checkStatistics(question = currentQuestionId, type = 0)) {
            val statistics =
                databaseHandler.getStatistics(question_id = currentQuestionId, type = 0)[0]
            timePassed = statistics.milliseconds
        } else {
            timePassed = 0
        }
        val timeText: TextView = findViewById(R.id.textViewTimeUsed)
        timeText.text = getString(R.string.time_used_text, getTimeFormatted(timePassed))
        databaseHandler.close()
    }

    fun stopTime() {
        timeStopped = true
    }

    fun getTimeFormatted(time: Int): String {
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

    override fun onDestroy() {
        lastQuestionIdUsed = -1
        super.onDestroy()
    }
}