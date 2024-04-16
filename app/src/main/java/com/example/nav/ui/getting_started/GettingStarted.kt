package com.example.nav.ui.getting_started

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import com.example.nav.R
import com.example.nav.services.StepsDetails
import com.google.android.material.textview.MaterialTextView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.squareup.picasso.Picasso

class GettingStartedFragment : Fragment() {
    private lateinit var gettingStartedContainer: ViewGroup

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_getting_started, container, false)
        gettingStartedContainer = root.findViewById(R.id.gettingStartedContainer)
        gettingStartedContainer.removeAllViews()

        setFragmentResultListener("gettingStartedResultKey") { _, bundle ->
            val stepsJson = bundle.getString("stepsData")
            val stepsData = Gson().fromJson<List<StepsDetails>>(stepsJson, object : TypeToken<List<StepsDetails>>() {}.type)

            // Now you have the stepsData, you can use it as needed
            displaySteps(stepsData)
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
    }
}