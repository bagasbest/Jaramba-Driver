package com.bagasbest.jarambadriver.model.data

import android.content.IntentSender
import androidx.fragment.app.FragmentActivity
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.tasks.Task

object Transportation {

    fun showLocationPrompt(context: FragmentActivity?) {
        val locationRequest = LocationRequest.create()
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        val builder =
            LocationSettingsRequest
                .Builder()
                .addLocationRequest(locationRequest)

        val result: Task<LocationSettingsResponse> =
            LocationServices
                .getSettingsClient(context)
                .checkLocationSettings(builder.build())

        result.addOnCompleteListener { task ->
            try {
                val response = task.getResult(ApiException::class.java)
                // All location settings are satisfied. The client can initialize location
                // requests here.
            } catch (e: ApiException) {
                when (e.statusCode) {
                    LocationSettingsStatusCodes.RESOLUTION_REQUIRED -> {
                        try {
                            // Cast to a resolvable exception.
                            val resolvable: ResolvableApiException = e as ResolvableApiException
                            // show the dialog by calling startResolutionForResult(),
                            // and check the result in onActivityResult(),
                            resolvable.startResolutionForResult(
                                context, LocationRequest.PRIORITY_HIGH_ACCURACY
                            )

                        } catch (e: IntentSender.SendIntentException) {
                            // ignore the error
                        } catch (e: ClassCastException) {
                            // ignore, should be an imposible error
                        }
                    }
                    LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE -> {
                        // Location settings are not satisfied. But could be fixed by showing the
                        // user a dialog.

                        // Location settings are not satisfied. However, we have no way to fix the
                        // settings so we won't show the dialog.
                    }
                }
            }
        }
    }
}