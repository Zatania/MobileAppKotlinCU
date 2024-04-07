package com.example.nav.ui.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.nav.R

class settings : Fragment() {

    private lateinit var sections: List<Pair<TextView, TextView>>
    private var settingsIcon: ImageView? = null // Declare settingsIcon as nullable

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_settings, container, false)

        // Find the settingsIcon view if available
       // settingsIcon = view.findViewById(R.id.)

        // Set OnClickListener for the settingsIcon if it's not null
        settingsIcon?.setOnClickListener {
            // Navigate back to the Profile Fragment
            findNavController().navigateUp()
        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initializeViews(view)
        setupClickListeners()
    }

    private fun initializeViews(view: View) {
        val acceptingTermsTitle = view.findViewById<TextView>(R.id.AcceptingTermsTitle)
        val acceptingTermsContent = view.findViewById<TextView>(R.id.AcceptingTermsContent)

        val yourAccountTitle = view.findViewById<TextView>(R.id.YourAccTitle)
        val yourAccountContent = view.findViewById<TextView>(R.id.YourAccContent)

        val privacyTitle = view.findViewById<TextView>(R.id.PrivacyTitle)
        val privacyContent = view.findViewById<TextView>(R.id.PrivacyContent)

        val commitmentTitle = view.findViewById<TextView>(R.id.CommitTitle)
        val commitmentContent = view.findViewById<TextView>(R.id.CommitContent)

        sections = listOf(
            acceptingTermsTitle to acceptingTermsContent,
            yourAccountTitle to yourAccountContent,
            privacyTitle to privacyContent,
            commitmentTitle to commitmentContent
        )
    }

    private fun setupClickListeners() {
        sections.forEach { (title, content) ->
            title.setOnClickListener {
                toggleVisibility(content)
                collapseOtherSections(content)
            }
        }
    }

    private fun toggleVisibility(content: TextView) {
        content.visibility = if (content.visibility == View.VISIBLE) {
            View.GONE
        } else {
            View.VISIBLE
        }
    }

    private fun collapseOtherSections(currentContent: TextView) {
        sections.filter { it.second != currentContent }
            .forEach { (_, content) ->
                if (content.visibility == View.VISIBLE) {
                    content.visibility = View.GONE
                }
            }
    }
}
