package com.bagasbest.jarambadriver.view.fragment

import android.app.DatePickerDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bagasbest.jarambadriver.R
import com.bagasbest.jarambadriver.databinding.FragmentHistoryBinding
import com.bagasbest.jarambadriver.viewmodel.adapter.HistoryAdapter
import com.bagasbest.jarambadriver.viewmodel.utils.DatePickerFragment
import com.bagasbest.jarambadriver.viewmodel.viewmodel.HistoryViewModel
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import java.text.SimpleDateFormat
import java.util.*


class HistoryFragment : Fragment(), DatePickerDialog.OnDateSetListener {

    private var binding: FragmentHistoryBinding? = null
    private lateinit var viewModel: HistoryViewModel
    private lateinit var adapter: HistoryAdapter

    override fun onResume() {
        super.onResume()
        initRecyclerView()
        initViewModel("all")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentHistoryBinding.inflate(layoutInflater, container, false)

        binding?.cityTimeIv?.let {
            Glide
                .with(this)
                .load(R.drawable.city_morning)
                .into(it)
        }

        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding?.historyDate?.setOnClickListener {
            val datePickerFragment = DatePickerFragment()
            datePickerFragment.setTargetFragment(this, 0)
            fragmentManager.let { it1 ->
                if (it1 != null) {
                    datePickerFragment.show(it1, "DatePicker")
                }
            }
        }

        binding?.historyDateAll?.setOnClickListener {
            initRecyclerView()
            initViewModel("all")
        }
    }

    private fun initRecyclerView() {
        val layoutManager = LinearLayoutManager(activity)
        layoutManager.reverseLayout = true
        layoutManager.stackFromEnd = true
        binding?.rvHistory?.layoutManager = layoutManager
        adapter = HistoryAdapter()
        binding?.rvHistory?.adapter = adapter
    }

    private fun initViewModel(date: String) {
        viewModel = activity?.let { ViewModelProvider(it, ViewModelProvider.NewInstanceFactory()) }!!.get(HistoryViewModel::class.java)



        binding?.progressBar?.visibility = View.VISIBLE
        if (date == "all") {
            viewModel.setHistory(requireActivity())
        }
        else if(date != "all") {
            viewModel.setHistoryByDate(requireActivity(), date)
        }


        viewModel.getHistoryMutableLiveData().observe(viewLifecycleOwner, {  historyList ->
            if(historyList.size > 0) {
                binding?.noData?.visibility = View.GONE
                adapter.setData(historyList)
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

    override fun onDateSet(p0: DatePicker?, year: Int, mon: Int, day: Int) {
        val calendar = Calendar.getInstance()
        calendar[Calendar.YEAR] = year
        calendar[Calendar.MONTH] = mon
        calendar[Calendar.DAY_OF_MONTH] = day

        val dateFormat = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())

        //set untuk TextView
        binding?.historyDate?.text = dateFormat.format(calendar.time)
        initRecyclerView()
        initViewModel(dateFormat.format(calendar.time))
    }

}