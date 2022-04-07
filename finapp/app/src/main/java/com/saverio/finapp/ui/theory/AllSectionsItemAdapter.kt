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
        holder.section_id.text = holder.section_text.replace("{{section_id}}", item.section)

        holder.sectionCard.setOnClickListener {
            val intent = Intent(context, SectionActivity::class.java)
            intent.putExtra("chapter_id", item.chapter)
            intent.putExtra("section_id", item.section)
            context.startActivity(intent)
        }

        /*
        holder.cardViewNews.setOnTouchListener(
            View.OnTouchListener { view, event ->
                val displayMetrics = view.resources.displayMetrics
                val cardWidth = view.width
                val cardStart = (displayMetrics.widthPixels.toFloat() / 2) - (cardWidth / 2)
                val MAX_SWIPE_LEFT_DISTANCE = 50
                val POSITION_TO_ARRIVE = MAX_SWIPE_LEFT_DISTANCE.toFloat() - (cardWidth / 2)
                val POSITION_ALL_TO_LEFT = -(cardWidth + cardStart)
                when (event.action) {
                    MotionEvent.ACTION_MOVE -> {
                        // get the new co-ordinate of X-axis
                        val newX = event.rawX

                        // carry out swipe only if newX < cardStart, that is,
                        // the card is swiped to the left side, not to the right
                        if (newX > MAX_SWIPE_LEFT_DISTANCE) {
                            view.animate()
                                .x(
                                    min(cardStart, newX - (cardWidth / 2))
                                )
                                .setDuration(0)
                                .start()
                        } else {
                            view.animate().x(POSITION_TO_ARRIVE)
                        }
                    }
                    MotionEvent.ACTION_UP -> {
                        //when the action is up
                        val POSITION_TO_ARRIVE_WITH_ERROR =
                            POSITION_TO_ARRIVE - (POSITION_TO_ARRIVE / 25)
                        if (view.x <= POSITION_TO_ARRIVE_WITH_ERROR) {
                            //TODO Activated || Enable the sharing
                            //Go back to the start position
                            view.animate().x(cardStart).setDuration(500).start()
                            //Go all to left
                            view.animate().x(POSITION_ALL_TO_LEFT).setDuration(500).start()
                            Handler().postDelayed(
                                {
                                    /*view.animate().x(-(POSITION_ALL_TO_LEFT * 2)).setDuration(0)
                                        .start()*/
                                    view.animate().x(cardStart).setDuration(100).start()
                                }, 500
                            )
                        } else {
                            //TODO Not activated (cancelled)
                            view.animate().x(cardStart).setDuration(500).start()
                        }
                    }
                }

                // required to by-pass lint warning
                view.performClick()
                return@OnTouchListener true
            }
        )
        */

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
        val section_id: TextView = view.findViewById(R.id.textViewSectionSection)
        val title: TextView = view.findViewById(R.id.textViewSectionTitle)
        val sectionCard: CardView = view.findViewById(R.id.cardViewSection)

        val section_text = view.resources.getString(R.string.section_id_text)
    }
}
