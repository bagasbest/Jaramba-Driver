package com.bagasbest.jarambadriver.model.data

import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

object History {

    var result:Boolean? = true

    fun setRating(rating: String, comment: String, tripId: String, format: String) {

        val data = mapOf(
            "rating" to rating,
            "comment" to comment,
            "finishDate" to format,
            "status" to "Selesai"
        )

        Firebase
            .firestore
            .collection("driver_history")
            .document(tripId)
            .update(data)
            .addOnCompleteListener {
                result = it.isSuccessful
            }

    }

}