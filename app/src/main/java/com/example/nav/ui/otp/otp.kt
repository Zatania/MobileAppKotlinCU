package com.example.nav.ui.otp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.lifecycleScope
import com.chaos.view.PinView
import com.example.nav.MainActivity
import com.example.nav.R
import com.example.nav.services.RetrofitClient
import com.example.nav.ui.login.LoginActivity
import com.google.gson.JsonObject
import kotlinx.coroutines.launch

class OTPActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_otp)

        // Disable night mode
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        val otpField=findViewById<PinView>(R.id.otpField)
        val verifyButton = findViewById<Button>(R.id.verifyButton)

        verifyButton.setOnClickListener {
            val otp = otpField.text.toString()
            val email:String = intent.getStringExtra("email").toString()
            if (otp.isNotEmpty()) {
                // Verify OTP
                // Create a JSON object with the required fields
                val requestBody = JsonObject().apply {
                    addProperty("email", email)
                    addProperty("otp", otp)
                }
                lifecycleScope.launch {
                    try {
                        val response = RetrofitClient.instance.verifyEmail(requestBody)
                        if (response.isSuccessful) {
                            val otpResponse = response.body()
                            if (otpResponse != null) {
                                // OTP verification successful
                                Toast.makeText(this@OTPActivity, "Account successfully verified.", Toast.LENGTH_SHORT).show()
                                // Start the login activity
                                val loginResponse = RetrofitClient.instance.login(JsonObject().apply {
                                    addProperty("usernameOrEmail", email)
                                    addProperty("password", "password")
                                })

                                if (loginResponse.isSuccessful) {
                                    val loginResponse = loginResponse.body()
                                    if (loginResponse != null) {
                                        // Login successful
                                        // Save token and other user data to SharedPreferences for persistence
                                        saveUserData(loginResponse.results.token, loginResponse.results.username, loginResponse.results.email)
                                        startMainActivity()
                                    } else {
                                        // Handle null response
                                        Toast.makeText(this@OTPActivity, "Null response", Toast.LENGTH_SHORT).show()
                                    }
                                } else {
                                    // Handle different HTTP error codes
                                    Toast.makeText(this@OTPActivity, "Error on logging in after verification.", Toast.LENGTH_SHORT).show()
                                    startLoginActivity()
                                }
                            } else {
                                // Handle null response
                                Toast.makeText(this@OTPActivity, "Null response", Toast.LENGTH_SHORT).show()
                            }
                        } else {
                            // Handle different HTTP error codes
                            Toast.makeText(this@OTPActivity, "Error verifying OTP", Toast.LENGTH_SHORT).show()
                        }
                    } catch (e: Exception) {
                        // Handle exceptions
                        Toast.makeText(this@OTPActivity, "Error verifying OTP", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                Toast.makeText(this, "Please enter the OTP", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun saveUserData(token: String, username: String, email: String) {
        // Save token to SharedPreferences
        val sharedPreferences = getSharedPreferences("user_data", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("token", token)
        editor.putString("username", username)
        editor.putString("email", email)
        editor.apply()
    }


    private fun startLoginActivity() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun startMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}