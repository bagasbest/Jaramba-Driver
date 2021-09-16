package com.bagasbest.jarambadriver.model.data

import android.util.Log
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

object Trip {

    private val TAG = Trip::class.java.simpleName

    fun startTrip(
        plat: String,
        format: String,
        timeInMillis: String,
        trayek: String,
        format2: String
    ) {

        // update bus status
        Firebase
            .firestore
            .collection("bus")
            .document(plat)
            .update("status", "Sedang Beroperasi")
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    Log.e(TAG, "Bus Sedang Beroperasi")
                } else {
                    Log.e(TAG, "error pada bagian -> Bus Sedang Beroperasi")
                }
            }


        val driverHistory = hashMapOf(
            "busId" to plat,
            "comment" to "",
            "driverName" to "bagasbest",
            "finishDate" to "",
            "income" to 0,
            "rating" to "",
            "startDate" to format,
            "status" to "Sedang Beroperasi",
            "totalPassanger" to 0,
            "trayek" to trayek,
            "tripId" to timeInMillis,
            "calendar" to format2
        )

        // create driver history
        Firebase
            .firestore
            .collection("driver_history")
            .document(timeInMillis)
            .set(driverHistory)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    Log.e(TAG, "Driver Sedang Beroperasi")
                } else {
                    Log.e(TAG, "error pada bagian -> Driver Sedang Beroperasi")
                }
            }


    }

    fun finishTrip(
        format: String,
        timeInMillis: String,
        plat: String?,
        trayek: String?,
        tripId: String?
    ) {
        // update bus status
        Firebase
            .firestore
            .collection("bus")
            .document(plat!!)
            .update("status", "Belum Beroperasi")
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    Log.e(TAG, "Bus Belum Beroperasi")
                } else {
                    Log.e(TAG, "error pada bagian -> Bus Belum Beroperasi")
                }
            }


        val driverHistory = mapOf(
            "finishDate" to format,
            "income" to 0,
            "status" to "Sukses Beroperasi",
            "totalPassanger" to 0,
        )

        // create driver history
        Firebase
            .firestore
            .collection("driver_history")
            .document(tripId!!)
            .update(driverHistory)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    Log.e(TAG, "Driver Sukses Beroperasi")
                } else {
                    Log.e(TAG, "error pada bagian -> Driver Sukses Beroperasi")
                }
            }

        // hapus trayek driver

        val data = mapOf(
            "trayek" to ""
        )

        FirebaseDatabase
            .getInstance()
            .getReference("driver_location")
            .child("bagasbest")
            .updateChildren(data)
    }


}