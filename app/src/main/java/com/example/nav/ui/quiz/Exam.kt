package com.example.nav.ui.quiz

import android.annotation.SuppressLint
import android.content.Context
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.RadioGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.marginBottom
import androidx.core.view.marginTop
import androidx.fragment.app.setFragmentResultListener
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.nav.R
import com.example.nav.databinding.FragmentLessonViewBinding
import com.example.nav.services.Chapter
import com.example.nav.services.ProgrammingLanguage
import com.example.nav.services.RetrofitClient
import com.google.android.material.button.MaterialButton
import com.google.android.material.radiobutton.MaterialRadioButton
import com.google.android.material.textview.MaterialTextView
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.launch

class Exam : Fragment() {
    private lateinit var examContainer: ViewGroup
    private var selectedChoices: MutableMap<Int, Int> = mutableMapOf()
    private lateinit var username : String
    private lateinit var token : String
    private var user_id : Int = 0
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val rootView = inflater.inflate(R.layout.fragment_exam, container, false)
        examContainer = rootView.findViewById(R.id.examContainer)

        val sharedPreferences = requireActivity().getSharedPreferences("user_data", Context.MODE_PRIVATE)
        username = sharedPreferences.getString("username", "") ?: ""
        token = sharedPreferences.getString("token", "") ?: ""
        user_id = sharedPreferences.getInt("id", 0)

        setFragmentResultListener("programmingLanguageDataKey") { _, bundle ->
            val examJson = bundle.getString("examData")
            val examData = Gson().fromJson<List<ProgrammingLanguage>>(examJson, object : TypeToken<List<ProgrammingLanguage>>() {}.type)

            // Now you have the lessonData, you can use it as needed
            displayAssessment(examData)
        }
        return rootView
    }

    @SuppressLint("SetTextI18n")
    private fun displayAssessment(examData: List<ProgrammingLanguage>) {
        examContainer.removeAllViews()

        val examTitle = context?.let {
            MaterialTextView(it).apply {
                text = "Exam"
                textSize = 24f
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

        examContainer.addView(examTitle)
        examData.let {response->
            for (programmingLanguage in response) {
                for (exam in programmingLanguage.exams) {


                    // Create TextView for the question
                    val questionTextView = context?.let {
                        MaterialTextView(it).apply {
                            text = exam.question_number.toString() + " " + exam.question
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
                    examContainer.addView(questionTextView)

                    // Create CardView with CodeSnippetTextView if code snippet is present
                    if (exam.code_snippet?.isNotEmpty() == true) {
                        val codeSnippetTextView = context?.let {
                            MaterialTextView(it).apply {
                                text = exam.code_snippet
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
                        examContainer.addView(codeSnippetTextView)
                    }
                    // Create RadioGroup for choices
                    val choicesGroup = RadioGroup(requireContext())
                    choicesGroup.orientation = RadioGroup.VERTICAL
                    examContainer.addView(choicesGroup)

                    // Create RadioButtons for each choice
                    for (i in 1..4) {
                        val choiceRadioButton = MaterialRadioButton(requireContext())
                        choiceRadioButton.text = when (i) {
                            1 -> exam.choice_1
                            2 -> exam.choice_2
                            3 -> exam.choice_3
                            4 -> exam.choice_4
                            else -> ""
                        }
                        choiceRadioButton.setOnCheckedChangeListener { buttonView, isChecked ->
                            if (isChecked) {
                                selectedChoices[exam.question_number] = i
                            }
                        }
                        choicesGroup.addView(choiceRadioButton)
                    }
                }

                // Create button for submitting answers
                val submitButton = context?.let {
                    MaterialButton(it).apply {
                        text = "Submit Answers"
                        setTextColor(ContextCompat.getColor(context, R.color.white))
                        setBackgroundColor(ContextCompat.getColor(context, R.color.bl))
                        layoutParams = LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                        )
                        (layoutParams as LinearLayout.LayoutParams).setMargins(
                            0,
                            resources.getDimensionPixelSize(R.dimen.submit_margin_top),
                            0,
                            0
                        )
                        setOnClickListener {
                            submitAnswers(examData)
                        }
                    }
                }
                examContainer.addView(submitButton)
            }
        }

    }

    private fun submitAnswers(programmingLanguages: List<ProgrammingLanguage>) {
        val totalQuestions = programmingLanguages.sumOf { it.exams.size }
        var score = 0 // Initialize total score\

        programmingLanguages.forEach { programmingLanguage ->
            programmingLanguage.exams.forEach { exam ->
                // Check if the selected choice matches the correct answer
                val selectedChoice = selectedChoices[exam.question_number]
                if (selectedChoice != null && selectedChoice == exam.correct_answer) {
                    score++ // Increment score for correct answer
                }
            }
        }

        // Calculate percentage score
        val percentageScore = (score.toFloat() / totalQuestions.toFloat()) * 100

        lifecycleScope.launch {
            val requestBody = JsonObject().apply {
                addProperty("user_id", user_id)
                addProperty("score", score)
            }

            RetrofitClient.instance.createExam("Bearer $token", requestBody)
        }

        if (percentageScore >= 75) {
            Toast.makeText(
                requireContext(),
                "Congratulations! You have passed the exam.",
                Toast.LENGTH_SHORT
            ).show()

            lifecycleScope.launch {
                val requestBody = JsonObject().apply {
                    addProperty("username", username)
                    addProperty("badge_name", "Exam Badge")
                }
                RetrofitClient.instance.addBadge("Bearer $token", requestBody)
            }

            Toast.makeText(requireContext(), "Score for exam: $score / $totalQuestions (${percentageScore}% correct)", Toast.LENGTH_SHORT).show()

            selectedChoices.clear()

            findNavController().navigate(R.id.navigation_lesson)
        } else {
            Toast.makeText(
                requireContext(),
                "Sorry, you have failed the exam.",
                Toast.LENGTH_SHORT
            ).show()

            Toast.makeText(requireContext(), "Score for exam: $score / $totalQuestions (${percentageScore}% correct)", Toast.LENGTH_SHORT).show()

            selectedChoices.clear()

            findNavController().navigate(R.id.navigation_lesson)
        }
    }
}