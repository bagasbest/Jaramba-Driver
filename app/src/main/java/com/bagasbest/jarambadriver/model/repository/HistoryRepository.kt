package com.bagasbest.jarambadriver.model.repository

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import com.bagasbest.jarambadriver.model.model.HistoryModel
import com.google.firebase.firestore.FirebaseFirestore
import java.lang.Exception

object HistoryRepository {
    private var historyMutableLiveData = MutableLiveData<ArrayList<HistoryModel>>()
    private val listHistory = ArrayList<HistoryModel>()
    private val TAG = HistoryRepository::class.java.simpleName

    fun setHistory(context: Context) {

        try {
            listHistory.clear()

            FirebaseFirestore
                .getInstance()
                .collection("driver_history")
                .get()
                .addOnSuccessListener { documents ->
                    for(document in documents) {
                        val historyModel = HistoryModel()
                        historyModel.comment = document.data["comment"].toString()
                        historyModel.busId = document.data["busId"].toString()
                        historyModel.driverName = document.data["driverName"].toString()
                        historyModel.income = document.data["income"].toString().toInt()
                        historyModel.finishDate = document.data["finishDate"].toString()
                        historyModel.totalPassanger = document.data["totalPassanger"].toString().toInt()
                        historyModel.rating = document.data["rating"].toString()
                        historyModel.startDate = document.data["startDate"].toString()
                        historyModel.status = document.data["status"].toString()
                        historyModel.trayek = document.data["trayek"].toString()
                        historyModel.tripId = document.data["tripId"].toString()

                        listHistory.add(historyModel)

                    }
                    historyMutableLiveData.postValue(listHistory)
                }
                .addOnFailureListener {
                    Log.w(TAG, "Error getting documents: ", it)
                    Toast.makeText(context, "error: ${it.message}", Toast.LENGTH_SHORT).show()
                }
        } catch (error: Exception) {
            error.printStackTrace()
        }
    }

    fun setHistoryByDate(context: Context, date: String) {

        try {
            listHistory.clear()

            FirebaseFirestore
                .getInstance()
                .collection("driver_history")
                .whereEqualTo("calendar", date)
                .get()
                .addOnSuccessListener { documents ->
                    for(document in documents) {
                        val historyModel = HistoryModel()
                        historyModel.comment = document.data["comment"].toString()
                        historyModel.busId = document.data["busId"].toString()
                        historyModel.driverName = document.data["driverName"].toString()
                        historyModel.income = document.data["income"].toString().toInt()
                        historyModel.finishDate = document.data["finishDate"].toString()
                        historyModel.totalPassanger = document.data["totalPassanger"].toString().toInt()
                        historyModel.rating = document.data["rating"].toString()
                        historyModel.startDate = document.data["startDate"].toString()
                        historyModel.status = document.data["status"].toString()
                        historyModel.trayek = document.data["trayek"].toString()
                        historyModel.tripId = document.data["tripId"].toString()

                        listHistory.add(historyModel)

                    }
                    historyMutableLiveData.postValue(listHistory)
                }
                .addOnFailureListener {
                    Log.w(TAG, "Error getting documents: ", it)
                    Toast.makeText(context, "error: ${it.message}", Toast.LENGTH_SHORT).show()
                }
        } catch (error: Exception) {
            error.printStackTrace()
        }
    }

    fun getHistoryUserMutableLiveData() : MutableLiveData<ArrayList<HistoryModel>> {
        return historyMutableLiveData
    }
}