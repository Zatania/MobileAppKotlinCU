package com.example.nav.ui.quiz

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.navigation.fragment.findNavController
import com.example.nav.R

class Chapter : Fragment() {

    companion object {
        fun newInstance() = Chapter()
    }

    private lateinit var viewModel: ChapterViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_chapter, container, false)

        // Initialize buttons
        val button1 = view.findViewById<Button>(R.id.button1)
        val button2 = view.findViewById<Button>(R.id.button2)
        val button3 = view.findViewById<Button>(R.id.button3)
        val button4 = view.findViewById<Button>(R.id.button4)
        val button5 = view.findViewById<Button>(R.id.button5)

        // Set click listeners for buttons
        button1.setOnClickListener {
            findNavController().navigate(R.id.action_chapterFragment_to_chapterAsses)
        }

        button2.setOnClickListener {
            findNavController().navigate(R.id.action_chapterFragment_to_chapter2Asses)
        }

        button3.setOnClickListener {
            findNavController().navigate(R.id.action_chapterFragment_to_chapter3Asses)
        }

        button4.setOnClickListener {
            findNavController().navigate(R.id.action_chapterFragment_to_chapter4Asses)
        }

        button5.setOnClickListener {
            findNavController().navigate(R.id.action_chapterFragment_to_chapter5Asses)
        }

        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this)[ChapterViewModel::class.java]
        // TODO: Use the ViewModel
    }
}
