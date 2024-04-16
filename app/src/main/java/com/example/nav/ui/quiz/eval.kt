package com.example.nav.ui.quiz

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.nav.R

class eval : Fragment() {

    companion object {
        fun newInstance() = eval()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_eval, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val buttonStartExercise = view.findViewById<View>(R.id.buttonStartExercise)
        val buttonStartExam = view.findViewById<View>(R.id.buttonStartExam)

        // Set click listeners for buttons
        buttonStartExercise.setOnClickListener {
            findNavController().navigate(R.id.fragment_chapterAssessments)
        }


        buttonStartExam.setOnClickListener {
                findNavController().navigate(R.id.fragment_exam)
            }

        }

    }
