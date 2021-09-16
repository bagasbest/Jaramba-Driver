package com.bagasbest.jarambadriver.model.repository

import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import com.bagasbest.jarambadriver.model.model.LoginModel
import com.bagasbest.jarambadriver.view.activity.LoginActivity
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.lang.Exception

object LoginRepository {

    private var userMutableLiveData = MutableLiveData<LoginModel?>()
    private val TAG = LoginRepository::class.java.simpleName

    fun login(username: String, otp: String, context: LoginActivity) {

        try {
            Firebase
                .firestore
                .collection("driver")
                .whereEqualTo("username", username)
                .whereEqualTo("otp", otp)
                .get()
                .addOnSuccessListener {

                    if(it.size() > 0) {
                        val model = LoginModel()
                        model.username = it.first()["username"].toString()
                        model.otp = it.first()["otp"].toString()

                        userMutableLiveData.postValue(model)
                    }
                    else {
                        Log.w(TAG, "Error getting documents: ")
                        Toast.makeText(context, "Maaf, Username atau OTP tidak terdaftar", Toast.LENGTH_SHORT).show()
                        userMutableLiveData.postValue(null)
                    }
                }
                .addOnFailureListener {
                    Log.w(TAG, "Error getting documents: ")
                    Toast.makeText(context, "Maaf, Terdapat kesalahan pada sistem", Toast.LENGTH_SHORT).show()
                    userMutableLiveData.postValue(null)
                }
        } catch (error: Exception){
            error.printStackTrace()
            Toast.makeText(context, "Maaf, Terdapat kesalahan pada sistem", Toast.LENGTH_SHORT).show()
            userMutableLiveData.postValue(null)
        }
    }

    fun getUserMutableLiveData() : MutableLiveData<LoginModel?> {
        return userMutableLiveData
    }

}