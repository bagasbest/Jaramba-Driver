package com.bagasbest.jarambadriver.viewmodel.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bagasbest.jarambadriver.model.model.MessageModel
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class ChatViewModel : ViewModel() {

    private val messageList = MutableLiveData<ArrayList<MessageModel>>()

    fun setMessage(myUid: String, uid: String, tripId: String) {
        val listItem = ArrayList<MessageModel>()

        try {
            Firebase.firestore.collection("chat")
                .document(tripId)
                .collection("$myUid$uid")
                .get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {
                        val model = MessageModel()
                        model.message = document.data["message"].toString()
                        model.time = document.data["timestamp"].toString()
                        model.uid = document.data["uid"].toString()

                        listItem.add(model)
                    }
                    messageList.postValue(listItem)
                }
                .addOnFailureListener {
                    it.printStackTrace()
                }

        } catch (error: Exception) {
            error.printStackTrace()
        }
    }

    fun getMessage() : LiveData<ArrayList<MessageModel>> {
        return messageList
    }
}