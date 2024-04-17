package edu.northeastern.new_final.ui.challengeGroup;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.bumptech.glide.Glide;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

import edu.northeastern.new_final.R;
public class ChallengeGroupMain extends AppCompatActivity {
    // Define variables for views
    private TextView pointsValueTextView;
    private ProgressBar progressBar;
    private TextView descriptionTextView;
    private TextView metricsValueTextView;
    private TextView timeSpanValueTextView;

    private String groupName;

    private ImageView leaderboardIcon;

    private ImageView bannerImageView;

    TextView groupNameTextView;

    Long goalAmountLong;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.challenge_group);

        // Retrieve the group name from intent extras from prev. screen
        groupName = getIntent().getStringExtra("groupName");

        // Initialize Views
        groupNameTextView = findViewById(R.id.groupName);
        pointsValueTextView = findViewById(R.id.pointsValueTextView);
        progressBar = findViewById(R.id.progressBar);
        descriptionTextView = findViewById(R.id.descriptionTextView);
        metricsValueTextView = findViewById(R.id.metricsValueTextView);
        timeSpanValueTextView = findViewById(R.id.timeSpanValueTextView);
        leaderboardIcon = findViewById(R.id.icon2);
        bannerImageView = findViewById(R.id.bannerImageView);

        // Set Group Name
        groupNameTextView.setText(groupName);


        // Set click listener for icon2
        leaderboardIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start ChallengeGroupLeaderboard activity
                Intent intent = new Intent(ChallengeGroupMain.this, ChallengeGroupLeaderboard.class);
                intent.putExtra("groupName", groupName); // Send groupName with putExtra()
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
            }
        });

        // Fetch group data on new thread
        new Thread(
                this::fetchGroupData)
                .start();

        new Thread(
                this::calculateGroupTotalPoints)
                .start();


    }


    private void fetchGroupData() {
        if (groupName != null) {
            DatabaseReference groupRef = FirebaseDatabase.getInstance().getReference().child("groups").child(groupName);
            groupRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    // Retrieve data from snapshots and update views
                    if (snapshot.exists()) {
                        String description = snapshot.child("description").getValue(String.class);
                        String groupProfileImgUrl = snapshot.child("groupProfileImg").getValue(String.class);
                        String endDate = snapshot.child("dueDate").getValue(String.class);
                        String startDate = snapshot.child("createDate").getValue(String.class);
                        String timeSpan = startDate + " to " + endDate;

                        String metric = snapshot.child("amountCategory").getValue(String.class);
                        metric = Objects.requireNonNull(metric).toLowerCase();

                        String metricUpdate;


                        if (metric.equals("time")) {
                            metricUpdate = "minutes";
                        } else if (metric.equals("distance")) {
                            metricUpdate = "miles";
                        } else {
                            metricUpdate = "EP";
                        }
                        Glide.with(ChallengeGroupMain.this)
                                .load(groupProfileImgUrl)
                                .into(bannerImageView);

                        goalAmountLong = snapshot.child("amount").getValue(Long.class);
                        String goalAmount = String.valueOf(goalAmountLong);
                        String fullMetric = goalAmount + " " + metricUpdate;

                        // Update the views with retrieved data
                        descriptionTextView.setText(description);
                        metricsValueTextView.setText(fullMetric);
                        timeSpanValueTextView.setText(timeSpan);

                        //pointsValueTextView.setText(goalAmount);

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(ChallengeGroupMain.this, "Error retrieving group data.", Toast.LENGTH_SHORT).show();

                }
            });
        } else {
            Toast.makeText(ChallengeGroupMain.this, "Group name is invalid.", Toast.LENGTH_SHORT).show();
        }
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

    private void calculateGroupTotalPoints() {
        DatabaseReference groupRef = FirebaseDatabase.getInstance().getReference().child("groups").child(groupName);
        groupRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                // Get the group details for filtering
                String groupAmountCategory = snapshot.child("amountCategory").getValue(String.class);
                String groupCreateDate = snapshot.child("createDate").getValue(String.class);
                String groupDueDate = snapshot.child("dueDate").getValue(String.class);

                // Convert group dates to milliseconds since the epoch
                long createDateMillis = convertDateStringToMillis(groupCreateDate);
                long dueDateMillis = convertDateStringToMillis(groupDueDate);

                // Variable to store the total points
                final long[] totalPoints = {0};

                // Find the members in a given group
                for (DataSnapshot memberSnapshot : snapshot.child("members").getChildren()) {
                    String memberID = memberSnapshot.getKey();

                    // Pull user data for the member from "users" node
                    DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("users").child(memberID);
                    userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot userSnapshot) {
                            // Pull workout history
                            long memberTotalAmount = 0;
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
                                        memberTotalAmount += workoutAmount;
                                    }
                                } else {
                                    // Check if the workout falls within the group's date range and has the same amount category
                                    if (workoutDateMillis >= createDateMillis &&
                                            workoutDateMillis <= dueDateMillis &&
                                            groupAmountCategory.equals(workoutAmountCategory)) {
                                        memberTotalAmount += workoutAmount;
                                    }
                                }
                            }

                            // Add the member's total amount to the running total
                            totalPoints[0] += memberTotalAmount;
                            String pointsToRender = String.valueOf(totalPoints[0]);
                            pointsValueTextView.setText(pointsToRender);

                            int goalAmountInt = Math.toIntExact(goalAmountLong);
                            int totalPointsInt = Math.toIntExact(totalPoints[0]);
                            int progress = (int) ((double) totalPointsInt / goalAmountInt * 100);
                            progressBar.setProgress(progress);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Log.e("FetchContenderData", "Error group data: " + error.getMessage());
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("FetchContenderData", "Error fetching group data: " + error.getMessage());
            }
        });
    }

}
