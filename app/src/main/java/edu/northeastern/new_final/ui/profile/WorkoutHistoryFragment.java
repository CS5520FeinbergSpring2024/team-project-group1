package edu.northeastern.new_final.ui.profile;

import androidx.lifecycle.ViewModelProvider;

import android.content.Context;
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

import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import edu.northeastern.new_final.R;

public class WorkoutHistoryFragment extends Fragment {

    private WorkoutHistoryViewModel mViewModel;
    String username;
    private RecyclerView recyclerView;
    private HistoricalWorkoutAdapter adapter;

    public static WorkoutHistoryFragment newInstance() {
        return new WorkoutHistoryFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_workout_history, container, false);

        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        username = sharedPreferences.getString("email", null);

        // Ensure we handle the case where the email (username) is null
        if (username == null) {
            // Handle the error - perhaps redirect to login screen or show a message
            return view;
        }

        String sanitizedUsername = username.replace(".", "_");

        // Initialize RecyclerView and Adapter
        recyclerView = view.findViewById(R.id.rv_workouts);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new HistoricalWorkoutAdapter(new ArrayList<>());
        recyclerView.setAdapter(adapter);


        // Get reference to the user's workout history in the database
        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference();
        DatabaseReference historicalWorkoutsReference = FirebaseDatabase.getInstance().getReference()
                .child("users").child(sanitizedUsername).child("workout_history");


        // Add a ValueEventListener to retrieve workout history from the database
        historicalWorkoutsReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<HistoricalWorkout> historicalWorkoutList = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                    // Extract values from the snapshot and convert them to strings
                    String activity = snapshot.child("activity").getValue(String.class);
                    String date = snapshot.child("date").getValue(String.class);
                    String amount = String.valueOf(snapshot.child("amount").getValue(Long.class));
                    String amountCategory = snapshot.child("amountCategory").getValue(String.class);
                    String energyPoints = String.valueOf(snapshot.child("energyPoints").getValue(Long.class));

                    // Log the extracted values
                    Log.d("Workout History", "Activity: " + activity);
                    Log.d("Workout History", "Date: " + date);
                    Log.d("Workout History", "Amount: " + amount);
                    Log.d("Workout History", "Amount Category: " + amountCategory);
                    Log.d("Workout History", "Energy Points: " + energyPoints);

                    // Update metric label
                    if (amountCategory == "distance") {
                        amountCategory = "miles";
                    } else {
                        amountCategory = "min.";
                    }

                    // Add "ep"
                    energyPoints = energyPoints + "ep";

                    // Create a Historical Workout object manually and add it to the list
                    HistoricalWorkout historicalWorkout = new HistoricalWorkout(activity, date, amount, amountCategory, energyPoints);
                    historicalWorkoutList.add(historicalWorkout);

                }
                adapter.setHistoricalWorkoutList(historicalWorkoutList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle database error
            }
        });


        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(WorkoutHistoryViewModel.class);
        // TODO: Use the ViewModel
    }

}