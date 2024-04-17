package com.example.nav.ui.lesson

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.core.view.setPadding
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.nav.R
import com.example.nav.services.ProgrammingLanguage
import com.example.nav.services.RetrofitClient
import com.example.nav.ui.getting_started.GettingStartedFragment
import com.google.android.material.button.MaterialButton
import com.google.android.material.textview.MaterialTextView
import kotlinx.coroutines.launch
import com.google.gson.Gson

class LessonFragment : Fragment() {

    private var currentChapterLayout: View? = null
    private lateinit var lessonsContainer: ViewGroup

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_lesson, container, false)

        // Retrieve saved user data
        val sharedPreferences = requireActivity().getSharedPreferences("user_data", Context.MODE_PRIVATE)
        val token = sharedPreferences.getString("token", "") ?: ""

        lessonsContainer = rootView.findViewById(R.id.lessonsContainer)
        lessonsContainer.removeAllViews()
        lifecycleScope.launch {
            fetchProgrammingLanguageData(token)
        }

        return rootView
    }

    private suspend fun fetchProgrammingLanguageData(token: String) {
        // Fetch programming language data from the API
        val response = RetrofitClient.instance.getProgrammingLanguages("Bearer $token")

        // Check if the response is successful
        if (response.isSuccessful) {
            val programmingLanguages = response.body()
            // Save programming language data to shared preferences
            displayFetchData(programmingLanguages)
        } else {
            // Handle error
        }
    }

    @SuppressLint("SetTextI18n")
    private fun displayFetchData(programmingLanguage: List<ProgrammingLanguage>?) {
        programmingLanguage?.let { response ->
            for (programmingLanguages in response) {
                val progLangName = programmingLanguages.programming_language
                val progLangDesc = programmingLanguages.description

                // Display programming language name and description
                val languageNameTextView = context?.let {
                    MaterialTextView(it).apply {
                        text = progLangName
                        textSize = 24f
                        setTypeface(typeface, Typeface.BOLD)
                        setTextColor(ContextCompat.getColor(context, R.color.bl))
                        textAlignment = View.TEXT_ALIGNMENT_TEXT_START
                        layoutParams = LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                        )
                        (layoutParams as LinearLayout.LayoutParams).setMargins(0, 0, 0, resources.getDimensionPixelSize(R.dimen.lessons_margin_bottom))
                    }
                }

                val descriptionTextView = context?.let {
                    MaterialTextView(it).apply {
                        text = progLangDesc
                        textSize = 16f
                        textAlignment = View.TEXT_ALIGNMENT_TEXT_START
                        layoutParams = LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                        )
                        setTextColor(ContextCompat.getColor(context, R.color.black))
                        (layoutParams as LinearLayout.LayoutParams).setMargins(0, 0, 0, resources.getDimensionPixelSize(R.dimen.lessons_margin_bottom))
                    }
                }

                // Add the TextViews for programming language and description
                lessonsContainer.apply {
                    addView(languageNameTextView)
                    addView(descriptionTextView)
                }



                for(gettingStarted in programmingLanguages.getting_started){
                    val getStartedButton = context?.let {
                        MaterialButton(it).apply {
                            text = "Getting Started"
                            textSize = 16f
                            setBackgroundColor(ContextCompat.getColor(context, R.color.lb))
                            isAllCaps = false
                            textAlignment = View.TEXT_ALIGNMENT_TEXT_START
                            layoutParams = LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.MATCH_PARENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT
                            )
                            (layoutParams as LinearLayout.LayoutParams).setMargins(0, 0, 0, resources.getDimensionPixelSize(R.dimen.lessons_margin_bottom))
                            setOnClickListener {
                                animateButtonClick(this)
                                this.postDelayed({
                                    val gson = Gson()
                                    val stepsJson = gson.toJson(gettingStarted.steps)

                                    setFragmentResult("gettingStartedResultKey", bundleOf("stepsData" to stepsJson))
                                    findNavController().navigate(R.id.navigation_getting_started)
                                }, 200)
                            }
                        }
                    }
                    lessonsContainer.addView(getStartedButton)
                }

                for(chapter in programmingLanguages.chapters){
                    val chapterButton = context?.let {
                        MaterialButton(it).apply {
                            text = chapter.chapter_name
                            textSize = 16f
                            setBackgroundColor(ContextCompat.getColor(context, R.color.lb))
                            isAllCaps = false
                            textAlignment = View.TEXT_ALIGNMENT_TEXT_START
                            layoutParams = LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.MATCH_PARENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT
                            )
                            (layoutParams as LinearLayout.LayoutParams).setMargins(0, 0, 0, resources.getDimensionPixelSize(R.dimen.lessons_margin_bottom))
                        }
                    }

                    val chapterLayout = context?.let {
                        LinearLayout(it).apply {
                            orientation = LinearLayout.VERTICAL
                            layoutParams = LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.MATCH_PARENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT
                            )
                            setPadding(resources.getDimensionPixelSize(R.dimen.lessons_padding))
                            visibility = View.GONE
                        }
                    }

                    // Add the chapter button and layout to the lessons container
                    lessonsContainer.apply {
                        addView(chapterButton)
                        addView(chapterLayout)
                    }



                    // Set up chapter button and layout
                    if (chapterButton != null) {
                        if (chapterLayout != null) {
                            setupChapterButton(chapterButton, chapterLayout)


                            for(lesson in chapter.lessons){
                                val lessonButton = context?.let {
                                    MaterialButton(it).apply {
                                        text = lesson.lesson_number + " " + lesson.lesson_title
                                        textSize = 16f
                                        setBackgroundColor(ContextCompat.getColor(context, R.color.lb))
                                        isAllCaps = false
                                        textAlignment = View.TEXT_ALIGNMENT_TEXT_START
                                        layoutParams = LinearLayout.LayoutParams(
                                            LinearLayout.LayoutParams.MATCH_PARENT,
                                            LinearLayout.LayoutParams.WRAP_CONTENT
                                        )
                                        (layoutParams as LinearLayout.LayoutParams).setMargins(0, 0, 0, resources.getDimensionPixelSize(R.dimen.lessons_margin_bottom))
                                        setOnClickListener {
                                            animateButtonClick(this)
                                            this.postDelayed({
                                                val gson = Gson()
                                                val lessonJSon = gson.toJson(lesson)
                                                Log.d("LessonFragment", "Lesson data: $lessonJSon")
                                                setFragmentResult("lessonResultKey", bundleOf("lessonData" to lessonJSon))
                                                findNavController().navigate(R.id.navigation_lesson_view)
                                            }, 200)
                                        }
                                    }
                                }
                                chapterLayout?.addView(lessonButton)
                            }

                            // Check if chapter assessments exist
                            if (chapter.chapter_assessment.isNotEmpty()) {
                                // Add chapter assessment button
                                val chapterAssessmentButton = context?.let {
                                    MaterialButton(it).apply {
                                        text = chapter.chapter_name + " Assessment"
                                        textSize = 16f
                                        setBackgroundColor(
                                            ContextCompat.getColor(
                                                context,
                                                R.color.lb
                                            )
                                        )
                                        isAllCaps = false
                                        textAlignment = View.TEXT_ALIGNMENT_TEXT_START
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
                                            animateButtonClick(this)
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
                                chapterLayout.addView(chapterAssessmentButton)
                            }
                        }
                    }
                }
            }
        }
    }
    private fun setupChapterButton(
        chapterButton: MaterialButton,
        chapterLayout: LinearLayout
    ) {
        chapterButton.setOnClickListener {
            if (currentChapterLayout == chapterLayout) {
                // Clicked on the same chapter button, close the layout
                currentChapterLayout?.visibility = View.GONE
                currentChapterLayout = null
            } else {
                // Clicked on a different chapter button
                currentChapterLayout?.visibility = View.GONE // Close the previously opened layout
                currentChapterLayout = chapterLayout
                chapterLayout.visibility = View.VISIBLE // Open the clicked chapter layout
                animateChapterLayout(chapterLayout)
            }
            animateButtonClick(chapterButton)
        }

        // Set click listeners for lesson buttons
        applyLessonButtonAnimationSequentially(chapterLayout)

        // Set click listener for chapter assessment button
        //chapterLayout.findViewById<MaterialButton>()?.setOnClickListener {
        //    findNavController().navigate(R.id.examFragment)
        //}
    }

    private fun applyLessonButtonAnimationSequentially(
        chapterLayout: View
    ) {
        // Iterate through lesson buttons and apply animations sequentially
        for (i in 0 until (chapterLayout as ViewGroup).childCount) {
            val lessonButton = chapterLayout.getChildAt(i) as? Button
            lessonButton?.let { button ->
                button.setOnClickListener {
                    // Navigate to lesson fragment
                    findNavController().navigate(R.id.navigation_getting_started)
                }
                // Apply animation directly
                animateLessonButton(button)
            }
        }
    }

    private fun animateLessonButton(button: Button) {
        val slideDownAnimation = AnimationUtils.loadAnimation(requireContext(), R.anim.slide_down)
        button.startAnimation(slideDownAnimation)
    }

    private fun animateChapterLayout(chapterLayout: View) {
        // Iterate through lesson buttons and apply animations directly
        for (i in 0 until (chapterLayout as ViewGroup).childCount) {
            val lessonButton = chapterLayout.getChildAt(i)
            animateLessonButton(lessonButton as Button)
        }
    }

    private fun animateButtonClick(button: Button) {
        val slideDownAnimation =
            AnimationUtils.loadAnimation(requireContext(), R.anim.slide_down)
        button.startAnimation(slideDownAnimation)
    }
}
