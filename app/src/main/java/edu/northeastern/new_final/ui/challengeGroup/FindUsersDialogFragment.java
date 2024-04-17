package edu.northeastern.new_final.ui.challengeGroup;

import static com.google.common.collect.ComparisonChain.start;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import edu.northeastern.new_final.R;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class FindUsersDialogFragment extends DialogFragment implements UserAdapter.SelectionChangeListener {

    private EditText searchEditText;
    private Button searchButton;
    private RecyclerView usersRecyclerView;
    private UserAdapter userAdapter;

    private TextView selectedUsersTV;

    private String currentUser;

    private List<String> userList = new ArrayList<>();

    private DatabaseReference usersRef;

    private OnUsersSelectedListener mListener;

    private String searchTerm;

    private TextView clearButton;
    private Button addButton;
    public interface OnUsersSelectedListener {
        void onUsersSelected(List<String> selectedUsers);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.find_users_dialog, container, false);

        // Initialize views
        searchEditText = view.findViewById(R.id.searchEditText);
        searchButton = view.findViewById(R.id.searchButton);
        usersRecyclerView = view.findViewById(R.id.usersRecyclerView);
        selectedUsersTV = view.findViewById(R.id.selectedUsersTV);
        addButton = view.findViewById(R.id.addButton);
        clearButton = view.findViewById(R.id.buttonClear);

        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        currentUser = sharedPreferences.getString("email", null);

        // Set up RecyclerView
        userAdapter = new UserAdapter(userList, currentUser, this);
        usersRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        usersRecyclerView.setAdapter(userAdapter);

        searchTerm = "";

        // Call firebase method
        new Thread(() -> fetchUserList(searchTerm)).start();



        // Search button click listener
        searchButton.setOnClickListener(v -> {
            searchTerm = searchEditText.getText().toString().trim();


            new Thread(() -> fetchUserList(searchTerm)).start();
        });

        // Clear Search button click listener
        clearButton.setOnClickListener(v -> {
            searchTerm = "";

            new Thread(() -> fetchUserList(searchTerm)).start();
        });


        // Add button click listener
        addButton.setOnClickListener(v -> {
            // Call the listener with the selected users list
            mListener.onUsersSelected(userAdapter.getSelectedUsers());
            // Dismiss the dialog
            dismiss();
        });

        return view;
    }

    private void fetchUserList(String searchTerm) {
        // Initialize Firebase Database reference
        usersRef = FirebaseDatabase.getInstance().getReference().child("users");

        // Clear the userList before fetching new users
        userList.clear();

        // Retrieve user data from Firebase
        usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    String username = userSnapshot.getKey(); // Get the username from the Firebase snapshot
                    if (!username.equals(currentUser)) { // Exclude the current user
                        if (searchTerm.isEmpty() || username.toLowerCase().contains(searchTerm.toLowerCase())) {
                            // If the search term is empty or matches the username, add it to the userList
                            userList.add(username);
                        }
                    }
                }
                userAdapter.notifyDataSetChanged(); // Notify the adapter of data set change
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle database error
                Toast.makeText(getActivity(), "Failed to retrieve users: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onSelectionChanged() {
        updateSelectedUsersUI();

        // Pass the selected users back to the listener
        if (getActivity() instanceof OnUsersSelectedListener) {
            ((OnUsersSelectedListener) getActivity()).onUsersSelected(userAdapter.getSelectedUsers());
        }
    }

    private void updateSelectedUsersUI() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Selected users: ");
        List<String> selectedUsers = userAdapter.getSelectedUsers();
        int size = selectedUsers.size();
        for (int i = 0; i < size; i++) {
            stringBuilder.append(selectedUsers.get(i));
            if (i < size - 1) {
                // Append comma if not the last user
                stringBuilder.append(", ");
            }
        }
        selectedUsersTV.setText(stringBuilder.toString());
    }

    public void setOnUsersSelectedListener(OnUsersSelectedListener listener) {
        this.mListener = listener;
    }

}

