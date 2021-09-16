package com.bagasbest.jarambadriver.viewmodel.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bagasbest.jarambadriver.model.model.LoginModel
import com.bagasbest.jarambadriver.model.repository.LoginRepository
import com.bagasbest.jarambadriver.view.activity.LoginActivity

class LoginViewModel : ViewModel() {
    private val userMutableLiveData = LoginRepository.getUserMutableLiveData()


    fun login(email: String, password: String, context: LoginActivity) {
        LoginRepository.login(email, password, context)
    }

    fun getUserMutableLiveData(): MutableLiveData<LoginModel?> {
        return userMutableLiveData
    }
}