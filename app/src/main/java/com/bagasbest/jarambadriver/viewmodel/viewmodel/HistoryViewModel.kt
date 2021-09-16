package com.bagasbest.jarambadriver.viewmodel.viewmodel

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bagasbest.jarambadriver.model.model.HistoryModel
import com.bagasbest.jarambadriver.model.repository.HistoryRepository

class HistoryViewModel : ViewModel() {

    private var historyMutableLiveData = HistoryRepository.getHistoryUserMutableLiveData()

    fun setHistory(context: Context) {
        HistoryRepository.setHistory(context)
    }

    fun setHistoryByDate(context: Context, date: String) {
        HistoryRepository.setHistoryByDate(context, date)
    }

    fun getHistoryMutableLiveData() : MutableLiveData<ArrayList<HistoryModel>> {
        return historyMutableLiveData
    }

}