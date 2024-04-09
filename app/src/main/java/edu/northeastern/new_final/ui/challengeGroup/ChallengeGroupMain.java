package edu.northeastern.new_final.ui.challengeGroup;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

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

    private ImageView leaderboardIcon;

    TextView groupNameTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.challenge_group);

        // Retrieve the group name from intent extras from prev. screen
        String groupName = getIntent().getStringExtra("groupName");

        // Initialize Views
        groupNameTextView = findViewById(R.id.groupName);
        pointsValueTextView = findViewById(R.id.pointsValueTextView);
        progressBar = findViewById(R.id.progressBar);
        descriptionTextView = findViewById(R.id.descriptionTextView);
        metricsValueTextView = findViewById(R.id.metricsValueTextView);
        timeSpanValueTextView = findViewById(R.id.timeSpanValueTextView);
        leaderboardIcon = findViewById(R.id.icon2);

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


        /*
        // Retrieve group data from Firebase
        DatabaseReference groupRef = FirebaseDatabase.getInstance().getReference().child("groups").child(groupName);
        groupRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Retrieve data from dataSnapshot and update views



            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle error
            }
        });

         */

    }
}
