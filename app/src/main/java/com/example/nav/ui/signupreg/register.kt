package com.example.nav.ui.signupreg

import android.content.Intent
import android.os.Bundle
import android.provider.ContactsContract.CommonDataKinds.Email
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.TextUtils
import android.text.method.LinkMovementMethod
import android.text.method.PasswordTransformationMethod
import android.text.style.ClickableSpan
import android.view.View
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.lifecycleScope
import com.example.nav.MainActivity
import com.example.nav.R
import com.example.nav.services.RetrofitClient
import com.example.nav.ui.login.LoginActivity
import com.example.nav.ui.otp.OTPActivity
import com.google.gson.JsonObject
import kotlinx.coroutines.launch
import org.json.JSONException
import org.json.JSONObject


class RegisterActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_register)

        // Disable night mode
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        // Initialize EditText fields and buttons
        val regName = findViewById<EditText>(R.id.regusername)
        val regMailEditText = findViewById<EditText>(R.id.regmail)
        val regPassEditText = findViewById<EditText>(R.id.regpass)
        val regConfirmPassEditText = findViewById<EditText>(R.id.signconfirm)
        val registerButton = findViewById<Button>(R.id.regbut)
        val check = findViewById<CheckBox>(R.id.check)
        val passwordVisibilityToggle = findViewById<ImageView>(R.id.passwordVisibilityToggle)
        val confirmPasswordVisibilityToggle = findViewById<ImageView>(R.id.confirmPasswordVisibilityToggle)

        // Initially disable the registerButton and checkbox
        registerButton.isEnabled = false

        check.setOnCheckedChangeListener { _, isChecked ->
            // Enable or disable the registerButton based on the isChecked status
            registerButton.isEnabled = isChecked
        }

        // Set OnClickListener for Register Button
        registerButton.setOnClickListener {
            val username = regName.text.toString()
            val email = regMailEditText.text.toString()
            val password = regPassEditText.text.toString()
            val confirmPassword = regConfirmPassEditText.text.toString()

            // Create a JSON object with the required fields
            val registerBody = JsonObject().apply {
                addProperty("username", username)
                addProperty("email", email)
                addProperty("password", password)
                addProperty("password_confirmation", confirmPassword)
            }

            if (username.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty() && confirmPassword.isNotEmpty()) {

                if (password == confirmPassword) {
                    // Launch registration process in ViewModel
                    lifecycleScope.launch {
                        try {
                            val response = RetrofitClient.instance.register(registerBody)
                            if(response.isSuccessful) {
                                val registerResponse = response.body()
                                if (registerResponse != null) {
                                    // Registration successful
                                    // Navigate to OTP verification screen
                                    startOTPActivity(email, password)
                                } else {
                                    // Handle null response
                                    Toast.makeText(this@RegisterActivity, "Null response", Toast.LENGTH_SHORT).show()
                                }
                            } else if (response.code() == 422) {
                                val errorBody = response.errorBody()?.string()
                                val errorMessage = try {
                                    val json = errorBody?.let { it1 -> JSONObject(it1) }
                                    val results = json?.getJSONObject("results")
                                    val errorMessages = mutableListOf<String>()
                                    results?.keys()?.forEach { key ->
                                        errorMessages.add(results.getString(key))
                                    }
                                    errorMessages.joinToString("\n")
                                } catch (e: JSONException) {
                                    "An error occurred."
                                }
                                Toast.makeText(this@RegisterActivity, errorMessage, Toast.LENGTH_SHORT).show()
                            } else if (response.code() == 409) {
                                Toast.makeText(this@RegisterActivity, "User already exists.", Toast.LENGTH_SHORT).show()
                            } else if (response.code() == 500) {
                                Toast.makeText(this@RegisterActivity, "An error occurred.", Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(this@RegisterActivity, "An error occurred.", Toast.LENGTH_SHORT).show()
                            }
                        } finally {
                        }
                    }
                } else {
                    Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            }
        }

        // Set OnClickListener for password visibility toggle
        passwordVisibilityToggle.setOnClickListener {
            togglePasswordVisibility(regPassEditText, passwordVisibilityToggle)
        }

        confirmPasswordVisibilityToggle.setOnClickListener {
            toggleConfirmPasswordVisibility(regConfirmPassEditText, confirmPasswordVisibilityToggle)
        }

        // Set OnClickListener for Login TextView
        val loginTextView = findViewById<TextView>(R.id.loginLink)
        loginTextView.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        val clickableSpan: ClickableSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                // Prevent CheckBox state from being toggled when link is clicked
                widget.cancelPendingInputEvents()
                // Do action for link text...
                showTermsAndConditions()
            }

            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                // Show links with underlines (optional)
                ds.isUnderlineText = false
            }
        }

        val linkText = SpannableString("I agree to the Terms & Conditions and Privacy Policy.")
        linkText.setSpan(clickableSpan, 15, linkText.length-1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        val cs = TextUtils.expandTemplate(
            "^1", linkText
        )

        check.text = cs

        check.movementMethod = LinkMovementMethod.getInstance()
    }

    private fun startOTPActivity(email: String, password: String) {
        val intent = Intent(this, OTPActivity::class.java)
        intent.putExtra("email", email)
        intent.putExtra("password", password)
        startActivity(intent)
        finish()
    }

    // Function to display Terms & Conditions in an AlertDialog
    private fun showTermsAndConditions() {
        val termsAndConditions = """
        Terms & Conditions

        These Terms & Conditions ("Terms") govern your use of our services, including the application referred to as Codes Unleash ("Service"). By accessing or using the Service, you agree to be bound by these Terms. If you disagree with any part of the terms, then you may not access the Service.
        
        1. Privacy Policy Agreement
        
        By using the Service, you acknowledge that you have read and understood our Privacy Policy, which governs the collection, use, and disclosure of your information. Our Privacy Policy can be found here.
        
        2. User Accounts
        
        You may be required to create an account to access certain features of the Service. You are responsible for maintaining the confidentiality of your account and password, and you agree to accept responsibility for all activities that occur under your account or password.
        
        3. Use of Information
        
        We may collect personal information from you as described in our Privacy Policy. By using the Service, you consent to the collection, use, and disclosure of your personal information in accordance with our Privacy Policy.
        
        4. Intellectual Property
        
        The Service and its original content, features, and functionality are owned by Codes Unleash and are protected by international copyright, trademark, patent, trade secret, and other intellectual property or proprietary rights laws.
        
        5. Termination
        
        We may terminate or suspend your access to the Service immediately, without prior notice or liability, for any reason whatsoever, including without limitation if you breach the Terms.
        
        6. Governing Law
        
        These Terms shall be governed and construed in accordance with the laws of the Philippines, without regard to its conflict of law provisions.
        
        7. Changes to Terms
        
        We reserve the right, at our sole discretion, to modify or replace these Terms at any time. If a revision is material, we will try to provide at least 30 days' notice prior to any new terms taking effect. What constitutes a material change will be determined at our sole discretion.
        
        8. Contact Us
        
        If you have any questions about these Terms, please contact us:
        
        By visiting this page on our website: Contact Us
        """.trimIndent()

        val privacyPolicy = """
        Privacy Policy
        Last updated: April 07, 2024
        
        This Privacy Policy describes Our policies and procedures on the collection, use and disclosure of Your information when You use the Service and tells You about Your privacy rights and how the law protects You.
        
        Collecting and Using Your Personal Data
        
        Types of Data Collected
        
        Personal Data
        
        While using Our Service, We may ask You to provide Us with certain personally identifiable information that can be used to contact or identify You. Personally identifiable information may include, but is not limited to:
        
        Email address
        
        First name and last name
        
        Usage Data
        
        Usage Data
        
        Usage Data is collected automatically when using the Service.
        
        Usage Data may include information such as Your Device's Internet Protocol address (e.g. IP address), browser type, browser version, the pages of our Service that You visit, the time and date of Your visit, the time spent on those pages, unique device identifiers and other diagnostic data.
        
        When You access the Service by or through a mobile device, We may collect certain information automatically, including, but not limited to, the type of mobile device You use, Your mobile device unique ID, the IP address of Your mobile device, Your mobile operating system, the type of mobile Internet browser You use, unique device identifiers and other diagnostic data.
        
        We may also collect information that Your browser sends whenever You visit our Service or when You access the Service by or through a mobile device.
        
        Information Collected while Using the Application
        
        While using Our Application, in order to provide features of Our Application, We may collect, with Your prior permission:
        
        Pictures and other information from your Device's camera and photo library
        
        We use this information to provide features of Our Service, to improve and customize Our Service. The information may be uploaded to the Company's servers and/or a Service Provider's server or it may be simply stored on Your device.
        
        You can enable or disable access to this information at any time, through Your Device settings.
        
        Links to Other Websites
        
        Our Service may contain links to other websites that are not operated by Us. If You click on a third party link, You will be directed to that third party's site. We strongly advise You to review the Privacy Policy of every site You visit.
        
        We have no control over and assume no responsibility for the content, privacy policies or practices of any third party sites or services.
        
        Changes to this Privacy Policy
        
        We may update Our Privacy Policy from time to time. We will notify You of any changes by posting the new Privacy Policy on this page.
        
        We will let You know via email and/or a prominent notice on Our Service, prior to the change becoming effective and update the "Last updated" date at the top of this Privacy Policy.
        
        You are advised to review this Privacy Policy periodically for any changes. Changes to this Privacy Policy are effective when they are posted on this page.
        
        Contact Us
        
        If you have any questions about this Privacy Policy, You can contact us:
        
        By visiting this page on our website: https://shorturl.at/tvBHN
        """

        showAlertDialog("Terms & Conditions and Privacy Policy", termsAndConditions + "\n\n" + privacyPolicy)
    }

    private fun showAlertDialog(title: String, message: String) {
        val alertDialogBuilder = AlertDialog.Builder(this)
        alertDialogBuilder.setTitle(title)
        alertDialogBuilder.setMessage(message)
        alertDialogBuilder.setPositiveButton("Agree") { dialog, _ ->
            dialog.dismiss()
            findViewById<CheckBox>(R.id.check).isChecked = true
        }

        alertDialogBuilder.create().show()
    }

    private fun togglePasswordVisibility(
        passwordEditText: EditText,
        visibilityToggle: ImageView
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

    private fun toggleConfirmPasswordVisibility(
        confirmPasswordEditText: EditText,
        visibilityToggle: ImageView
    ) {
        val isPasswordVisible = confirmPasswordEditText.transformationMethod == null

        // Toggle password visibility
        if (isPasswordVisible) {
            confirmPasswordEditText.transformationMethod =
                PasswordTransformationMethod.getInstance()
            visibilityToggle.setImageResource(R.drawable.ic_visibility_off)
        } else {
            confirmPasswordEditText.transformationMethod = null
            visibilityToggle.setImageResource(R.drawable.ic_visibility_on)
        }

        // Move cursor to the end of the text
        confirmPasswordEditText.setSelection(confirmPasswordEditText.text.length)
    }

    fun togglePasswordVisibility(view: View) {}
}