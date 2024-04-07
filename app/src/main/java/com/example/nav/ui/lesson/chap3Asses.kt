package com.example.nav.ui.lesson

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.nav.R

class chap3Asses : Fragment() {

    companion object {
        fun newInstance() = chap3Asses()
    }

    private lateinit var viewModel: Chap3AssesViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_chap3_asses, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(Chap3AssesViewModel::class.java)
        // TODO: Use the ViewModel
    }

}