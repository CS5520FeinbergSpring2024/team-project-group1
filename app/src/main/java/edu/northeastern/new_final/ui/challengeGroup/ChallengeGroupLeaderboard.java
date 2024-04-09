package edu.northeastern.new_final.ui.challengeGroup;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import edu.northeastern.new_final.R;


public class ChallengeGroupLeaderboard extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.challenge_group_leaderboard);

        ImageView challengeMainIcon = findViewById(R.id.icon1);

        // Set click listener for icon to jump back to challengeMain
        challengeMainIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start ChallengeGroupMain activity
                Intent intent = new Intent(ChallengeGroupLeaderboard.this, ChallengeGroupMain.class);
                startActivity(intent);
            }
        });

    }
}
