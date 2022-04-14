package com.saverio.finapp.ui.quiz

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.saverio.finapp.R
import com.saverio.finapp.db.ChaptersModel
import com.saverio.finapp.db.DatabaseHandler
import com.saverio.finapp.db.SimulationQuizzesModel
import com.saverio.finapp.db.StatisticsModel

class ResultsSimulationQuizActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_results_simulation_quiz)

        val bundle = intent.extras

        var datetimeSimulation = ""

        if (bundle != null) {
            datetimeSimulation = bundle.getString("datetime")!!
        }

        if (datetimeSimulation != "") {
            val databaseHandler = DatabaseHandler(this)
            val getStatistics = databaseHandler.getStatistics(
                type = 1,
                datetime = datetimeSimulation
            )
            setupRecyclerView(clear = true, search = false, getStatistics = getStatistics)
        } else {
            val noResults: TextView = findViewById(R.id.no_results_available_text)
            noResults.text = getString(R.string.results_error_text)
            noResults.visibility = View.VISIBLE
        }

        val actionBar = getSupportActionBar()
        if (actionBar != null) {
            //show the back button in the action bar
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.title = ""
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        //add the back button event in the actionbar
        when (item.getItemId()) {
            android.R.id.home -> {
                onBackPressed()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        finish()
        super.onBackPressed()
    }

    fun setupRecyclerView(
        clear: Boolean = false,
        search: Boolean = false,
        getStatistics: ArrayList<StatisticsModel>
    ) {
        val resultsItemsList: RecyclerView = findViewById(R.id.simulation_results_items_list)
        val noResults: TextView = findViewById(R.id.no_results_available_text)

        noResults.text = getString(R.string.no_results_available_text)

        if (clear) resultsItemsList.adapter = null

        if (getStatistics.size > 0) {
            noResults.visibility = View.GONE
            resultsItemsList.visibility = View.VISIBLE

            resultsItemsList.layoutManager = LinearLayoutManager(this)
            //binding.newsItemsList.setHasFixedSize(true)
            val itemAdapter = ResultSimulationItemAdapter(this, getStatistics)
            resultsItemsList.adapter = itemAdapter
        } else {
            noResults.visibility = View.VISIBLE
            resultsItemsList.visibility = View.GONE
        }
    }
}