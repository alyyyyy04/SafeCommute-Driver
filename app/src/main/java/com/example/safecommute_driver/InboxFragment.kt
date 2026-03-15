package com.example.safecommute_driver

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.safecommute_driver.databinding.FragmentInboxBinding

class InboxFragment : Fragment() {

    private var _binding: FragmentInboxBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentInboxBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.recyclerInbox.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerInbox.adapter = InboxAdapter(createSampleMessages())
    }

    private fun createSampleMessages(): List<InboxMessage> = listOf(
        InboxMessage(
            sender = getString(R.string.operations_center),
            subject = getString(R.string.route_change_notification),
            preview = getString(R.string.route_change_preview),
            time = getString(R.string.hours_ago_2),
            isNew = true
        ),
        InboxMessage(
            sender = getString(R.string.fleet_manager),
            subject = getString(R.string.vehicle_maintenance_reminder),
            preview = getString(R.string.maintenance_preview),
            time = getString(R.string.hours_ago_5),
            isNew = true
        ),
        InboxMessage(
            sender = getString(R.string.operations_center),
            subject = getString(R.string.schedule_adjustment),
            preview = getString(R.string.schedule_preview),
            time = getString(R.string.days_ago_2),
            isNew = false
        ),
        InboxMessage(
            sender = getString(R.string.safety_department),
            subject = getString(R.string.safety_protocol_update),
            preview = getString(R.string.safety_protocol_preview),
            time = getString(R.string.day_ago_1),
            isNew = false
        )
    )

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

data class InboxMessage(
    val sender: String,
    val subject: String,
    val preview: String,
    val time: String,
    val isNew: Boolean
)
