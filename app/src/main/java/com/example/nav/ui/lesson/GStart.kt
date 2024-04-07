package com.example.nav.ui.lesson

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.TextView
import androidx.navigation.fragment.findNavController
import com.example.nav.R

class GStart : Fragment() {

    companion object {
        fun newInstance() = GStart()
    }

    private lateinit var viewModel: GStartViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_g_start, container, false)

        val proceedToLesson1TextView: TextView = rootView.findViewById(R.id.proceedToLesson1TextView)

        proceedToLesson1TextView.setOnClickListener {
            // Navigate to Chapter 1 Lesson 1
            findNavController().navigate(R.id.action_GStartFragment_to_lesson11)
        }

        // Animate the views within the fragment layout sequentially
        animateViewsSequentially(rootView)

        return rootView
    }

    private fun animateViewsSequentially(rootView: View) {
        val animationDelay = 100L // Delay between animations in milliseconds
        var currentDelay = 0L

        // Animate each view
        val proceedToLesson1TextView = rootView.findViewById<TextView>(R.id.proceedToLesson1TextView)
        proceedToLesson1TextView?.startAnimationWithDelay(animationDelay, currentDelay)

        // Increment the delay for the next animation
        currentDelay += animationDelay

        // Add more views to animate sequentially as needed

    }

    private fun View.startAnimationWithDelay(animationDelay: Long, currentDelay: Long) {
        val context = context ?: return // Ensure context is not null
        val animation: Animation = AnimationUtils.loadAnimation(context, R.anim.slide_down)
        animation.startOffset = currentDelay
        startAnimation(animation)
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(GStartViewModel::class.java)
        // TODO: Use the ViewModel
    }
}
