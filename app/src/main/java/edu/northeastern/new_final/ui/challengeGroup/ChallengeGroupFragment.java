package edu.northeastern.new_final.ui.challengeGroup;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
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
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import edu.northeastern.new_final.R;
import edu.northeastern.new_final.databinding.FragmentChallengeGroupBinding;
import edu.northeastern.new_final.ui.LoginActivity;
import edu.northeastern.new_final.ui.logWorkout.Workout;


public class ChallengeGroupFragment extends Fragment {
    private EditText groupNameEditText;
    private EditText amountEditText;
    private EditText dateEditText;
    private EditText description;
    private Button addGroupButton;
    private TextView cancelButton;
    private ToggleButton distanceToggleButton;
    private ToggleButton timeToggleButton;
    private ToggleButton energyPointsToggleButton;
    private Spinner workoutType;
    private TextView metricLbl;
    private String amountCategory;
    private String username;
    private Button uploadImgBtn;
    private DatabaseReference databaseRef;

    private boolean blockAddGroup;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.create_group, container, false);


        groupNameEditText = root.findViewById(R.id.editGroupName);
        amountEditText = root.findViewById(R.id.editTextAmount);
        workoutType = root.findViewById(R.id.spinnerWorkoutType);
        dateEditText = root.findViewById(R.id.dateEditText);
        addGroupButton = root.findViewById(R.id.buttonAddGroup);
        cancelButton = root.findViewById(R.id.buttonCancel);
        distanceToggleButton = root.findViewById(R.id.toggleButtonDistance);
        timeToggleButton = root.findViewById(R.id.toggleButtonTime);
        energyPointsToggleButton = root.findViewById(R.id.toggleButtonEP);
        description = root.findViewById(R.id.longResponseEditText);
        uploadImgBtn = root.findViewById(R.id.uploadImgBtn);
        blockAddGroup = false;
        metricLbl = root.findViewById(R.id.metricTypeLbl);


        // Initialize with distance selected
        distanceToggleButton.setChecked(true);
        timeToggleButton.setChecked(false);
        energyPointsToggleButton.setChecked(false);
        distanceToggleButton.setBackgroundResource(R.drawable.toggle_button_border);
        timeToggleButton.setBackgroundResource(R.drawable.toggle_button_border_unselected);
        energyPointsToggleButton.setBackgroundResource(R.drawable.toggle_button_border_unselected);
        amountCategory = "distance";
        metricLbl.setText("miles");

        // Populate activity spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(requireContext(),
                R.array.activityTypes, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        workoutType.setAdapter(adapter);

        // Get database ref
        databaseRef = FirebaseDatabase.getInstance().getReference("groups");
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        username = sharedPreferences.getString("email", null);
        username = username.replace(".", "_").replace("#", "_")
                .replace("$", "_").replace("[", "_").replace("]", "_");

        // Set listeners
        groupNameEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                groupNameEditText.setBackgroundResource(R.drawable.charcoal_outline);
            }
        });
        amountEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                updateAddGroupButtonState();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        dateEditText.setOnClickListener(v -> {
            showDatePickerDialog();
        });

        distanceToggleButton.setOnClickListener(v -> {
            if (!distanceToggleButton.isChecked()) {
                distanceToggleButton.setChecked(true);
            }
            if (timeToggleButton.isChecked() || energyPointsToggleButton.isChecked()) {
                timeToggleButton.setChecked(false);
                energyPointsToggleButton.setChecked(false);
            }
            distanceToggleButton.setBackgroundResource(R.drawable.toggle_button_border);
            energyPointsToggleButton.setBackgroundResource(R.drawable.toggle_button_border_unselected);
            timeToggleButton.setBackgroundResource(R.drawable.toggle_button_border_unselected);
            amountCategory = "distance";
            metricLbl.setText("miles");
        });

        timeToggleButton.setOnClickListener(v -> {
            if (!timeToggleButton.isChecked()) {
                timeToggleButton.setChecked(true);
            }
            if (distanceToggleButton.isChecked() || energyPointsToggleButton.isChecked()) {
                distanceToggleButton.setChecked(false);
                energyPointsToggleButton.setChecked(false);
            }
            timeToggleButton.setBackgroundResource(R.drawable.toggle_button_border);
            energyPointsToggleButton.setBackgroundResource(R.drawable.toggle_button_border_unselected);
            distanceToggleButton.setBackgroundResource(R.drawable.toggle_button_border_unselected);
            amountCategory = "time";
            metricLbl.setText("min");
        });

        energyPointsToggleButton.setOnClickListener(v -> {
            if (!energyPointsToggleButton.isChecked()) {
                energyPointsToggleButton.setChecked(true);
            }
            if (distanceToggleButton.isChecked() || timeToggleButton.isChecked()) {
                distanceToggleButton.setChecked(false);
                timeToggleButton.setChecked(false);
            }
            timeToggleButton.setBackgroundResource(R.drawable.toggle_button_border_unselected);
            energyPointsToggleButton.setBackgroundResource(R.drawable.toggle_button_border);
            distanceToggleButton.setBackgroundResource(R.drawable.toggle_button_border_unselected);
            amountCategory = "ep";
            metricLbl.setText("pts");
        });

        addGroupButton.setOnClickListener(v -> {
            // Check if any of the fields are empty
            String groupName = groupNameEditText.getText().toString().trim();
            String groupProfileImg = "imgpathhere";
            String amountStr = amountEditText.getText().toString().trim();
            String date = dateEditText.getText().toString().trim();
            String descriptionResponse = description.getText().toString().trim();
            String activityType = workoutType.getSelectedItem().toString();
            ArrayList<String> members = new ArrayList<>();
            members.add(username);


            String username2 = "testUser";
            members.add(username2);

            // Log the contents of the members ArrayList
            Log.d("Members", "Members: " + members.toString());


            if (groupName.isEmpty() || groupName.equals("") || groupName.equals(" ") || amountStr.isEmpty() || date.isEmpty() || blockAddGroup) {
                Toast.makeText(getContext(), "Error: Please fill out all fields", Toast.LENGTH_SHORT).show();
            } else {
                // All fields are filled, proceed with adding workout
                ChallengeGroup newGroup = new ChallengeGroup(
                        groupName,
                        descriptionResponse,
                        groupProfileImg,
                        activityType,
                        amountStr,
                        amountCategory,
                        date,
                        members);
                new Thread(() -> {
                    Looper.prepare();
                    addGroupToDatabase(newGroup);
                    Looper.loop();
                }).start();
            }
        });

        cancelButton.setOnClickListener(v -> {
        });


        return root;
    }

    private void updateAddGroupButtonState() {
        String amountStr = amountEditText.getText().toString().trim();
        blockAddGroup = false;
        if (!amountStr.isEmpty()) {
            try {
                double amount = Double.parseDouble(amountStr);
                amountEditText.setBackgroundResource(R.drawable.charcoal_outline);
            } catch (NumberFormatException e) {
                // Set invalid input style
                setInvalidInputStyle(amountEditText);
                Toast.makeText(getContext(), "Error: Please enter a valid number.", Toast.LENGTH_SHORT).show();
                blockAddGroup = true;
            }
        } else {
            // Reset EditText border color to default
            amountEditText.setBackgroundResource(R.drawable.charcoal_outline);
            blockAddGroup = true;
        }
    }

    private void setInvalidInputStyle(EditText editText) {
        editText.setBackgroundResource(R.drawable.invalid_input_outline);
    }

    private void showDatePickerDialog() {
        // Get current date
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

        // Create DatePickerDialog
        DatePickerDialog datePickerDialog = new DatePickerDialog(requireContext(),
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    // Set selected date to EditText
                    String selectedDate = selectedYear + "-" + (selectedMonth + 1) + "-" + selectedDay;
                    dateEditText.setText(selectedDate);
                },
                year, month, dayOfMonth);

        datePickerDialog.show();
    }

    private void addGroupToDatabase(ChallengeGroup newGroup) {
        DatabaseReference groupRef = databaseRef.child(newGroup.getGroupName());
        groupRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Group name already exists
                    Toast.makeText(getContext(), "Group Name already in use.",
                            Toast.LENGTH_SHORT).show();
                    setInvalidInputStyle(groupNameEditText);
                } else {

                    // Convert newGroup to Map (helps add member child nodes)
                    Map<String, Object> groupValues = new HashMap<>();
                    groupValues.put("groupName", newGroup.getGroupName());
                    groupValues.put("description", newGroup.getDescription());
                    groupValues.put("groupProfileImg", newGroup.getGroupProfileImg());
                    groupValues.put("activityType", newGroup.getActivityType());
                    groupValues.put("amount", newGroup.getAmount());
                    groupValues.put("amountCategory", newGroup.getAmountCategory());
                    groupValues.put("dueDate", newGroup.getDueDate());

                    // Add members separately (to allow for specific member child nodes)
                    Map<String, Object> membersMap = new HashMap<>();
                    for (String member : newGroup.getMembers()) {
                        membersMap.put(member, true);
                    }
                    groupValues.put("members", membersMap);

                    // Update database with the entire newGroup object
                    groupRef.updateChildren(groupValues)


                            .addOnCompleteListener((Activity) requireContext(), task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(getContext(), "Group added successfully!",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getContext(), "Failed to add group" + Objects.requireNonNull(task.getException()).getMessage(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getContext(), "Database error: " + databaseError.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}