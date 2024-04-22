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
import com.example.nav.services.ChapAss
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
    private var selectedChoices: MutableMap<Int, Int> = mutableMapOf()
    private lateinit var token: String
    private lateinit var username: String
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val rootView = inflater.inflate(R.layout.fragment_chap_asses, container, false)
        chapAssContainer = rootView.findViewById(R.id.chapAssContainer)

        val sharedPreferences = requireActivity().getSharedPreferences("user_data", Context.MODE_PRIVATE)
        token = sharedPreferences.getString("token", "") ?: ""
        username = sharedPreferences.getString("username", "") ?: ""

        setFragmentResultListener("chapterResultKey") { _, bundle ->
            val chapJSON = bundle.getString("chapterData")
            val chapData = Gson().fromJson<Chapter>(chapJSON, object : TypeToken<Chapter>() {}.type)

            // Now you have the lessonData, you can use it as needed
            displayAssessment(chapData)
        }
        return rootView
    }

    @SuppressLint("SetTextI18n")
    private fun displayAssessment(chapAss: Chapter) {
        chapAssContainer.removeAllViews()

        val chapAssTitle = context?.let {
            MaterialTextView(it).apply {
                text = chapAss.chapter_name + " Assessment"
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

        chapAssContainer.addView(chapAssTitle)

        lifecycleScope.launch {
            val response = RetrofitClient.instance.getUserChapAss("Bearer $token", username)

            val responseBody = response.body()

            if (response.isSuccessful && responseBody != null) {
                responseBody.let {
                    val chapAssData: List<ChapAss> = it

                    val passedChaps = chapAssData.find { it.status == "Passed" && it.chapter_name == chapAss.chapter_name }
                    val failedChaps = chapAssData.find { it.status == "Failed" && it.chapter_name == chapAss.chapter_name }

                    if (passedChaps != null) {
                        val messageTextView = context?.let {
                            MaterialTextView(it).apply {
                                text = "You have passed this assessment with a score of ${passedChaps.latest_score} / ${passedChaps.total_items} last attempted on ${passedChaps.last_attempt}"
                                textSize = 10f
                                textAlignment = View.TEXT_ALIGNMENT_CENTER
                                setTextColor(ContextCompat.getColor(context, R.color.green))
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

                        chapAssContainer.addView(messageTextView)
                    } else if (failedChaps != null) {
                        val messageTextView = context?.let {
                            MaterialTextView(it).apply {
                                text = "You have failed this assessment with a score of ${failedChaps.latest_score} / ${failedChaps.total_items} last attempted on ${failedChaps.last_attempt}. Please try again."
                                textSize = 10f
                                textAlignment = View.TEXT_ALIGNMENT_CENTER
                                setTextColor(ContextCompat.getColor(context, R.color.red))
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

                        chapAssContainer.addView(messageTextView)
                    }
                }
            }   else {
                Toast.makeText(requireContext(), "Failed to fetch chapter assessment", Toast.LENGTH_SHORT).show()
            }
        }

        for (question in chapAss.chapter_assessment) {
            // Create TextView for the question
            val questionTextView = context?.let {
                MaterialTextView(it).apply {
                    text = question.question_number.toString() + " " + question.question
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
        val chapID = chapter.id

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
        val userID = sharedPreferences.getInt("id", 0)
        val token = sharedPreferences.getString("token", "")

        lifecycleScope.launch {
            val requestBody = JsonObject().apply {
                addProperty("chapter_id", chapID)
                addProperty("user_id", userID)
                addProperty("score", score)
            }
            RetrofitClient.instance.createProgress("Bearer $token", requestBody)
        }

        if (percentageScore >= 75) {
            Toast.makeText(requireContext(), "Congratulations! You passed the assessment!", Toast.LENGTH_SHORT).show()
            lifecycleScope.launch {
                val requestBody = JsonObject().apply {
                    addProperty("chapter_id", chapID)
                }
                val response = RetrofitClient.instance.getNextChapterId("Bearer $token", requestBody)

                val nextChapterID = response.body()?.next_chapter_id

                if (response.isSuccessful && nextChapterID != null) {
                    val requestBodyLesson = JsonObject().apply {
                        addProperty("chapter_id", nextChapterID)
                    }

                    val responseLesson = RetrofitClient.instance.getFirstLessonID("Bearer $token", requestBodyLesson)

                    val firstLessonID = responseLesson.body()?.first_lesson_id

                    if (responseLesson.isSuccessful && firstLessonID != null) {
                        val checkStatusBody = JsonObject().apply {
                            addProperty("user_id", userID)
                            addProperty("chapter_id", nextChapterID)
                            addProperty("lesson_id", firstLessonID)
                        }
                        val checkChapterandLessonID = RetrofitClient.instance.getStatusID("Bearer $token", checkStatusBody)
                        if (checkChapterandLessonID.isSuccessful) {
                            Toast.makeText(
                                requireContext(),
                                "Congratulations! You have completed the chapter. You can now proceed to the next chapter.",
                                Toast.LENGTH_SHORT
                            ).show()
                            val requestBodyProgress = JsonObject().apply {
                                addProperty("user_id", userID)
                                addProperty("completion_status", "inprogress")
                                addProperty("lesson_id", firstLessonID)
                                addProperty("chapter_id", nextChapterID)
                            }
                            val responseProgress = RetrofitClient.instance.createProgress(
                                "Bearer $token",
                                requestBodyProgress
                            )

                            if (responseProgress.isSuccessful) {
                                Toast.makeText(
                                    requireContext(),
                                    "Congratulations! You have completed the chapter. You can now proceed to the next chapter.",
                                    Toast.LENGTH_SHORT
                                ).show()
                            } else {
                                Toast.makeText(requireContext(), "Failed to create progress", Toast.LENGTH_SHORT).show()
                            }
                        } else {
                            val requestBodyProgress = JsonObject().apply {
                                addProperty("user_id", userID)
                                addProperty("completion_status", "inprogress")
                                addProperty("lesson_id", firstLessonID)
                                addProperty("chapter_id", nextChapterID)
                            }
                            val responseProgress = RetrofitClient.instance.createProgress(
                                "Bearer $token",
                                requestBodyProgress
                            )

                            if (responseProgress.isSuccessful) {
                                Toast.makeText(
                                    requireContext(),
                                    "Congratulations! You have completed the chapter. You can now proceed to the next chapter.",
                                    Toast.LENGTH_SHORT
                                ).show()
                            } else {
                                Toast.makeText(requireContext(), "Failed to create progress", Toast.LENGTH_SHORT).show()
                            }
                        }
                    } else {
                        Toast.makeText(requireContext(), "Failed to fetch last lesson", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(requireContext(), "Failed to fetch next chapter", Toast.LENGTH_SHORT).show()
                }
            }
            Toast.makeText(requireContext(), "Assessment submitted! Score for $chapName: $score / $totalQuestions (${percentageScore}% correct)", Toast.LENGTH_SHORT).show()

            selectedChoices.clear()

            findNavController().navigate(R.id.navigation_lesson)
        } else {
            Toast.makeText(requireContext(), "Sorry, you did not pass the assessment. Please try again to unlock next chapter. Score for $chapName: $score / $totalQuestions (${percentageScore}% correct)", Toast.LENGTH_SHORT).show()

            selectedChoices.clear()

            findNavController().navigate(R.id.navigation_lesson)
        }
    }
}