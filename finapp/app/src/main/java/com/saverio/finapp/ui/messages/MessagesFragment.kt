package com.saverio.finapp.ui.messages

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.view.isGone
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.saverio.finapp.MainActivity
import com.saverio.finapp.internet.NetworkConnection
import com.saverio.finapp.R
import com.saverio.finapp.api.ApiClient
import com.saverio.finapp.api.messages.MessagesSectionsList
import com.saverio.finapp.databinding.FragmentMessagesBinding
import com.saverio.finapp.db.DatabaseHandler
import com.saverio.finapp.db.SectionsModel
import com.saverio.finapp.ui.profile.ProfileActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MessagesFragment : Fragment() {

    private var _binding: FragmentMessagesBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    lateinit var swipeRefreshLayout: SwipeRefreshLayout

    var connected: Boolean = true

    var currentRunnable: Runnable? = null
    val mainHandler = Handler(Looper.getMainLooper())

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val messagesViewModel =
            ViewModelProvider(this).get(MessagesViewModel::class.java)

        _binding = FragmentMessagesBinding.inflate(inflater, container, false)
        val root: View = binding.root

        (activity as MainActivity).currentFragment = "messages"

        swipeRefreshLayout = binding.swipeRefreshLayoutFragmentMessages
        swipeRefreshLayout.setColorSchemeColors(
            ContextCompat.getColor(
                requireContext(),
                R.color.white
            )
        )

        swipeRefreshLayout.setOnRefreshListener {
            //getMessagesSections(startTask = false)
            swipeRefreshLayout.isRefreshing = false
            load(startTask = false)
        }

        binding.imageViewAllDiscussionsHelp.setOnClickListener {
            val builder: AlertDialog.Builder = AlertDialog.Builder(requireContext())
            builder.setTitle(R.string.message_title_help_discussions)
            builder.setMessage(R.string.message_text_help_discussions)
            builder.setCancelable(true)
            builder.create().show() //create and show the alert dialog
        }

        return root
    }

    fun load(startTask: Boolean = true) {
        val networkConnection = NetworkConnection(requireContext())
        networkConnection.observe(this, Observer { isConnected ->
            connected = isConnected
            if ((activity as MainActivity).currentFragment == "messages") {
                if (isConnected) {
                    //connected
                    binding.constraintLayoutNoInternetConnectionFragmentMessages.isGone = true
                    binding.noLoggedMessagesText.text = getString(R.string.no_logged_text)
                    if ((activity as MainActivity).checkLogged()) {
                        //logged
                        binding.noMessagesAvailableText.isGone = false
                        binding.buttonLoginMessages.isGone = true
                        binding.noLoggedMessagesText.isGone = true
                        binding.noMessagesAvailableText.text =
                            getString(R.string.loading_discussions_text)
                        binding.messagesItemsList.isGone = true
                        getMessagesSections(startTask = startTask)
                    } else {
                        //no logged
                        binding.noMessagesAvailableText.isGone = true
                        binding.buttonLoginMessages.isGone = false
                        binding.noLoggedMessagesText.isGone = false
                        binding.messagesItemsList.isGone = true
                        binding.buttonLoginMessages.setOnClickListener {
                            val intent = Intent(requireContext(), ProfileActivity::class.java)
                            intent.putExtra("source", "messages")
                            startActivity(intent)
                        }
                    }
                } else {
                    //not connected
                    //println("No connection available")
                    binding.noMessagesAvailableText.isGone = true
                    binding.noLoggedMessagesText.text =
                        getString(R.string.no_internet_connection_available_text)
                    binding.constraintLayoutNoInternetConnectionFragmentMessages.isGone = false
                    swipeRefreshLayout.isRefreshing = false
                    binding.messagesItemsList.isGone = true
                }
            }
        })
    }

    override fun onDestroyView() {
        if (currentRunnable != null) mainHandler.removeCallbacks(currentRunnable!!)
        super.onDestroyView()
        _binding = null
    }

    fun getMessagesSections(startTask: Boolean = false) {
        if (connected) {
            swipeRefreshLayout.isRefreshing = true
            try {
                val call: Call<MessagesSectionsList> =
                    ApiClient.client.getUserMessagesSectionsInfo(userid = (activity as MainActivity).getUserid())
                call.enqueue(object : Callback<MessagesSectionsList> {

                    override fun onResponse(
                        call: Call<MessagesSectionsList>?,
                        response: Response<MessagesSectionsList>?
                    ) {
                        //println("Response:\n" + response!!.body()!!)
                        try {
                            val currentFragmentCondition =
                                (activity as MainActivity).currentFragment == "messages"
                            if (response!!.isSuccessful && response.body() != null && currentFragmentCondition) {
                                val responseList = response.body()!!

                                val databaseHandler = DatabaseHandler(requireContext())
                                val allSectionsSaved = databaseHandler.getSections()

                                var sectionsJoined = ArrayList<String>()
                                var sectionsNotJoinedToPass = ArrayList<SectionsModel>()
                                var sectionsToPass = ArrayList<SectionsModel>()
                                responseList.sections?.forEach {
                                    val sectionTempSaved =
                                        databaseHandler.getSection(section = it.section)
                                    sectionsToPass.add(sectionTempSaved)
                                    sectionsJoined.add(it.section)
                                }
                                allSectionsSaved.forEach {
                                    if (sectionsJoined.indexOf(it.section) != -1) {
                                        //present in the "sectionsJoined" database, so don't add to the "general sections messages"
                                        //(already added in the recyclerview "joined")
                                    } else {
                                        //add to the other recyclerview ("noJoined")
                                        val sectionTempSaved =
                                            databaseHandler.getSection(section = it.section)
                                        sectionsToPass.add(sectionTempSaved)
                                    }
                                }
                                databaseHandler.close()

                                swipeRefreshLayout.isRefreshing = false

                                this@MessagesFragment.setupRecyclerView(
                                    clear = true,
                                    getSections = sectionsToPass,
                                    sectionsJoined = sectionsJoined
                                )
                            }
                        } catch (e: Exception) {
                            Log.v("Exception", e.toString())
                        }
                    }

                    override fun onFailure(call: Call<MessagesSectionsList>?, t: Throwable?) {
                        //progerssProgressDialog.dismiss()
                        Log.v("Error", t.toString())
                    }

                })
            } catch (e: Exception) {
                Log.v("Exception", e.toString())
            }
        }
        if (startTask) {
            currentRunnable = Runnable { getMessagesSections(startTask = true) }
            mainHandler.postDelayed(
                currentRunnable!!,
                60000
            ) //every 1 minutes (1000 milliseconds * 1 minute (->60 seconds) = 60000)
        }

    }

    fun setupRecyclerView(
        clear: Boolean = false,
        getSections: ArrayList<SectionsModel>,
        sectionsJoined: ArrayList<String>
    ) {
        try {
            if (clear) binding.messagesItemsList.adapter = null

            binding.noMessagesAvailableText.text =
                getString(R.string.no_messages_available_text)
            if (getSections.size > 0) {
                binding.messagesItemsList.visibility = View.VISIBLE
                binding.noMessagesAvailableText.isGone = true

                binding.messagesItemsList.layoutManager = LinearLayoutManager(requireContext())
                //binding.newsItemsList.setHasFixedSize(true)
                val itemAdapter = SectionsMessagesItemAdapter(
                    context = requireContext(),
                    items = getSections,
                    sectionsJoined = sectionsJoined
                )
                binding.messagesItemsList.adapter = itemAdapter
            } else {
                binding.messagesItemsList.visibility = View.GONE
                binding.noMessagesAvailableText.isGone = false
            }
        } catch (e: Exception) {

        }
    }

    override fun onResume() {
        load()
        super.onResume()
    }
}