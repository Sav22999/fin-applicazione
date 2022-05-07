package com.saverio.finapp.ui.theory

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.view.isGone
import androidx.recyclerview.widget.RecyclerView
import com.saverio.finapp.R
import com.saverio.finapp.db.SectionsModel


class AllSectionsItemAdapter(
    private val context: Context,
    private val items: ArrayList<SectionsModel>,
    private val chapter: Int?
) :
    RecyclerView.Adapter<AllSectionsItemAdapter.ItemViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, type: Int): ItemViewHolder {
        return ItemViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.section_theory_recyclerview, parent, false)
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
        holder.section_id.text =
            holder.itemView.resources.getString(R.string.section_id_text, item.section)

        holder.sectionCard.setOnClickListener {
            val intent = Intent(context, SectionActivity::class.java)
            intent.putExtra("chapter_id", item.chapter)
            intent.putExtra("section_id", item.section)
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }

    class ItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val section_id: TextView = view.findViewById(R.id.textViewSectionSection)
        val title: TextView = view.findViewById(R.id.textViewSectionTitle)
        val sectionCard: CardView = view.findViewById(R.id.cardViewSection)
    }
}
