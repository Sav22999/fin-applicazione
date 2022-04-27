package com.saverio.finapp.ui.statistics

import android.content.Intent
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
import com.saverio.finapp.R
import com.saverio.finapp.databinding.FragmentStatisticsBinding
import com.saverio.finapp.db.DatabaseHandler
import com.saverio.finapp.ui.quiz.ResultsSimulationQuizActivity


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
        val showHideButton = binding.showHideDetailsStatisticsText

        val correctAnswers =
            databaseHandler.getWrongCorrectSkippedAnswersStatistics(filter = "c").size
        val skippedQuestions =
            databaseHandler.getWrongCorrectSkippedAnswersStatistics(filter = "s").size
        val wrongAnswers =
            databaseHandler.getWrongCorrectSkippedAnswersStatistics(filter = "w").size
        val totalAnswers = correctAnswers + skippedQuestions + wrongAnswers
        if (totalAnswers > 0) {
            val widthBar = ((activity as MainActivity).getScreenWidth() / 2) - 25

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
            showHideButton.isGone = false

            /*
            val percentCorrect = (100 * correctAnswers) / totalAnswers
            val percentSkipped = (100 * skippedQuestions) / totalAnswers
            val percentWrong = (100 * wrongAnswers) / totalAnswers
            */

            binding.textViewTotalQuestionsStatistics.text = getString(
                R.string.total_questions_details_statistics_text, totalAnswers
            )
            binding.textViewCorrectAnswersStatistics.text = getString(
                R.string.correct_answers_details_statistics_text, correctAnswers
            )
            binding.textViewSkippedQuestionsStatistics.text = getString(
                R.string.skipped_questions_details_statistics_text, skippedQuestions
            )
            binding.textViewWrongAnswersStatistics.text = getString(
                R.string.wrong_answers_details_statistics_text, wrongAnswers
            )

            showHideButton.setOnClickListener {
                if (binding.layoutConstraintDetailsStatistics.isGone) {
                    //show
                    binding.showHideDetailsStatisticsText.text =
                        getString(R.string.hide_details_text)
                    binding.layoutConstraintDetailsStatistics.isGone = false
                } else {
                    //hide
                    binding.showHideDetailsStatisticsText.text =
                        getString(R.string.show_details_text)
                    binding.layoutConstraintDetailsStatistics.isGone = true
                }
            }
        } else {
            noSimulation.isGone = false
            showHideButton.isGone = true
        }

        binding.cardViewStatisticsViewAllSimulations.setOnClickListener {
            val intent = Intent(requireContext(), AllSimulations::class.java)
            startActivity(intent)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}