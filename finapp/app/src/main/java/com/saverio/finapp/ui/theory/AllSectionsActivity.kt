package com.saverio.finapp.ui.theory

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isGone
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.saverio.finapp.R
import com.saverio.finapp.db.DatabaseHandler
import com.saverio.finapp.db.SectionsModel
import java.lang.Exception


class AllSectionsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_all_sections)

        val bundle = intent.extras

        var chapterId: Int? = null
        var sectionId: String? = null
        var initialSearch = ""

        if (bundle != null) {
            chapterId = bundle.getInt("chapter_id")
            initialSearch = bundle.getString("search")!!
        }
        val databaseHandler = DatabaseHandler(this)

        val getSections = databaseHandler.getSections(chapter = chapterId)
        setupRecyclerView(clear = true, chapter = chapterId, getSections = getSections)

        val searchBox: EditText = findViewById(R.id.editTextSearchSections)
        searchBox.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
                if (s.toString() != "") {
                    search(expression = s.toString(), chapterId = chapterId, sectionId = null)
                } else {
                    setupRecyclerView(clear = true, chapter = chapterId, getSections = getSections)
                }
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
        })
        searchBox.setOnKeyListener(View.OnKeyListener { v, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_UP) {
                //when tap "enter" or "go"
                v.hideSoftInput()
                v.clearFocus()
                return@OnKeyListener true
            }
            false
        })

        if (initialSearch != "") {
            searchBox.setText(initialSearch)
            showSoftKeyboard(searchBox)
        }

        val actionBar = getSupportActionBar()
        if (actionBar != null) {
            //show the back button in the action bar
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.title = getString(R.string.chapter_id_text, chapterId)
            actionBar.subtitle = getString(R.string.title_sections)
        }
    }

    private fun showSoftKeyboard(view: View) {
        if (view.requestFocus()) {
            val inputMethodManager: InputMethodManager =
                getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)
        }
    }

    private fun View.hideSoftInput() {
        //hide the keyboard
        val inputMethodManager =
            context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(windowToken, 0)
    }

    fun search(expression: String, chapterId: Int? = null, sectionId: String? = null) {
        val databaseHandler = DatabaseHandler(this)
        val expressionToSearch: String = expression
        val getSectionsSearch = databaseHandler.searchInTheory(
            chapter = chapterId,
            section = sectionId,
            expression = expressionToSearch
        )
        setupRecyclerView(clear = true, search = true, chapter = chapterId, getSectionsSearch)
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

    override fun onDestroy() {
        super.onDestroy()
    }

    fun setupRecyclerView(
        clear: Boolean = false,
        search: Boolean = false,
        chapter: Int? = null,
        getSections: ArrayList<SectionsModel>
    ) {
        try {
            val sectionsItemsList: RecyclerView = findViewById(R.id.sections_items_list)
            val noSectionsAvailableText: TextView = findViewById(R.id.no_sections_available_text)
            val editTextSearchSections: EditText = findViewById(R.id.editTextSearchSections)

            //val getSections = DatabaseHandler(this).getSections(chapter = chapter)

            if (clear) sectionsItemsList.adapter = null

            if (search) noSectionsAvailableText.text = getString(R.string.no_results_section_text)
            else noSectionsAvailableText.text = getString(R.string.no_sections_available_text)

            if (getSections.size > 0) {
                if (!search) editTextSearchSections.isGone = false
                noSectionsAvailableText.visibility = View.GONE
                sectionsItemsList.visibility = View.VISIBLE

                sectionsItemsList.layoutManager = LinearLayoutManager(this)
                //newsItemsList.setHasFixedSize(true)
                val itemAdapter = AllSectionsItemAdapter(this, getSections, chapter)
                sectionsItemsList.adapter = itemAdapter
            } else {
                if (!search) editTextSearchSections.isGone = true
                noSectionsAvailableText.visibility = View.VISIBLE
                sectionsItemsList.visibility = View.GONE
            }
        }catch (e:Exception) {

        }
    }

    override fun onResume() {
        //supportActionBar?.hide()
        super.onResume()
    }
}