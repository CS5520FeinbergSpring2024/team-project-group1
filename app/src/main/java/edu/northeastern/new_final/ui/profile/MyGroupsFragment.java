package edu.northeastern.new_final.ui.profile;

import androidx.lifecycle.ViewModelProvider;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import edu.northeastern.new_final.R;

public class MyGroupsFragment extends Fragment {

    private MyGroupsViewModel mViewModel;

    String username;
    private RecyclerView recyclerView;
    private GroupAdapter adapter;
    private TextView textViewNoGroups;

    public static MyGroupsFragment newInstance() {
        return new MyGroupsFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_groups, container, false);

        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        username = sharedPreferences.getString("email", null);

        // Ensure we handle the case where the email (username) is null
        if (username == null) {
            return view;
        }



        textViewNoGroups = view.findViewById(R.id.textViewNoGroups);

        // Initialize RecyclerView and Adapter
        recyclerView = view.findViewById(R.id.rv_groups);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));
        adapter = new GroupAdapter(new ArrayList<>());
        recyclerView.setAdapter(adapter);


        fetchUserGroups();


        return view;
    }


    private void fetchUserGroups() {
        String sanitizedUsername = username.replace(".", "_");

        DatabaseReference groupsRef = FirebaseDatabase.getInstance().getReference().child("groups");


        // Add value event listener to pull group info from db
        groupsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<Group> groupList = new ArrayList<>();
                for(DataSnapshot groupSnapshot : dataSnapshot.getChildren()) {

                    // Check if current user is a member of this group
                    if (groupSnapshot.child("members").hasChild(sanitizedUsername)) {

                        // Extract group data from the snapshot
                        String groupName = groupSnapshot.child("groupName").getValue(String.class);
                        String groupProfileImg = groupSnapshot.child("groupProfileImg").getValue(String.class);
                        String energyPoints = String.valueOf(groupSnapshot.child("amount").getValue(Long.class));

                        // Create a Group object and add it to the list
                        Group group = new Group(groupName, groupProfileImg, energyPoints);
                        groupList.add(group);
                    }
                }

                // Update the RecyclerView with the retrieved group list
                adapter.setGroupList(groupList);


                // Check if list is empty
                if (groupList.isEmpty()) {
                    textViewNoGroups.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                } else {
                    textViewNoGroups.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(),"Error retrieving group information.",Toast.LENGTH_SHORT);

            }
        });


    }



    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(MyGroupsViewModel.class);
        // TODO: Use the ViewModel
    }

}