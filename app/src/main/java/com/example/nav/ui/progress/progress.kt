package com.example.nav.ui.progress

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.view.setPadding
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.nav.R
import com.example.nav.services.ProgressUser
import com.example.nav.services.RetrofitClient
import com.google.android.material.textview.MaterialTextView
import kotlinx.coroutines.launch

class ProgressFragment : Fragment() {
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var username: String
    private lateinit var token: String
    private lateinit var progressContainer: ViewGroup
    private lateinit var scoreContainer: ConstraintLayout
    private lateinit var progressLayout: LinearLayout

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val rootView = inflater.inflate(R.layout.fragment_progress, container, false)
        progressLayout = rootView.findViewById<LinearLayout>(R.id.progressLayout)
        progressContainer = rootView.findViewById(R.id.progressContainer)
        scoreContainer = rootView.findViewById(R.id.scoreContainer)
        scoreContainer.removeAllViews()

        sharedPreferences = requireContext().getSharedPreferences("user_data", Context.MODE_PRIVATE)
        username = sharedPreferences.getString("username", "") ?: ""
        token = sharedPreferences.getString("token", "") ?: ""

        fetchProgress()

        return rootView
    }

    private fun fetchProgress() {
        lifecycleScope.launch {
            val response = RetrofitClient.instance.getProgress("Bearer $token", username)

            if (response.isSuccessful) {
                val progress = response.body()
                progress?.let {
                    addProgressCircles(progress)
                    addScoreDetails(progress)
                }
            }
        }
    }

    private fun addProgressCircles(progress: List<ProgressUser>) {
        progressContainer.removeAllViews()
        val progressTextView = MaterialTextView(requireContext())
        progressTextView.text = "Lessons Progress"
        progressTextView.textSize = 22f
        progressTextView.setTypeface(progressTextView.typeface, Typeface.BOLD)
        progressTextView.setTextColor(ContextCompat.getColor(requireContext(), R.color.bl))
        progressTextView.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        (progressTextView.layoutParams as LinearLayout.LayoutParams).setMargins(0, 0, 0, 8)
        progressContainer.addView(progressTextView)

        progress.chunked(2).forEach { group ->
            val progressRow = LinearLayout(requireContext())
            progressRow.layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            progressRow.isBaselineAligned = false
            progressRow.orientation = LinearLayout.HORIZONTAL

            progressContainer.addView(progressRow)

            group.forEach { progressUser ->
                val groupLayout = LinearLayout(requireContext())
                groupLayout.layoutParams = LinearLayout.LayoutParams(
                    resources.getDimensionPixelSize(R.dimen.progressLayoutWidth),
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    1f
                )
                (groupLayout.layoutParams as LinearLayout.LayoutParams).setMargins(
                    8,
                    8,
                    8,
                    8
                )
                groupLayout.orientation = LinearLayout.VERTICAL


                val progressBarLayout = ConstraintLayout(requireContext())
                progressBarLayout.layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT
                )

                val progressBar = ProgressBar(
                    requireContext(),
                    null,
                    android.R.attr.progressBarStyleHorizontal
                )
                progressBar.id =
                    View.generateViewId() // Generate unique ID for each ProgressBar
                progressBar.layoutParams = ConstraintLayout.LayoutParams(
                    resources.getDimensionPixelSize(R.dimen.progress_width),
                    resources.getDimensionPixelSize(R.dimen.progress_height)
                )
                progressBar.background =
                    ContextCompat.getDrawable(requireContext(), R.drawable.circular_shape)
                progressBar.isIndeterminate = false
                progressBar.progressDrawable = ContextCompat.getDrawable(
                    requireContext(),
                    R.drawable.circular_progress_bar
                )
                progressBar.textAlignment = View.TEXT_ALIGNMENT_CENTER
                val progressValue =
                    (progressUser.completed_lessons.toFloat() / progressUser.total_lessons.toFloat() * 100).toInt()
                progressBar.progress = progressValue
                progressBar.max = 100

                val progressText = MaterialTextView(requireContext())
                progressText.layoutParams = ConstraintLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
                progressText.id = View.generateViewId()
                progressText.textAlignment = View.TEXT_ALIGNMENT_CENTER
                progressText.setTextColor(Color.BLACK)
                progressText.textSize = 18f

                val progressLabelTextView = MaterialTextView(requireContext())
                progressLabelTextView.text = progressUser.chapter_name
                progressLabelTextView.textSize = 16f
                progressLabelTextView.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
                progressLabelTextView.layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                progressLabelTextView.gravity = Gravity.CENTER

                progressBarLayout.addView(progressBar)
                progressBarLayout.addView(progressText)

                updateProgressWithAnimation(progressBar, progressText, progressValue)

                // Add constraints
                val progressBarId = progressBar.id
                val progressTextId = progressText.id
                progressBarLayout.apply {
                    findViewById<View>(progressBarId).apply {
                        layoutParams = ConstraintLayout.LayoutParams(
                            resources.getDimensionPixelSize(R.dimen.progress_width),
                            resources.getDimensionPixelSize(R.dimen.progress_height)
                        ).apply {
                            startToStart = ConstraintLayout.LayoutParams.PARENT_ID
                            endToEnd = ConstraintLayout.LayoutParams.PARENT_ID
                            topToTop = ConstraintLayout.LayoutParams.PARENT_ID
                            bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID
                        }
                    }
                    findViewById<View>(progressTextId).apply {
                        layoutParams = ConstraintLayout.LayoutParams(
                            ViewGroup.LayoutParams.WRAP_CONTENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT
                        ).apply {
                            startToStart = ConstraintLayout.LayoutParams.PARENT_ID
                            endToEnd = ConstraintLayout.LayoutParams.PARENT_ID
                            topToTop = ConstraintLayout.LayoutParams.PARENT_ID
                            bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID
                            horizontalBias = 0.506f
                        }
                    }
                }
                groupLayout.addView(progressBarLayout)
                groupLayout.addView(progressLabelTextView)
                progressRow.addView(groupLayout)
            }
        }
    }
    private fun addScoreDetails(progress: List<ProgressUser>) {

        progress.forEach { progressUser ->
            val constraintLayout = ConstraintLayout(requireContext())
            constraintLayout.layoutParams = ConstraintLayout.LayoutParams(
                ConstraintLayout.LayoutParams.MATCH_PARENT,
                ConstraintLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(0, 16, 0, 0)
            }
            constraintLayout.setPadding(16, 16, 16, 16)

            val chapterLabel = MaterialTextView(requireContext())
            chapterLabel.id = View.generateViewId()
            chapterLabel.text = "${progressUser.chapter_name} Average Score"
            chapterLabel.textSize = 20f
            chapterLabel.setTypeface(chapterLabel.typeface, Typeface.BOLD)
            chapterLabel.setTextColor(ContextCompat.getColor(requireContext(), R.color.bl))
            chapterLabel.layoutParams = ConstraintLayout.LayoutParams(
                ConstraintLayout.LayoutParams.WRAP_CONTENT,
                ConstraintLayout.LayoutParams.WRAP_CONTENT
            )

            val progressBarScore = ProgressBar(
                requireContext(),
                null,
                android.R.attr.progressBarStyleHorizontal
            )
            progressBarScore.id = View.generateViewId()
            progressBarScore.layoutParams = ConstraintLayout.LayoutParams(
                resources.getDimensionPixelSize(R.dimen.progressBarScoreWidth),
                ConstraintLayout.LayoutParams.WRAP_CONTENT
            )
            progressBarScore.max = progressUser.total_items
            progressBarScore.progress = progressUser.average_score.toInt()
            progressBarScore.progressDrawable = ContextCompat.getDrawable(
                requireContext(),
                R.drawable.progress_bar
            )
            progressBarScore.layoutParams.apply {
                (this as ConstraintLayout.LayoutParams).setMargins(0, 16, 0, 0)
            }

            val scoreText = MaterialTextView(requireContext())
            scoreText.id = View.generateViewId()
            scoreText.text = "Average: ${progressUser.average_score}"
            scoreText.textSize = 16f
            scoreText.setTextColor(Color.WHITE)
            scoreText.layoutParams = ConstraintLayout.LayoutParams(
                ConstraintLayout.LayoutParams.WRAP_CONTENT,
                ConstraintLayout.LayoutParams.WRAP_CONTENT
            )
            scoreText.visibility = View.VISIBLE
            scoreText.layoutParams.apply {
                (this as ConstraintLayout.LayoutParams).setMargins(0, 4, 4, 4)
            }

            constraintLayout.addView(chapterLabel)
            constraintLayout.addView(progressBarScore)
            constraintLayout.addView(scoreText)

            val chapterLabelID = chapterLabel.id
            val progressBarScoreID = progressBarScore.id
            val scoreTextID = scoreText.id

            constraintLayout.apply {
                findViewById<View>(chapterLabelID).apply {
                    layoutParams = ConstraintLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                    ).apply {
                        startToStart = ConstraintLayout.LayoutParams.PARENT_ID
                        topToTop = ConstraintLayout.LayoutParams.PARENT_ID
                    }
                }
                findViewById<View>(progressBarScoreID).apply {
                    layoutParams = ConstraintLayout.LayoutParams(
                        resources.getDimensionPixelSize(R.dimen.progressBarScoreWidth),
                        ViewGroup.LayoutParams.WRAP_CONTENT
                    ).apply {
                        startToStart = ConstraintLayout.LayoutParams.PARENT_ID
                        endToEnd = ConstraintLayout.LayoutParams.PARENT_ID
                        topToBottom = chapterLabelID
                        topMargin = resources.getDimensionPixelSize(R.dimen.margin_top_progress_bar)
                    }
                }
                findViewById<View>(scoreTextID).apply {
                    layoutParams = ConstraintLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                    ).apply {
                        endToEnd = progressBarScoreID
                        topToTop = progressBarScoreID
                        bottomToBottom = progressBarScoreID
                    }
                }
            }

            // Add TextView for last attempted
            val lastAttemptedTextView = MaterialTextView(requireContext())
            lastAttemptedTextView.id = View.generateViewId()
            lastAttemptedTextView.text = progressUser.last_attempt
            lastAttemptedTextView.textSize = 16f
            lastAttemptedTextView.layoutParams = ConstraintLayout.LayoutParams(
                ConstraintLayout.LayoutParams.WRAP_CONTENT,
                ConstraintLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                topMargin = resources.getDimensionPixelSize(R.dimen.margin_top_last_attempted)
            }

            constraintLayout.addView(lastAttemptedTextView)

            // Set constraints for last attempted TextView
            constraintLayout.findViewById<View>(lastAttemptedTextView.id)?.apply {
                layoutParams = ConstraintLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                ).apply {
                    startToStart = ConstraintLayout.LayoutParams.PARENT_ID
                    topToBottom = progressBarScore.id // Assuming progressBarScore is the last added view
                    //marginStart = resources.getDimensionPixelSize(R.dimen.margin_start_last_attempted)
                    topMargin = resources.getDimensionPixelSize(R.dimen.margin_top_last_attempted)
                }
            }

            progressLayout.addView(constraintLayout)
        }
    }

    @SuppressLint("SetTextI18n")
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
}
