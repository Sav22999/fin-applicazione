package com.saverio.finapp.ui.quiz

import android.content.Intent
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.saverio.finapp.R
import com.saverio.finapp.db.DatabaseHandler
import com.saverio.finapp.db.QuizzesModel
import com.saverio.finapp.ui.theory.SectionActivity

class QuestionsQuizActivity : AppCompatActivity() {
    var selectedQuestion: Int = -1
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_questions_quiz)

        val bundle = intent.extras

        var chapterId: Int? = null
        var questionId: Int = 0

        val databaseHandler = DatabaseHandler(this)

        if (bundle != null) {
            chapterId = bundle.getInt("chapter_id")
            questionId = bundle.getInt("question_id")
            selectedQuestion = bundle.getInt("selected_question")
        }
        val getQuiz = databaseHandler.getQuiz(id = questionId)
        setupQuiz(
            chapter = chapterId,
            getQuiz = getQuiz,
            total = databaseHandler.getQuizzes(chapter = chapterId).size
        )

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

    fun setupQuiz(
        chapter: Int? = null,
        getQuiz: QuizzesModel,
        total: Int
    ) {
        val question: TextView = findViewById(R.id.textViewQuestionQuestion)
        val question1: TextView = findViewById(R.id.textViewQuestion1)
        val question2: TextView = findViewById(R.id.textViewQuestion2)
        val question3: TextView = findViewById(R.id.textViewQuestion3)
        val question4: TextView = findViewById(R.id.textViewQuestion4)

        val theoryButton: Button = findViewById(R.id.buttonTheory)
        val exitTestButton: Button = findViewById(R.id.buttonExitTest)

        theoryButton.setOnClickListener {
            val intent = Intent(this, SectionActivity::class.java)
            intent.putExtra("chapter_id", getQuiz.chapter)
            intent.putExtra("section_id", getQuiz.section)
            this.startActivity(intent)
        }

        val questionsLayout = arrayOf(
            findViewById<ConstraintLayout>(R.id.constraintLayoutQuestion1),
            findViewById<ConstraintLayout>(R.id.constraintLayoutQuestion2),
            findViewById<ConstraintLayout>(R.id.constraintLayoutQuestion3),
            findViewById<ConstraintLayout>(R.id.constraintLayoutQuestion4)
        )

        question.text = getQuiz.question
        question1.text = getQuiz.A
        question2.text = getQuiz.B
        question3.text = getQuiz.C
        question4.text = getQuiz.D

        if (selectedQuestion != -1) selectOption(index = selectedQuestion)

        questionsLayout.forEachIndexed { index, it ->
            it.setOnClickListener {
                selectOption(index)
            }
        }
    }

    fun selectOption(index: Int) {
        val questionsImage = arrayOf(
            findViewById<ImageView>(R.id.imageViewQuestion1),
            findViewById<ImageView>(R.id.imageViewQuestion2),
            findViewById<ImageView>(R.id.imageViewQuestion3),
            findViewById<ImageView>(R.id.imageViewQuestion4)
        )

        questionsImage[0].setBackgroundResource(R.drawable.ic_checkbox)
        questionsImage[1].setBackgroundResource(R.drawable.ic_checkbox)
        questionsImage[2].setBackgroundResource(R.drawable.ic_checkbox)
        questionsImage[3].setBackgroundResource(R.drawable.ic_checkbox)

        questionsImage[index].setBackgroundResource(R.drawable.ic_checkbox_checked)
        selectedQuestion = index
    }

    override fun onResume() {
        if (selectedQuestion != -1) selectOption(index = selectedQuestion)
        super.onResume()
    }
}