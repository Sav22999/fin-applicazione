package com.saverio.finapp.ui.quiz

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isGone
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.tabs.TabLayout
import com.saverio.finapp.MainActivity
import com.saverio.finapp.R
import com.saverio.finapp.databinding.FragmentQuizBinding
import com.saverio.finapp.db.ChaptersModel
import com.saverio.finapp.db.DatabaseHandler

class QuizFragment : Fragment() {

    private var _binding: FragmentQuizBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val quizViewModel =
            ViewModelProvider(this).get(QuizViewModel::class.java)

        _binding = FragmentQuizBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val databaseHandler = DatabaseHandler(requireContext())
        val getChapters = databaseHandler.getChapters()
        setupRecyclerView(clear = true, search = false, getChapters)

        val main = activity as MainActivity
        main.allCheckes()

        val simulationCard = binding.cardViewQuizSimulation
        simulationCard.setOnClickListener {
            val intent = Intent(context, SimulationQuizActivity::class.java)
            startActivity(intent)
        }
        val mistakesCard = binding.cardViewQuizMistakes
        mistakesCard.setOnClickListener {
            val intent = Intent(context, MistakesQuizActivity::class.java)
            startActivity(intent)
        }

        (activity as MainActivity).pushStatistics()

        val tabs = binding.tabLayout
        val tabsLayout = arrayOf(binding.tab1Layout, binding.tab2Layout)
        tabsLayout[0].isGone = false
        tabs.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                tabsLayout.forEach {
                    it.isGone = true
                }
                tabsLayout[tab.position].isGone = false
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
            }
        })
        return root
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
        if (clear) binding.chaptersQuizItemsList.adapter = null

        if (search) binding.noChaptersQuizAvailableText.text =
            getString(R.string.no_results_chapter_text)
        else binding.noChaptersQuizAvailableText.text =
            getString(R.string.no_chapters_available_text)

        if (getChapters.size > 0) {
            binding.noChaptersQuizAvailableText.visibility = View.GONE
            binding.chaptersQuizItemsList.visibility = View.VISIBLE

            binding.chaptersQuizItemsList.layoutManager = LinearLayoutManager(requireContext())
            //binding.newsItemsList.setHasFixedSize(true)
            val itemAdapter = ChaptersQuizItemAdapter(requireContext(), getChapters)
            binding.chaptersQuizItemsList.adapter = itemAdapter
        } else {
            binding.noChaptersQuizAvailableText.visibility = View.VISIBLE
            binding.chaptersQuizItemsList.visibility = View.GONE
        }
    }
}