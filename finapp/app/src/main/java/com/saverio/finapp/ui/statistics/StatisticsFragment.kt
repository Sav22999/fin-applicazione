package com.saverio.finapp.ui.statistics

import android.os.Bundle
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.cardview.widget.CardView
import androidx.core.view.isGone
import androidx.core.view.marginStart
import androidx.core.view.updateLayoutParams
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.saverio.finapp.MainActivity
import com.saverio.finapp.databinding.FragmentStatisticsBinding
import com.saverio.finapp.db.DatabaseHandler


class StatisticsFragment : Fragment() {

    private var _binding: FragmentStatisticsBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val statisticsViewModel =
            ViewModelProvider(this).get(StatisticsViewModel::class.java)

        _binding = FragmentStatisticsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        loadBar()

        return root
    }

    fun loadBar() {
        val bar: CardView = binding.cardViewStatisticsBar
        val correct: View = binding.statisticsBarCorrectAnswers
        val skipped: View = binding.statisticsBarSkippedQuestions
        val wrong: View = binding.statisticsBarWrongAnswers

        val databaseHandler = DatabaseHandler(requireContext())

        val noSimulation = binding.noSimulationDoneText

        val correctAnswers =
            databaseHandler.getWrongCorrectSkippedAnswersStatistics(filter = "c").size
        val skippedQuestions =
            databaseHandler.getWrongCorrectSkippedAnswersStatistics(filter = "s").size
        val wrongAnswers =
            databaseHandler.getWrongCorrectSkippedAnswersStatistics(filter = "w").size
        val totalAnswers = correctAnswers + skippedQuestions + wrongAnswers
        if (totalAnswers > 0) {
            val widthBar = ((activity as MainActivity).getScreenWidth() / 2) - 25
            println(widthBar)
            val widthCorrect = (widthBar * correctAnswers) / totalAnswers
            val widthSkipped = (widthBar * skippedQuestions) / totalAnswers
            val widthWrong = (widthBar * wrongAnswers) / totalAnswers
            correct.updateLayoutParams { width = widthCorrect }
            skipped.updateLayoutParams { width = widthSkipped }
            //wrong.updateLayoutParams { width = widthWrong }

            correct.isGone = widthCorrect == 0
            skipped.isGone = widthSkipped == 0
            wrong.isGone = widthWrong == 0
            noSimulation.isGone = true
        } else {
            noSimulation.isGone = false
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}