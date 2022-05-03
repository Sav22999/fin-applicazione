package com.saverio.finapp.ui.firstrun

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.MotionEvent
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.view.isGone
import androidx.core.view.isInvisible
import androidx.core.view.marginEnd
import androidx.core.view.marginStart
import com.saverio.finapp.MainActivity
import com.saverio.finapp.MainActivity.Companion.FIRST_RUN_APP
import com.saverio.finapp.R

class FirstRunActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_first_run)

        setupGestures()

        val actionBar = getSupportActionBar()
        if (actionBar != null) {
            //hide the action bar
            actionBar.setDisplayHomeAsUpEnabled(false)
            actionBar.hide()
        }
    }

    fun animationSwipeLeft(cardView: CardView, iterator: Int) {
        //cardView.isGone = true
        val initialX = cardView.marginStart.toFloat()
        cardView.animate().x((cardView.width + cardView.marginStart + cardView.marginEnd) * 2f)
            .setDuration(0).start()
        if (iterator != 0) cardView.animate().x(initialX).setDuration(300).start()
        //cardView.isGone = false

        animateNavPointsBar(iterator)
    }

    fun animationSwipeRight(cardView: CardView, iterator: Int) {
        //cardView.isGone = true
        val initialX = cardView.marginStart.toFloat()
        cardView.animate().x(-((cardView.marginStart * 2f) + cardView.width)).setDuration(0).start()
        if (iterator != 0) cardView.animate().x(initialX).setDuration(400).start()
        //cardView.isGone = false

        animateNavPointsBar(iterator)
    }

    fun animationEnter(cardView: CardView) {
        animationSwipeRight(cardView, 0)
    }

    fun animationLeave(cardView: CardView) {
        animationSwipeLeft(cardView, 0)
    }

    fun animateNavPointsBar(number: Int) {
        if (number > 0) {
            val newX = when (number) {
                1 -> {
                    val viewBar1: View = findViewById(R.id.viewBar1)
                    viewBar1.x
                }
                2 -> {
                    val viewBar2: View = findViewById(R.id.viewBar2)
                    viewBar2.x
                }
                3 -> {
                    val viewBar3: View = findViewById(R.id.viewBar3)
                    viewBar3.x
                }
                else -> {
                    /*nothing*/
                    0
                }
            }.toString().toFloat()
            val viewBarSelected: View = findViewById(R.id.viewBarSelected)
            viewBarSelected.animate().x(newX).setDuration(500).start()
        }
    }

    fun setupGestures() {
        val cardView1: CardView = findViewById(R.id.cardView1FirstRun)
        val cardView2: CardView = findViewById(R.id.cardView2FirstRun)
        val cardView3: CardView = findViewById(R.id.cardView3FirstRun)

        Handler().postDelayed({
            animationSwipeLeft(cardView1, 1)
            animationLeave(cardView2)
            animationLeave(cardView3)
        }, 200)

        val startX1 = cardView1.x + (cardView1.marginStart)

        setMoveGesture(1, null, cardView1, cardView2, startX1)
        setMoveGesture(2, cardView1, cardView2, cardView3, startX1)
        setMoveGesture(3, cardView2, cardView3, null, startX1)

        val buttonForward1: Button = findViewById(R.id.buttonForward1FirstRun)
        buttonForward1.setOnClickListener {
            animationSwipeLeft(cardView2, 2) //enter
            animationLeave(cardView1) //out
        }

        val buttonBack2: Button = findViewById(R.id.buttonBack2FirstRun)
        val buttonForward2: Button = findViewById(R.id.buttonForward2FirstRun)
        buttonBack2.setOnClickListener {
            animationSwipeRight(cardView1, 1) //enter
            animationLeave(cardView2) //out
        }
        buttonForward2.setOnClickListener {
            animationSwipeLeft(cardView3, 3) //enter
            animationLeave(cardView2) //out
        }

        val buttonBack3: Button = findViewById(R.id.buttonBack3FirstRun)
        val buttonForward3: Button = findViewById(R.id.buttonForward3FirstRun)
        buttonBack3.setOnClickListener {
            animationSwipeRight(cardView2, 2) //enter
            animationLeave(cardView3) //out
        }
        buttonForward3.setOnClickListener {
            //set "FirstRun" as "true"
            setVariable(FIRST_RUN_APP, false)

            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent) //run MainActivity
            finish() //close FirstRunActivity
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    fun setMoveGesture(
        currentIterator: Int,
        previousCardView: CardView?,
        cardView: CardView,
        nextCardView: CardView?,
        startX: Float
    ) {
        var direction = "right" // "right" or "left"
        cardView.setOnTouchListener(
            View.OnTouchListener { view, event ->
                val X_TO_ACTIVATE_LEFT = -(view.width / 4 - view.width / 30).toFloat()
                val X_TO_ACTIVATE_RIGHT = (view.width / 4 - view.width / 30).toFloat() / 2

                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        //onlyClicked = true
                    }
                    MotionEvent.ACTION_MOVE -> {
                        //onlyClicked = false

                        // get the new co-ordinate of X-axis
                        val newX = event.rawX - (view.width / 2) - view.marginStart

                        // carry out swipe only if newX > MAX_SWIPE_LEFT_DISTANCE, i.e.
                        // the card is swiped to the left side, not to the right
                        if (newX <= (startX)) {
                            //left
                            direction = "left"
                            if (nextCardView != null) {
                                view.animate()
                                    .x(newX)
                                    .setDuration(0)
                                    .start()
                            }
                        } else {
                            //right
                            direction = "right"
                            if (previousCardView != null) {
                                view.animate()
                                    .x(newX)
                                    .setDuration(0)
                                    .start()
                            }
                            //Disabled because you cannot go back in this section
                        }
                    }
                    MotionEvent.ACTION_UP -> {
                        //when the action is up
                        //"onClick"
                        //if (onlyClicked){}

                        //"onActionUp"
                        if (view.x <= X_TO_ACTIVATE_LEFT || view.x >= X_TO_ACTIVATE_RIGHT) {
                            if (direction == "left" && nextCardView != null) {
                                //Activated || left
                                animationSwipeLeft(nextCardView, currentIterator + 1) //enter
                                animationLeave(cardView) //out
                            } else if (direction == "right" && previousCardView != null) {
                                //Activated || right
                                animationLeave(cardView) //out
                                animationSwipeRight(previousCardView, currentIterator - 1) //enter
                            }
                        } else {
                            //Not activated (cancelled)
                            cardView.animate().x(startX).setDuration(500).start()
                        }
                    }
                }

                // required to by-pass lint warning
                view.performClick()
                return@OnTouchListener true
            }
        )
    }

    private fun setVariable(variable: String, value: Boolean = false) {
        getSharedPreferences("QuizNuotoPreferences", Context.MODE_PRIVATE).edit()
            .putBoolean(variable, value).apply()
    }
}