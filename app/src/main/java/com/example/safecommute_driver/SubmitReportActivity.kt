package com.example.safecommute_driver

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.safecommute_driver.databinding.ActivitySubmitReportBinding

class SubmitReportActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySubmitReportBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySubmitReportBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnBack.setOnClickListener { finish() }
        binding.btnCancel.setOnClickListener { finish() }
        binding.btnSubmit.setOnClickListener {
            Toast.makeText(this, getString(R.string.submit_report), Toast.LENGTH_SHORT).show()
            finish()
        }
    }
}
