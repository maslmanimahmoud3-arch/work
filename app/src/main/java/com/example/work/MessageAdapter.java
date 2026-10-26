package com.example.work;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;
import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {

    private List<ChatMessage> messages = new ArrayList<>();
    private String currentUserRole = "client";

    public MessageAdapter(String currentUserRole) {
        this.currentUserRole = currentUserRole != null ? currentUserRole : "client";
    }

    public void setMessages(List<ChatMessage> newMessages) {
        this.messages = newMessages != null ? newMessages : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_message, parent, false);
        return new MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        ChatMessage message = messages.get(position);
        holder.bind(message, currentUserRole);
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    static class MessageViewHolder extends RecyclerView.ViewHolder {
        private final LinearLayout llMessageContainer;
        private final MaterialCardView cardMessageBubble;
        private final TextView tvMessageSender;
        private final TextView tvMessageText;
        private final TextView tvMessageTime;

        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);
            llMessageContainer = itemView.findViewById(R.id.llMessageContainer);
            cardMessageBubble = itemView.findViewById(R.id.cardMessageBubble);
            tvMessageSender = itemView.findViewById(R.id.tvMessageSender);
            tvMessageText = itemView.findViewById(R.id.tvMessageText);
            tvMessageTime = itemView.findViewById(R.id.tvMessageTime);
        }

        public void bind(ChatMessage message, String currentUserRole) {
            boolean isMyMessage = currentUserRole.equalsIgnoreCase(message.getSenderRole());

            tvMessageSender.setText(message.getSenderName() != null ? message.getSenderName() : "");
            tvMessageText.setText(message.getText() != null ? message.getText() : "");
            tvMessageTime.setText(message.getTimeFormatted() != null ? message.getTimeFormatted() : "");

            if (isMyMessage) {
                llMessageContainer.setGravity(Gravity.END);
                cardMessageBubble.setCardBackgroundColor(ColorStateList.valueOf(ContextCompat.getColor(itemView.getContext(), R.color.orange_primary)));
                tvMessageSender.setTextColor(Color.WHITE);
                tvMessageText.setTextColor(Color.WHITE);
                tvMessageTime.setTextColor(Color.parseColor("#FEF08A"));
            } else {
                llMessageContainer.setGravity(Gravity.START);
                cardMessageBubble.setCardBackgroundColor(ColorStateList.valueOf(ContextCompat.getColor(itemView.getContext(), R.color.surface_white)));
                tvMessageSender.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.orange_primary));
                tvMessageText.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.text_primary));
                tvMessageTime.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.text_secondary));
            }
        }
    }
}
