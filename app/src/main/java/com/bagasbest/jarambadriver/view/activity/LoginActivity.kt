package com.bagasbest.jarambadriver.view.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.bagasbest.jarambadriver.R
import com.bagasbest.jarambadriver.databinding.ActivityLoginBinding
import com.bagasbest.jarambadriver.viewmodel.viewmodel.LoginViewModel

class LoginActivity : AppCompatActivity() {

    private var binding: ActivityLoginBinding? = null
    private lateinit var loginViewModel: LoginViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding?.root)


        binding?.loginBtn?.setOnClickListener {
            formValidation()
        }

        binding?.forgotPasswordTv?.setOnClickListener {
            Toast.makeText(this, "Maaf, fitur ini sedang dikembangkan", Toast.LENGTH_SHORT).show()
        }

    }

    private fun formValidation() {
        val username = binding?.emailEt?.text.toString().trim()
        val otp = binding?.passwordEt?.text.toString().trim()

        if(username.isEmpty()) {
            binding?.emailEt?.error = resources.getString(R.string.empty_username)
            return
        }
        if(otp.isEmpty()) {
            binding?.passwordEt?.error = resources.getString(R.string.empty_otp)
            return
        }

        // JIKA TIDAK KOSONG, CEK APAKAH USERNAME & OTP TELAH TERDAFTAR DALAM DATABASE

        loginViewModel = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory())[LoginViewModel::class.java]

        binding?.progressBar?.visibility = View.VISIBLE
        loginViewModel.login(username, otp, this)
        loginViewModel.getUserMutableLiveData().observe(this, { data ->
            if(data != null) {
                startActivity(Intent(this, HomepageActivity::class.java))
                finish()
            }
            binding?.progressBar?.visibility = View.GONE
        })

    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }

}