package com.saverio.finapp.ui.account

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isGone
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.saverio.finapp.MainActivity
import com.saverio.finapp.databinding.FragmentAccountBinding
import com.saverio.finapp.ui.profile.ProfileActivity
import com.saverio.finapp.ui.settings.SettingsActivity
import com.saverio.finapp.ui.statistics.AllSimulations
import com.saverio.finapp.ui.statistics.StatisticsActivity

class AccountFragment : Fragment() {

    private var _binding: FragmentAccountBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val accountViewModel =
            ViewModelProvider(this).get(AccountViewModel::class.java)

        _binding = FragmentAccountBinding.inflate(inflater, container, false)
        val root: View = binding.root

        (activity as MainActivity).currentFragment = "account"

        binding.cardViewAccountSettings.setOnClickListener {
            val intent = Intent(requireContext(), SettingsActivity::class.java)
            startActivity(intent)
        }

        binding.cardViewAccountViewProfile.setOnClickListener {
            val intent = Intent(requireContext(), ProfileActivity::class.java)
            startActivity(intent)
        }

        binding.cardViewAccountViewStatistics.setOnClickListener {
            val intent = Intent(requireContext(), StatisticsActivity::class.java)
            startActivity(intent)
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onResume() {
        val main = (activity as MainActivity)
        if (main.getVariable("reset", false)!!) {
            main.setVariable("reset", false)
            //resetted
            main.checkFirstRunTutorial()
        }
        super.onResume()
    }
}