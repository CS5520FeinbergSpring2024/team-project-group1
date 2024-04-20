package edu.northeastern.new_final.ui;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;


import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import edu.northeastern.new_final.MainActivity; // Make sure this import matches the location of your MainActivity
import edu.northeastern.new_final.R;

public class LoginActivity extends AppCompatActivity {

    private Button buttonLogin;
    private Button buttonSignup;

    private EditText editTextEmail, editTextPassword;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        buttonSignup = findViewById(R.id.buttonSignUp);
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        buttonLogin = findViewById(R.id.buttonLogin);
        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signInUser();
            }
        });
        buttonSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSignUpDialog();
            }
        });
    }

    private void signInUser() {
        final String email = editTextEmail.getText().toString().trim();
        final String password = editTextPassword.getText().toString().trim();


        if (email.isEmpty()) {
            editTextEmail.setError("Email is required");
            editTextEmail.requestFocus();
            return;
        }

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("users");
        Query query = databaseReference.orderByChild("email").equalTo(email);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {


                        String userId = userSnapshot.getKey();
                        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("email", email);
                        editor.apply();

                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        intent.putExtra("userId", userId);
                        startActivity(intent);
                        finish();

                    }
                } else {

                    Toast.makeText(LoginActivity.this, "User not found", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(LoginActivity.this, "Database error: " + databaseError.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void showSignUpDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        LayoutInflater inflater = this.getLayoutInflater();



        View dialogView = inflater.inflate(R.layout.dialog_signup, null);
        builder.setView(dialogView);

        EditText editTextDialogEmail = dialogView.findViewById(R.id.editTextDialogEmail);
        EditText editTextDialogPassword = dialogView.findViewById(R.id.editTextDialogPassword);
        Button buttonDialogSignup = dialogView.findViewById(R.id.buttonDialogSignup);

        Button buttonDialogCancel = dialogView.findViewById(R.id.buttonDialogCancel);

        final AlertDialog dialog = builder.create();

        buttonDialogCancel.setOnClickListener(v -> dialog.dismiss());
        // Set the custom sign up button click listener
        buttonDialogSignup.setOnClickListener(v -> {
            String email = editTextDialogEmail.getText().toString().trim();
            String password = editTextDialogPassword.getText().toString().trim();
            // Input validation and sign up logic can go here
            signUpUser(email, password);
            // Dismiss the dialog here
            dialog.dismiss();
        });
        builder.setNegativeButton("Cancel", (dialogInterface, id) -> dialogInterface.cancel());
        dialog.show();
    }

    private void signUpUser(String email, String password) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference usersRef = database.getReference("users");
        String sanitizedEmail = email.replace(".", "_").replace("#", "_")
                .replace("$", "_").replace("[", "_").replace("]", "_");
        DatabaseReference userRef = usersRef.child(sanitizedEmail);
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Email already exists
                    Toast.makeText(LoginActivity.this, "Email already in use.",
                            Toast.LENGTH_SHORT).show();
                } else {

                    HashMap<String, Object> user = new HashMap<>();
                    user.put("email", email);
                    user.put("password", password);
                    user.put("total_EP", 0);
                    user.put("workout_history", new ArrayList<>());
                    user.put("team_id", new ArrayList<>());

                    // for demo purpose only!!
                    HashMap<String, Object> goalDetails = new HashMap<>();
                    goalDetails.put("activity", "Demo");
                    goalDetails.put("metric_type", "0");
                    goalDetails.put("metric_amount", "0");
                    goalDetails.put("date", "0");

                    HashMap<String, Object> personalGoals = new HashMap<>();
                    personalGoals.put("0", goalDetails); // Use "0" as a key for the goal details map
                    user.put("personalGoals", personalGoals);


                    // Basic validation
                    if (email.isEmpty() || password.isEmpty()) {
                        Toast.makeText(LoginActivity.this, "Email and password are required.",
                                Toast.LENGTH_SHORT).show();
                        return;
                    }
                    userRef.setValue(user)
                            .addOnCompleteListener(LoginActivity.this, task -> {
                                if (task.isSuccessful()) {
                                    Toast.makeText(LoginActivity.this, "Signed up successfully",
                                            Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(LoginActivity.this, "Failed to sign up user: " + task.getException().getMessage(),
                                            Toast.LENGTH_SHORT).show();
                                }
                            });
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(LoginActivity.this, "Database error: " + databaseError.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

}
