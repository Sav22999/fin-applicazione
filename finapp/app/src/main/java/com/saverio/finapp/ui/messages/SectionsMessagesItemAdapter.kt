package com.saverio.finapp.ui.messages

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.view.isInvisible
import androidx.recyclerview.widget.RecyclerView
import com.saverio.finapp.R
import com.saverio.finapp.db.SectionsModel


class SectionsMessagesItemAdapter(
    private val context: Context,
    private val items: ArrayList<SectionsModel>,
    private val sectionsJoined: ArrayList<String> //
) :
    RecyclerView.Adapter<SectionsMessagesItemAdapter.ItemViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, type: Int): ItemViewHolder {
        return ItemViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.sections_message_recyclerview, parent, false)
        )
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = items[position]

        holder.chapterSectionTitle.text = holder.itemView.resources.getString(
            R.string.chapter_section_text,
            item.chapter,
            item.section
        )
        holder.sectionTitle.text = item.title

        if (sectionsJoined.indexOf(item.section) != -1) {
            //it's present in "Joined" section list
            holder.status.isInvisible = false
            holder.status.setBackgroundColor(holder.colorPrimary)
        } else {
            holder.status.isInvisible = true
        }

        holder.card.setOnClickListener {
            val intent = Intent(context, MessagesActivity::class.java)
            intent.putExtra("section_id", item.section)
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }

    class ItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val chapterSectionTitle: TextView = view.findViewById(R.id.textViewChapterMessage)
        val sectionTitle: TextView = view.findViewById(R.id.textViewSimulationDateTitle)
        val status: View = view.findViewById(R.id.viewStatusMessage)
        val card: CardView = view.findViewById(R.id.cardViewAllMessagesMessage)

        val colorPrimary = view.resources.getColor(R.color.colorLightBlue)
    }
}
