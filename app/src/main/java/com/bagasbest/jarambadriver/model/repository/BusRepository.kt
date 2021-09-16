package com.bagasbest.jarambadriver.model.repository

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import com.bagasbest.jarambadriver.model.model.BusModel
import com.bagasbest.jarambadriver.model.model.TrayekModel
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.lang.Exception

object BusRepository {

    private var mutableLiveData = MutableLiveData<ArrayList<BusModel>>()
    private var mutableLiveDataTrayek = MutableLiveData<ArrayList<TrayekModel>>()
    private val busList = ArrayList<BusModel>()
    private val trayekList = ArrayList<TrayekModel>()
    private val TAG = BusRepository::class.java.simpleName

    fun setAllBus(context: Context) {

        busList.clear()

        try {
            Firebase
                .firestore
                .collection("bus")
                .get()
                .addOnSuccessListener { documents ->
                    for(document in documents) {
                        val model = BusModel()
                        model.busId = document.data["busId"].toString()
                        model.name = document.data["name"].toString()
                        model.status = document.data["status"].toString()

                        busList.add(model)
                    }
                    mutableLiveData.postValue(busList)
                }
                .addOnFailureListener {
                    Log.w(TAG, "Error getting documents: ")
                    Toast.makeText(context, "Maaf, Terdapat kesalahan pada sistem", Toast.LENGTH_SHORT).show()
                }
        } catch (error: Exception){
            error.printStackTrace()
            Toast.makeText(context, "Maaf, Terdapat kesalahan pada sistem", Toast.LENGTH_SHORT).show()
        }
    }

    fun getAllBus() : MutableLiveData<ArrayList<BusModel>> {
        return mutableLiveData
    }



    fun setAllTrayek(context: Context) {
        trayekList.clear()

        try {
            Firebase
                .firestore
                .collection("trayek")
                .get()
                .addOnSuccessListener { documents ->
                    for(document in documents) {
                        val model = TrayekModel()
                        model.trayek = document.data["trayek"].toString()

                        trayekList.add(model)
                    }
                    mutableLiveDataTrayek.postValue(trayekList)
                }
                .addOnFailureListener {
                    Log.w(TAG, "Error getting documents: ")
                    Toast.makeText(context, "Maaf, Terdapat kesalahan pada sistem", Toast.LENGTH_SHORT).show()
                }
        } catch (error: Exception){
            error.printStackTrace()
            Toast.makeText(context, "Maaf, Terdapat kesalahan pada sistem", Toast.LENGTH_SHORT).show()
        }
    }

    fun getAllTrayek() : MutableLiveData<ArrayList<TrayekModel>> {
        return mutableLiveDataTrayek
    }


}