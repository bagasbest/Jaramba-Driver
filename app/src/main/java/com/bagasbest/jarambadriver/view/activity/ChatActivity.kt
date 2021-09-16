package com.bagasbest.jarambadriver.view.activity

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bagasbest.jarambadriver.databinding.ActivityChatBinding
import com.bagasbest.jarambadriver.model.data.Chat
import com.bagasbest.jarambadriver.viewmodel.adapter.ChatAdapter
import com.bagasbest.jarambadriver.viewmodel.viewmodel.ChatViewModel
import java.text.SimpleDateFormat
import java.util.*

class ChatActivity : AppCompatActivity() {
    private var binding: ActivityChatBinding? = null
    private lateinit var adapter: ChatAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding?.root)


        initRecyclerView()
        initViewModel()

        binding?.sendBtn?.setOnClickListener {
            sendMessage()
        }

        binding?.backButton?.setOnClickListener {
            onBackPressed()
        }
    }

    private fun sendMessage() {
        val message = binding?.chatBox?.text.toString().trim()
        val uid = "KgPLiZGLXBaXRRVCobIJlcVmZOG3"
        val tripId = intent.getStringExtra(EXTRA_TRIP_ID)

        if (message.isEmpty()) {
            Toast.makeText(this, "Pesan tidak boleh kosong", Toast.LENGTH_SHORT).show()
            return
        }

        @SuppressLint("SimpleDateFormat")
        val simpleDateFormat = SimpleDateFormat("dd MMM yyyy, HH:mm:ss")
        val format: String = simpleDateFormat.format(Date())
        val myUid = "GXyLe2wg6rowATG4jRnG"

        Chat.sendChat(message, format, myUid, uid, tripId)
        binding?.chatBox?.text?.clear()

        initRecyclerView()
        initViewModel()

    }

    private fun initRecyclerView() {
        val myUid = "GXyLe2wg6rowATG4jRnG"

        val linearLayout = LinearLayoutManager(this)
        linearLayout.stackFromEnd = true
        binding?.recyclerView?.layoutManager = linearLayout
        adapter = ChatAdapter(myUid)
        binding?.recyclerView?.adapter = adapter
    }

    private fun initViewModel() {
        val myUid = "GXyLe2wg6rowATG4jRnG"
        val uid = "KgPLiZGLXBaXRRVCobIJlcVmZOG3"
        val tripId = intent.getStringExtra(EXTRA_TRIP_ID)

        val viewModel = ViewModelProvider(
            this,
            ViewModelProvider.NewInstanceFactory()
        )[ChatViewModel::class.java]

        binding?.progressBar?.visibility = View.VISIBLE
        if (tripId != null) {
            viewModel.setMessage(myUid, uid, tripId)
        }
        viewModel.getMessage().observe(this, { message ->
            if (message != null) {
                binding?.progressBar?.visibility = View.GONE
                adapter.setData(message)
            }
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }

    companion object {
        const val EXTRA_TRIP_ID = "tripId"
    }
}