package com.example.nav.ui.profile

import android.R.attr
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Bundle
import android.os.FileUtils
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.net.toFile
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.media3.common.util.Util
import com.example.nav.R
import com.example.nav.services.BadgesResponse
import com.example.nav.services.RetrofitClient
import com.example.nav.ui.screen.screen
import com.github.dhaval2404.imagepicker.ImagePicker
import com.squareup.picasso.Picasso
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream


class ProfileFragment : Fragment() {
    private lateinit var imageView: ImageView
    private lateinit var badgesContainer: ViewGroup
    private lateinit var username: String
    private lateinit var token: String

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        // Retrieve saved user data
        val sharedPreferences = requireActivity().getSharedPreferences("user_data", Context.MODE_PRIVATE)
        username = sharedPreferences.getString("username", "") ?: ""
        token = sharedPreferences.getString("token", "") ?: ""
        val email = sharedPreferences.getString("email", "") ?: ""

        // Set username and email to TextViews
        val usernameTextView: TextView = view.findViewById(R.id.textView2)
        val emailTextView: TextView = view.findViewById(R.id.textView3)
        usernameTextView.text = username
        emailTextView.text = email

        // Initialize badges container
        badgesContainer = view.findViewById(R.id.badgesContainer)
        badgesContainer.removeAllViews()
        lifecycleScope.launch {
            fetchUserBadges(token, username)
        }

        val logoutButton: Button = view.findViewById(R.id.button6)
        logoutButton.setOnClickListener {
            logout()
        }

        val feedbackButton: Button = view.findViewById(R.id.button)
        feedbackButton.setOnClickListener {
            openFeedbackForm()
        }
        
        imageView = view.findViewById(R.id.imageView)
        lifecycleScope.launch {
            val response = RetrofitClient.instance.getProfile("Bearer $token",username)

            if(response.isSuccessful){
                val profile = response.body()?.string()
                Picasso.get().load(profile).into(imageView)
            }
        }
        imageView.setOnClickListener {
            openImageSelectionDialog()
        }

        return view
    }

    private suspend fun fetchUserBadges(token: String, username: String) {
        val response = RetrofitClient.instance.getUserBadges("Bearer $token",username)

        // Check if the response is successful
        if (response.isSuccessful) {
            val badges = response.body()
            displayBadges(badges)
        }
    }

    private fun displayBadges(badgesResponse: List<BadgesResponse>?) {
        badgesResponse?.let { response ->
            for (badgeResponse in response) {
                val badge = badgeResponse.badge

                // Create a new ImageView for each badge
                val imageView = ImageView(context)
                // Set layout parameters for the ImageView
                val layoutParams = LinearLayout.LayoutParams(
                    resources.getDimensionPixelSize(R.dimen.badge_width),
                    resources.getDimensionPixelSize(R.dimen.badge_height)
                )
                layoutParams.marginEnd = resources.getDimensionPixelSize(R.dimen.badge_margin_end)
                imageView.layoutParams = layoutParams

                // Load badge image into ImageView using Picasso library
                Picasso.get().load(badge.badge_image).into(imageView)

                // Add ImageView to badges container
                badgesContainer.addView(imageView)
            }
        }
    }

    private fun logout() {
        val intent = Intent(requireContext(), screen::class.java)
        startActivity(intent)
        requireActivity().finish()
    }

    private fun openFeedbackForm() {
        val formUrl =
            "https://docs.google.com/forms/d/e/1FAIpQLScbraozxtFf8B8HycEg4-s8zoi4KkiAZNCaZvOXuoccQMusjg/viewform"
        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(formUrl))
        startActivity(browserIntent)
    }

    @SuppressLint("IntentReset")
    private fun openImageSelectionDialog() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            type = "image/*"
            addCategory(Intent.CATEGORY_OPENABLE)
        }
        startActivityForResult(intent, Companion.REQUEST_IMAGE_OPEN)
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_IMAGE_OPEN && resultCode == Activity.RESULT_OK) {
            val fileUri = data?.data
            Toast.makeText(requireContext(), "Image selected: $fileUri", Toast.LENGTH_SHORT).show()
        }
    }
    private fun uploadFile(file: File) {
        lifecycleScope.launch {
            try {
                val requestFile: RequestBody = RequestBody.create("image/*".toMediaTypeOrNull(), file)
                val body: MultipartBody.Part = MultipartBody.Part.createFormData("image", file.name, requestFile)

                // Upload the file using Retrofit
                val response = RetrofitClient.instance.uploadImage("Bearer $token", username, body)

                // Handle response
                if (response.isSuccessful) {
                    // File uploaded successfully
                    Toast.makeText(requireContext(), "Image uploaded successfully", Toast.LENGTH_SHORT).show()
                    // Reload profile image or handle UI update
                } else {
                    // Handle error
                    Toast.makeText(requireContext(), "Failed to upload image", Toast.LENGTH_SHORT).show()
                    Log.d("ProfileFragment", "Failed to upload image: ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(requireContext(), "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
    companion object {
        const val REQUEST_IMAGE_OPEN = 1
    }

}
