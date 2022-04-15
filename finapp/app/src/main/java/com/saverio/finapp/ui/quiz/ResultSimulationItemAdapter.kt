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


class ResultSimulationItemAdapter(
    private val context: Context,
    private val items: ArrayList<StatisticsModel>
) :
    RecyclerView.Adapter<ResultSimulationItemAdapter.ItemViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, type: Int): ItemViewHolder {
        return ItemViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.result_simulation_recyclerview, parent, false)
        )
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = items[position]


        holder.questionNumber.text =
            holder.question_n_text.replace("%d", (position + 1).toString())

        val databaseHandler = DatabaseHandler(context)

        val correctListLettToNum = mapOf("A" to 0, "B" to 1, "C" to 2, "D" to 3)
        val correctListNumToLett = mapOf(0 to "A", 1 to "B", 2 to "C", 3 to "D")


        val getQuiz = databaseHandler.getQuiz(item.question_id)
        val questions = arrayOf(getQuiz.A, getQuiz.B, getQuiz.C, getQuiz.D)

        holder.questionText.text = getQuiz.question
        if (item.user_answer != item.correct_answer) {
            if (item.user_answer != "") {
                holder.constraintQuestion1.isGone = false
                holder.textQuestion1Results.text =
                    questions[correctListLettToNum[item.user_answer]!!]
                holder.statusText.text = holder.wrong_text
            } else {
                holder.constraintQuestion1.isGone = true
                holder.statusText.text = holder.skipped_text
            }
            holder.statusText.setTextColor(holder.colorRed)
            holder.statusBar.setBackgroundColor(holder.colorRed)
        } else {
            holder.constraintQuestion1.isGone = true

            holder.statusText.text = holder.correct_text
            holder.statusText.setTextColor(holder.colorGreen)
            holder.statusBar.setBackgroundColor(holder.colorGreen)
        }
        holder.textQuestion2Results.text = questions[correctListLettToNum[item.correct_answer]!!]
    }

    override fun getItemCount(): Int {
        return items.size
    }

    class ItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val questionText: TextView = view.findViewById(R.id.textViewQuestionTextResults)
        val questionNumber: TextView = view.findViewById(R.id.textViewQuestionNumberResults)
        val statusText: TextView = view.findViewById(R.id.textViewResultStatus)
        val statusBar: View = view.findViewById(R.id.viewStatusResults)
        val constraintQuestion1: ConstraintLayout =
            view.findViewById(R.id.constraintLayoutQuestion1Results)
        val constraintQuestion2: ConstraintLayout =
            view.findViewById(R.id.constraintLayoutQuestion2Results)
        val textQuestion1Results: TextView = view.findViewById(R.id.textViewQuestion1Results)
        val imageQuestion1Results: ImageView = view.findViewById(R.id.imageViewQuestion1Results)
        val textQuestion2Results: TextView = view.findViewById(R.id.textViewQuestion2Results)
        val imageQuestion2Results: ImageView = view.findViewById(R.id.imageViewQuestion2Results)

        val question_n_text = view.resources.getString(R.string.question_n_text)
        val correct_text = view.resources.getString(R.string.correct_text)
        val wrong_text = view.resources.getString(R.string.wrong_text)
        val skipped_text = view.resources.getString(R.string.skipped_text)
        val colorRed = view.resources.getColor(R.color.colorTimeRed)
        val colorGreen = view.resources.getColor(R.color.colorTimeGreen)
    }
}
