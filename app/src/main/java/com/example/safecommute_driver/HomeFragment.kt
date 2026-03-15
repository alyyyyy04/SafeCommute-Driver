package com.example.safecommute_driver

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.safecommute_driver.databinding.DialogEmergencySosBinding
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
        val dialogBinding = DialogEmergencySosBinding.inflate(layoutInflater)
        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogBinding.root)
            .create()

        listOf(
            dialogBinding.emergencyMedical,
            dialogBinding.emergencyHijack,
            dialogBinding.emergencyBreakdown,
            dialogBinding.emergencyAccident,
            dialogBinding.emergencyFire,
            dialogBinding.emergencyRoadHazard
        ).forEach { card ->
            card.setOnClickListener { /* optional: highlight selected type */ }
        }

        dialogBinding.btnCancel.setOnClickListener { dialog.dismiss() }
        dialogBinding.btnSendAlert.setOnClickListener { dialog.dismiss() }

        dialog.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
