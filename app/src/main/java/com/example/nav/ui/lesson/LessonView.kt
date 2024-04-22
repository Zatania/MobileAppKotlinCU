package com.example.nav.ui.lesson

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import android.webkit.WebViewFragment
import android.widget.LinearLayout
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import com.example.nav.R
import com.example.nav.databinding.FragmentLessonViewBinding
import com.example.nav.services.Lesson
import com.example.nav.services.RetrofitClient
import com.google.android.material.button.MaterialButton
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.launch


class LessonViewFragment : Fragment() {
    private lateinit var viewLessonContainer: ViewGroup
    private lateinit var binding: FragmentLessonViewBinding
    private lateinit var videoView: WebView
    private lateinit var token: String
    private var user_id: Int = 0
    private lateinit var username: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLessonViewBinding.inflate(inflater, container, false)
        viewLessonContainer = binding.viewLessonContainer

        val sharedPreferences = requireActivity().getSharedPreferences("user_data", Context.MODE_PRIVATE)
        token = sharedPreferences.getString("token", "") ?: ""
        user_id = sharedPreferences.getInt("id", 0)
        username = sharedPreferences.getString("username", "") ?: ""

        setFragmentResultListener("lessonResultKey") { _, bundle ->
            val lessonJson = bundle.getString("lessonData")
            val lessonData = Gson().fromJson<Lesson>(lessonJson, object : TypeToken<Lesson>() {}.type)

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

        val doneButton = context?.let {
            MaterialButton(it).apply {
                text = "Done"
                textSize = 16f
                isEnabled = true
                setBackgroundColor(ContextCompat.getColor(context, R.color.lb))
                isAllCaps = false
                textAlignment = View.TEXT_ALIGNMENT_CENTER
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    resources.getDimensionPixelSize(R.dimen.done_button_height)
                )
                (layoutParams as LinearLayout.LayoutParams).setMargins(0, resources.getDimensionPixelSize(R.dimen.button_margin_top), 0, 0)
                setOnClickListener {
                    lifecycleScope.launch {
                        try {
                            val requestBodyNext = JsonObject().apply {
                                addProperty("chapter_id", lesson.chapter_id)
                                addProperty("lesson_id", lesson.id)
                            }
                            val response = RetrofitClient.instance.getNextLessonId("Bearer $token", requestBodyNext)
                            if (response.isSuccessful) {
                                val nextLessonId = response.body()?.next_lesson_id
                                if (nextLessonId != null) {
                                    val requestBody = JsonObject().apply {
                                        addProperty("user_id", user_id)
                                        addProperty("completion_status", "inprogress")
                                        addProperty("lesson_id", nextLessonId)
                                        addProperty("chapter_id", lesson.chapter_id)
                                    }
                                    val responseFinal = RetrofitClient.instance.createProgress("Bearer $token", requestBody)

                                    if (responseFinal.isSuccessful) {
                                        val requestUpdateBody = JsonObject().apply {
                                            addProperty("user_id", user_id)
                                            addProperty("chapter_id", lesson.chapter_id)
                                            addProperty("lesson_id", lesson.id)
                                            addProperty("completion_status", "completed")
                                        }
                                        RetrofitClient.instance.updateProgress("Bearer $token", requestUpdateBody)
                                        Toast.makeText(context, "Lesson done. You can now proceed to next lesson.", Toast.LENGTH_SHORT).show()
                                        findNavController().navigate(R.id.navigation_lesson)
                                    } else {
                                        // Handle error
                                    }
                                } else {
                                    Toast.makeText(context, "No next lesson found", Toast.LENGTH_SHORT).show()
                                }
                            } else {
                                val requestUpdateBody = JsonObject().apply {
                                    addProperty("user_id", user_id)
                                    addProperty("chapter_id", lesson.chapter_id)
                                    addProperty("lesson_id", lesson.id)
                                    addProperty("completion_status", "completed")
                                }
                                RetrofitClient.instance.updateProgress("Bearer $token", requestUpdateBody)
                                Toast.makeText(context, "Lesson done. You can now proceed to next lesson.", Toast.LENGTH_SHORT).show()
                                findNavController().navigate(R.id.navigation_lesson)
                            }
                        } catch (e: Exception) {
                            Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                            Log.e("LessonViewFragment", "Error fetching next lesson: ${e.message}", e)
                        }
                    }
                }
            }
        }

        lifecycleScope.launch {
            val requestBody = JsonObject().apply {
                addProperty("user_id", user_id)
            }
            val response = RetrofitClient.instance.getCompleted("Bearer $token", requestBody)

            val progress = response.body()

            val isCompleted = progress?.any { it.chapter_id == lesson.chapter_id && it.lesson_id == lesson.id } == true

            if (!isCompleted) {
                // Add doneButton only if chapter id and lesson id combination is not completed
                viewLessonContainer.addView(doneButton)
            }
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun loadVideo(videoUrl: String) {
        videoView = binding.root.findViewById(R.id.videoView)
        videoView.settings.javaScriptEnabled = true
        videoView.settings.loadWithOverviewMode = true
        videoView.settings.useWideViewPort = true
        videoView.settings.builtInZoomControls = true
        videoView.settings.displayZoomControls = false
        videoView.webChromeClient = WebChromeClient()
        videoView.webViewClient = WebViewClient()
        videoView.loadUrl(videoUrl)
    }
}