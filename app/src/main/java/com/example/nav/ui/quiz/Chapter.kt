package com.example.nav.ui.quiz

import android.annotation.SuppressLint
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.setFragmentResultListener
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.nav.R
import com.example.nav.services.ChapterAssessmentResponse
import com.example.nav.services.Completed
import com.example.nav.services.ProgrammingLanguage
import com.example.nav.services.RetrofitClient
import com.example.nav.services.StepsDetails
import com.example.nav.services.Unlocked
import com.google.android.material.button.MaterialButton
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.launch

class Chapter : Fragment() {
    private lateinit var buttonContainer: ViewGroup
    private var completedChaptersData: List<Completed>? = null
    private var unlockedChaptersData: List<Unlocked>? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_chapter, container, false)
        buttonContainer = rootView.findViewById(R.id.buttonContainer)
        buttonContainer.removeAllViews()

        val sharedPreferences = requireActivity().getSharedPreferences("user_data", 0)
        val token = sharedPreferences.getString("token", "") ?: ""

        lifecycleScope.launch {
            fetchCompleted(token)
            fetchUnlocked(token)
            setFragmentResultListener("programmingLanguageDataKey") { _, bundle ->
                val progamJson = bundle.getString("chapterAssessmentData")
                val programData = Gson().fromJson<List<ProgrammingLanguage>>(progamJson, object : TypeToken<List<ProgrammingLanguage>>() {}.type)
                displayData(programData)
            }
        }

        return rootView
    }

    private suspend fun fetchCompleted(token: String) {
        val response = RetrofitClient.instance.getCompleted("Bearer $token")

        val responseBody = response.body()

        responseBody?.let {
            completedChaptersData = it
            Log.d("ChapterFragment", "Completed chapters: $completedChaptersData")
        }
    }

    private suspend fun fetchUnlocked(token: String) {
        val response = RetrofitClient.instance.getUnlocked("Bearer $token")

        val responseBody = response.body()
        responseBody?.let {
            unlockedChaptersData = it
        }
    }

    @SuppressLint("SetTextI18n")
    private fun displayData(programData: List<ProgrammingLanguage>) {
        fun countCompleted(chapterId: Int): Int {
            var totalLessons = 0
            var completedLessons = 0

            programData.forEach { programmingLanguages ->
                programmingLanguages.chapters.forEach { chapter ->
                    if (chapter.id == chapterId) {
                        totalLessons += chapter.lessons.size
                        chapter.lessons.forEach { lesson ->
                            val lessonExists = completedChaptersData?.any { it.lesson_id == lesson.id }
                            if (lessonExists == true) {
                                completedLessons++
                            }
                        }
                    }
                }
            }

            return completedLessons
        }

        fun chapterLessons(chapterId: Int): Int {
            var totalLessons = 0
            var completedLessons = 0

            programData.forEach { programmingLanguages ->
                programmingLanguages.chapters.forEach { chapter ->
                    if (chapter.id == chapterId) {
                        totalLessons += chapter.lessons.size
                        chapter.lessons.forEach { lesson ->
                            val lessonExists = unlockedChaptersData?.any { it.lesson_id == lesson.id }
                            if (lessonExists == true) {
                                completedLessons++
                            }
                        }
                    }
                }
            }

            return totalLessons
        }
        programData.let { response ->
            for (program in response) {
                for(chapter in program.chapters) {
                    if(chapter.chapter_assessment.isNotEmpty()) {
                        val chapLesson = chapterLessons(chapter.id)
                        val completed = countCompleted(chapter.id)
                        val chapterAssessmentButton = context?.let {
                            MaterialButton(it).apply {
                                text = chapter.chapter_name + " Assessment"
                                isEnabled = chapLesson == completed
                                textSize = 16f
                                setBackgroundColor(
                                    ContextCompat.getColor(
                                        context,
                                        R.color.lb
                                    )
                                )
                                layoutParams = LinearLayout.LayoutParams(
                                    LinearLayout.LayoutParams.MATCH_PARENT,
                                    LinearLayout.LayoutParams.WRAP_CONTENT
                                )
                                (layoutParams as LinearLayout.LayoutParams).setMargins(
                                    0,
                                    0,
                                    0,
                                    resources.getDimensionPixelSize(R.dimen.lessons_margin_bottom)
                                )
                                setOnClickListener {
                                    this.postDelayed({
                                        val gson = Gson()
                                        val chapterJson = gson.toJson(chapter)
                                        Log.d("LessonFragment", "Chapter data: $chapterJson")
                                        setFragmentResult(
                                            "chapterResultKey",
                                            bundleOf("chapterData" to chapterJson)
                                        )
                                        findNavController().navigate(R.id.navigation_chapter_assessment)
                                    }, 200)
                                }
                            }
                        }
                        buttonContainer.addView(chapterAssessmentButton)
                    }
                }
            }
        }
    }
}
