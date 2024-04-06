package edu.northeastern.new_final.ui.profile;

import edu.northeastern.new_final.R;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentContainerView;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import android.util.DisplayMetrics;
import android.widget.ImageView;

import edu.northeastern.new_final.databinding.FragmentProfileBinding;

public class ProfileFragment extends Fragment {

    private FragmentProfileBinding binding;
    private FragmentContainerView fragmentContainer;

    String username;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        ProfileViewModel profileViewModel =
                new ViewModelProvider(this).get(ProfileViewModel.class);

        binding = FragmentProfileBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textProfile;
        profileViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);

        // Retrieve email from shared preferences
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        String email = sharedPreferences.getString("email", "");

        // Set email to the textViewUsername
        TextView textViewUsername = binding.textViewUsername;
        textViewUsername.setText(email);

        //Section helps keep garden image at 2/3 of screen height (shows on emulator)
        // Get display metrics to calculate screen height
        DisplayMetrics displayMetrics = new DisplayMetrics();
        requireActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int screenHeight = displayMetrics.heightPixels;

        // Calculate height of the image to be two-thirds of the screen height
        int imageHeight = (int) (screenHeight * 0.55);

        // Get reference to the ImageView and adjust its height
        ImageView imageView = binding.imageViewGarden;
        ViewGroup.LayoutParams layoutParams = imageView.getLayoutParams();
        layoutParams.height = imageHeight;
        imageView.setLayoutParams(layoutParams);


        //Section for profile sub-fragments (Home, My Groups, Workout History)
        // Find fragment container
        fragmentContainer = root.findViewById(R.id.fragment_container);

        // Set initial fragment
        loadFragment(new HomeFragment());

        // Set onClickListeners for bottom bar nav buttons. Programmatically change button color.
        Button buttonHome = root.findViewById(R.id.button_home);
        Button buttonGroups = root.findViewById(R.id.button_groups);
        Button buttonWorkouts = root.findViewById(R.id.button_workouts);

        buttonHome.setOnClickListener(v -> {
            loadFragment(new HomeFragment());
            buttonHome.setBackgroundColor(ContextCompat.getColor(requireContext(),R.color.purple_pastel));
            buttonGroups.setBackgroundColor(ContextCompat.getColor(requireContext(),R.color.white));
            buttonWorkouts.setBackgroundColor(ContextCompat.getColor(requireContext(),R.color.white));
        });

        buttonGroups.setOnClickListener(v -> {
            loadFragment(new MyGroupsFragment());
            buttonHome.setBackgroundColor(ContextCompat.getColor(requireContext(),R.color.white));
            buttonGroups.setBackgroundColor(ContextCompat.getColor(requireContext(),R.color.purple_pastel));
            buttonWorkouts.setBackgroundColor(ContextCompat.getColor(requireContext(),R.color.white));
        });

        buttonWorkouts.setOnClickListener(v -> {
            loadFragment(new WorkoutHistoryFragment());
            buttonHome.setBackgroundColor(ContextCompat.getColor(requireContext(),R.color.white));
            buttonGroups.setBackgroundColor(ContextCompat.getColor(requireContext(),R.color.white));
            buttonWorkouts.setBackgroundColor(ContextCompat.getColor(requireContext(),R.color.purple_pastel));
        });



        username = sharedPreferences.getString("email", null);
        String sanitizedUsername = username.replace(".", "_");

        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference();
        DatabaseReference userEPRef = databaseRef.child("users").child(sanitizedUsername).child("total_EP");

        userEPRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d("ProfileFragment", "11111111");
                if (dataSnapshot.exists()) {
                    Log.d("ProfileFragment", "2222222222");
                    Integer totalEP = dataSnapshot.getValue(Integer.class);
                    if (totalEP != null) {
                        Log.d("ProfileFragment", "total_EP: " + totalEP); // Debugging log
                        // Using ViewBinding to find the ImageView
                        ImageView imageViewGarden = binding.imageViewGarden;
                        if (totalEP >= 0 && totalEP <= 30) {
                            imageViewGarden.setImageResource(R.drawable.stage1);
                        } else if (totalEP > 30 && totalEP <= 100) {
                            imageViewGarden.setImageResource(R.drawable.stage2);
                        } else if (totalEP > 100 && totalEP <= 300) {
                            imageViewGarden.setImageResource(R.drawable.stage3);
                        } else if (totalEP > 300) {
                            imageViewGarden.setImageResource(R.drawable.stage4);
                        }
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle error case
                Log.w("TAG", "loadTotalEP:onCancelled", databaseError.toException());
            }
        });










        return root;
    }


    private void loadFragment(Fragment fragment) {
        Log.d("ProfileFragment", "Loading fragment: " + fragment.getClass().getSimpleName());
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container,fragment);
        transaction.commit();
        Log.d("ProfileFragment", "Fragment loaded successfully");
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}

