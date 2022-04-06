package com.saverio.finapp.ui.home

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.saverio.finapp.R
import com.saverio.finapp.db.NewsModel
import kotlin.math.min

class ItemAdapter(private val context: Context, private val items: ArrayList<NewsModel>) :
    RecyclerView.Adapter<ItemAdapter.ItemViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, type: Int): ItemViewHolder {
        //change the home_recyclerview layout

        return ItemViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.home_recyclerview, parent, false)
        )
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = items[position]

        holder.button.text = item.id.toString()


        /*
        holder.cardView.setOnTouchListener(
            View.OnTouchListener { view, event ->
                val displayMetrics = view.resources.displayMetrics
                val cardWidth = view.width
                val cardStart = (displayMetrics.widthPixels.toFloat() / 2) - (cardWidth / 2)
                val MAX_SWIPE_LEFT_DISTANCE = 100
                val POSITION_TO_ARRIVE = MAX_SWIPE_LEFT_DISTANCE.toFloat() - (cardWidth / 2)
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
                        //Go back to the start position when the action is up
                        val POSITION_TO_ARRIVE_WITH_ERROR =
                            POSITION_TO_ARRIVE - (POSITION_TO_ARRIVE / 25)
                        if (view.x <= POSITION_TO_ARRIVE_WITH_ERROR) {
                            //TODO Activated || Enable the sharing
                        } else {
                            //TODO Not activated (cancelled)
                        }
                        view.animate().x(cardStart)
                            .setDuration(500).start()
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
            //holder.background
        } else {
            //odd position
        }
    }

    override fun getItemViewType(position: Int): Int {
        return items[position].type
    }

    override fun getItemCount(): Int {
        return items.size
    }

    class ItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        //val item = view.
        //val background = view.
        val button: Button = view.findViewById(R.id.button5)
        val cardView: CardView = view.findViewById(R.id.cardView)
        val constraintLayoutRecyclerCard: ConstraintLayout =
            view.findViewById(R.id.constraintLayoutRecyclerCard)
    }

    companion object {
        const val TYPE_NEWS_0 = 0 //General
        const val TYPE_NEWS_1 = 1 //Swimming pill
        const val TYPE_NEWS_2 = 2 //Did you know...
        const val TYPE_NEWS_3 = 3 //News about rules
        const val TYPE_NEWS_4 = 4 //News and alerts from app
    }
}
