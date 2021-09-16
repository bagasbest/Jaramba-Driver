package com.bagasbest.jarambadriver.viewmodel.adapter

import android.annotation.SuppressLint
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bagasbest.jarambadriver.databinding.ItemPassangerBinding
import com.bagasbest.jarambadriver.model.model.PassangerModel
import com.bagasbest.jarambadriver.view.activity.ChatActivity
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class PassangerAdapter : RecyclerView.Adapter<PassangerAdapter.ViewHolder>() {

    private val passangerList = ArrayList<PassangerModel>()
    fun setData(items: ArrayList<PassangerModel>) {
        passangerList.clear()
        passangerList.addAll(items)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemPassangerBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(passangerList[position])
    }

    override fun getItemCount(): Int = passangerList.size


    inner class ViewHolder(private val binding: ItemPassangerBinding) : RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(model: PassangerModel) {
            with(binding) {

                trayek.text = "Trayek: " + model.trayek
                totalPassanger.text = "Total penumpang: ${model.totalPerson.toString()}"
                price.text = "Total biaya: ${model.totalPrice.toString()}"

                Firebase
                    .firestore
                    .collection("history")
                    .document(model.tripId!!)
                    .get()
                    .addOnSuccessListener {
                        if(it.data?.get("status")?.equals("Dalam Perjalanan") == true) {
                            acceptBtn.visibility = View.GONE
                            chatUser.visibility = View.VISIBLE
                        }
                        else {
                            acceptBtn.visibility = View.VISIBLE
                            chatUser.visibility = View.GONE
                        }
                    }

                acceptBtn.setOnClickListener {
                    Firebase
                        .firestore
                        .collection("history")
                        .document(model.tripId!!)
                        .update("status", "Dalam Perjalanan")
                        .addOnCompleteListener {
                            if(it.isSuccessful) {
                                Toast.makeText(itemView.context, "Berhasil menerima orderan", Toast.LENGTH_SHORT).show()
                            }
                        }
                    acceptBtn.visibility = View.GONE
                    chatUser.visibility = View.VISIBLE
                }

                chatUser.setOnClickListener {
                    val intent = Intent(it.context, ChatActivity::class.java)
                    intent.putExtra(ChatActivity.EXTRA_TRIP_ID, model.tripId)
                    itemView.context.startActivity(intent)
                }

            }
        }

    }
}