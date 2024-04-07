package com.example.nav.ui.lesson

import android.net.Uri
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.MediaController
import android.widget.VideoView
import com.example.nav.R

class Lesson21 : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_lesson21, container, false)

        val videoView = rootView.findViewById<VideoView>(R.id.videoView21)

        // Set the video path (replace R.raw.sample_video with your actual video file)
        val videoPath = "android.resource://${requireActivity().packageName}/${R.raw.l21}"
        videoView.setVideoURI(Uri.parse(videoPath))

        // Create MediaController
        val mediaController = MediaController(requireContext())
        mediaController.setAnchorView(videoView)

        // Set MediaController to VideoView
        videoView.setMediaController(mediaController)

        // Start playing the video
        videoView.start()

        return rootView // Return the rootView that was inflated at the beginning
    }
}