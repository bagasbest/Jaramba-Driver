package com.bagasbest.jarambadriver.viewmodel.viewmodel

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bagasbest.jarambadriver.model.model.BusModel
import com.bagasbest.jarambadriver.model.model.TrayekModel
import com.bagasbest.jarambadriver.model.repository.BusRepository

class BusViewModel : ViewModel() {
    private val mutableLiveData = BusRepository.getAllBus()
    private val mutableLiveDataTrayek = BusRepository.getAllTrayek()

    fun setAllBus(context: Context) {
        BusRepository.setAllBus(context)
    }

    fun getAllBus(): MutableLiveData<ArrayList<BusModel>> {
        return mutableLiveData
    }

    fun setAllTrayek(context: Context) {
        BusRepository.setAllTrayek(context)
    }

    fun getAllTrayek(): MutableLiveData<ArrayList<TrayekModel>> {
        return mutableLiveDataTrayek
    }
}