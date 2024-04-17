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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import edu.northeastern.new_final.R;

public class WorkoutHistoryFragment extends Fragment {

    private WorkoutHistoryViewModel mViewModel;
    String username;
    private RecyclerView recyclerView;
    private HistoricalWorkoutAdapter adapter;
    private TextView textViewNoHistory;

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

        textViewNoHistory = view.findViewById(R.id.textViewNoHistory);

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
                fetchHistoryFromDatabase(dataSnapshot);
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

    private void fetchHistoryFromDatabase(@NonNull DataSnapshot dataSnapshot) {
        List<HistoricalWorkout> historicalWorkoutList = new ArrayList<>();
        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

            // Extract values from the snapshot and convert them to strings
            String activity = snapshot.child("activity").getValue(String.class);
            String date = snapshot.child("date").getValue(String.class);
            String amount = String.valueOf(snapshot.child("amount").getValue(Long.class));
            String amountCategory = snapshot.child("amountCategory").getValue(String.class);
            String energyPoints = String.valueOf(snapshot.child("energyPoints").getValue(Long.class));


            // Update metric label
            if (amountCategory.equals("distance")) {
                amountCategory = "miles";
            } else {
                amountCategory = "min.";
            }

            // Update activity label
            if (activity.equals("Calisthenics")) {
                activity = "Walk";
            }



            // Add "ep"
            energyPoints = energyPoints + "ep";

            // Create a Historical Workout object manually and add it to the list
            HistoricalWorkout historicalWorkout = new HistoricalWorkout(activity, date, amount, amountCategory, energyPoints);
            historicalWorkoutList.add(historicalWorkout);

            historicalWorkoutList.sort(new Comparator<HistoricalWorkout>() {
                @Override
                public int compare(HistoricalWorkout workout1, HistoricalWorkout workout2) {
                    // Parse the dates and compare them
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                    try {
                        Date date1 = dateFormat.parse(workout1.getDate());
                        Date date2 = dateFormat.parse(workout2.getDate());
                        // Compare dates in reverse order to get most recent first
                        return date2.compareTo(date1);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    return 0;
                }
            });

            //Check if list is empty
            if (historicalWorkoutList.isEmpty()) {

                textViewNoHistory.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
            } else{
                // Hide the message and show the RecyclerView
                textViewNoHistory.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);

                adapter.setHistoricalWorkoutList(historicalWorkoutList); }
        }
    }

}