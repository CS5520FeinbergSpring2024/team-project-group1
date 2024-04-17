package edu.northeastern.new_final.ui.challengeGroup;

import android.content.Intent;
import android.os.Bundle;
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
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
            }
        });

        fetchGroupData();


    }


    private void fetchGroupData() {
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
                    String metricUpdate;
                    if (metric == "time") {
                        metricUpdate = "minutes";
                    } else if (metric == "distance") {
                        metricUpdate = "miles";
                    } else {
                        metricUpdate = "EP";
                    }
                    Glide.with(ChallengeGroupMain.this)
                            .load(groupProfileImgUrl)
                            .into(bannerImageView);

                    Long goalAmountLong = snapshot.child("amount").getValue(Long.class);
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
    }

}
