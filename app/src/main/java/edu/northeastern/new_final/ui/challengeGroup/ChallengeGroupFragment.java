package edu.northeastern.new_final.ui.challengeGroup;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;

import edu.northeastern.new_final.R;
import edu.northeastern.new_final.databinding.FragmentChallengeGroupBinding;


public class ChallengeGroupFragment extends Fragment {
    private FragmentChallengeGroupBinding binding;
    private ChallengeGroupViewModel challengeGroupViewModel;
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
    private static final int REQUEST_CODE_PICK_IMAGE = 1001;
    private static final int REQUEST_CODE_PERMISSION_STORAGE = 1002;

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
        //metricLbl = root.findViewById(R.id.metricTypeLbl);

        // Initialize with distance selected
        distanceToggleButton.setChecked(true);
        timeToggleButton.setChecked(false);
        energyPointsToggleButton.setChecked(false);
        distanceToggleButton.setBackgroundResource(R.drawable.toggle_button_border);
        timeToggleButton.setBackgroundResource(R.drawable.toggle_button_border_unselected);
        energyPointsToggleButton.setBackgroundResource(R.drawable.toggle_button_border_unselected);
        amountCategory = "distance";
        //metricLbl.setText("miles");

        // Populate activity spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(requireContext(),
                R.array.activityTypes, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        workoutType.setAdapter(adapter);

        // Get database ref
        databaseRef = FirebaseDatabase.getInstance().getReference("users");
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        username = sharedPreferences.getString("email", null);
        username = username.replace(".", "_").replace("#", "_")
                .replace("$", "_").replace("[", "_").replace("]", "_");

        // Set listeners
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

        addGroupButton.setOnClickListener(v -> {
            // Check if any of the fields are empty
            String groupName = groupNameEditText.getText().toString().trim();
            String amountStr = amountEditText.getText().toString().trim();
            String date = dateEditText.getText().toString().trim();

            if (groupName.isEmpty() || amountStr.isEmpty() || date.isEmpty() || blockAddGroup) {
                Toast.makeText(getContext(), "Error: Please fill out all fields", Toast.LENGTH_SHORT).show();
            } else {
                // All fields are filled, proceed with adding group
                // Your code to add the group goes here
            }
        });

        cancelButton.setOnClickListener(v -> {
            // Handle cancel button click
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


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}