package edu.northeastern.new_final.ui.personalGoal;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;


import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import edu.northeastern.new_final.R;
import edu.northeastern.new_final.databinding.FragmentPersonalGoalBinding;

public class PersonalGoalFragment extends Fragment {

    private FragmentPersonalGoalBinding binding;
    private DatabaseReference databaseReference;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        PersonalGoalViewModel personalGoalViewModel =
                new ViewModelProvider(this).get(PersonalGoalViewModel.class);

        binding = FragmentPersonalGoalBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textPersonalGoal;
        personalGoalViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);


        // Access XML elements and perform actions
        EditText editTextAmount = binding.editTextAmount;
        Spinner spinnerActivity = binding.spinnerActivityActivity;
        Spinner spinnerMetric = binding.spinnerActivityMetric;
        EditText editTextDate = binding.editTextDate;
        Button buttonAddWorkout = binding.buttonAddWorkout;

        // Spinner options
        ArrayAdapter<CharSequence> activityAdapter = ArrayAdapter.createFromResource(requireContext(),
                R.array.activity_options, android.R.layout.simple_spinner_item);
        activityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerActivity.setAdapter(activityAdapter);

        ArrayAdapter<CharSequence> metricAdapter = ArrayAdapter.createFromResource(requireContext(),
                R.array.metric_options, android.R.layout.simple_spinner_item);
        metricAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerMetric.setAdapter(metricAdapter);

        // Example: Retrieving the value from EditText
        //String amount = editTextAmount.getText().toString();

        if (editTextAmount == null) {
            Log.e("PersonalGoalFragment", "editTextAmount is null");
        }
        if (spinnerActivity == null) {
            Log.e("PersonalGoalFragment", "spinnerActivity is null");
        }
        if (spinnerMetric == null) {
            Log.e("PersonalGoalFragment", "spinnerMetric is null");
        }
        if (editTextDate == null) {
            Log.e("PersonalGoalFragment", "spinnerDuration is null");
        }
        if (buttonAddWorkout == null) {
            Log.e("PersonalGoalFragment", "buttonAddWorkout is null");
        }

        // Check if any of the views are null
        if (editTextAmount != null && spinnerActivity != null && spinnerMetric != null &&
                editTextDate != null && buttonAddWorkout != null) {
            buttonAddWorkout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Call method to add workout to Firebase
                    addGoalToFirebase();
                }
            });
        } else {
            // Handle null views here
            Log.e("PersonalGoalFragment", "One or more XML elements are null");
        }



        return root;
    }



    // Method to add goal to Firebase
    private void addGoalToFirebase() {
        // Access XML elements and perform actions
        EditText editTextAmount = binding.editTextAmount;
        Spinner spinnerActivity = binding.spinnerActivityActivity;
        Spinner spinnerMetric = binding.spinnerActivityMetric;
        EditText editTextDate = binding.editTextDate;

        // Get values from UI elements
        String amount = editTextAmount.getText().toString();
        String activity = spinnerActivity.getSelectedItem().toString();
        String metric = spinnerMetric.getSelectedItem().toString();
        String date = editTextDate.getText().toString(); // Assuming this is where the user enters the date

        // Check if any field is empty
        if (TextUtils.isEmpty(amount) || TextUtils.isEmpty(date)) {
            // Notify user of empty fields
            Toast.makeText(requireContext(), "Please fill all fields!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Check if the date string contains a slash character
        if (date.contains("/")) {
            // Format the date string
            date = formatDate(date);
        }

        // Generate a unique ID using the username and date
        String uniqueID = generateUniqueID("testUser1", date);

        // Prepare data to be added to Firebase
        Map<String, Object> workoutData = new HashMap<>();
        workoutData.put("metric_amount", amount);
        workoutData.put("activity", activity);
        workoutData.put("metric_type", metric);
        workoutData.put("date", date);

        // Get a reference to your Firebase database
        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference();

        // Push the data to the database with a unique key
        databaseRef.child("personal_goals").child(uniqueID).setValue(workoutData);

        // Notify user that data has been added
        Toast.makeText(requireContext(), "Personal Goal added!", Toast.LENGTH_SHORT).show();
    }

    // Method to format the date string
    private String formatDate(String date) {
        // Assuming date format is MM/DD/YYYY
        // Replace slashes with empty string to remove them
        return date.replaceAll("/", "");
    }

    private String generateUniqueID(String username, String date) {
        // Get the current timestamp
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault());
        String timestamp = sdf.format(Calendar.getInstance().getTime());

        // Concatenate username, date, and timestamp to create a unique ID
        return username + "_" + date + "_" + timestamp;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}