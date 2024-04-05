package edu.northeastern.new_final.ui.profile;

import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import android.widget.TextView;

import edu.northeastern.new_final.R;
import edu.northeastern.new_final.ui.logWorkout.LogWorkoutActivity;

public class HomeFragment extends Fragment {

    private HomeViewModel mViewModel;
    private TextView textTotalEP; // TextView to display Total Energy Points
    private DatabaseReference databaseReference; // Reference to the user in the database
    String username;
    public static HomeFragment newInstance() {
        return new HomeFragment();
    }
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        username = sharedPreferences.getString("email", null);

        // Ensure we handle the case where the email (username) is null
        if (username == null) {
            // Handle the error - perhaps redirect to login screen or show a message
            return view;
        }

        String sanitizedUsername = username.replace(".", "_");
        textTotalEP = view.findViewById(R.id.textView_EPNumber); // Find the TextView by ID

        Log.d("HomeFragment", "Username for Key: " + sanitizedUsername);

        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference();
        DatabaseReference userEPRef = databaseRef.child("users").child(sanitizedUsername).child("total_EP");

        userEPRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Integer totalEP = dataSnapshot.getValue(Integer.class); // Directly get the value of total_EP
                    Log.d("HomeFragment", "Total EP from database: " + totalEP);
                    if (totalEP != null) {
                        textTotalEP.setText(String.valueOf(totalEP)); // Update the TextView
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        return view;
    }
    private void updateTotalEPDisplay(int totalEP) {
        textTotalEP.setText(String.valueOf(totalEP));
    }
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        // TODO: Use the ViewModel
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Button buttonLogWorkout = view.findViewById(R.id.button_logWorkout); // Assuming the button ID is button_log_workout
        buttonLogWorkout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startLogWorkoutActivity(v);
            }
        });
    }



    public void startLogWorkoutActivity(View view) {
        Intent intent = new Intent(getContext(), LogWorkoutActivity.class);
        startActivity(intent);
    }



}