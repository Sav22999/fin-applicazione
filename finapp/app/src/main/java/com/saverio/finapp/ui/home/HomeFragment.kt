package com.saverio.finapp.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.saverio.finapp.MainActivity
import com.saverio.finapp.databinding.FragmentHomeBinding
import com.saverio.finapp.db.ChaptersModel

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        //TODO: Don't work the following methods
        binding.button.setOnClickListener {
            //SHOW

            println("Checking")
            (activity as MainActivity).checkChapters()
            println("Checked")
        }
        binding.button2.setOnClickListener {
            //SHOW

            println("Showing")
            val allChapters = (activity as MainActivity).getChapters()
            allChapters.forEach {
                println("${it.chapter} | ${it.title}")
            }
            println("Shown")
        }
        binding.button3.setOnClickListener {
            //DELETE

            println("Deleting")
            val allChapters = (activity as MainActivity).getChapters()
            allChapters.forEach {
                (activity as MainActivity).delete(ChaptersModel(it.chapter, null))
            }
            println("Deleted")
        }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}