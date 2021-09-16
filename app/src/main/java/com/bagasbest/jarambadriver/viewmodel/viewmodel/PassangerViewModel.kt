package com.bagasbest.jarambadriver.viewmodel.viewmodel

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bagasbest.jarambadriver.model.model.PassangerModel
import com.bagasbest.jarambadriver.model.repository.PassangerRepository

class PassangerViewModel : ViewModel() {

    private val mutableLiveData = PassangerRepository.getPassangerMutableLiveData()

    fun setAllPassanger(context: Context) {
        PassangerRepository.setAllPassanger(context)
    }

    fun getAllPassanger(): MutableLiveData<ArrayList<PassangerModel>> {
        return mutableLiveData
    }

}