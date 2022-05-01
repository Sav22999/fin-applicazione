package com.saverio.finapp.ui.theory

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.core.view.isGone
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.saverio.finapp.MainActivity
import com.saverio.finapp.R
import com.saverio.finapp.databinding.FragmentTheoryBinding
import com.saverio.finapp.db.ChaptersModel
import com.saverio.finapp.db.DatabaseHandler

class TheoryFragment : Fragment() {

    private var _binding: FragmentTheoryBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val theoryViewModel =
            ViewModelProvider(this).get(TheoryViewModel::class.java)

        _binding = FragmentTheoryBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val databaseHandler = DatabaseHandler(requireContext())
        val getChapters = databaseHandler.getChapters()
        theoryViewModel.chaptersChanged.observe(requireActivity()) { o ->
            if (o) {
                setupRecyclerView(clear = true, search = false, getChapters)
            }
        }
        setupRecyclerView(clear = true, search = false, getChapters)


        binding.editTextSearchChapters.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
                if (s.toString() != "") {
                    search(expression = s.toString())
                } else {
                    setupRecyclerView(clear = true, search = false, getChapters)
                }
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
        })
        binding.editTextSearchChapters.setOnKeyListener(View.OnKeyListener { v, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_UP) {
                //when tap "enter" or "go"
                v.hideSoftInput()
                v.clearFocus()
                return@OnKeyListener true
            }
            false
        })


        return root
    }

    fun View.hideSoftInput() {
        //hide the keyboard
        val inputMethodManager =
            context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(windowToken, 0)
    }

    fun search(expression: String) {
        val databaseHandler = DatabaseHandler(requireContext())
        val expressionToSearch: String = expression
        val getSectionsSearch = databaseHandler.searchInTheory(
            chapter = null,
            section = null,
            expression = expressionToSearch
        )
        val getChaptersSearch = ArrayList<ChaptersModel>()
        val onlyChaptersAlreadyAdded = ArrayList<Int?>()
        getSectionsSearch.forEach {
            if (!(it.chapter in onlyChaptersAlreadyAdded) && it.chapter != null) {
                onlyChaptersAlreadyAdded.add(it.chapter)
                getChaptersSearch.add(databaseHandler.getChapter(it.chapter!!))
            }
        }
        setupRecyclerView(clear = true, search = true, getChapters = getChaptersSearch)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun setupRecyclerView(
        clear: Boolean = false,
        search: Boolean = false,
        getChapters: ArrayList<ChaptersModel>
    ) {
        if (clear) binding.chaptersItemsList.adapter = null

        if (search) binding.noChaptersAvailableText.text =
            getString(R.string.no_results_chapter_text)
        else binding.noChaptersAvailableText.text = getString(R.string.no_chapters_available_text)

        if (getChapters.size > 0) {
            binding.editTextSearchChapters.isGone = false
            binding.noChaptersAvailableText.visibility = View.GONE
            binding.chaptersItemsList.visibility = View.VISIBLE

            binding.chaptersItemsList.layoutManager = LinearLayoutManager(requireContext())
            //binding.newsItemsList.setHasFixedSize(true)
            val itemAdapter = ChaptersItemAdapter(
                context = requireContext(),
                items = getChapters,
                search = binding.editTextSearchChapters.text.toString()
            )
            binding.chaptersItemsList.adapter = itemAdapter
        } else {
            binding.editTextSearchChapters.isGone = true
            binding.noChaptersAvailableText.visibility = View.VISIBLE
            binding.chaptersItemsList.visibility = View.GONE
        }
    }
}