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
import androidx.activity.result.contract.ActivityResultContracts
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
    private lateinit var profileView: ImageView
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

        profileView = view.findViewById(R.id.imageView)
        profileView.setOnClickListener {
            selectImage()
        }
        lifecycleScope.launch {
            val response = RetrofitClient.instance.getProfile("Bearer $token",username)

            if(response.isSuccessful){
                val profile = response.body()?.string()
                if(!profile.isNullOrEmpty()){ // Check if profile is not empty
                    Picasso.get().load(profile).into(profileView)
                } else {
                    // Load a default image when profile URL is empty
                    Picasso.get().load("https://www.gravatar.com/avatar/").into(profileView)
                }
            } else {
                // Load a default image when the response is not successful
                Picasso.get().load("https://www.gravatar.com/avatar/").into(profileView)
            }
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

    private fun selectImage() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        resultLauncher.launch(intent)
    }

    private val resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data: Intent? = result.data
            val selectedImageUri: Uri? = data?.data
            selectedImageUri?.let {
                uploadImage(it)
            }
        }
    }

    private fun uploadImage(imageUri: Uri) {
        val file = File(requireContext().cacheDir, "temp_image.jpg")
        file.createNewFile()

        val inputStream = requireContext().contentResolver.openInputStream(imageUri)
        val outputStream = FileOutputStream(file)
        inputStream?.copyTo(outputStream)
        inputStream?.close()
        outputStream.close()

        val requestBody = file.asRequestBody("image/*".toMediaTypeOrNull())
        val imagePart = MultipartBody.Part.createFormData("image", file.name, requestBody)

        lifecycleScope.launch {
            try {
                val response = RetrofitClient.instance.uploadImage("Bearer $token", username, imagePart)
                if (response.isSuccessful) {
                    // Image uploaded successfully
                    Toast.makeText(requireContext(), "Image uploaded successfully", Toast.LENGTH_SHORT).show()
                    // Refresh profile picture
                    loadImageFromUri(imageUri)
                } else {
                    // Handle error
                    Toast.makeText(requireContext(), "Failed to upload image", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                // Handle exception
                Toast.makeText(requireContext(), "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun loadImageFromUri(imageUri: Uri) {
        profileView.setImageURI(imageUri)
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

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_IMAGE_OPEN && resultCode == Activity.RESULT_OK) {
            val fileUri = data?.data
            Toast.makeText(requireContext(), "Image selected: $fileUri", Toast.LENGTH_SHORT).show()
        }
    }
    companion object {
        const val REQUEST_IMAGE_OPEN = 1
    }

}
