package com.example.safecommute_driver

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.SystemClock
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import com.example.safecommute_driver.databinding.DialogEmergencySosBinding
import com.google.android.gms.location.LocationServices
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ServerValue

class EmergencySosDialogFragment : DialogFragment() {

    private val requestLocationPermission =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { result ->
            val granted = result.values.any { it }
            if (!granted) {
                Toast.makeText(
                    requireContext(),
                    "Location permission is required to send an SOS alert.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialogBinding = DialogEmergencySosBinding.inflate(layoutInflater)

        val emergencyCards = linkedMapOf(
            dialogBinding.emergencyMedical to getString(R.string.medical),
            dialogBinding.emergencyHijack to getString(R.string.hijack),
            dialogBinding.emergencyBreakdown to getString(R.string.breakdown),
            dialogBinding.emergencyAccident to getString(R.string.accident),
            dialogBinding.emergencyFire to getString(R.string.fire),
            dialogBinding.emergencyRoadHazard to getString(R.string.road_hazard)
        )

        val normalBg = ContextCompat.getColor(requireContext(), R.color.emergency_option_card_bg)
        val selectedBg = ContextCompat.getColor(requireContext(), R.color.emergency_selected_bg)
        val normalStroke = ContextCompat.getColor(requireContext(), R.color.emergency_option_stroke)
        val selectedStroke = ContextCompat.getColor(requireContext(), R.color.emergency_red)

        val selectedTypes = linkedSetOf<String>()

        fun renderSelection() {
            emergencyCards.forEach { (card, type) ->
                val isSelected = selectedTypes.contains(type)
                card.setCardBackgroundColor(if (isSelected) selectedBg else normalBg)
                card.strokeColor = if (isSelected) selectedStroke else normalStroke
                card.strokeWidth = if (isSelected) 3 else 1
            }
        }

        emergencyCards.forEach { (card, type) ->
            card.setOnClickListener {
                if (selectedTypes.contains(type)) {
                    selectedTypes.remove(type)
                } else {
                    selectedTypes.add(type)
                }
                renderSelection()
            }
        }
        renderSelection()

        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogBinding.root)
            .create()

        dialogBinding.btnCancel.setOnClickListener { dialog.dismiss() }
        dialogBinding.btnSendAlert.setOnClickListener {
            if (selectedTypes.isEmpty()) {
                Toast.makeText(requireContext(), "Please select at least one emergency type.", Toast.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            }
            val type = selectedTypes.joinToString(", ")

            val driverId = FirebaseAuth.getInstance().currentUser?.uid
            if (driverId.isNullOrBlank()) {
                Toast.makeText(requireContext(), "You must be logged in to send an alert.", Toast.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            }

            if (!hasLocationPermission()) {
                requestLocationPermission.launch(
                    arrayOf(
                        android.Manifest.permission.ACCESS_FINE_LOCATION,
                        android.Manifest.permission.ACCESS_COARSE_LOCATION
                    )
                )
                return@setOnClickListener
            }

            dialogBinding.btnSendAlert.isEnabled = false
            dialogBinding.btnSendAlert.text = "SENDING..."

            fetchLastKnownLocation()
                .addOnSuccessListener { location ->
                    if (location == null) {
                        Toast.makeText(
                            requireContext(),
                            "Unable to get location. Please try again.",
                            Toast.LENGTH_SHORT
                        ).show()
                        dialogBinding.btnSendAlert.isEnabled = true
                        dialogBinding.btnSendAlert.text = getString(R.string.send_alert)
                        return@addOnSuccessListener
                    }

                    val emergencyRef = FirebaseDatabase.getInstance()
                        .reference
                        .child("emergencies")
                        .push()

                    val payload = mapOf(
                        "driverId" to driverId,
                        "type" to type,
                        "latitude" to location.latitude,
                        "longitude" to location.longitude,
                        "status" to "pending",
                        "timestamp" to ServerValue.TIMESTAMP,
                        "clientUptimeMs" to SystemClock.elapsedRealtime()
                    )

                    emergencyRef
                        .setValue(payload)
                        .addOnSuccessListener {
                            Toast.makeText(requireContext(), "Emergency alert sent.", Toast.LENGTH_SHORT).show()
                            dialog.dismiss()
                        }
                        .addOnFailureListener { e ->
                            Toast.makeText(
                                requireContext(),
                                "Failed to send alert: ${e.message ?: "unknown error"}",
                                Toast.LENGTH_LONG
                            ).show()
                            dialogBinding.btnSendAlert.isEnabled = true
                            dialogBinding.btnSendAlert.text = getString(R.string.send_alert)
                        }
                }
                .addOnFailureListener {
                    Toast.makeText(requireContext(), "Unable to get location. Please try again.", Toast.LENGTH_SHORT)
                        .show()
                    dialogBinding.btnSendAlert.isEnabled = true
                    dialogBinding.btnSendAlert.text = getString(R.string.send_alert)
                }
        }

        return dialog
    }

    private fun hasLocationPermission(): Boolean {
        val ctx = requireContext()
        val fine = ContextCompat.checkSelfPermission(ctx, android.Manifest.permission.ACCESS_FINE_LOCATION)
        val coarse = ContextCompat.checkSelfPermission(ctx, android.Manifest.permission.ACCESS_COARSE_LOCATION)
        return fine == PackageManager.PERMISSION_GRANTED || coarse == PackageManager.PERMISSION_GRANTED
    }

    @SuppressLint("MissingPermission")
    private fun fetchLastKnownLocation() =
        LocationServices.getFusedLocationProviderClient(requireContext()).lastLocation
}

