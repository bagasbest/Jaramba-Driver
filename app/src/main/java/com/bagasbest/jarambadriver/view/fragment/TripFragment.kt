package com.bagasbest.jarambadriver.view.fragment

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.bagasbest.jarambadriver.R
import com.bagasbest.jarambadriver.databinding.FragmentTripBinding
import com.bagasbest.jarambadriver.model.data.History
import com.bagasbest.jarambadriver.model.data.Trip
import com.bagasbest.jarambadriver.view.activity.PassangerSearchActivity
import com.bagasbest.jarambadriver.viewmodel.viewmodel.BusViewModel
import com.bumptech.glide.Glide
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class TripFragment : Fragment() {

    private var binding: FragmentTripBinding? = null
    private lateinit var busViewModel: BusViewModel
    private val listTrayek = ArrayList<String>()
    private val listPlat = ArrayList<String>()
    private var trayek: String? = null
    private var plat: String? = null
    private lateinit var prefs: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentTripBinding.inflate(layoutInflater, container, false)

        binding?.cityTimeIv?.let {
            Glide
                .with(this)
                .load(R.drawable.city_morning)
                .into(it)
        }

        // check is driver already trip or not
        checkDriverTripOrNot()

        // initiate bus and trayek
        initViewModel()

        // show all bus and trayek into dropdown
        populateBusAndTrayek()

        // start trip
        startTrip()

        // finish trip
        finishTrip()

        // searchPassanger
        searchPassanger()


        return binding?.root
    }

    private fun searchPassanger() {
        binding?.passanger?.setOnClickListener {
            startActivity(Intent(activity, PassangerSearchActivity::class.java))
        }
    }

    private fun checkDriverTripOrNot() {
        val preferences = activity
            ?.getSharedPreferences("pref", Context.MODE_PRIVATE)

        if (preferences?.getString("tripId", "") != "") {
            binding?.startTrip?.visibility = View.GONE
            binding?.plat?.isEnabled = false
            binding?.trayek?.isEnabled = false
            binding?.finishTrip?.visibility = View.VISIBLE
            binding?.passanger?.visibility = View.VISIBLE
        }
    }


    @SuppressLint("SimpleDateFormat")
    private fun startTrip() {
        binding?.startTrip?.setOnClickListener {
            when {
                trayek == null -> {
                    Toast.makeText(
                        activity,
                        "Silahkan pilih trayek terlebih dahulu",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                plat == null -> {
                    Toast.makeText(
                        activity,
                        "Silahkan pilih bus terlebih dahulu",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                else -> {

                    val calendar = Calendar.getInstance()
                    val timeOfDay: Int = calendar.get(Calendar.HOUR_OF_DAY)

                    if (timeOfDay > 23) {
                        Toast.makeText(
                            activity,
                            "Tidak dapat memulai perjalanan, karena waktu perjalanan sudah habis",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        val builder = AlertDialog.Builder(activity)
                        builder.setTitle("Konfirmasi Memulai Perjalanan")
                        builder.setMessage("Apakah anda yakin ingin memulai perjalanan (start trip) ?")
                        builder.setIcon(R.drawable.ic_baseline_warning_24)
                        builder.setPositiveButton("Yakin") { dialog, _ ->

                            val preferences = activity
                                ?.getSharedPreferences("pref", Context.MODE_PRIVATE)

                            val simpleDateFormat = SimpleDateFormat("dd MMMM yyyy, hh:mm:ss")
                            val format = simpleDateFormat.format(Date())

                            val simpleDateFormat2 = SimpleDateFormat("dd MMMM yyyy")
                            val format2 = simpleDateFormat2.format(Date())

                            val timeInMillis = System.currentTimeMillis().toString()

                            preferences?.edit()?.putString("tripId", timeInMillis)?.apply()
                            preferences?.edit()?.putString("trayek", trayek)?.apply()
                            preferences?.edit()?.putString("startTime", format)?.apply()
                            preferences?.edit()?.putString("busId", plat)?.apply()

                            Trip.startTrip(plat!!, format, timeInMillis, trayek!!, format2)
                            dialog.dismiss()
                            binding?.startTrip?.visibility = View.GONE
                            binding?.plat?.isEnabled = false
                            binding?.trayek?.isEnabled = false
                            binding?.finishTrip?.visibility = View.VISIBLE
                            binding?.passanger?.visibility = View.VISIBLE

                        }
                        builder.setNegativeButton("Tidak") { dialog, _ ->
                            dialog.dismiss()
                        }
                        builder.create().show()
                    }
                }
            }
        }
    }

    @SuppressLint("SimpleDateFormat")
    private fun finishTrip() {
        binding?.finishTrip?.setOnClickListener {

            val builder = AlertDialog.Builder(activity)
            builder.setTitle("Konfirmasi Menyelesaikan Perjalanan")
            builder.setMessage("Apakah anda yakin ingin menyelesaikan perjalanan (finish trip) ?")
            builder.setIcon(R.drawable.ic_baseline_warning_24)
            builder.setPositiveButton("Yakin") { dialog, _ ->

                // driver give rating and comment
                giveRating(dialog)

            }
            builder.setNegativeButton("Tidak") { dialog, _ ->
                dialog.dismiss()
            }
            builder.create().show()
        }
    }

    @SuppressLint("SimpleDateFormat")
    private fun giveRating(dismiss: DialogInterface) {
        val dialog = Dialog(requireContext())
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
                Toast.makeText(activity, "Rating tidak boleh kosong", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }


            // SIMPAN RATING & COMMENT KE DATABASE
            pb.visibility = View.VISIBLE

            val simpleDateFormat = SimpleDateFormat("dd MMMM yyyy")
            val format = simpleDateFormat.format(Date())

            val preferences = activity
                ?.getSharedPreferences("pref", Context.MODE_PRIVATE)
            val tripId = preferences?.getString("tripId", "")

            Log.d("Tag", tripId.toString())

            tripId?.let { it1 ->
                History.setRating(
                    rating,
                    comment,
                    it1,
                    format
                )
            }


            Handler(Looper.getMainLooper()).postDelayed({
                pb.visibility = View.GONE
                if (History.result == true) {

                    val preferences = activity
                        ?.getSharedPreferences("pref", Context.MODE_PRIVATE)


                    val simpleDateFormat = SimpleDateFormat("dd MMMM yyyy, hh:mm:ss")
                    val format = simpleDateFormat.format(Date())
                    val timeInMillis = System.currentTimeMillis().toString()

                    val tripId = preferences?.getString("tripId", "")
                    val trayek = preferences?.getString("trayek", "")
                    val plat = preferences?.getString("busId", "")

                    Trip.finishTrip(format, timeInMillis, plat, trayek, tripId)
                    dialog.dismiss()
                    dismiss.dismiss()
                    binding?.finishTrip?.visibility = View.GONE
                    binding?.plat?.isEnabled = true
                    binding?.trayek?.isEnabled = true
                    binding?.startTrip?.visibility = View.VISIBLE
                    binding?.passanger?.visibility = View.GONE
                    preferences?.edit()?.clear()?.apply()

                } else {
                    Toast.makeText(activity, "Gagal mengakhiri perjalanan", Toast.LENGTH_SHORT).show()
                    dialog.dismiss()
                }
            }, 3000)
        }

        btnDismiss.setOnClickListener {
            dialog.dismiss()
        }

        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()
    }

    private fun populateBusAndTrayek() {
        val adapter: ArrayAdapter<String> = ArrayAdapter<String>(
            requireContext(),
            android.R.layout.simple_spinner_dropdown_item,
            listTrayek
        )
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        // Apply the adapter to the spinner
        binding?.trayekEt?.setAdapter(adapter)
        binding?.trayekEt?.setOnItemClickListener { adapterView, view, i, l ->
            trayek = binding?.trayekEt?.text.toString()
        }


        val adapter2: ArrayAdapter<String> = ArrayAdapter<String>(
            requireContext(),
            android.R.layout.simple_spinner_dropdown_item,
            listPlat
        )
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        // Apply the adapter to the spinner
        binding?.platEt?.setAdapter(adapter2)
        binding?.platEt?.setOnItemClickListener { adapterView, view, i, l ->
            plat = binding?.platEt?.text.toString()
        }
    }

    private fun initViewModel() {
        busViewModel = ViewModelProvider(
            this,
            ViewModelProvider.NewInstanceFactory()
        )[BusViewModel::class.java]

        busViewModel.setAllBus(requireActivity())
        activity?.let {
            busViewModel.getAllBus().observe(it, { data ->
                if (data != null) {
                    for (i in 0 until data.size) {
                        listPlat.add(data[i].busId!!)
                    }
                }
            })
        }

        busViewModel.setAllTrayek(requireActivity())
        activity?.let {
            busViewModel.getAllTrayek().observe(it, { data ->
                if (data != null) {
                    for (i in 0 until data.size) {
                        listTrayek.add(data[i].trayek!!)
                    }
                }
            })
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }


}