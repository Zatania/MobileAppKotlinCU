package com.example.nav.ui.profile

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.nav.R
import com.example.nav.services.BadgesResponse
import com.example.nav.services.RetrofitClient
import com.example.nav.ui.screen.screen
import com.squareup.picasso.Picasso
import kotlinx.coroutines.launch

class ProfileFragment : Fragment() {
    private lateinit var imageView: ImageView
    private lateinit var badgesContainer: ViewGroup

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        // Retrieve saved user data
        val sharedPreferences = requireActivity().getSharedPreferences("user_data", Context.MODE_PRIVATE)
        val username = sharedPreferences.getString("username", "") ?: ""
        val token = sharedPreferences.getString("token", "") ?: ""
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

    private fun openImageSelectionDialog() {
        val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(galleryIntent, GALLERY_REQUEST_CODE)
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == GALLERY_REQUEST_CODE) {
                data?.data?.let { uri ->
                    imageView.setImageURI(uri)
                }
            }
        }
    }

    companion object {
        private const val GALLERY_REQUEST_CODE = 100
    }
}
