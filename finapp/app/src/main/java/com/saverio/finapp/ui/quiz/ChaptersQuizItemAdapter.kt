package com.saverio.finapp.ui.quiz

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.view.isGone
import androidx.recyclerview.widget.RecyclerView
import com.saverio.finapp.R
import com.saverio.finapp.db.ChaptersModel
import com.saverio.finapp.db.DatabaseHandler


class ChaptersQuizItemAdapter(
    private val context: Context,
    private val items: ArrayList<ChaptersModel>
) :
    RecyclerView.Adapter<ChaptersQuizItemAdapter.ItemViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, type: Int): ItemViewHolder {
        return ItemViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.chapter_theory_recyclerview, parent, false)
        )
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = items[position]

        if (item.title != null && item.title != "" && item.title != "NULL") {
            holder.title.isGone = false
            holder.title.text = item.title
        } else {
            holder.title.isGone = true
        }
        holder.chapterId.text =
            holder.itemView.resources.getString(R.string.chapter_id_text, item.chapter)

        val databaseHandler = DatabaseHandler(context)

        holder.chapterCard.setOnClickListener {
            if (databaseHandler.getQuizzes(chapter = item.chapter).size > 0) {
                val intent = Intent(context, QuestionsQuizActivity::class.java)
                intent.putExtra("chapter_id", item.chapter)
                intent.putExtra("question_number", 1)
                context.startActivity(intent)
            }
        }
        databaseHandler.close()

        if (position % 2 == 0) {
            //even position
        } else {
            //odd position
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }

    class ItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val chapterId: TextView = view.findViewById(R.id.textViewChapterChapter)
        val title: TextView = view.findViewById(R.id.textViewChapterTitle)
        val chapterCard: CardView = view.findViewById(R.id.cardViewSection)
    }
}
