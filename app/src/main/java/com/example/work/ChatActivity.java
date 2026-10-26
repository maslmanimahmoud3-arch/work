package com.example.work;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

public class ChatActivity extends AppCompatActivity {

    private static final String TAG = "ChatActivity";

    private TextView tvChatReceiverName;
    private TextView tvChatServiceTitle;
    private RecyclerView rvChatMessages;
    private TextInputEditText etChatMessageInput;
    private MaterialButton btnSendChatMessage;

    private MessageAdapter messageAdapter;
    private DatabaseReference chatRef;

    private String chatId;
    private String receiverName;
    private String serviceTitle;
    private String currentRole = "client";
    private String currentUserName = "عميل";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        chatId = getIntent().getStringExtra("CHAT_ID");
        receiverName = getIntent().getStringExtra("RECEIVER_NAME");
        serviceTitle = getIntent().getStringExtra("SERVICE_TITLE");
        currentRole = getIntent().getStringExtra("CURRENT_ROLE");
        currentUserName = getIntent().getStringExtra("CURRENT_USER_NAME");

        if (chatId == null || chatId.trim().isEmpty()) {
            Toast.makeText(this, "تعذر فتح المحادثة، معرف المحادثة غير صحيح", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        if (currentRole == null) currentRole = "client";
        if (currentUserName == null) currentUserName = currentRole.equals("client") ? "العميل" : "مقدم الخدمة";

        initViews();
        setupRecyclerView();
        connectFirebaseChat();
    }

    private void initViews() {
        tvChatReceiverName = findViewById(R.id.tvChatReceiverName);
        tvChatServiceTitle = findViewById(R.id.tvChatServiceTitle);
        rvChatMessages = findViewById(R.id.rvChatMessages);
        etChatMessageInput = findViewById(R.id.etChatMessageInput);
        btnSendChatMessage = findViewById(R.id.btnSendChatMessage);

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        if (tvChatReceiverName != null && receiverName != null) {
            tvChatReceiverName.setText(receiverName);
        }
        if (tvChatServiceTitle != null && serviceTitle != null) {
            tvChatServiceTitle.setText("بشأن: " + serviceTitle);
        }

        btnSendChatMessage.setOnClickListener(v -> sendMessage());
    }

    private void setupRecyclerView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true);
        rvChatMessages.setLayoutManager(layoutManager);

        messageAdapter = new MessageAdapter(currentRole);
        rvChatMessages.setAdapter(messageAdapter);
    }

    private void connectFirebaseChat() {
        FirebaseHelper.ensureAuthenticated(() -> {
            try {
                FirebaseDatabase db = FirebaseHelper.getDatabase();
                if (db != null) {
                    chatRef = db.getReference("chats").child(chatId).child("messages");
                    listenToMessages();
                }
            } catch (Exception e) {
                Log.e(TAG, "Chat Firebase error: " + e.getMessage());
            }
        });
    }

    private void listenToMessages() {
        if (chatRef == null) return;

        chatRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<ChatMessage> list = new ArrayList<>();
                for (DataSnapshot child : snapshot.getChildren()) {
                    ChatMessage msg = child.getValue(ChatMessage.class);
                    if (msg != null) {
                        list.add(msg);
                    }
                }

                list.sort((o1, o2) -> Long.compare(o1.getTimestamp(), o2.getTimestamp()));

                runOnUiThread(() -> {
                    messageAdapter.setMessages(list);
                    if (!list.isEmpty()) {
                        rvChatMessages.smoothScrollToPosition(list.size() - 1);
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Chat cancelled: " + error.getMessage());
            }
        });
    }

    private void sendMessage() {
        if (etChatMessageInput == null) return;

        String text = etChatMessageInput.getText() != null ? etChatMessageInput.getText().toString().trim() : "";
        if (text.isEmpty()) return;

        String msgId = UUID.randomUUID().toString();
        long now = System.currentTimeMillis();
        String timeFormatted = new SimpleDateFormat("hh:mm a", Locale.getDefault()).format(new Date(now));

        ChatMessage message = new ChatMessage(
                msgId,
                chatId,
                currentUserName,
                currentRole,
                text,
                now,
                timeFormatted
        );

        etChatMessageInput.setText("");

        FirebaseHelper.ensureAuthenticated(() -> {
            try {
                if (chatRef == null) {
                    FirebaseDatabase db = FirebaseHelper.getDatabase();
                    if (db != null) {
                        chatRef = db.getReference("chats").child(chatId).child("messages");
                    }
                }
                if (chatRef != null) {
                    chatRef.child(msgId).setValue(message, (error, ref) -> {
                        if (error != null) {
                            Log.e(TAG, "Failed sending message: " + error.getMessage());
                            Toast.makeText(ChatActivity.this, "فشل إرسال الرسالة: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                        } else {
                            Log.d(TAG, "Message sent successfully!");
                        }
                    });
                }
            } catch (Exception e) {
                Log.e(TAG, "Send message exception: " + e.getMessage());
            }
        });
    }
}
