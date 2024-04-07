package com.example.nav.ui.lesson

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

class Lesson22 : Fragment() {

    private lateinit var viewModel: Lesson22ViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = ViewModelProvider(this).get(Lesson22ViewModel::class.java)
        val rootView = inflater.inflate(R.layout.fragment_lesson22, container, false)

        // Use the correct ID for the VideoView
        val videoView = rootView.findViewById<VideoView>(R.id.videoView21)

        // Add code here to configure the VideoView, set the video URI, etc.
        val mediaController = MediaController(requireContext())
        mediaController.setAnchorView(videoView)
        videoView.setMediaController(mediaController)
        val videoPath = "android.resource://" + requireActivity().packageName + "/" + R.raw.l22
        val uri = Uri.parse(videoPath)
        videoView.setVideoURI(uri)

        return rootView
    }

}
