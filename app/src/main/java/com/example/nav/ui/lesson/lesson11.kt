package com.example.nav.ui.lesson

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.MediaController
import android.widget.TextView
import android.widget.VideoView
import com.example.nav.R

class lesson11 : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_lesson11, container, false)

        val videoView = rootView.findViewById<VideoView>(R.id.videoView4)

        // Set the video path (replace R.raw.sample_video with your actual video file)
        val videoPath = "android.resource://${requireActivity().packageName}/${R.raw.l1}"
        videoView.setVideoURI(Uri.parse(videoPath))

        // Create MediaController
        val mediaController = MediaController(requireContext())
        mediaController.setAnchorView(videoView)

        // Set MediaController to VideoView
        videoView.setMediaController(mediaController)

        // Start playing the video
        videoView.start()

        val codeTextView = rootView.findViewById<TextView>(R.id.textView30)

        // Enable text selection and copy functionality
        codeTextView.setOnLongClickListener {
            val clipboard = context?.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("copied_text", codeTextView.text)
            clipboard.setPrimaryClip(clip)
            true
        }

        return rootView
    }
}