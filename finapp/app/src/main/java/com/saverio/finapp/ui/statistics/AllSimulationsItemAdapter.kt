package com.saverio.finapp.ui.statistics

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
import com.saverio.finapp.ui.quiz.ResultsSimulationQuizActivity
import com.saverio.finapp.ui.theory.AllSectionsActivity


class AllSimulationsItemAdapter(
    private val context: Context,
    private val items: ArrayList<StatisticsModel>
) :
    RecyclerView.Adapter<AllSimulationsItemAdapter.ItemViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, type: Int): ItemViewHolder {
        return ItemViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.simulation_recyclerview, parent, false)
        )
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = items[position]


        holder.title.text = holder.simulation_text.replace("%s", item.datetime)

        val databaseHandler = DatabaseHandler(context)

        if (databaseHandler.getWrongCorrectSkippedAnswersStatistics(
                datetime = item.datetime,
                filter = "ws"
            ).size < 5
        ) {
            //passed
            holder.passedOrNot.text = holder.passed_text
            holder.statusBar.setBackgroundColor(holder.colorGreen)
        } else {
            //not passed
            holder.passedOrNot.text = holder.not_passed_text
            holder.statusBar.setBackgroundColor(holder.colorRed)
        }

        holder.cardView.setOnClickListener {
            val intent = Intent(context, ResultsSimulationQuizActivity::class.java)
            intent.putExtra("datetime", item.datetime)
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }

    class ItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val title: TextView = view.findViewById(R.id.textViewSimulationDateTitle)
        val passedOrNot: TextView = view.findViewById(R.id.textViewSimulationPassedOrNot)
        val statusBar: View = view.findViewById(R.id.viewStatusSimulation)
        val cardView: CardView = view.findViewById(R.id.cardViewSimulation)

        val simulation_text = view.resources.getString(R.string.simulation_date_text)
        val passed_text = view.resources.getString(R.string.exam_passed_text)
        val not_passed_text = view.resources.getString(R.string.exam_not_passed_text)
        val colorRed = view.resources.getColor(R.color.colorTimeRed)
        val colorGreen = view.resources.getColor(R.color.colorTimeGreen)
    }
}
