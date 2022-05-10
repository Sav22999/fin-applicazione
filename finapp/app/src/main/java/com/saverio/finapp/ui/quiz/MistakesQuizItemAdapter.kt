package com.saverio.finapp.ui.quiz

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isGone
import androidx.recyclerview.widget.RecyclerView
import com.saverio.finapp.R
import com.saverio.finapp.db.ChaptersModel
import com.saverio.finapp.db.DatabaseHandler
import com.saverio.finapp.db.SimulationQuizzesModel
import com.saverio.finapp.db.StatisticsModel


class MistakesQuizItemAdapter(
    private val context: Context,
    private val items: ArrayList<StatisticsModel>
) :
    RecyclerView.Adapter<MistakesQuizItemAdapter.ItemViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, type: Int): ItemViewHolder {
        return ItemViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.mistakes_quiz_recyclerview, parent, false)
        )
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = items[position]

        holder.chapterIdText.text =
            holder.itemView.resources.getString(R.string.question_n_text, item.question_id)

        val databaseHandler = DatabaseHandler(context)

        if (databaseHandler.getQuizzes().size > 0) {
            val getQuiz = databaseHandler.getQuiz(item.question_id)
            holder.questionText.text = getQuiz.question
            val questionsLetter =
                arrayOf("A" to getQuiz.A, "B" to getQuiz.B, "C" to getQuiz.C, "D" to getQuiz.D)


            var indexToUse = 0
            questionsLetter.forEachIndexed { index, it ->
                if (getQuiz.correct != it.first) {
                    holder.textQuestionsMistakes[indexToUse].text = it.second.toString()
                    indexToUse++
                } else {
                    holder.textQuestionCorrect.text = it.second.toString()
                }
            }
        }
        databaseHandler.close()
    }

    override fun getItemCount(): Int {
        return items.size
    }

    class ItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val questionText: TextView = view.findViewById(R.id.textViewQuestionTextMistakes)
        val chapterIdText: TextView = view.findViewById(R.id.textViewChapterIdMistakes)

        val textQuestionCorrect: TextView = view.findViewById(R.id.textViewQuestion1Mistakes)
        val textQuestionsMistakes = arrayOf(
            view.findViewById<TextView>(R.id.textViewQuestion2Mistakes),
            view.findViewById<TextView>(R.id.textViewQuestion3Mistakes),
            view.findViewById<TextView>(R.id.textViewQuestion4Mistakes)
        )
    }
}
