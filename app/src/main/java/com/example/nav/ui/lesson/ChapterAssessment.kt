package com.example.nav.ui.lesson

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.RadioGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.setFragmentResultListener
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.nav.R
import com.example.nav.databinding.FragmentLessonViewBinding
import com.example.nav.services.Chapter
import com.example.nav.services.RetrofitClient
import com.google.android.material.button.MaterialButton
import com.google.android.material.radiobutton.MaterialRadioButton
import com.google.android.material.textview.MaterialTextView
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.launch

class ChapterAssessment : Fragment() {
    private lateinit var chapAssContainer: ViewGroup
    private lateinit var binding: FragmentLessonViewBinding
    private var selectedChoices: MutableMap<Int, Int> = mutableMapOf()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLessonViewBinding.inflate(inflater, container, false)
        chapAssContainer = binding.viewLessonContainer

        setFragmentResultListener("chapterResultKey") { _, bundle ->
            val chapJSON = bundle.getString("chapterData")
            val chapData = Gson().fromJson<Chapter>(chapJSON, object : TypeToken<Chapter>() {}.type)

            // Now you have the lessonData, you can use it as needed
            displayAssessment(chapData)
        }
        return binding.root
    }

    @SuppressLint("SetTextI18n")
    private fun displayAssessment(chapAss: Chapter) {
        chapAssContainer.removeAllViews()

        val chapAssTitle = context?.let {
            MaterialTextView(it).apply {
                text = chapAss.chapter_name + " Assessment"
                textSize = 20f
                textAlignment = View.TEXT_ALIGNMENT_CENTER
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

        chapAssContainer.addView(chapAssTitle)

        for (question in chapAss.chapter_assessment) {
            // Create TextView for the question
            val questionTextView = context?.let {
                MaterialTextView(it).apply {
                    text = "$(question.question_number). $(question.question)"
                    textSize = 16f
                    textAlignment = View.TEXT_ALIGNMENT_TEXT_START
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
            chapAssContainer.addView(questionTextView)

            // Create CardView with CodeSnippetTextView if code snippet is present
            if (question.code_snippet?.isNotEmpty() == true) {
                val codeSnippetTextView = context?.let {
                    MaterialTextView(it).apply {
                        text = question.code_snippet
                        textSize = 14f
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
                chapAssContainer.addView(codeSnippetTextView)
            }
            // Create RadioGroup for choices
            val choicesGroup = RadioGroup(requireContext())
            choicesGroup.orientation = RadioGroup.VERTICAL
            chapAssContainer.addView(choicesGroup)

            // Create RadioButtons for each choice
            for (i in 1..4) {
                val choiceRadioButton = MaterialRadioButton(requireContext())
                choiceRadioButton.text = when (i) {
                    1 -> question.choice_1
                    2 -> question.choice_2
                    3 -> question.choice_3
                    4 -> question.choice_4
                    else -> ""
                }
                choiceRadioButton.setOnCheckedChangeListener { buttonView, isChecked ->
                    if (isChecked) {
                        selectedChoices[question.question_number] = i
                    }
                }
                choicesGroup.addView(choiceRadioButton)
            }
        }

        // Create button for submitting answers
        val submitButton = MaterialButton(requireContext())
        submitButton.text = "Submit Answers"
        submitButton.setOnClickListener {
            submitAnswers(chapAss.reference_number, chapAss)
        }
        chapAssContainer.addView(submitButton)

    }

    private fun submitAnswers(chap_reference_number: String, chapter: Chapter) {
        var score = 0 // Initialize score
        val totalQuestions = chapter.chapter_assessment.size
        val chapName = chapter.chapter_name

        // Iterate through each question
        for (question in chapter.chapter_assessment) {
            // Check if the selected choice matches the correct answer
            val selectedChoice = selectedChoices[question.question_number]
            if (selectedChoice != null && selectedChoice == question.correct_answer) {
                score++ // Increment score for correct answer
            }
        }

        // Calculate percentage score
        val percentageScore = (score.toFloat() / totalQuestions.toFloat()) * 100

        val sharedPreferences = requireActivity().getSharedPreferences("user_data", Context.MODE_PRIVATE)
        val username = sharedPreferences.getString("username", "")
        val token = sharedPreferences.getString("token", "")

        lifecycleScope.launch {
            val requestBody = JsonObject().apply {
                addProperty("username", username)
                addProperty("chap_ref", chapter.reference_number)
                addProperty("score", score)
            }
            RetrofitClient.instance.createProgressChapterAssessment("Bearer $token", requestBody)
        }

        Toast.makeText(requireContext(), "Assessment submitted!", Toast.LENGTH_SHORT).show()

        Toast.makeText(requireContext(), "Score for $chapName: $score / $totalQuestions (${percentageScore}% correct)", Toast.LENGTH_SHORT).show()

        println("Score for Chapter $chap_reference_number: $score / $totalQuestions (${percentageScore}% correct)")

        selectedChoices.clear()

        findNavController().navigate(R.id.navigation_lesson)
    }
}