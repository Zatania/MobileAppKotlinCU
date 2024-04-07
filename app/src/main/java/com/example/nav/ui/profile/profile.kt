package com.example.nav.ui.profile

import android.animation.ValueAnimator
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
import android.widget.ProgressBar
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import com.example.nav.R
import com.example.nav.ui.screen.screen

class ProfileFragment : Fragment() {
    private lateinit var imageView: ImageView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        // Retrieve saved user data
        val sharedPreferences = requireActivity().getSharedPreferences("user_data", Context.MODE_PRIVATE)
        val username = sharedPreferences.getString("username", "") ?: ""
        val email = sharedPreferences.getString("email", "") ?: ""

        // Set username and email to TextViews
        val usernameTextView: TextView = view.findViewById(R.id.textView2)
        val emailTextView: TextView = view.findViewById(R.id.textView3)
        usernameTextView.text = username
        emailTextView.text = email

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
