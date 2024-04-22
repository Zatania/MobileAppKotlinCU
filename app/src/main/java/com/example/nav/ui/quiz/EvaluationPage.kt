package com.example.nav.ui.quiz

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.setFragmentResult
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.nav.R
import com.example.nav.services.Completed
import com.example.nav.services.RetrofitClient
import com.google.android.material.button.MaterialButton
import com.google.gson.Gson
import com.google.gson.JsonObject
import kotlinx.coroutines.launch

class EvaluationPage : Fragment() {

    private var completedChaptersData: List<Completed>? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_eval, container, false)
        val buttonStartExercise: MaterialButton = rootView.findViewById(R.id.buttonStartExercise)
        val buttonStartExam: MaterialButton = rootView.findViewById(R.id.buttonStartExam)

        val sharedPreferences = requireActivity().getSharedPreferences("user_data", Context.MODE_PRIVATE)
        val token = sharedPreferences.getString("token", "") ?: ""
        val user_id = sharedPreferences.getInt("id", 0)
        lifecycleScope.launch {
            fetchCompleted(token, user_id)
            val response = RetrofitClient.instance.getProgrammingLanguages("Bearer $token")

            if (response.isSuccessful) {
                val programmingLanguage = response.body()
                if (programmingLanguage != null) {

                    var completedChapters = 0
                    var totalChapters = 0

                    programmingLanguage.forEach { programmingLanguages ->
                        programmingLanguages.chapters.forEach { chapter ->
                            totalChapters++
                            val chapExists = completedChaptersData?.any { it.chapter_id == chapter.id }
                            if (chapExists == true) {
                                completedChapters++
                            }
                        }
                    }

                    if (completedChapters == totalChapters) {
                        buttonStartExam.isEnabled = true
                    } else {
                        buttonStartExam.isEnabled = false
                    }

                    buttonStartExercise.setOnClickListener {
                        // Navigate to the exam fragment
                        val gson = Gson()
                        val chapJson = gson.toJson(programmingLanguage)
                        setFragmentResult("programmingLanguageDataKey", Bundle().apply {
                            putString("chapterAssessmentData", chapJson)
                        })
                        findNavController().navigate(R.id.action_navigation_quiz_to_ChapterFragment)
                    }
                    buttonStartExam.setOnClickListener {
                        // Navigate to the chapter assessments fragment
                        val gson = Gson()
                        val chapJson = gson.toJson(programmingLanguage)
                        setFragmentResult("programmingLanguageDataKey", Bundle().apply {
                            putString("examData", chapJson)
                        })
                        findNavController().navigate(R.id.action_navigation_quiz_to_ExamFragment)
                    }
                } else {
                    // Handle null response
                }
            } else {
                // Handle unsuccessful response
            }
        }
        return rootView
    }
    private suspend fun fetchCompleted(token: String, user_id: Int) {
        val requestBody = JsonObject().apply {
            addProperty("user_id", user_id)
        }
        val response = RetrofitClient.instance.getCompleted("Bearer $token", requestBody)

        val responseBody = response.body()

        responseBody?.let {
            completedChaptersData = it
            Log.d("ChapterFragment", "Completed chapters: $completedChaptersData")
        }
    }
}

