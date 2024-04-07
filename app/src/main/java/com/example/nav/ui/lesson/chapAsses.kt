package com.example.nav.ui.lesson

import android.graphics.Color
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import com.example.nav.R

class chapAsses : Fragment() {
    private lateinit var submitButton: Button
    private lateinit var radioGroup1: RadioGroup
    private lateinit var radioGroup2: RadioGroup
    private lateinit var radioGroup3: RadioGroup

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_chap_asses, container, false)

        submitButton = view.findViewById(R.id.submit_button)
        radioGroup1 = view.findViewById(R.id.radioGroup1)
        radioGroup2 = view.findViewById(R.id.radioGroup2)
        radioGroup3 = view.findViewById(R.id.radioGroup3)

        submitButton.setOnClickListener {
            checkAnswers()
        }

        return view
    }

    private fun checkAnswers() {
        val radioButton1Correct = radioGroup1.findViewById<RadioButton>(R.id.radioButton1a)
        val radioButton2Correct = radioGroup2.findViewById<RadioButton>(R.id.radioButton2c)
        val radioButton3Correct = radioGroup3.findViewById<RadioButton>(R.id.radioButton3a)

        if (radioGroup1.checkedRadioButtonId == R.id.radioButton1a) {
            radioButton1Correct.setTextColor(Color.GREEN)
            radioButton1Correct.visibility = View.VISIBLE
        } else {
            radioButton1Correct.setTextColor(Color.RED)
            radioButton1Correct.visibility = View.VISIBLE
        }

        if (radioGroup2.checkedRadioButtonId == R.id.radioButton2c) {
            radioButton2Correct.setTextColor(Color.GREEN)
            radioButton2Correct.visibility = View.VISIBLE
        } else {
            radioButton2Correct.setTextColor(Color.RED)
            radioButton2Correct.visibility = View.VISIBLE
        }

        if (radioGroup3.checkedRadioButtonId == R.id.radioButton3a) {
            radioButton3Correct.setTextColor(Color.GREEN)
            radioButton3Correct.visibility = View.VISIBLE
        } else {
            radioButton3Correct.setTextColor(Color.RED)
            radioButton3Correct.visibility = View.VISIBLE
        }
    }
}