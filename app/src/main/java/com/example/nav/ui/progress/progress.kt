package com.example.nav.ui.progress

import android.animation.ValueAnimator
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.nav.R
import com.example.nav.databinding.FragmentProgressBinding
import java.text.SimpleDateFormat
import java.util.*

class ProgressFragment : Fragment() {
    private var _binding: FragmentProgressBinding? = null
    private val binding get() = _binding!!

    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProgressBinding.inflate(inflater, container, false)
        val root: View = binding.root

        sharedPreferences = requireContext().getSharedPreferences("progress", Context.MODE_PRIVATE)

        val progressBarGetStarted = binding.progressBarGetStarted
        val progressTextGetStarted = binding.progressTextGetStarted

        updateChapterProgress(progressBarGetStarted, 0)

        // Apply sample progress to Get Started
        val sampleProgressGetStarted = 45 // Sample progress value (0-100)
        updateProgressWithAnimation(progressBarGetStarted, progressTextGetStarted, sampleProgressGetStarted)

        return root
    }

    private fun navigateToChapterLesson(chapter: Int) {
        val lastOpenedChapterKey = "last_opened_chapter_$chapter"
        val lastOpenedLessonKey = "last_opened_lesson_$chapter"
        val lastOpenedChapter = sharedPreferences.getInt(lastOpenedChapterKey, 1)
        val lastOpenedLesson = sharedPreferences.getInt(lastOpenedLessonKey, 1)

        findNavController().navigate(getLessonId(chapter, lastOpenedLesson))
    }

    private fun getLessonId(chapter: Int, lesson: Int): Int {
        return when (chapter) {
            1 -> when (lesson) {
                1 -> R.id.action_progressFragment_to_lesson11
                2 -> R.id.action_progressFragment_to_lesson12
                3 -> R.id.action_progressFragment_to_lesson13
                4 -> R.id.action_progressFragment_to_lesson14
                else -> R.id.action_progressFragment_to_lesson11
            }
            2 -> when (lesson) {
                1 -> R.id.action_progressFragment_to_lesson21
                2 -> R.id.action_progressFragment_to_lesson22
                3 -> R.id.action_progressFragment_to_lesson23
                4 -> R.id.action_progressFragment_to_lesson24
                else -> R.id.action_progressFragment_to_lesson21
            }
            3 -> when (lesson) {
                1 -> R.id.action_progressFragment_to_lesson31
                2 -> R.id.action_progressFragment_to_lesson32
                3 -> R.id.action_progressFragment_to_lesson33
                4 -> R.id.action_progressFragment_to_lesson34
                else -> R.id.action_progressFragment_to_lesson31
            }
            4 -> when (lesson) {
                1 -> R.id.action_progressFragment_to_lesson41
                2 -> R.id.action_progressFragment_to_lesson42
                3 -> R.id.action_progressFragment_to_lesson43
                4 -> R.id.action_progressFragment_to_lesson44
                else -> R.id.action_progressFragment_to_lesson41
            }
            5 -> when (lesson) {
                1 -> R.id.action_progressFragment_to_lesson51
                2 -> R.id.action_progressFragment_to_lesson52
                3 -> R.id.action_progressFragment_to_lesson53
                4 -> R.id.action_progressFragment_to_lesson54
                5 -> R.id.action_progressFragment_to_lesson55
                else -> R.id.action_progressFragment_to_lesson51
            }
            else -> R.id.action_progressFragment_to_lesson11
        }
    }

    private fun updateChapterProgress(progressBar: ProgressBar, chapter: Int) {
        val totalLessons = 5 // Assuming there are 5 lessons in each chapter
        var completedLessons = 0
        for (i in 1..totalLessons) {
            val lessonKey = "chapter${chapter}_lesson_$i"
            val lessonCompleted = sharedPreferences.getBoolean(lessonKey, false)
            if (lessonCompleted) {
                completedLessons++
            }
        }
        val progress = (completedLessons.toFloat() / totalLessons.toFloat() * 100).toInt()
        progressBar.progress = progress
    }

    private fun updateProgressWithAnimation(progressBar: ProgressBar, progressText: TextView, progressTo: Int) {
        val animator = ValueAnimator.ofInt(0, progressTo)
        animator.duration = 1000
        animator.addUpdateListener { animation ->
            val animatedValue = animation.animatedValue as Int
            progressBar.progress = animatedValue
            progressText.text = "$animatedValue%"
        }
        animator.start()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
