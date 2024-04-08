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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

import java.util.ArrayList;
import java.util.List;

import edu.northeastern.new_final.R;
import edu.northeastern.new_final.ui.logWorkout.LogWorkoutActivity;

public class HomeFragment extends Fragment {

    private HomeViewModel mViewModel;
    private TextView textTotalEP; // TextView to display Total Energy Points
    private DatabaseReference databaseReference; // Reference to the user in the database
    String username;
    private RecyclerView recyclerView;
    private PersonalGoalAdapter adapter;

    private TextView textViewNoGoals;

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

        textViewNoGoals = view.findViewById(R.id.textViewNoGoals);

        // Initialize RecyclerView and Adapter
        recyclerView = view.findViewById(R.id.personal_goal_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new PersonalGoalAdapter(new ArrayList<>());
        recyclerView.setAdapter(adapter);


        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference();
        DatabaseReference userEPRef = databaseRef.child("users").child(sanitizedUsername).child("total_EP");

        userEPRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Integer totalEP = dataSnapshot.getValue(Integer.class);
                    if (totalEP != null) {
                        textTotalEP.setText(String.valueOf(totalEP));
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        // Get reference to the user's personal goals in the database
        DatabaseReference personalGoalsReference = FirebaseDatabase.getInstance().getReference()
                .child("users").child(sanitizedUsername).child("personalGoals");

        // Add a ValueEventListener to retrieve personal goals from the database
        personalGoalsReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<PersonalGoal> personalGoalsList = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    //PersonalGoal personalGoal = snapshot.getValue(PersonalGoal.class);


                    // Extract values from the snapshot and convert them to strings
                    String activity = snapshot.child("activity").getValue(String.class);
                    String date = snapshot.child("date").getValue(String.class);
                    String metricAmount = snapshot.child("metric_amount").getValue(String.class);
                    String metricType = snapshot.child("metric_type").getValue(String.class);

                    // Log the extracted values
                    Log.d("PersonalGoal", "Activity: " + activity);
                    Log.d("PersonalGoal", "Date: " + date);
                    Log.d("PersonalGoal", "Metric Amount: " + metricAmount);
                    Log.d("PersonalGoal", "Metric Type: " + metricType);

                    // Create a PersonalGoal object manually and add it to the list
                    PersonalGoal personalGoal = new PersonalGoal(activity, date, metricAmount, metricType);
                    personalGoalsList.add(personalGoal);


                }

                //Check if list is empty
                if (personalGoalsList.isEmpty()) {

                    textViewNoGoals.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                } else{
                    // Hide the message and show the RecyclerView
                    textViewNoGoals.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);

                    adapter.setPersonalGoalsList(personalGoalsList);
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle database error
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