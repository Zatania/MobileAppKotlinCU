package com.example.nav.ui.lesson

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.nav.R

class LessonFragment : Fragment() {

    private var currentChapterLayout: View? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_lesson, container, false)

        val getStartedButton: Button = rootView.findViewById(R.id.GetStarted)
        getStartedButton.setOnClickListener {
            animateButtonClick(getStartedButton)
            getStartedButton.postDelayed({
                findNavController().navigate(R.id.action_LessonFragment_to_GStart)
            }, 200)
        }

        val chapter1Button: Button = rootView.findViewById(R.id.c1button)
        val chapter1Layout: View = rootView.findViewById(R.id.chapter1Layout)
        val chapter2Button: Button = rootView.findViewById(R.id.c2button)
        val chapter2Layout: View = rootView.findViewById(R.id.chapter2Layout)
        val chapter3Button: Button = rootView.findViewById(R.id.c3button)
        val chapter3Layout: View = rootView.findViewById(R.id.chapter3Layout)
        val chapter4Button: Button = rootView.findViewById(R.id.c4button)
        val chapter4Layout: View = rootView.findViewById(R.id.chapter4Layout)
        val chapter5Button: Button = rootView.findViewById(R.id.c5button)
        val chapter5Layout: View = rootView.findViewById(R.id.chapter5Layout)

        setupChapterButton(
            chapter1Button,
            chapter1Layout,
            R.id.action_LessonFragment_to_lesson11,
            R.id.action_LessonFragment_to_chapAsses
        )
        setupChapterButton(
            chapter2Button,
            chapter2Layout,
            R.id.action_LessonFragment_to_lesson21,
            R.id.action_LessonFragment_to_chap2Asses
        )
        setupChapterButton(
            chapter3Button,
            chapter3Layout,
            R.id.action_LessonFragment_to_lesson31,
            R.id.action_LessonFragment_to_chap3Asses
        )
        setupChapterButton(
            chapter4Button,
            chapter4Layout,
            R.id.action_LessonFragment_to_lesson41,
            R.id.action_LessonFragment_to_chap4Asses
        )
        setupChapterButton(
            chapter5Button,
            chapter5Layout,
            R.id.action_LessonFragment_to_lesson51,
            R.id.action_LessonFragment_to_chap5Asses
        )

        return rootView
    }

    private fun setupChapterButton(
        chapterButton: Button,
        chapterLayout: View,
        navAction: Int,
        assessmentAction: Int
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
        applyLessonButtonAnimationSequentially(chapterLayout, navAction)

        // Set click listener for chapter assessment button
        chapterLayout.findViewById<Button>(assessmentAction)?.setOnClickListener {
            findNavController().navigate(assessmentAction)
        }
    }

    private fun applyLessonButtonAnimationSequentially(
        chapterLayout: View,
        navAction: Int
    ) {
        // Iterate through lesson buttons and apply animations sequentially
        for (i in 0 until (chapterLayout as ViewGroup).childCount) {
            val lessonButton = chapterLayout.getChildAt(i) as? Button
            lessonButton?.let { button ->
                button.setOnClickListener {
                    // Navigate to lesson fragment
                    findNavController().navigate(navAction)
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
