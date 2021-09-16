package com.bagasbest.jarambadriver.view.fragment

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.location.Location
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Looper.getMainLooper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.bagasbest.jarambadriver.R
import com.bagasbest.jarambadriver.databinding.FragmentHomeBinding
import com.bagasbest.jarambadriver.model.data.CountdownTimer
import com.bagasbest.jarambadriver.model.data.Transportation
import com.bagasbest.jarambadriver.view.activity.LoginActivity
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import java.text.SimpleDateFormat
import java.util.*


class HomeFragment : Fragment(), OnMapReadyCallback {

    private var binding: FragmentHomeBinding? = null
    private var mMap: GoogleMap? = null
    private lateinit var currentLocation: Location
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private val permissionCode = 101
    var locationRequest: LocationRequest? = null

    var userLocationMarker: Marker? = null
    var userLocationAccuracy: Circle? = null


    override fun onStart() {
        /// Activity sudah terlihat tapi belum bisa berinteraksi. Method ini jarang dipakai, tapi bisa sangat berguna untuk mendaftarkan sebuah BroadcastReceiver untuk mengamati perubahan yang dapat mempengaruhi UI.
        super.onStart()
        // minta permission, aktifkan gps
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            startLocationUpdates()
        } else {
            // req permission
        }
    }

    /// Kebalikan dari onStart() Activity sudah tidak terlihat. Biasanya kita melakukan undo untuk pekerjaan yang dilakukan di dalam onStart().
    override fun onStop() {
        super.onStop()
        // misal ke halaman lain, proses yg lagi berjalan sekarang itu di berhenittiin
        stopLocationUpdates()
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(layoutInflater, container, false)

        fusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(activity)

        fetchLocation()

        // set countdown timer
        CountdownTimer.setCountdownTimer(16, 0, binding?.timer, activity)

        // check if countdown finish or not
        checkIsCountdownFinishOrNot()

        // auto request GPS to turn on
        Transportation.showLocationPrompt(activity)


        //set berapa detik sekali akan update ke database
        locationRequest = LocationRequest()
        locationRequest?.interval = 3000
        locationRequest?.priority = LocationRequest.PRIORITY_HIGH_ACCURACY


        // save all route that driver drive every 15 minute
        Handler(getMainLooper()).postDelayed({

            val timeInMillis = System.currentTimeMillis().toString()
            val simpleDateFormat = SimpleDateFormat("dd MMMM yyyy, hh:mm:ss")
            val format = simpleDateFormat.format(Date())
            val prefs = activity
                ?.getSharedPreferences("pref", Context.MODE_PRIVATE)

            val driverLocation = mapOf(
                "tripId" to prefs?.getString("tripId", ""),
                "trayek" to prefs?.getString("trayek", ""),
                "latitude" to currentLocation.latitude,
                "longitude" to currentLocation.longitude,
                "dateTime" to format
            )

            FirebaseDatabase
                .getInstance()
                .getReference("driver_history")
                .child("bagasbest")
                .child(prefs?.getString("startTime", "")!!)
                .child(timeInMillis)
                .setValue(driverLocation)
        }, 900000)

        return binding?.root
    }

    private fun checkIsCountdownFinishOrNot() {
        Handler(getMainLooper()).postDelayed({
            if (CountdownTimer.countdownTimeFinished == true) {
                val builder = AlertDialog.Builder(activity)
                builder.setTitle("Waktu Trip Telah Berakhir")
                builder.setMessage("Terima kasih telah memberikan layanan dengan baik\n\nNamun mohon maaf, waktu perjalanan (Trip) driver sudah habis\n\nAnda dapat melakukan perjalanan kembali esok hari.\n\nSistem akan otomatis logout ketika anda klik oke")
                builder.setCancelable(false)
                builder.setPositiveButton("Oke") { dialog, _ ->
                    val intent = Intent(activity, LoginActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                    dialog.dismiss()
                    activity?.finish()
                }
                builder.create().show()
            }
        }, 1000)

    }

    private fun fetchLocation() {
        if (ActivityCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                permissionCode
            )
            return
        }

        val task = fusedLocationProviderClient.lastLocation
        task.addOnSuccessListener { location ->
            if (location != null) {
                currentLocation = location

                val supportMapFragment =
                    childFragmentManager.findFragmentById(R.id.google_maps) as SupportMapFragment
                supportMapFragment.getMapAsync(this)
            }
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap?.mapType = GoogleMap.MAP_TYPE_NORMAL
        val latLng = LatLng(currentLocation.latitude, currentLocation.longitude)

        mMap?.animateCamera(CameraUpdateFactory.newLatLng(latLng))
        mMap?.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 18F))

        if (ContextCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION
            )
            == PackageManager.PERMISSION_GRANTED
        ) {
            mMap?.isMyLocationEnabled = true
            mMap?.uiSettings?.isMyLocationButtonEnabled = true
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String?>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            permissionCode -> if (grantResults.isNotEmpty() && grantResults[0] ==
                PackageManager.PERMISSION_GRANTED
            ) {
                fetchLocation()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            LocationRequest.PRIORITY_HIGH_ACCURACY -> {
                if (resultCode == Activity.RESULT_OK) {
                    Log.e("Status", "On")
                } else {
                    Log.e("Status", "Off")
                }
            }
        }
    }


    @SuppressLint("MissingPermission")
    private fun startLocationUpdates() {
        // ini fungsi untuk nge looping, 1 detik sekali
        fusedLocationProviderClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            getMainLooper()
        )
    }

    private fun stopLocationUpdates() {
        fusedLocationProviderClient.removeLocationUpdates(locationCallback)
    }


    //get latLng / location callback
    private var locationCallback: LocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            super.onLocationResult(locationResult)
            Log.d("LOCATION_RESULT", locationResult.lastLocation.toString())
            if (mMap != null) {
                setUserLocationMarker(locationResult.lastLocation)
            }
        }
    }

    private fun setUserLocationMarker(lastLocation: Location) {
        val latLng = LatLng(lastLocation.latitude, lastLocation.longitude)

        val prefs = activity
            ?.getSharedPreferences("pref", Context.MODE_PRIVATE)

        if (userLocationMarker != null) {
            val markerOptions = MarkerOptions()
            markerOptions.position(latLng)
            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_bus))
            markerOptions.rotation(lastLocation.bearing)
            markerOptions.anchor(0.5.toFloat(), 0.5.toFloat())
            if (prefs?.getString("trayek", "") != "") {
                markerOptions.title("Trayek: " + prefs?.getString("trayek", ""))
            } else {
                markerOptions.title("Trayek: Belum memilih trayek")
            }
            userLocationMarker = mMap?.addMarker(markerOptions)
            mMap?.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 18F));
        } else {
            // when page repeated
            userLocationMarker?.position = latLng
            userLocationMarker?.rotation = lastLocation.bearing
            if (prefs?.getString("trayek", "") != "") {
                userLocationMarker?.setTitle("Trayek: " + prefs?.getString("trayek", ""))
            } else {
                userLocationMarker?.setTitle("Trayek: Belum memilih trayek")
            }
            mMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 18F));
        }

        if (userLocationAccuracy == null) {
            val circleOptions = CircleOptions()
            circleOptions.center(latLng)
            circleOptions.strokeWidth(4f)
            circleOptions.strokeColor(Color.argb(255, 0, 0, 255))
            circleOptions.fillColor(Color.argb(32, 0, 0, 255))
            circleOptions.radius(lastLocation.accuracy.toDouble())
            userLocationAccuracy = mMap?.addCircle(circleOptions)
        } else {
            userLocationAccuracy?.center = latLng
            userLocationAccuracy?.radius = lastLocation.accuracy.toDouble()
        }

        // update realtime database every -+3000 ms, but if driver start trip
        if (prefs?.getString("trayek", "") != "") {

            val timeInMillis = System.currentTimeMillis().toString()
            val simpleDateFormat = SimpleDateFormat("dd MMMM yyyy, hh:mm:ss")
            val format = simpleDateFormat.format(Date())

            val driverLocation = mapOf(
                "tripId" to prefs?.getString("tripId", ""),
                "trayek" to prefs?.getString("trayek", ""),
                "latitude" to lastLocation.latitude,
                "longitude" to lastLocation.longitude,
                "dateTime" to format
            )

            // tracking driver by last location
            FirebaseDatabase
                .getInstance()
                .getReference("driver_location")
                .child("bagasbest")
                .updateChildren(driverLocation)

        }

        // put marker on all driver location
        val driverReference =
            FirebaseDatabase
                .getInstance()
                .getReference("driver_location")

        driverReference.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {

                val lat = snapshot.child("latitude").value as Double
                val lon = snapshot.child("longitude").value as Double
                var trayek = snapshot.child("trayek").value as String?

                val newLocation = LatLng(
                    lat,
                    lon
                )

                if(trayek == "") {
                    trayek = "Belum memilih trayek"
                }

                mMap?.addMarker(
                    MarkerOptions()
                        .position(newLocation)
                        .title("Trayek: $trayek")
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_bus))
                )
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
            }

            override fun onCancelled(error: DatabaseError) {
            }

        })
        mMap?.clear()


    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }

}