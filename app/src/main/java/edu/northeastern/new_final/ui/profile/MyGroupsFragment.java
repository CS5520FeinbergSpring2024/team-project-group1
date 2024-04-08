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
            // Handle the error - perhaps redirect to login screen or show a message
            return view;
        }

        String sanitizedUsername = username.replace(".", "_");

        textViewNoGroups = view.findViewById(R.id.textViewNoGroups);

        // Initialize RecyclerView and Adapter
        recyclerView = view.findViewById(R.id.rv_groups);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));
        adapter = new GroupAdapter(new ArrayList<>());
        recyclerView.setAdapter(adapter);


        // Start of Dummy Data section
        List<Group> dummyGroupList = new ArrayList<>();

        dummyGroupList.add(new Group("Group 1", "image1", "100"));
        dummyGroupList.add(new Group("Group 2", "image2", "200"));
        dummyGroupList.add(new Group("Group 3", "image3", "300"));
        dummyGroupList.add(new Group("Group 4", "image4", "400"));
        dummyGroupList.add(new Group("Group 5", "image5", "500"));
        dummyGroupList.add(new Group("Group 6", "image6", "600"));
        dummyGroupList.add(new Group("Group 7", "image7", "700"));
        dummyGroupList.add(new Group("Group 8", "image8", "800"));
        dummyGroupList.add(new Group("Group 9", "image9", "900"));

        //Check if list is empty
        if (dummyGroupList.isEmpty()) {

            textViewNoGroups.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else{
            // Hide the message and show the RecyclerView
            textViewNoGroups.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);

            adapter.setGroupList(dummyGroupList); }
        // End of Dummy Data section


        //Untested real firebase data pull section
        /*
        // Get reference to the user's groups in the database
        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference();
        DatabaseReference groupsReference = FirebaseDatabase.getInstance().getReference()
                .child("users").child(sanitizedUsername).child("groups");


        // Add a ValueEventListener to retrieve group info from the database
        groupsReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<Group> groupList = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                    // Extract values from the snapshot and convert them to strings
                    String groupName = snapshot.child("name").getValue(String.class);
                    String imageName = snapshot.child("imageName").getValue(String.class);
                    String energyPoints = String.valueOf(snapshot.child("energyPoints").getValue(Long.class));

                    // Add "ep"
                    energyPoints = energyPoints + "ep";

                    // Create a Group object manually and add it to the list
                    Group group = new Group(groupName, imageName, energyPoints);
                    groupList.add(group);


                    //Check if list is empty
                    if (groupList.isEmpty()) {

                        textViewNoGroups.setVisibility(View.VISIBLE);
                        recyclerView.setVisibility(View.GONE);
                    } else{
                        // Hide the message and show the RecyclerView
                        textViewNoGroups.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.VISIBLE);

                        adapter.setGroupList(groupList); }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle database error
            }
        });

         */



        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(MyGroupsViewModel.class);
        // TODO: Use the ViewModel
    }

}