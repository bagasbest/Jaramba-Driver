package com.bagasbest.jarambadriver.viewmodel.adapter

import android.annotation.SuppressLint
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bagasbest.jarambadriver.R
import com.bagasbest.jarambadriver.databinding.ItemHistoryBinding
import com.bagasbest.jarambadriver.model.data.History
import com.bagasbest.jarambadriver.model.model.HistoryModel
import java.util.*
import kotlin.collections.ArrayList

class HistoryAdapter : RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder>() {

    private val historyList = ArrayList<HistoryModel>()
    fun setData(items: ArrayList<HistoryModel>) {
        historyList.clear()
        historyList.addAll(items)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        val binding = ItemHistoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return HistoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        holder.bind(historyList[position])
    }

    override fun getItemCount(): Int = historyList.size


    inner class HistoryViewHolder(private val binding: ItemHistoryBinding) : RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n", "ResourceAsColor")
        fun bind(historyModel: HistoryModel) {
            with(binding) {
                transportMode.text = "Moda Transportasi: Travel dan Bus "
                destination.text = "Destinasi: " + historyModel.trayek
                rating.text = "Rating: ${historyModel.rating}"
                comment.text = "Komentar: ${historyModel.comment}"
                status.text = "Status: " + historyModel.status


                if(historyModel.status == "Sukses Beroperasi") {
                    view2.background = ContextCompat.getDrawable(itemView.context, R.drawable.ic_rounded_2)
                    transportMode.setTextColor(R.color.black)
                    destination.setTextColor(R.color.black)
                }



                itemView.setOnClickListener {
                    if(historyModel.status == "Sukses Beroperasi" && historyModel.comment == "") {
                        val dialog = Dialog(itemView.context)
                        dialog.setContentView(R.layout.popup_rating)
                        dialog.setCanceledOnTouchOutside(false)

                        val btnSubmit = dialog.findViewById(R.id.submitRating) as Button
                        val btnDismiss = dialog.findViewById(R.id.dismissBtn) as Button
                        val commentEt = dialog.findViewById(R.id.comment) as EditText
                        val ratingBar = dialog.findViewById(R.id.ratingBar) as RatingBar
                        val pb = dialog.findViewById(R.id.progress_bar) as ProgressBar

                        btnSubmit.setOnClickListener {
                            val comment = commentEt.text.toString().trim()
                            val rating = ratingBar.rating.toString()

                            if(comment.isEmpty()) {
                                commentEt.error = "Komentar tidak boleh kosong"
                                return@setOnClickListener
                            }
                            else if(rating == "0.0") {
                                Toast.makeText(itemView.context, "Rating tidak boleh kosong", Toast.LENGTH_SHORT).show()
                                return@setOnClickListener
                            }

                        }

                        btnDismiss.setOnClickListener {
                            dialog.dismiss()
                        }

                        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                        dialog.show()
                    }
                }
            }
        }

    }


}