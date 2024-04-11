package edu.northeastern.new_final.ui.challengeGroup;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import edu.northeastern.new_final.R;

import java.util.ArrayList;
import java.util.List;

public class FindUsersDialogFragment extends DialogFragment {

    private EditText searchEditText;
    private Button searchButton;
    private RecyclerView usersRecyclerView;
    private UserAdapter userAdapter;

    // Dummy list of users (replace with your actual data)
    private List<String> userList = new ArrayList<>();



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.find_users_dialog, container, false);

        // Initialize views
        searchEditText = view.findViewById(R.id.searchEditText);
        searchButton = view.findViewById(R.id.searchButton);
        usersRecyclerView = view.findViewById(R.id.usersRecyclerView);

        // Set up RecyclerView
        userAdapter = new UserAdapter(userList);
        usersRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        usersRecyclerView.setAdapter(userAdapter);

        // Dummy list of users
        userList.add("User 1");
        userList.add("User 2");
        userList.add("User 3");
        userList.add("User 4");
        userList.add("User 5");
        userList.add("User 6");
        userList.add("User 7");
        userList.add("User 8");
        userList.add("User 9");
        userList.add("User 10");
        userList.add("User 11");

        // Notify the adapter that the data set has changed
        userAdapter.notifyDataSetChanged();

        // Search button click listener
        searchButton.setOnClickListener(v -> {
            String searchTerm = searchEditText.getText().toString().trim();
            // Perform search based on searchTerm
            // Update userList accordingly
            // Notify adapter of data set change
            // For example, you can show a Toast for now
            Toast.makeText(getActivity(), "Search not implemented yet", Toast.LENGTH_SHORT).show();
        });

        return view;
    }
}

