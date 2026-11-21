package com.ute.studentmvvm.ui

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.ute.studentmvvm.R
import com.ute.studentmvvm.databinding.FragmentStudentListBinding

class StudentListFragment : AppCompatActivity() {
    private lateinit var binding: FragmentStudentListBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = FragmentStudentListBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
    }
}