package com.saverio.finapp.ui.messages

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.Image
import android.os.AsyncTask
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isGone
import androidx.core.view.isInvisible
import androidx.recyclerview.widget.RecyclerView
import com.saverio.finapp.R
import com.saverio.finapp.api.messages.AllMessagesItemsList
import com.saverio.finapp.api.messages.AllMessagesList
import com.saverio.finapp.db.DatabaseHandler
import com.saverio.finapp.db.SectionsModel
import org.w3c.dom.Text
import java.security.MessageDigest


class AllMessagesItemAdapter(
    private val context: Context,
    private val items: List<AllMessagesItemsList>,
    private val username: String
) :
    RecyclerView.Adapter<AllMessagesItemAdapter.ItemViewHolder>() {

    var sentFromAnotherMap: MutableMap<Int, TextView> = mutableMapOf()
    var sentFromMeMap: MutableMap<Int, TextView> = mutableMapOf()
    var cardViewImageMap: MutableMap<Int, CardView> = mutableMapOf()
    var imageViewImageMap: MutableMap<Int, ImageView> = mutableMapOf()

    override fun onCreateViewHolder(parent: ViewGroup, type: Int): ItemViewHolder {
        return ItemViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.discussion_message_recyclerview, parent, false)
        )
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = items[position]

        if (item.username == username) {
            //"you"
            holder.constraintLayoutMe.isGone = false
            holder.constraintLayoutUser.isGone = true
            holder.sentFromMe.text = holder.itemView.resources.getString(
                R.string.sent_message_text,
                item.username,
                item.datetime
            )
            holder.textViewFromMe.text = item.text
            holder.sentFromMe.isGone = false
            sentFromMeMap[position] = holder.sentFromMe
            if (position > 0 && sentFromMeMap.isNotEmpty() && item.username == items[position - 1].username) {
                sentFromMeMap[position - 1]?.isGone = true
            }
        } else {
            //"other user"
            holder.constraintLayoutMe.isGone = true
            holder.constraintLayoutUser.isGone = false
            holder.sentFromAnother.text = holder.itemView.resources.getString(
                R.string.sent_message_text,
                item.username,
                item.datetime
            )
            holder.textViewFromAnother.text = item.text
            holder.sentFromAnother.isGone = false
            holder.cardViewImage.isGone = false

            sentFromAnotherMap[position] = holder.sentFromAnother
            cardViewImageMap[position] = holder.cardViewImage
            imageViewImageMap[position] = holder.imageViewAccount

            val imageUrl = "https://www.gravatar.com/avatar/${item.email.toMD5()}?s=50&r=g"

            if (position > 0 && sentFromAnotherMap.isNotEmpty() && cardViewImageMap.isNotEmpty() && item.username == items[position - 1].username) {
                sentFromAnotherMap[position - 1]?.isGone = true
                cardViewImageMap[position - 1]?.isInvisible = true
                if (imageViewImageMap[position - 1] != null) {
                    holder.imageViewAccount.setImageDrawable(imageViewImageMap[position - 1]?.drawable)
                } else {
                    DownloadImageFromInternet(holder.imageViewAccount).execute(imageUrl)
                }
            } else {
                DownloadImageFromInternet(holder.imageViewAccount).execute(imageUrl)
            }
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }

    class ItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val constraintLayoutMe: ConstraintLayout =
            view.findViewById(R.id.constraintLayoutMessageFromMe)
        val constraintLayoutUser: ConstraintLayout =
            view.findViewById(R.id.constraintLayoutMessageFromAnother)
        val cardViewImage: CardView = view.findViewById(R.id.cardViewAccountImage)
        val imageViewAccount: ImageView = view.findViewById(R.id.imageViewAccountImage)
        val textViewFromMe: TextView = view.findViewById(R.id.textViewMessageFromMe)
        val textViewFromAnother: TextView = view.findViewById(R.id.textViewReplyToMessage)
        val sentFromMe: TextView = view.findViewById(R.id.textViewSentMessageFromMe)
        val sentFromAnother: TextView = view.findViewById(R.id.textViewSentMessageFromAnother)
    }

    fun String.toMD5(): String {
        val bytes = MessageDigest.getInstance("MD5").digest(this.toByteArray())
        return bytes.toHex()
    }

    fun ByteArray.toHex(): String {
        return joinToString("") { "%02x".format(it) }
    }

    private inner class DownloadImageFromInternet(var imageView: ImageView) :
        AsyncTask<String, Void, Bitmap?>() {
        init {
            //initial phase of download image
            imageView.isGone = true
        }

        override fun doInBackground(vararg urls: String): Bitmap? {
            val imageURL = urls[0]
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
}
