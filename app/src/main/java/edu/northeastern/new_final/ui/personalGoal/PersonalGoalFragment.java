package edu.northeastern.new_final.ui.personalGoal;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
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
import edu.northeastern.new_final.ui.logWorkout.LogWorkoutActivity;

public class PersonalGoalFragment extends Fragment {

    private FragmentPersonalGoalBinding binding;
    private DatabaseReference databaseRef;

    String username;
    private double amount;
    private String activity;
    private String metric;
    private String date;
    private boolean blockAddGoal;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        PersonalGoalViewModel personalGoalViewModel =
                new ViewModelProvider(this).get(PersonalGoalViewModel.class);

        binding = FragmentPersonalGoalBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Retrieve username from shared preferences
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        username = sharedPreferences.getString("email", null);
        databaseRef = FirebaseDatabase.getInstance().getReference();

        // Log the username obtained from shared preferences
        Log.d("PersonalGoalFragment", "Username: " + username);

        final TextView textView = binding.textPersonalGoal;
        personalGoalViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);


        // Access XML elements and perform actions
        EditText editTextAmount = binding.editTextAmount;
        Spinner spinnerActivity = binding.spinnerActivityActivity;
        Spinner spinnerMetric = binding.spinnerActivityMetric;
        EditText editTextDate = binding.editTextDate;
        Button buttonAddWorkout = binding.buttonAddWorkout;
        TextView clearButton = binding.clearButton;
        clearButton.setOnClickListener(v -> clearFields(editTextAmount, spinnerActivity, spinnerMetric, editTextDate));
        activity = "Run";
        metric = "Miles";

        // Set default day to today
        Calendar calendar = Calendar.getInstance();
        int initialYear = calendar.get(Calendar.YEAR);
        int initialMonth = calendar.get(Calendar.MONTH);
        int initialDayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

        String initialDate = initialYear + "-" + (initialMonth + 1) + "-" + initialDayOfMonth;
        editTextDate.setText(initialDate);
        date = initialDate;

        // Spinner options
        ArrayAdapter<CharSequence> activityAdapter = ArrayAdapter.createFromResource(requireContext(),
                R.array.activity_options, android.R.layout.simple_spinner_item);
        activityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerActivity.setAdapter(activityAdapter);

        ArrayAdapter<CharSequence> metricAdapter = ArrayAdapter.createFromResource(requireContext(),
                R.array.metric_options, android.R.layout.simple_spinner_item);
        metricAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerMetric.setAdapter(metricAdapter);

        editTextDate.setOnClickListener(v -> showDatePickerDialog(editTextDate));

        editTextAmount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String amountStr = editTextAmount.getText().toString().trim();
                boolean blockAddGoal = false;
                if (!amountStr.isEmpty()) {
                    try {
                        double amount = Double.parseDouble(amountStr);
                        editTextAmount.setBackgroundResource(R.drawable.charcoal_outline);
                    } catch (NumberFormatException e) {
                        // Set invalid input style
                        setInvalidInputStyle(editTextAmount);
                        requireActivity().runOnUiThread(() ->
                                Toast.makeText(requireContext(), "Invalid Input, please enter number", Toast.LENGTH_SHORT).show());
                        blockAddGoal = true;
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        buttonAddWorkout.setOnClickListener(v -> {
            // Execute the task on a new thread
            new Thread(
                    () -> addGoalToFirebase(editTextAmount, spinnerActivity, spinnerMetric, editTextDate))
                    .start();
        });

        return root;
    }

    // Method to add goal to Firebase
    private void addGoalToFirebase(EditText editTextAmount, Spinner spinnerActivity,
                                   Spinner spinnerMetric, EditText editTextDate) {

        // Get values from UI elements
        String amountStr = editTextAmount.getText() != null ? editTextAmount.getText().toString().trim() : "";
        amount = !amountStr.equals("") ? Double.parseDouble(amountStr) : -1;
        activity = spinnerActivity.getSelectedItem().toString();
        metric = spinnerMetric.getSelectedItem().toString();
        date = editTextDate.getText().toString();

        // Check if any field is empty
        if (TextUtils.isEmpty(date) || amountStr.equals("") || date.isEmpty() || amount < 0 || amount > 300 || blockAddGoal) {
            requireActivity().runOnUiThread(() ->
                    Toast.makeText(requireContext(), "Error: Invalid Input", Toast.LENGTH_SHORT).show());
            return;
        }

        Map<String, Object> workoutData = new HashMap<>();
        workoutData.put("metric_amount", amountStr);
        workoutData.put("activity", activity);
        workoutData.put("metric_type", metric);
        workoutData.put("date", date);

        // Push the data to the 'personalGoals' node for the current user
        username = username.replace(".", "_").replace("#", "_")
                .replace("$", "_").replace("[", "_").replace("]", "_");
        databaseRef.child("users").child(username).child("personalGoals").push().setValue(workoutData)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Notify user that data has been added
                        Toast.makeText(requireContext(), "Personal Goal added!", Toast.LENGTH_SHORT).show();

                        // Clear fields after successful entry
                        editTextAmount.setText("");
                        editTextDate.setText("");
                        spinnerActivity.setSelection(0); // Set default selection for spinner
                        spinnerMetric.setSelection(0); // Set default selection for spinner
                    } else {
                        // Handle unsuccessful entry
                        Toast.makeText(requireContext(), "Failed to add Personal Goal!", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private String generateUniqueID(String username, String date) {
        // Get the current timestamp
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault());
        String timestamp = sdf.format(Calendar.getInstance().getTime());

        // Concatenate username, date, and timestamp to create a unique ID
        return username + "_" + date + "_" + timestamp;
    }

    private void showDatePickerDialog(EditText editTextDate) {
        // Current Date
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

        // Create DatePickerDialog
        DatePickerDialog datePickerDialog = new DatePickerDialog(requireContext(),
                (view, selectedYear, selectedMonth, selectedDay) -> {

                    //Set selected date to editText
                    String selectedDate = selectedYear + "-" + (selectedMonth + 1) + "-" + selectedDay;
                    editTextDate.setText(selectedDate);
                },
                year, month, dayOfMonth);

        datePickerDialog.show();
    }

    private void setInvalidInputStyle(EditText editText) {
        editText.setBackgroundResource(R.drawable.invalid_input_outline);
    }

    private void clearFields(EditText editTextAmount, Spinner spinnerActivity,
                             Spinner spinnerMetric, EditText editTextDate) {
        // Clear all fields to default values
        editTextAmount.setText("0");
        spinnerActivity.setSelection(0);
        spinnerMetric.setSelection(0);
        activity = "Run";
        metric = "miles";

        // Set the date to today
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
        String today = year + "-" + (month + 1) + "-" + dayOfMonth;
        editTextDate.setText(today);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}