package edu.northeastern.new_final.ui.challengeGroup;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.Locale;

import edu.northeastern.new_final.R;

public class ChallengeGroupMessaging extends AppCompatActivity {
    private RecyclerView recyclerView;
    private ArrayList<GroupMessage> messageList;
    private GroupMessageAdapter adapter;
    private ImageView mainDisplay;
    private ImageView leaderboard;
    private Button postBtn;
    private EditText messageInput;
    private String groupName;
    private String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.challenge_group_messages);
        groupName = getIntent().getStringExtra("groupName");

        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        username = sharedPreferences.getString("email", null);

        mainDisplay = findViewById(R.id.displayIcon);
        leaderboard = findViewById(R.id.starIcon);
        postBtn = findViewById(R.id.postButton);
        messageInput = findViewById(R.id.messageEditText);

        // Get the messages from the database to display
        messageList = new ArrayList<>();
        new Thread(this::fetchMessages).start();

        // Initialize RecyclerView and Adapter
        recyclerView = findViewById(R.id.recyclerViewMessages);
        adapter = new GroupMessageAdapter(messageList);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        // By default, scroll to the bottom of the recycyler view for newest messages
        if (adapter.getItemCount() >= 1) {
            recyclerView.post(() -> {
                recyclerView.smoothScrollToPosition(adapter.getItemCount() - 1);
            });
        }

        mainDisplay.setOnClickListener(v -> {
            // Start ChallengeGroupLeaderboard activity
            Intent intent = new Intent(ChallengeGroupMessaging.this, ChallengeGroupMain.class);
            intent.putExtra("groupName", groupName); // Send groupName with putExtra()
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
        });

        leaderboard.setOnClickListener(v -> {
            // Start ChallengeGroupLeaderboard activity
            Intent intent = new Intent(ChallengeGroupMessaging.this, ChallengeGroupLeaderboard.class);
            intent.putExtra("groupName", groupName); // Send groupName with putExtra()
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
        });

        postBtn.setOnClickListener(v -> {
            Thread separateThread = new Thread(this::postMessage);
            separateThread.start();
        });
    }

    private void postMessage() {
        String typedMsg = String.valueOf(messageInput.getText());
        Calendar calendar = Calendar.getInstance();
        int initialYear = calendar.get(Calendar.YEAR);
        int initialMonth = calendar.get(Calendar.MONTH);
        int initialDayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

        String today = initialYear + "-" + (initialMonth + 1) + "-" + initialDayOfMonth;
        GroupMessage newMessage = new GroupMessage(username, today, typedMsg);

        if (typedMsg.equals("")) {
            runOnUiThread(() -> Toast
                    .makeText(getApplicationContext(), "Must type in message to post", Toast.LENGTH_SHORT)
                    .show());
        } else {

            DatabaseReference groupRef = FirebaseDatabase.getInstance()
                    .getReference()
                    .child("groups")
                    .child(groupName);

            groupRef.child("messages").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    ArrayList<GroupMessage> messageList = new ArrayList<>();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        GroupMessage message = snapshot.getValue(GroupMessage.class);
                        messageList.add(message);
                    }
                    int newIndex = messageList.size();
                    messageList.add(newMessage);

                    // Update the messages
                    groupRef.child("messages").setValue(messageList)
                            .addOnSuccessListener(aVoid -> {
                                adapter.setMessageList(messageList);
                                runOnUiThread(() -> Toast
                                        .makeText(getApplicationContext(), "Message posted!", Toast.LENGTH_SHORT)
                                        .show());
                                runOnUiThread(() -> messageInput.setText(""));
                                runOnUiThread(() -> recyclerView.post(() -> {
                                    recyclerView.scrollToPosition(adapter.getItemCount() - 1);
                                }));

                            })
                            .addOnFailureListener(e -> {
                                runOnUiThread(() -> Toast.makeText(getApplicationContext(), "Failed to post message", Toast.LENGTH_SHORT).show());
                            });
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    runOnUiThread(() -> Toast.makeText(getApplicationContext(), "Failed to post message", Toast.LENGTH_SHORT).show());
                }
            });
        }
    }

    private void fetchMessages() {
        messageList.clear();
        DatabaseReference groupRef = FirebaseDatabase.getInstance()
                .getReference()
                .child("groups")
                .child(groupName)
                .child("messages");

        groupRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //messageList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    GroupMessage message = snapshot.getValue(GroupMessage.class);
                    messageList.add(message);
                }

                messageList.sort((message1, message2) -> {
                    // Sort each group message by time and date to put the oldest messages at the top
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
                    try {
                        Date dateTime1 = dateFormat.parse(message1.getDate() + " " + message1.getTime());
                        Date dateTime2 = dateFormat.parse(message2.getDate() + " " + message2.getTime());
                        // Compare dates and times
                        return dateTime1.compareTo(dateTime2);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    return 0;
                });

                adapter.setMessageList(messageList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                runOnUiThread(() -> Toast.makeText(getApplicationContext(), "Failed to fetch messages", Toast.LENGTH_SHORT).show());
            }
        });
    }
}
