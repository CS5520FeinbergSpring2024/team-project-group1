package edu.northeastern.new_final.ui.challengeGroup;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import edu.northeastern.new_final.R;
import edu.northeastern.new_final.databinding.FragmentChallengeGroupBinding;


public class ChallengeGroupFragment extends Fragment {
    private FragmentChallengeGroupBinding binding;
    private ChallengeGroupViewModel challengeGroupViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentChallengeGroupBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        challengeGroupViewModel =
                new ViewModelProvider(this).get(ChallengeGroupViewModel.class);

        // Observe the layout resource ID LiveData
        challengeGroupViewModel.getLayout().observe(getViewLifecycleOwner(), layoutResId -> {
            // Inflate the layout resource and add it to the fragment container
            View contentView = inflater.inflate(layoutResId, container, false);
            ViewGroup fragmentContainer = root.findViewById(R.id.challengeGroupContainer);
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