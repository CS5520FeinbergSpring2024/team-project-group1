package edu.northeastern.new_final.ui.challengeGroup;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicLong;

import edu.northeastern.new_final.R;
import edu.northeastern.new_final.ui.profile.HistoricalWorkoutAdapter;


public class ChallengeGroupLeaderboard extends AppCompatActivity {

    private String groupName;

    private RecyclerView recyclerView;
    private ContenderAdapter adapter;
    private List<Contender> contenderList;
    TextView groupNameTextView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.challenge_group_leaderboard);

        // Retrieve the group name from intent extras from prev. screen
        groupName = getIntent().getStringExtra("groupName");

        groupNameTextView = findViewById(R.id.groupName);
        loadGroupProfileImage();

        // Set Group Name
        groupNameTextView.setText(groupName);

        // Initialize RecyclerView and Adapter
        recyclerView = findViewById(R.id.leaderboardRV);
        contenderList = new ArrayList<>();
        adapter = new ContenderAdapter(contenderList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        // Fetch data from Firebase
        new Thread(
                this::fetchContenderData)
                .start();


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

                // Get the group details for filtering
                String groupAmountCategory = snapshot.child("amountCategory").getValue(String.class);
                String groupCreateDate = snapshot.child("createDate").getValue(String.class);
                String groupDueDate = snapshot.child("dueDate").getValue(String.class);

                // Convert group dates to milliseconds since the epoch
                long createDateMillis = convertDateStringToMillis(groupCreateDate);
                long dueDateMillis = convertDateStringToMillis(groupDueDate);

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
                            String profileImageUrl = userSnapshot.child("profileImageUrl").getValue(String.class);

                            // Pull workout history
                            long totalAmount = 0;
                            for (DataSnapshot workoutSnapshot : userSnapshot.child("workout_history").getChildren()) {
                                String workoutDate = workoutSnapshot.child("date").getValue(String.class);
                                String workoutAmountCategory = workoutSnapshot.child("amountCategory").getValue(String.class);

                                long workoutAmount = groupAmountCategory.equals("ep") ?
                                        workoutSnapshot.child("energyPoints").getValue(Long.class) :
                                        workoutSnapshot.child("amount").getValue(Long.class);

                                // Convert workout date to milliseconds since the epoch
                                long workoutDateMillis = convertDateStringToMillis(workoutDate);

                                // If tracking EP for group, include workouts in date range agnostic of distance/time
                                if (groupAmountCategory.equals("ep")) {
                                    // Check if the workout falls within the group's date range
                                    if (workoutDateMillis >= createDateMillis &&
                                            workoutDateMillis <= dueDateMillis) {
                                        totalAmount += workoutAmount;
                                    }
                                } else {
                                    // Check if the workout falls within the group's date range and has the same amount category
                                    if (workoutDateMillis >= createDateMillis &&
                                            workoutDateMillis <= dueDateMillis &&
                                            groupAmountCategory.equals(workoutAmountCategory)) {
                                        totalAmount += workoutAmount;
                                    }
                                }
                            }


                            // Add amount category to contender's card
                           String workoutAmountCatAbbrev;
                           if (groupAmountCategory.equals("time")) {
                               workoutAmountCatAbbrev = "min.";
                           } else if (groupAmountCategory.equals("distance")) {
                               workoutAmountCatAbbrev = "mi.";
                           } else {
                               workoutAmountCatAbbrev = "ep";
                           }

                           String totalEntry = String.valueOf(totalAmount) + " " + workoutAmountCatAbbrev;

                           // Create a Contender object and add it to the contenderList
                           Contender contender = new Contender(profileImageUrl, username, totalEntry);
                           contenderList.add(contender);


                           // Sort the contenderList based on totalAmount in descending order
                           Collections.sort(contenderList, new Comparator<Contender>() {
                               @Override
                               public int compare(Contender contender1, Contender contender2) {
                                   // Convert totalAmount strings to Long for comparison
                                   long totalAmount1 = Long.parseLong(contender1.getMemberAmount().split(" ")[0]);
                                   long totalAmount2 = Long.parseLong(contender2.getMemberAmount().split(" ")[0]);

                                   // Compare totalAmounts in descending order
                                   return Long.compare(totalAmount2, totalAmount1);
                               }
                           });


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
    private void loadGroupProfileImage() {
        DatabaseReference groupRef = FirebaseDatabase.getInstance().getReference().child("groups").child(groupName);
        groupRef.child("groupProfileImg").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String imageUrl = dataSnapshot.getValue(String.class);
                    if (imageUrl != null && !imageUrl.isEmpty()) {
                        ImageView bannerImageView = findViewById(R.id.bannerImageView);
                        Glide.with(ChallengeGroupLeaderboard.this).load(imageUrl).into(bannerImageView);
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("GroupLeaderboard", "Failed to load group image.", databaseError.toException());
            }
        });
    }
    private long convertDateStringToMillis(String dateString) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US); // Define the date format
        try {
            Date date = sdf.parse(dateString); // Parse the date string
            return date.getTime(); // Get the milliseconds since the epoch
        } catch (ParseException e) {
            e.printStackTrace();
            return 0; // Return 0 if parsing fails
        }
    }
}
