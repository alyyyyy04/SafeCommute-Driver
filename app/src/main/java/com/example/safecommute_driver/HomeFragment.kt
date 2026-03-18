package com.example.safecommute_driver

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.safecommute_driver.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnEmergencySos.setOnClickListener { showEmergencySosDialog() }
        binding.btnSubmitReport.setOnClickListener {
            startActivity(Intent(requireContext(), SubmitReportActivity::class.java))
        }
    }

    private fun showEmergencySosDialog() {
        EmergencySosDialogFragment().show(parentFragmentManager, "EmergencySosDialog")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
