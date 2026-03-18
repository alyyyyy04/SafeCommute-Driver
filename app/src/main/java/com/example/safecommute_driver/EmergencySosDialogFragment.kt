package com.example.safecommute_driver

import android.app.Dialog
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.example.safecommute_driver.databinding.DialogEmergencySosBinding

class EmergencySosDialogFragment : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialogBinding = DialogEmergencySosBinding.inflate(layoutInflater)

        val selectedEmergencyIds = mutableSetOf<Int>()
        val emergencyCards = listOf(
            dialogBinding.emergencyMedical,
            dialogBinding.emergencyHijack,
            dialogBinding.emergencyBreakdown,
            dialogBinding.emergencyAccident,
            dialogBinding.emergencyFire,
            dialogBinding.emergencyRoadHazard
        )

        emergencyCards.forEach { card ->
            card.isSelected = false
            card.setOnClickListener {
                val nowSelected = !card.isSelected
                card.isSelected = nowSelected
                if (nowSelected) selectedEmergencyIds.add(card.id) else selectedEmergencyIds.remove(card.id)
            }
        }

        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogBinding.root)
            .create()

        dialogBinding.btnCancel.setOnClickListener { dialog.dismiss() }
        dialogBinding.btnSendAlert.setOnClickListener {
            if (selectedEmergencyIds.isEmpty()) {
                Toast.makeText(requireContext(), "Please select at least one emergency.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            Toast.makeText(requireContext(), "Your report has been submitted.", Toast.LENGTH_SHORT).show()
            dialog.dismiss()
        }

        return dialog
    }
}

