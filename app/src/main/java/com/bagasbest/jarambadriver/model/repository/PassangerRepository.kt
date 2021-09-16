package com.bagasbest.jarambadriver.model.repository

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import com.bagasbest.jarambadriver.model.model.PassangerModel
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.lang.Exception

object PassangerRepository {

    private var passangerMutableLiveData = MutableLiveData<ArrayList<PassangerModel>>()
    private val listPassanger = ArrayList<PassangerModel>()
    private val TAG = PassangerRepository::class.java.simpleName

    fun setAllPassanger(context: Context) {

        try {
            listPassanger.clear()
            Firebase
                .firestore
                .collection("history")
                .get()
                .addOnSuccessListener { documents ->
                    for(document in documents) {
                        val model = PassangerModel()
                        model.totalPerson = document.data["totalPerson"].toString().toInt()
                        model.totalPrice = document.data["totalPrice"].toString().toInt()
                        model.trayek = document.data["trayek"].toString()
                        model.tripId = document.data["tripId"].toString()

                        listPassanger.add(model)
                    }
                    passangerMutableLiveData.postValue(listPassanger)
                }
                .addOnFailureListener {
                    Log.w(TAG, "Error getting documents: ", it)
                    Toast.makeText(context, "error: ${it.message}", Toast.LENGTH_SHORT).show()
                }
        } catch (error: Exception) {
            error.printStackTrace()
        }
    }

    fun getPassangerMutableLiveData() : MutableLiveData<ArrayList<PassangerModel>> {
        return passangerMutableLiveData
    }

}