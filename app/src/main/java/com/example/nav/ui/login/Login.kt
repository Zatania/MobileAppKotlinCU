package com.example.nav.ui.login


import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.method.PasswordTransformationMethod
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.nav.MainActivity
import com.example.nav.R
import com.example.nav.services.RetrofitClient
import com.example.nav.ui.signupreg.RegisterActivity
import kotlinx.coroutines.launch
import com.google.gson.JsonObject
import org.json.JSONException
import org.json.JSONObject

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_login)

        val logMailEditText = findViewById<EditText>(R.id.logmail)
        val logPassEditText = findViewById<EditText>(R.id.logpass)
        val loginButton = findViewById<Button>(R.id.logbut)
        val passwordVisibilityToggle = findViewById<ImageView>(R.id.passwordVisibilityToggle)

        val signUpLink = findViewById<TextView>(R.id.txtSignUpLink)

        loginButton.setOnClickListener {
            val usernameOrEmail = logMailEditText.text.toString()
            val password = logPassEditText.text.toString()

            // Create a JSON object with the required fields
            val requestBody = JsonObject().apply {
                addProperty("usernameOrEmail", usernameOrEmail)
                addProperty("password", password)
            }
            lifecycleScope.launch {
                try {
                    val response = RetrofitClient.instance.login(requestBody)
                    if (response.isSuccessful) {
                        val loginResponse = response.body()
                        if (loginResponse != null) {
                            // Login successful
                            // Save token and other user data to SharedPreferences for persistence
                            saveUserData(loginResponse.results.id, loginResponse.results.token, loginResponse.results.username, loginResponse.results.email)
                            startMainActivity()
                        } else {
                            // Handle null response
                            Toast.makeText(this@LoginActivity, "Null response", Toast.LENGTH_SHORT).show()
                        }
                    } else if (response.code() == 404) {
                        Toast.makeText(this@LoginActivity, "User not found.", Toast.LENGTH_SHORT).show()
                    } else if (response.code() == 422) {
                        Toast.makeText(this@LoginActivity, "Email and password should not be empty.", Toast.LENGTH_SHORT).show()
                    } else if (response.code() == 500) {
                        val errorBody = response.errorBody()?.string()
                        val errorMessage = try {
                            val json = errorBody?.let { JSONObject(it) }
                            val message = json?.optString("message", "An error occurred.")

                            // Customize error messages based on the received message
                            when (message) {
                                "Incorrect credentials" -> "Username or password is incorrect."
                                "Please verify email first." -> "Account is not yet verified."
                                else -> message
                            }
                        } catch (e: JSONException) {
                            "An error occurred."
                        }
                        Toast.makeText(this@LoginActivity, errorMessage, Toast.LENGTH_SHORT).show()
                    } else {
                        // Handle other response codes
                        Toast.makeText(this@LoginActivity, "An error occurred", Toast.LENGTH_SHORT).show()
                    }
                } finally {

                }
            }
        }

        // Set OnClickListener for password visibility toggle
        passwordVisibilityToggle.setOnClickListener {
            togglePasswordVisibility(logPassEditText, passwordVisibilityToggle)
        }

        signUpLink.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }

    private fun saveUserData(id: Int, token: String, username: String, email: String) {
        // Save token to SharedPreferences
        val sharedPreferences = getSharedPreferences("user_data", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putInt("id", id)
        editor.putString("token", token)
        editor.putString("username", username)
        editor.putString("email", email)
        editor.apply()
    }

    private fun togglePasswordVisibility(
        passwordEditText: EditText,
        visibilityToggle: ImageView,
    ) {
        val isPasswordVisible = passwordEditText.transformationMethod == null

        // Toggle password visibility
        if (isPasswordVisible) {
            passwordEditText.transformationMethod =
                PasswordTransformationMethod.getInstance()
            visibilityToggle.setImageResource(R.drawable.ic_visibility_off)
        } else {
            passwordEditText.transformationMethod = null
            visibilityToggle.setImageResource(R.drawable.ic_visibility_on)
        }

        // Move cursor to the end of the text
        passwordEditText.setSelection(passwordEditText.text.length)
    }

    private fun startMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}
