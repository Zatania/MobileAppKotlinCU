package com.example.nav.ui.lesson

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebView
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import com.example.nav.R
import com.example.nav.databinding.FragmentLessonViewBinding
import com.example.nav.services.Lesson
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken


class LessonViewFragment : Fragment() {
    private lateinit var viewLessonContainer: ViewGroup
    private lateinit var binding: FragmentLessonViewBinding
    private lateinit var videoWebView: WebView
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLessonViewBinding.inflate(inflater, container, false)
        viewLessonContainer = binding.viewLessonContainer

        setFragmentResultListener("lessonResultKey") { _, bundle ->
            val lessonJson = bundle.getString("lessonData")
            val lessonData = Gson().fromJson<Lesson>(lessonJson, object : TypeToken<Lesson>() {}.type)

            Log.d("LessonViewFragment", "Lesson data: $lessonData")
            // Now you have the lessonData, you can use it as needed
            displayLesson(lessonData)
        }
        return binding.root
    }

    @SuppressLint("SetTextI18n")
    private fun displayLesson(lesson: Lesson) {
        binding.lessonName.text = lesson.lesson_number + " " + lesson.lesson_title
        binding.lessonDescription.text = lesson.lesson_description
        binding.exampleCode.text = lesson.lesson_example_code
        binding.output.text = lesson.lesson_output
        binding.explanation.text = lesson.lesson_explanation

        if (lesson.lesson_video.isNotEmpty()) {
            loadVideo(lesson.lesson_video)
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun loadVideo(videoUrl: String) {
        videoWebView = binding.root.findViewById(R.id.videoWebView)
        videoWebView.settings.javaScriptEnabled = true
        videoWebView.loadUrl(videoUrl)
    }
}