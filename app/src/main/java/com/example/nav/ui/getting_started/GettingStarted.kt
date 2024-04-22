package com.example.nav.ui.getting_started

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import com.example.nav.R
import com.example.nav.services.ChapterData
import com.example.nav.services.ChaptersData
import com.example.nav.services.Lesson
import com.example.nav.services.RetrofitClient
import com.example.nav.services.StepsDetails
import com.google.android.material.button.MaterialButton
import com.google.android.material.textview.MaterialTextView
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken
import com.squareup.picasso.Picasso
import kotlinx.coroutines.launch
import kotlin.properties.Delegates

class GettingStartedFragment : Fragment() {
    private lateinit var gettingStartedContainer: ViewGroup
    private lateinit var token: String
    private var user_id: Int = 0
    private var ChapterID: Int = 0
    private var LessonID: Int = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_getting_started, container, false)
        gettingStartedContainer = root.findViewById(R.id.gettingStartedContainer)
        gettingStartedContainer.removeAllViews()

        val sharedPreferences = requireActivity().getSharedPreferences("user_data", Context.MODE_PRIVATE)
        token = sharedPreferences.getString("token", "") ?: ""
        user_id = sharedPreferences.getInt("id", 0)
        setFragmentResultListener("gettingStartedResultKey") { _, bundle ->
            val stepsJson = bundle.getString("stepsData")
            val stepsData = Gson().fromJson<List<StepsDetails>>(stepsJson, object : TypeToken<List<StepsDetails>>() {}.type)

            // Now you have the stepsData, you can use it as needed
            displaySteps(stepsData)
        }

        setFragmentResultListener("chaptersLessonsIDAndName") { _, bundle ->
            val chapterJson = bundle.getString("fetchData")
            val chapterData = Gson().fromJson<ChaptersData>(chapterJson, object : TypeToken<ChaptersData>() {}.type)
            Log.d("GettingStartedFragment", "Chapter data: $chapterData")
            val chapter1 = chapterData.chapters.find { chapter: ChapterData ->
                chapter.chapter_name == "Chapter 1"
            }
            Log.d("GettingStartedFragment", "Chapter 1: $chapter1")
            chapter1?.let { chapter ->
                // Find the lesson with number "1.1"
                val lesson1_1 = chapter.lessons.find { it.lesson_number == "1.1" }
                lesson1_1?.let { lesson ->
                    ChapterID = chapter.id
                    LessonID = lesson.id
                }
            }
        }
        return root
    }

    @SuppressLint("SetTextI18n")
    private fun displaySteps(steps: List<StepsDetails>) {
        val gettingStartedTextView = context?.let {
            MaterialTextView(it).apply {
                text = "Getting Started"
                textSize = 24f
                textAlignment = View.TEXT_ALIGNMENT_TEXT_START
                setTextColor(ContextCompat.getColor(context, R.color.bl))
                setTypeface(null, android.graphics.Typeface.BOLD)
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    setMargins(
                        0,
                        0,
                        0,
                        resources.getDimensionPixelSize(R.dimen.lessons_margin_bottom)
                    )
                }
            }
        }

        gettingStartedContainer.addView(gettingStartedTextView)
        for (step in steps) {
            val stepTextView = context?.let {
                MaterialTextView(it).apply {
                    text = "Step ${step.order}: ${step.name}"
                    textSize = 16f
                    textAlignment = View.TEXT_ALIGNMENT_TEXT_START
                    setTextColor(ContextCompat.getColor(context, R.color.black))
                    layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    ).apply {
                        setMargins(
                            0,
                            0,
                            0,
                            resources.getDimensionPixelSize(R.dimen.lessons_margin_bottom)
                        )
                    }
                }
            }

            val stepDescriptionView = context?.let {
                MaterialTextView(it).apply {
                    text = step.description
                    textSize = 16f
                    textAlignment = View.TEXT_ALIGNMENT_TEXT_START
                    setTextColor(ContextCompat.getColor(context, R.color.black))
                    layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    ).apply {
                        setMargins(
                            0,
                            0,
                            0,
                            resources.getDimensionPixelSize(R.dimen.lessons_margin_bottom)
                        )
                    }
                }
            }

            val stepImageView = ImageView(context)
            stepImageView.adjustViewBounds = true
            stepImageView.layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(0, 0, 0, resources.getDimensionPixelSize(R.dimen.lessons_margin_bottom))
            }
            // Load image using Picasso
            Picasso.get().load(step.image).into(stepImageView)

            gettingStartedContainer.apply {
                addView(stepTextView)
                addView(stepDescriptionView)
                addView(stepImageView)
            }
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
                        val requestBody = JsonObject().apply {
                            addProperty("user_id", user_id)
                            addProperty("chapter_id", ChapterID)
                            addProperty("lesson_id", LessonID)
                            addProperty("completion_status", "inprogress")
                        }
                        val response = RetrofitClient.instance.createProgress("Bearer $token", requestBody)
                        if (response.isSuccessful) {
                            // Handle success
                            Toast.makeText(context, "Getting Started done. You can now proceed to Chapter 1.", Toast.LENGTH_SHORT).show()
                            findNavController().navigate(R.id.navigation_lesson)
                        } else {
                            // Handle error
                        }
                    }
                }
            }
        }

        lifecycleScope.launch {
            val requestBody = JsonObject().apply {
                addProperty("user_id", user_id)
            }
            val response = RetrofitClient.instance.getUnlocked("Bearer $token", requestBody)

            val unlocked = response.body()

            val isChapterLessonCompleted = unlocked?.any { it.chapter_id == ChapterID && it.lesson_id == LessonID } == true

            if (!isChapterLessonCompleted) {
                // Add doneButton only if chapter id and lesson id combination is not completed
                gettingStartedContainer.addView(doneButton)
            }
        }
    }
}