package com.bagasbest.jarambadriver.model.data

import android.app.AlertDialog
import android.content.Intent
import android.os.CountDownTimer
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import com.bagasbest.jarambadriver.view.activity.LoginActivity
import java.util.*

object CountdownTimer {

    private var START_TIME_IN_MILLIS: Long = 0L
    private var mTimeLeftInMillis = START_TIME_IN_MILLIS
    var countdownTimeFinished:Boolean? = false

    fun setCountdownTimer(hours: Long, minute: Long, timer: TextView?, activity: FragmentActivity?) {

        // convert hours & minute to millisecond
        val hoursEnd = hours * 3600 * 1000
        val minuteEnd = minute * 60 * 1000

        // combine 2 variable below in millisecond
        val result = hoursEnd + minuteEnd

        val currentTime = Calendar.getInstance()
        val currentHour = currentTime.get(Calendar.HOUR_OF_DAY)
        val currentMinute = currentTime.get(Calendar.MINUTE)
        val currentSecond = currentTime.get(Calendar.SECOND)

        val currTime =
            (currentHour * 3600 * 1000) + (currentMinute * 60 * 1000) + (currentSecond * 1000)
        START_TIME_IN_MILLIS = result - currTime
        mTimeLeftInMillis = START_TIME_IN_MILLIS

        if (currTime <= result) {
            startCountdownTimer(timer, activity)
            updateCountdownText(timer)
            countdownTimeFinished = false
        }
        else {
            timer?.text = "00:00:00"
            countdownTimeFinished = true
        }
    }

    private fun startCountdownTimer(timerCd: TextView?, activity: FragmentActivity?) {
        val timer = object: CountDownTimer(mTimeLeftInMillis, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                mTimeLeftInMillis = millisUntilFinished
                updateCountdownText(timerCd)
            }

            override fun onFinish() {
                val builder = AlertDialog.Builder(activity)
                builder.setTitle("Waktu Trip Telah Berakhir")
                builder.setMessage("Terima kasih telah memberikan layanan dengan baik\n\nNamun mohon maaf, waktu perjalanan (Trip) driver sudah habis\n\nAnda dapat melakukan perjalanan kembali esok hari.\n\nSistem akan otomatis logout ketika anda klik oke")
                builder.setCancelable(false)
                builder.setPositiveButton("Oke") { dialog, _ ->
                    val intent = Intent(activity, LoginActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                    activity?.startActivity(intent)
                    dialog.dismiss()
                    activity?.finish()
                }
                builder.create().show()
            }
        }
        timer.start()
    }

    private fun updateCountdownText(timer: TextView?) {
        val hours = (mTimeLeftInMillis / 1000).toInt() / 3600
        val minutes = (mTimeLeftInMillis / 1000 % 3600).toInt() / 60
        val seconds = (mTimeLeftInMillis / 1000).toInt() % 60

        val timeLeftFormatted = if (hours > 0) {
            java.lang.String.format(Locale.getDefault(), "%18d:%02d:%02d", hours, minutes, seconds)
        } else {
            java.lang.String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds)
        }

        timer?.text = timeLeftFormatted

    }


}