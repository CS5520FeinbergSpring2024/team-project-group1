package edu.northeastern.new_final.ui.logWorkout;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.view.View;

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
        amountCategory = "";
        metricLbl = findViewById(R.id.metricTypeLbl);
        energyPoints = findViewById(R.id.textViewActivityEP);

        databaseRef = FirebaseDatabase.getInstance().getReference("users");

        // Populate activity spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.activityTypes, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        activitySpinner.setAdapter(adapter);

        /**
        activitySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedActivity = parent.getItemAtPosition(position).toString();
                //energyPoints.setText(checkUpdateEP());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        amountEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //energyPoints.setText(checkUpdateEP());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
         */

        distanceToggleButton.setOnClickListener(v -> {
            if (distanceToggleButton.isChecked()) {
                timeToggleButton.setChecked(false);
                distanceToggleButton.setBackgroundResource(R.drawable.toggle_button_border);
                timeToggleButton.setBackgroundResource(R.drawable.toggle_button_border_unselected);
                amountCategory = "distance";
                metricLbl.setText("miles");
                //energyPoints.setText(checkUpdateEP());
            }
        });

        timeToggleButton.setOnClickListener(v -> {
            if (timeToggleButton.isChecked()) {
                distanceToggleButton.setChecked(false);
                timeToggleButton.setBackgroundResource(R.drawable.toggle_button_border);
                distanceToggleButton.setBackgroundResource(R.drawable.toggle_button_border_unselected);
                amountCategory = "time";
                metricLbl.setText("min");
                //energyPoints.setText(checkUpdateEP());
            }
        });

        addWorkout.setOnClickListener(v -> {
            // Check if any of the fields are empty
            activity = activitySpinner.getSelectedItem().toString();
            String amountStr = amountEditText.getText().toString().trim();
            amount = Double.parseDouble(amountStr);
            date = dateEntry.getText().toString().trim();

            if (activity.isEmpty() || amountStr.isEmpty() || date.isEmpty() || amountCategory.equals("") || amount < 0 || amount > 300) {
                Toast.makeText(LogWorkoutActivity.this, "Error: Please fill out all fields", Toast.LENGTH_SHORT).show();
            } else {
                // All fields are filled, proceed with adding workout
                Workout newWorkout = new Workout(activity, amount, date, amountCategory);
                DatabaseReference userWorkoutRef = databaseRef.child("newuser@gmail.com");
                userWorkoutRef.child("workout_history").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            // User has workout history, add workout object to the existing array
                            List<Workout> workoutHistory = new ArrayList<>();
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                Workout workout = snapshot.getValue(Workout.class);
                                workoutHistory.add(workout);
                            }
                            // Add your new workout object to the array
                            workoutHistory.add(newWorkout);

                            // Update the workout history in the database
                            userWorkoutRef.child("workout_history").setValue(workoutHistory);
                        } else {
                            // User doesn't have workout history, initialize it with a new array containing your workout object
                            List<Workout> workoutHistory = new ArrayList<>();
                            workoutHistory.add(newWorkout);

                            // Save the initialized workout history to the database
                            userWorkoutRef.child("workout_history").setValue(workoutHistory);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });

                Toast.makeText(LogWorkoutActivity.this, "Workout added successfully", Toast.LENGTH_SHORT).show();

            }
        });

        dateEntry.setOnClickListener(v -> {
            // Get current date
            Calendar calendar = Calendar.getInstance();
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

            // Show DatePickerDialog
            datePickerDialog.show();
        });

        // Add functionality for other views as needed

    }

    private int checkUpdateEP() {
        String amountStr = amountEditText.getText().toString().trim();
        if (activity == null || amountStr == null || amountCategory == null || amountCategory.equals("")) {
            return 0;
        } else {
            return calculateEnergyPoints(amount, amountCategory);
        }
    }

    private int calculateEnergyPoints(double amount, String amountCategory) {
        if (amountCategory.equals("time")) {
            return Math.round((float) amount / 10);
        } else {
            return Math.round((float) amount);
        }
    }

}
