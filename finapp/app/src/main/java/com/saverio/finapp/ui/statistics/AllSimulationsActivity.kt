package com.saverio.finapp.ui.statistics

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.core.view.isGone
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.saverio.finapp.R
import com.saverio.finapp.db.DatabaseHandler
import com.saverio.finapp.db.StatisticsModel
import com.saverio.finapp.ui.quiz.ResultSimulationItemAdapter

class AllSimulationsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_all_simulations)

        val databaseHandler = DatabaseHandler(this)
        val getSimulations = databaseHandler.getAllSimulations()
        databaseHandler.close()

        setupRecyclerView(clear = true, getSimulations)

        val actionBar = getSupportActionBar()
        if (actionBar != null) {
            //show the back button in the action bar
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.title = getString(R.string.statistics_all_simulations_title)
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
        getSimulations: ArrayList<String>
    ) {
        try {
            val resultsItemsList: RecyclerView = findViewById(R.id.all_simulations_items_list)
            val noResults: TextView = findViewById(R.id.no_simulations_available_text)
            val databaseHandler = DatabaseHandler(this)

            noResults.text = getString(R.string.no_simulations_available_text)

            if (clear) resultsItemsList.adapter = null

            if (getSimulations.size > 0) {
                noResults.visibility = View.GONE
                resultsItemsList.visibility = View.VISIBLE

                resultsItemsList.layoutManager = LinearLayoutManager(this)
                //binding.newsItemsList.setHasFixedSize(true)
                var getStatistics = ArrayList<StatisticsModel>()
                getSimulations.forEach {
                    val getStatisticsTemp = databaseHandler.getStatistics(datetime = it)[0]
                    //println(getStatisticsTemp.id)
                    getStatistics.add(getStatisticsTemp)
                }
                val itemAdapter = AllSimulationsItemAdapter(this, getStatistics)
                resultsItemsList.adapter = itemAdapter
            } else {
                noResults.visibility = View.VISIBLE
                resultsItemsList.visibility = View.GONE
            }
            databaseHandler.close()
        }catch (e:Exception) {

        }
    }
}