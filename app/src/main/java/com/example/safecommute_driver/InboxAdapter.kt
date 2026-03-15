package com.example.safecommute_driver

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.safecommute_driver.databinding.ItemInboxMessageBinding

class InboxAdapter(
    private val messages: List<InboxMessage>
) : RecyclerView.Adapter<InboxAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemInboxMessageBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(messages[position])
    }

    override fun getItemCount(): Int = messages.size

    class ViewHolder(
        private val binding: ItemInboxMessageBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(message: InboxMessage) {
            binding.textSender.text = message.sender
            binding.textSubject.text = message.subject
            binding.textPreview.text = message.preview
            binding.textTime.text = message.time
            binding.badgeNew.visibility = if (message.isNew) android.view.View.VISIBLE else android.view.View.GONE
        }
    }
}
