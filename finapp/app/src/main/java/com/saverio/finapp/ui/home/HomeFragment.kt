package com.saverio.finapp.ui.home

import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.saverio.finapp.MainActivity
import com.saverio.finapp.R
import com.saverio.finapp.databinding.FragmentHomeBinding
import com.saverio.finapp.db.DatabaseHandler
import com.saverio.finapp.db.NewsModel
import com.saverio.finapp.db.StatisticsModel

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    lateinit var swipeRefreshLayout: SwipeRefreshLayout

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val homeViewModel =
            ViewModelProvider(requireActivity()).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val main = activity as MainActivity
        swipeRefreshLayout = binding.swipeRefreshLayout
        homeViewModel.newsChanged.observe(requireActivity()) { o ->
            if (o) {
                setupRecyclerView(main, clear = true)
            }
        }
        homeViewModel.loading.observe(requireActivity()) { o ->
            swipeRefreshLayout.isRefreshing = o
        }

        swipeRefreshLayout.setColorSchemeColors(
            ContextCompat.getColor(
                requireContext(),
                R.color.white
            )
        )

        swipeRefreshLayout.setOnRefreshListener {
            checkNews(main)
            checkLoading()
        }
        setupRecyclerView(main, clear = true)
        Handler().postDelayed(Runnable {
            if (main.checkForInternetConnection(requireContext())) {
                checkNews(main)
            }
        }, 500)

        binding.button.setOnClickListener {
            //CHECK

            //println("Checking")
            (activity as MainActivity).checkNews()
            //println("Checked")
        }
        binding.button2.setOnClickListener {
            //SHOW

            //println("Showing")
            val allNews = (activity as MainActivity).getNews()
            allNews.forEach {
                println("${it.id} | ${it.type} | ${it.text}")
            }
            val databaseHandler = DatabaseHandler(requireContext())
            val allStatistics = databaseHandler.getStatistics()
            allStatistics.forEach {
                println("id:${it.id} | type:${it.type} | question:${it.question_id} | datetime:${it.datetime} | correct:${it.correct_answer} | user:${it.user_answer}")
            }
            //println("Shown")
        }
        binding.button3.setOnClickListener {
            //DELETE

            //println("Deleting")
            val allNews = (activity as MainActivity).getNews()
            allNews.forEach {
                (activity as MainActivity).delete(
                    NewsModel(it.id, 0, "", null, null, null, "")
                )
            }
            val databaseHandler = DatabaseHandler(requireContext())
            val allStatistics = databaseHandler.getStatistics()
            allStatistics.forEach {
                databaseHandler.deleteAllStatistics()
            }
            //println("Deleted")
        }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun checkLoading() {
        val homeViewModel =
            ViewModelProvider(requireActivity()).get(HomeViewModel::class.java)
        if (homeViewModel.loading.value == false) {
            swipeRefreshLayout.isRefreshing = false
        } else {
            Handler().postDelayed(Runnable {
                checkLoading()
            }, 1000)
        }
    }

    fun checkNews(main: MainActivity) {
        Handler().postDelayed(
            {
                checkNews(main)
            },
            (60000 * 10)
        ) //(1minute*10)=10 minutes (in milliseconds)  60000milliseconds=1minute
        main.checkNews()
    }

    fun setupRecyclerView(main: MainActivity, clear: Boolean = false) {
        if (clear) binding.newsItemsList.adapter = null
        if (main.getNews().size > 0) {
            binding.noNewsAvailableText.visibility = View.GONE
            binding.newsItemsList.visibility = View.VISIBLE

            binding.newsItemsList.layoutManager = LinearLayoutManager(requireContext())
            //binding.newsItemsList.setHasFixedSize(true)
            val itemAdapter = NewsItemAdapter(requireContext(), main.getNews())
            binding.newsItemsList.adapter = itemAdapter
        } else {
            binding.noNewsAvailableText.visibility = View.VISIBLE
            binding.newsItemsList.visibility = View.GONE
        }
    }
}