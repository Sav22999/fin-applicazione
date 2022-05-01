package com.saverio.finapp.ui.messages

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.isGone
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.saverio.finapp.MainActivity
import com.saverio.finapp.databinding.FragmentMessagesBinding
import com.saverio.finapp.ui.profile.ProfileActivity

class MessagesFragment : Fragment() {

    private var _binding: FragmentMessagesBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val messagesViewModel =
            ViewModelProvider(this).get(MessagesViewModel::class.java)

        _binding = FragmentMessagesBinding.inflate(inflater, container, false)
        val root: View = binding.root

        if ((activity as MainActivity).checkLogged()) {
            //logged
            binding.editTextSearchMessages.isGone = false
            binding.noMessagesAvailableText.isGone = false
            binding.buttonLoginMessages.isGone = true
            binding.noLoggedMessagesText.isGone = true
        } else {
            //no logged
            binding.editTextSearchMessages.isGone = true
            binding.noMessagesAvailableText.isGone = true
            binding.buttonLoginMessages.isGone = false
            binding.noLoggedMessagesText.isGone = false
            binding.buttonLoginMessages.setOnClickListener {
                val intent = Intent(requireContext(), ProfileActivity::class.java)
                intent.putExtra("source", "messages")
                startActivity(intent)
            }
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}