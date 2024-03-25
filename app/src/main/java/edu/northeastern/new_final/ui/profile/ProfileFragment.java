package edu.northeastern.new_final.ui.profile;

import edu.northeastern.new_final.R;
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

import android.util.DisplayMetrics;
import android.widget.ImageView;

import edu.northeastern.new_final.databinding.FragmentProfileBinding;

public class ProfileFragment extends Fragment {

    private FragmentProfileBinding binding;
    private FragmentContainerView fragmentContainer;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        ProfileViewModel profileViewModel =
                new ViewModelProvider(this).get(ProfileViewModel.class);

        binding = FragmentProfileBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textProfile;
        profileViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);



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

        // Set onClickListeners for bottom bar nav buttons
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

