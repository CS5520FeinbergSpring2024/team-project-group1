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
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

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

        // Basic input validation
        if (email.isEmpty()) {
            editTextEmail.setError("Email is required");
            editTextEmail.requestFocus();
            return;
        }

        if (password.isEmpty()) {
            editTextPassword.setError("Password is required");
            editTextPassword.requestFocus();
            return;
        }

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("users");
        Query query = databaseReference.orderByChild("email").equalTo(email);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                        // Check if the password matches
                        if (userSnapshot.child("password").getValue(String.class).equals(password)) {
                            // Password matches, get the user ID
                            String userId = userSnapshot.getKey();

                            // Pass the user ID to MainActivity
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            intent.putExtra("userId", userId); // Pass the user ID to MainActivity
                            startActivity(intent);
                            finish();
                        } else {
                            // Password does not match
                            Toast.makeText(LoginActivity.this, "Invalid password", Toast.LENGTH_LONG).show();
                        }
                    }
                } else {
                    // User not found
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
        // Create a AlertDialog Builder.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        // Get the layout inflater
        LayoutInflater inflater = this.getLayoutInflater();

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because it's going in the dialog layout
        View dialogView = inflater.inflate(R.layout.dialog_signup, null);
        builder.setView(dialogView);

        EditText editTextDialogEmail = dialogView.findViewById(R.id.editTextDialogEmail);
        EditText editTextDialogPassword = dialogView.findViewById(R.id.editTextDialogPassword);
        Button buttonDialogSignup = dialogView.findViewById(R.id.buttonDialogSignup);

        Button buttonDialogCancel = dialogView.findViewById(R.id.buttonDialogCancel);

        // Create the dialog here so we can dismiss it in the button's OnClickListener
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

        // Set the negative/cancel button
        builder.setNegativeButton("Cancel", (dialogInterface, id) -> dialogInterface.cancel());

        // Show the dialog
        dialog.show();
    }

    private void signUpUser(String email, String password) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference usersRef = database.getReference("users");

        // Create a new user object or use HashMap
        HashMap<String, Object> user = new HashMap<>();
        user.put("email", email);
        user.put("password", password);

        // Generate a unique key for each new user or use something unique from the user like the email
        String userId = usersRef.push().getKey();
        // Basic validation
        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(LoginActivity.this, "Email and password are required.",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        // Assuming email is unique and using it as a key (you would need to sanitize the email as Firebase keys can't contain '.', '#', '$', '[', or ']')
        usersRef.child(userId).setValue(user)
                .addOnCompleteListener(this, task -> {
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
