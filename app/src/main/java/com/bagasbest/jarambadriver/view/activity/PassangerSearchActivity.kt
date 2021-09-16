package com.bagasbest.jarambadriver.view.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bagasbest.jarambadriver.databinding.ActivityPassangerSearchBinding
import com.bagasbest.jarambadriver.viewmodel.adapter.HistoryAdapter
import com.bagasbest.jarambadriver.viewmodel.adapter.PassangerAdapter
import com.bagasbest.jarambadriver.viewmodel.viewmodel.HistoryViewModel
import com.bagasbest.jarambadriver.viewmodel.viewmodel.PassangerViewModel
import com.google.firebase.auth.FirebaseAuth

class PassangerSearchActivity : AppCompatActivity() {

    private var binding: ActivityPassangerSearchBinding? = null
    private lateinit var adapter: PassangerAdapter
    private lateinit var viewModel: PassangerViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPassangerSearchBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        initRecyclerview()
        initViewModel()

    }

    private fun initRecyclerview() {
        val layoutManager = LinearLayoutManager(this)
        layoutManager.reverseLayout = true
        layoutManager.stackFromEnd = true
        binding?.rvPassanger?.layoutManager = layoutManager
        adapter = PassangerAdapter()
        binding?.rvPassanger?.adapter = adapter
    }

    private fun initViewModel() {
        viewModel = ViewModelProvider(
            this,
            ViewModelProvider.NewInstanceFactory()
        )[PassangerViewModel::class.java]

        binding?.progressBar?.visibility = View.VISIBLE
        viewModel.setAllPassanger(this)
        viewModel.getAllPassanger().observe(this, { data ->
            if (data.size > 0) {
                binding?.noData?.visibility = View.GONE
                adapter.setData(data)
            } else {
                binding?.noData?.visibility = View.VISIBLE
            }
            binding?.progressBar?.visibility = View.GONE
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }
}