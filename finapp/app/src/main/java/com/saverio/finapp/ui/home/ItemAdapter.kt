package com.saverio.finapp.ui.home

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.AsyncTask
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat.startActivity
import androidx.core.view.isGone
import androidx.recyclerview.widget.RecyclerView
import com.saverio.finapp.R
import com.saverio.finapp.db.NewsModel


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

        //item.id.toString()
        if (item.title != null && item.title != "" && item.title != "NULL") {
            holder.title.isGone = false
            holder.title.text = item.title
        } else {
            holder.title.isGone = true
        }
        holder.type.text = when (item.type) {
            1 -> holder.type1
            2 -> holder.type2
            3 -> holder.type3
            4 -> holder.type4
            else -> holder.type0
        }
        holder.imageType.setImageResource(
            when (item.type) {
                1 -> R.drawable.ic_pill
                2 -> R.drawable.ic_multiple_stars
                3 -> R.drawable.ic_rules
                4 -> R.drawable.ic_megaphone
                else -> R.drawable.ic_topic
            }
        )

        holder.date.text = item.date

        if (item.image != null && item.image != "" && item.image != "NULL") {
            DownloadImageFromInternet(holder.image).execute(item.image)
        } else {
            holder.image.isGone = true
        }

        if (item.link != null && item.link != "" && item.link != "NULL") {
            holder.link.isGone = false
            holder.link.setOnClickListener {
                val uri: Uri = Uri.parse(item.link)
                val intent = Intent(Intent.ACTION_VIEW, uri)
                startActivity(context, intent, null)
            }
        } else {
            holder.link.isGone = true
        }

        var first50Words = ""
        val first50WordsArray = item.text.split(" ")
        val MAX_WORDS_NUMBER = 50
        if (first50WordsArray.size > MAX_WORDS_NUMBER) {
            var counter = 1
            first50Words = first50WordsArray[0]
            while (counter < MAX_WORDS_NUMBER) {
                first50Words = "${first50Words} ${first50WordsArray[counter]}"
                counter++
            }
            first50Words += " …"
            holder.readMore.isGone = false
            holder.readMore.text = holder.readMoreText
            holder.readMore.setOnClickListener {
                if (holder.readMore.text == holder.readMoreText) {
                    //when press on read more
                    holder.readMore.text = holder.readLessText
                    holder.description.text = item.text
                } else {
                    //when press on read less
                    holder.readMore.text = holder.readMoreText
                    holder.description.text = first50Words
                }
            }
        } else {
            first50Words = item.text
            holder.readMore.isGone = true
        }

        holder.description.text = first50Words

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
        val imageType: ImageView = view.findViewById(R.id.imageViewType)
        val type: TextView = view.findViewById(R.id.textViewType)
        val date: TextView = view.findViewById(R.id.textViewDate)
        val title: TextView = view.findViewById(R.id.textViewTitle)
        val image: ImageView = view.findViewById(R.id.imageViewImage)
        val link: Button = view.findViewById(R.id.buttonLink)
        val description: TextView = view.findViewById(R.id.textViewDescription)
        val readMore: TextView = view.findViewById(R.id.textViewReadMore)

        val type0 = view.resources.getString(R.string.type_0_general_news_text)
        val type1 = view.resources.getString(R.string.type_1_pill_news_text)
        val type2 = view.resources.getString(R.string.type_2_did_you_know_news_text)
        val type3 = view.resources.getString(R.string.type_3_news_rules_news_text)
        val type4 = view.resources.getString(R.string.type_4_news_and_alerts_news_text)
        val readMoreText = view.resources.getString(R.string.read_more_text)
        val readLessText = view.resources.getString(R.string.read_less_text)
    }

    private inner class DownloadImageFromInternet(var imageView: ImageView) :
        AsyncTask<String, Void, Bitmap?>() {
        init {
            //initial phase of download image
            imageView.isGone = true
        }

        override fun doInBackground(vararg urls: String): Bitmap? {
            val imageURL = urls[0]
            println(imageURL)
            var image: Bitmap? = null
            try {
                val `in` = java.net.URL(imageURL).openStream()
                image = BitmapFactory.decodeStream(`in`)
            } catch (e: Exception) {
                Log.e("Error Message", e.message.toString())
                imageView.isGone = true
            }
            return image
        }

        override fun onPostExecute(result: Bitmap?) {
            imageView.setImageBitmap(result)
            imageView.isGone = false
        }
    }

    companion object {
        const val TYPE_NEWS_0 = 0 //General
        const val TYPE_NEWS_1 = 1 //Swimming pill
        const val TYPE_NEWS_2 = 2 //Did you know...
        const val TYPE_NEWS_3 = 3 //News about rules
        const val TYPE_NEWS_4 = 4 //News and alerts from app
    }
}
