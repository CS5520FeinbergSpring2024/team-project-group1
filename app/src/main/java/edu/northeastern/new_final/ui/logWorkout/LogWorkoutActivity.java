package edu.northeastern.new_final.ui.logWorkout;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;
import android.widget.ArrayAdapter;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import edu.northeastern.new_final.R;

public class LogWorkoutActivity extends AppCompatActivity {

    private Spinner activitySpinner;
    private EditText amountEditText;
    private ToggleButton distanceToggleButton;
    private ToggleButton timeToggleButton;
    private EditText dateEntry;
    private Button addWorkout;
    private TextView cancelWorkout;
    private String amountCategory;
    private String activity;
    private double amount;
    private String date;
    private DatabaseReference databaseRef;
    private TextView metricLbl;
    private TextView energyPoints;
    private String username;
    private boolean blockLogWorkout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_workout);

        // Initialize views

        activitySpinner = findViewById(R.id.spinnerActivity);
        amountEditText = findViewById(R.id.editTextAmount);
        distanceToggleButton = findViewById(R.id.toggleButtonDistance);
        timeToggleButton = findViewById(R.id.toggleButtonTime);
        dateEntry = findViewById(R.id.dateEditText);
        addWorkout = findViewById(R.id.buttonAddWorkout);
        cancelWorkout = findViewById(R.id.buttonCancel);
        metricLbl = findViewById(R.id.metricTypeLbl);
        energyPoints = findViewById(R.id.textViewActivityEP);
        activity = "Run";
        blockLogWorkout = false;

        Calendar calendar = Calendar.getInstance();
        int initialYear = calendar.get(Calendar.YEAR);
        int initialMonth = calendar.get(Calendar.MONTH);
        int initialDayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

        // Set default day to today
        String initialDate = initialYear + "-" + (initialMonth + 1) + "-" + initialDayOfMonth;
        dateEntry.setText(initialDate);
        date = initialDate;

        databaseRef = FirebaseDatabase.getInstance().getReference("users");
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        username = sharedPreferences.getString("email", null);
        username = username.replace(".", "_").replace("#", "_")
                .replace("$", "_").replace("[", "_").replace("]", "_");

        // Initialize with distance selected
        distanceToggleButton.setChecked(true);
        timeToggleButton.setChecked(false);
        distanceToggleButton.setBackgroundResource(R.drawable.toggle_button_border);
        timeToggleButton.setBackgroundResource(R.drawable.toggle_button_border_unselected);
        amountCategory = "distance";
        metricLbl.setText("miles");

        // Populate activity spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.activityTypes, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        activitySpinner.setAdapter(adapter);

        amountEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                updateEnergyPoints();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        distanceToggleButton.setOnClickListener(v -> {
            if (!distanceToggleButton.isChecked()) {
                distanceToggleButton.setChecked(true);
            }
            if (timeToggleButton.isChecked()) {
                timeToggleButton.setChecked(false);
            }
            distanceToggleButton.setBackgroundResource(R.drawable.toggle_button_border);
            timeToggleButton.setBackgroundResource(R.drawable.toggle_button_border_unselected);
            amountCategory = "distance";
            metricLbl.setText("miles");
            updateEnergyPoints();
        });

        timeToggleButton.setOnClickListener(v -> {
            if (!timeToggleButton.isChecked()) {
                timeToggleButton.setChecked(true);
            }
            if (distanceToggleButton.isChecked()) {
                distanceToggleButton.setChecked(false);
            }
            timeToggleButton.setBackgroundResource(R.drawable.toggle_button_border);
            distanceToggleButton.setBackgroundResource(R.drawable.toggle_button_border_unselected);
            amountCategory = "time";
            metricLbl.setText("min");
            updateEnergyPoints();
        });


        addWorkout.setOnClickListener(v -> {
            // Check if any of the fields are empty
            activity = activitySpinner.getSelectedItem().toString();
            String amountStr = amountEditText.getText() != null ? amountEditText.getText().toString().trim() : "";
            amount = !amountStr.equals("") ? Double.parseDouble(amountStr) : -1;
            date = dateEntry.getText().toString().trim();

            if (amountStr.equals("") || date.isEmpty() || amount < 0 || amount > 300 || blockLogWorkout) {
                Toast.makeText(LogWorkoutActivity.this, "Error: Please fill out all fields", Toast.LENGTH_SHORT).show();
            } else {
                // All fields are filled, proceed with adding workout
                Workout newWorkout = new Workout(activity, amount, date, amountCategory);
                new Thread(() -> {
                    Looper.prepare();
                    addWorkoutToDatabase(newWorkout);
                    Looper.loop();
                }).start();

                // return to previous page
                finish();
            }
        });

        dateEntry.setOnClickListener(v -> {
            // Get current date
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

            // Create DatePickerDialog
            DatePickerDialog datePickerDialog = new DatePickerDialog(LogWorkoutActivity.this,
                    (view, selectedYear, selectedMonth, selectedDay) -> {
                        // Set selected date to EditText
                        String selectedDate = selectedYear + "-" + (selectedMonth + 1) + "-" + selectedDay;
                        dateEntry.setText(selectedDate);
                    },
                    year, month, dayOfMonth);

            datePickerDialog.show();
        });

        cancelWorkout.setOnClickListener(v -> {
            finish();
        });

    }

    private void updateEnergyPoints() {
        String amountStr = amountEditText.getText().toString().trim();
        blockLogWorkout = false;
        if (!amountStr.isEmpty()) {
            try {
                double amount = Double.parseDouble(amountStr);
                energyPoints.setText(String.valueOf(calculateEnergyPoints(amount, amountCategory)));
                amountEditText.setBackgroundResource(R.drawable.charcoal_outline);
            } catch (NumberFormatException e) {
                // Set invalid input style
                setInvalidInputStyle(amountEditText);
                Toast.makeText(this, "Error: Please enter a valid number.", Toast.LENGTH_SHORT).show();
                energyPoints.setText("0");
                blockLogWorkout = true;
            }
        } else {
            energyPoints.setText("0");
            // Reset EditText border color to default
            amountEditText.setBackgroundResource(R.drawable.charcoal_outline);
            blockLogWorkout = true;
        }
    }

    private void setInvalidInputStyle(EditText editText) {
        editText.setBackgroundResource(R.drawable.invalid_input_outline);
    }

    private int calculateEnergyPoints(double amount, String amountCategory) {
        if (amountCategory.equals("time")) {
            return Math.round((float) amount / 10);
        } else {
            return Math.round((float) amount);
        }
    }

    private void addWorkoutToDatabase(Workout newWorkout) {
        DatabaseReference userWorkoutRef = databaseRef.child(username);

        incrementTotalEP(userWorkoutRef, newWorkout);
        userWorkoutRef.child("workout_history").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    List<Workout> workoutHistory = new ArrayList<>();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Workout workout = snapshot.getValue(Workout.class);
                        workoutHistory.add(workout);
                    }
                    workoutHistory.add(newWorkout);
                    userWorkoutRef.child("workout_history").setValue(workoutHistory);
                } else {
                    List<Workout> workoutHistory = new ArrayList<>();
                    workoutHistory.add(newWorkout);
                    userWorkoutRef.child("workout_history").setValue(workoutHistory);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    private void incrementTotalEP(DatabaseReference userWorkoutRef, Workout newWorkout) {
        userWorkoutRef.child("total_EP").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int totalEP = 0;
                if (dataSnapshot.exists()) {
                    totalEP = dataSnapshot.getValue(Integer.class);
                }
                totalEP = totalEP + newWorkout.getEnergyPoints();
                userWorkoutRef.child("total_EP").setValue(totalEP);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

}
