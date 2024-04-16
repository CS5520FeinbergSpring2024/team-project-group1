package edu.northeastern.new_final.ui.challengeGroup;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import edu.northeastern.new_final.R;
import edu.northeastern.new_final.ui.profile.HistoricalWorkoutAdapter;


public class ChallengeGroupLeaderboard extends AppCompatActivity {

    private String groupName;

    private RecyclerView recyclerView;
    private ContenderAdapter adapter;
    private List<Contender> contenderList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.challenge_group_leaderboard);

        // Retrieve the group name from intent extras from prev. screen
        groupName = getIntent().getStringExtra("groupName");

        // Initialize RecyclerView and Adapter
        recyclerView = findViewById(R.id.leaderboardRV);
        contenderList = new ArrayList<>();
        adapter = new ContenderAdapter(contenderList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        // Fetch data from Firebase
        fetchContenderData();


        ImageView challengeMainIcon = findViewById(R.id.icon1);

        // Set click listener for icon to jump back to challengeMain
        challengeMainIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start ChallengeGroupMain activity
                Intent intent = new Intent(ChallengeGroupLeaderboard.this, ChallengeGroupMain.class);
                intent.putExtra("groupName", groupName); // Send groupName with putExtra()
                startActivity(intent);
            }
        });

    }

    private void fetchContenderData() {
        DatabaseReference groupRef = FirebaseDatabase.getInstance().getReference().child("groups").child(groupName);
        groupRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                // Clear the contenderList before populating it with new data
                contenderList.clear();

                // Find the members in a given group
                for (DataSnapshot memberSnapshot : snapshot.child("members").getChildren()) {
                    String memberID = memberSnapshot.getKey();


                    // Pull user data for the member from "users" node
                    DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("users").child(memberID);
                    userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                       @Override
                       public void onDataChange(@NonNull DataSnapshot userSnapshot) {
                           // Get user data from the snapshot
                           String username = userSnapshot.child("email").getValue(String.class);

                           int amountInt = userSnapshot.child("total_EP").getValue(Integer.class);
                           String amount = String.valueOf(amountInt);

                           String profileImageUrl = userSnapshot.child("profileImageUrl").getValue(String.class);

                           // Create a Contender object and add it to the contenderList
                           Contender contender = new Contender(profileImageUrl,username, amount);
                           contenderList.add(contender);

                           // Update RecyclerView with sorted userList
                           adapter.notifyDataSetChanged();
                    }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Toast.makeText(ChallengeGroupLeaderboard.this, "Error retrieving user data.", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ChallengeGroupLeaderboard.this, "Error retrieving user data.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
