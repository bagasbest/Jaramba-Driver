package com.bagasbest.jarambadriver.model.data

import android.content.ContentValues.TAG
import android.util.Log
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

object Chat {
    fun sendChat(message: String, format: String, myUid: String, uid: String, tripId: String?) {
        val logChat = hashMapOf(
            "message" to message,
            "timestamp" to format,
            "uid" to myUid,
        )

        if(tripId != null) {
            // save log chat (in sender side)
            Firebase.firestore.collection("chat")
                .document(tripId)
                .collection("$myUid$uid")
                .document(System.currentTimeMillis().toString())
                .set(logChat)
                .addOnSuccessListener {
                    Log.d(TAG, "Success send message")
                }
                .addOnFailureListener {
                    Log.e(TAG, it.message.toString())
                }

            // save log chat (in receiver side)
            Firebase.firestore.collection("chat")
                .document(tripId)
                .collection("$uid$myUid")
                .document(System.currentTimeMillis().toString())
                .set(logChat)
                .addOnSuccessListener {
                    Log.d(TAG, "Success send message")
                }
                .addOnFailureListener {
                    Log.e(TAG, it.message.toString())
                }
        }
    }
}