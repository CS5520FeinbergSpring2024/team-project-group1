package edu.northeastern.new_final.ui.logWorkout;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import edu.northeastern.new_final.R;
import edu.northeastern.new_final.databinding.FragmentLogWorkoutBinding;


public class LogWorkoutFragment extends Fragment {

    private FragmentLogWorkoutBinding binding;
    private LogWorkoutViewModel logWorkoutViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentLogWorkoutBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        logWorkoutViewModel =
                new ViewModelProvider(this).get(LogWorkoutViewModel.class);

        // Observe the layout resource ID LiveData
        logWorkoutViewModel.getLayout().observe(getViewLifecycleOwner(), layoutResId -> {
            // Inflate the layout resource and add it to the fragment container
            View contentView = inflater.inflate(layoutResId, container, false);
            ViewGroup fragmentContainer = root.findViewById(R.id.logWorkoutContainer);
            fragmentContainer.removeAllViews(); // Remove existing views if any
            fragmentContainer.addView(contentView);
        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
