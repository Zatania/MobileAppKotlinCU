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
import androidx.navigation.fragment.findNavController
import com.example.nav.R
import com.example.nav.services.ChapterAssessmentResponse
import com.example.nav.services.ProgrammingLanguage
import com.example.nav.services.StepsDetails
import com.google.android.material.button.MaterialButton
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class Chapter : Fragment() {
    private lateinit var buttonContainer: ViewGroup
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_chapter, container, false)
        buttonContainer = rootView.findViewById(R.id.buttonContainer)
        buttonContainer.removeAllViews()
        setFragmentResultListener("programmingLanguageDataKey") { _, bundle ->
            val progamJson = bundle.getString("chapterAssessmentData")
            val programData = Gson().fromJson<List<ProgrammingLanguage>>(progamJson, object : TypeToken<List<ProgrammingLanguage>>() {}.type)
            displayData(programData)
        }

        return rootView
    }

    @SuppressLint("SetTextI18n")
    private fun displayData(programData: List<ProgrammingLanguage>) {
        programData.let { response ->
            for (program in response) {
                for(chapter in program.chapters) {
                    if(chapter.chapter_assessment.isNotEmpty()) {
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
