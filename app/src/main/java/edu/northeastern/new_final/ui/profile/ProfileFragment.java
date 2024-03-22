package edu.northeastern.new_final.ui.profile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.util.DisplayMetrics;
import android.widget.ImageView;

import edu.northeastern.new_final.databinding.FragmentProfileBinding;

public class ProfileFragment extends Fragment {

    private FragmentProfileBinding binding;

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



        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}