package edu.northeastern.new_final.ui.challengeGroup;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import edu.northeastern.new_final.R;
import edu.northeastern.new_final.databinding.FragmentChallengeGroupBinding;
import edu.northeastern.new_final.ui.LoginActivity;
import edu.northeastern.new_final.ui.logWorkout.Workout;


public class ChallengeGroupFragment extends Fragment implements FindUsersDialogFragment.OnUsersSelectedListener{
    private EditText groupNameEditText;
    private EditText amountEditText;
    private EditText dateEditText;
    private EditText description;
    private Button addGroupButton;
    private TextView clearButton;
    private ToggleButton distanceToggleButton;
    private ToggleButton timeToggleButton;
    private ToggleButton energyPointsToggleButton;
    private Spinner workoutType;
    private TextView metricLbl;
    private String amountCategory;
    private String username;
    private Button uploadImgBtn;
    private DatabaseReference databaseRef;

    private Uri imageUri = null;

    private ArrayList<String> members = new ArrayList<>();
    private boolean blockAddGroup;

    private Button addMembersBtn;
    private TextView membersTV;

    private static final int PICK_IMAGE = 123;

    private static final int REQUEST_IMAGE_CAPTURE = 100;


    private String currentGroupName;

    private static final int PERMISSIONS_REQUEST_READ_STORAGE = 1;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.create_group, container, false);

        Button uploadGroupImageButton = root.findViewById(R.id.uploadImgBtn);

        groupNameEditText = root.findViewById(R.id.editGroupName);
        amountEditText = root.findViewById(R.id.editTextAmount);
        workoutType = root.findViewById(R.id.spinnerWorkoutType);
        dateEditText = root.findViewById(R.id.dateEditText);
        addGroupButton = root.findViewById(R.id.buttonAddGroup);
        clearButton = root.findViewById(R.id.buttonClear);
        distanceToggleButton = root.findViewById(R.id.toggleButtonDistance);
        timeToggleButton = root.findViewById(R.id.toggleButtonTime);
        energyPointsToggleButton = root.findViewById(R.id.toggleButtonEP);
        description = root.findViewById(R.id.longResponseEditText);
        uploadImgBtn = root.findViewById(R.id.uploadImgBtn);
        blockAddGroup = false;
        metricLbl = root.findViewById(R.id.metricTypeLbl);
        addMembersBtn = root.findViewById(R.id.AddGroupMembersBtn);
        membersTV = root.findViewById(R.id.membersTV);



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


        addMembersBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFindUsersDialog();
            }
        });

        addGroupButton.setOnClickListener(v -> {
            // Check if any of the fields are empty
            String groupName = groupNameEditText.getText().toString().trim();
            String groupProfileImg = "imgpathhere";
            String amountStr = amountEditText.getText().toString().trim();
            String date = dateEditText.getText().toString().trim();
            String descriptionResponse = description.getText().toString().trim();
            String activityType = workoutType.getSelectedItem().toString();

            members.add(username);



            //String username2 = "testUser";
            //members.add(username2);

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
                    addGroupToDatabase(newGroup, imageUri);
                    Looper.loop();
                }).start();
            }
        });

        // Reset all UI elements to original state
        clearButton.setOnClickListener(v -> {
                    // Clear all the EditText fields
                    groupNameEditText.setText("");
                    amountEditText.setText("");
                    dateEditText.setText("");
                    description.setText("");

                    // Reset the toggle buttons to their initial state
                    distanceToggleButton.setChecked(true);
                    timeToggleButton.setChecked(false);
                    energyPointsToggleButton.setChecked(false);

                    // Reset the spinner to the default selection
                    workoutType.setSelection(0);

                    // Clear the imageUri
                    imageUri = null;

                    // Clear the members list and update the UI
                    members.clear();
                    updateSelectedMembersUI(new ArrayList<>());

                    //Toast update at conclusion
                    Toast.makeText(getContext(), "Form cleared", Toast.LENGTH_SHORT).show();
                });


        uploadGroupImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get the group name from the EditText
                String groupName = groupNameEditText.getText().toString().trim();
                if (!groupName.isEmpty()) {
                    openImageSelector(); // Pass the group name to your image selector method
                } else {
                    // Handle the case where the group name is empty
                    Toast.makeText(getContext(), "Please enter a group name.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return root;
    }

    private void uploadGroupImageToFirebaseStorage(Uri imageUri, String groupName) {
        if (imageUri != null) {
            // Sanitize the group name to be Firebase Storage safe
            String sanitizedGroupName = groupName.replace(".", "_").replace("#", "_")
                    .replace("$", "_").replace("[", "_").replace("]", "_");

            // Reference to where the image will be stored in Firebase Storage
            StorageReference groupImageRef = FirebaseStorage.getInstance().getReference("group_images/" + sanitizedGroupName + ".jpg");

            // Upload the image to Firebase Storage
            groupImageRef.putFile(imageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            // Retrieve the download URL
                            groupImageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri downloadUri) {
                                    // Log the URL for debugging purposes
                                    Log.d("GroupProfile", "Image upload successful. URL: " + downloadUri.toString());

                                    // Update the group's profile image URL in Firebase Database
                                    DatabaseReference groupsRef = FirebaseDatabase.getInstance().getReference("groups");
                                    DatabaseReference groupRef = groupsRef.child(sanitizedGroupName);
                                    groupRef.child("groupProfileImg").setValue(downloadUri.toString())
                                            .addOnCompleteListener(task -> {
                                                if (task.isSuccessful()) {
                                                    Log.d("GroupProfile", "Group profile image URL updated successfully.");
                                                } else {
                                                    Log.e("GroupProfile", "Failed to update group profile image URL.", task.getException());
                                                }
                                            });
                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            Log.e("GroupProfile", "Image upload failed: " + exception.getMessage());
                        }
                    });
        }
    }
    private void openImageSelector() {
        final CharSequence[] options = {"Take Photo", "Choose from Gallery", "Cancel"};

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Select Option");

        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (options[item].equals("Take Photo")) {
                    Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    if (takePicture.resolveActivity(getActivity().getPackageManager()) != null) {
                        startActivityForResult(takePicture, REQUEST_IMAGE_CAPTURE);
                    }
                } else if (options[item].equals("Choose from Gallery")) {
                    Intent pickPhoto = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(pickPhoto, PICK_IMAGE);
                } else if (options[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });

        builder.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == PICK_IMAGE && data != null && data.getData() != null) {
                imageUri = data.getData();
                // Use this Uri in your app. For example, display it in an ImageView or upload it to Firebase.
            } else if (requestCode == REQUEST_IMAGE_CAPTURE && data != null) {
                Bundle extras = data.getExtras();
                if (extras != null) {
                    Bitmap imageBitmap = (Bitmap) extras.get("data");
                    // Process and upload the Bitmap as needed. You might want to save it as a file and then upload it.
                    // Ensure the following method exists and handles the Bitmap correctly
                    imageUri = getImageUri(getContext(), imageBitmap);
                }
            }

            // Here you might want to update your ImageView or UI element with the new image.
            // Also, ensure that imageUri is being used to update Firebase.
        }
    }

    private Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "GroupImage", null);
        return Uri.parse(path);
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

    private void addGroupToDatabase(ChallengeGroup newGroup, Uri imageUri) {
        DatabaseReference groupRef = databaseRef.child(newGroup.getGroupName());
        groupRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()) {
                    // Group does not exist, proceed with creation
                    String createDate = getCurrentDate();

                    Map<String, Object> groupValues = new HashMap<>();
                    groupValues.put("groupName", newGroup.getGroupName());
                    groupValues.put("description", newGroup.getDescription());
                    groupValues.put("activityType", newGroup.getActivityType());
                    groupValues.put("amount", newGroup.getAmount());
                    groupValues.put("amountCategory", newGroup.getAmountCategory());
                    groupValues.put("dueDate", newGroup.getDueDate());
                    groupValues.put("createDate", createDate);
                    // Initially, you can set the groupProfileImg to a placeholder
                    groupValues.put("groupProfileImg", "default_placeholder_img_url");

                    // Add members
                    Map<String, Object> membersMap = new HashMap<>();
                    for (String member : newGroup.getMembers()) {
                        membersMap.put(member, true);
                    }
                    groupValues.put("members", membersMap);

                    // Create the new group entry in the database
                    groupRef.setValue(groupValues).addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(getContext(), "Group added successfully!",
                                    Toast.LENGTH_SHORT).show();
                            if (imageUri != null) {
                                // After group is successfully added, upload the image
                                uploadGroupImageToFirebaseStorage(imageUri, newGroup.getGroupName(), groupRef);
                            }
                        } else {
                            Toast.makeText(getContext(), "Failed to add group: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    // Group name already exists
                    Toast.makeText(getContext(), "Group Name already in use.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getContext(), "Database error: " + databaseError.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void uploadGroupImageToFirebaseStorage(Uri imageUri, String groupName, DatabaseReference groupRef) {
        StorageReference imageRef = FirebaseStorage.getInstance().getReference("group_images/" + groupName + ".jpg");

        imageRef.putFile(imageUri).addOnSuccessListener(taskSnapshot -> {
            imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                // Update the group's profile image URL in the database
                groupRef.child("groupProfileImg").setValue(uri.toString()).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(getContext(), "Group image uploaded successfully", Toast.LENGTH_SHORT).show();
                        if (getActivity() != null) {
                            getActivity().runOnUiThread(() -> {
                                // Get the ImageView reference from the activity layout
                                ImageView bannerImageView = getActivity().findViewById(R.id.bannerImageView);

                                if (bannerImageView != null) {
                                    Glide.with(getActivity()).load(uri.toString()).into(bannerImageView);
                                }
                            });
                        }
                    } else {
                        Toast.makeText(getContext(), "Failed to upload group image: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }).addOnFailureListener(e -> {
                Toast.makeText(getContext(), "Image upload failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
            });
        });
    }



    private void showFindUsersDialog() {
        FindUsersDialogFragment dialogFragment = new FindUsersDialogFragment();

        // Set this fragment as the listener for user selection events
        dialogFragment.setOnUsersSelectedListener(this);

        dialogFragment.show(getChildFragmentManager(), "FindUsersDialog");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    public void onUsersSelected(List<String> selectedUsers) {
        members.addAll(selectedUsers);

        updateSelectedMembersUI(selectedUsers);
    }

    private void updateSelectedMembersUI(List<String> selectedUsers) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Selected members:\n");
        for (int i = 0; i < selectedUsers.size(); i++) {
            stringBuilder.append(selectedUsers.get(i));
            if (i < selectedUsers.size() - 1) {
                // Append comma if not the last user
                stringBuilder.append(", ");
            }
        }
        membersTV.setText(stringBuilder.toString());
    }

    private String getCurrentDate() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        return dateFormat.format(calendar.getTime());
    }
}