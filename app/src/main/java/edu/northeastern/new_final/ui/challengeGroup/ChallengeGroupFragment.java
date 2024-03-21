package edu.northeastern.new_final.ui.challengeGroup;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import edu.northeastern.new_final.databinding.FragmentChallengeGroupBinding;


public class ChallengeGroupFragment extends Fragment {

    private FragmentChallengeGroupBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        ChallengeGroupViewModel challengeGroupViewModel =
                new ViewModelProvider(this).get(ChallengeGroupViewModel.class);

        binding = FragmentChallengeGroupBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textChallengeGroup;
        challengeGroupViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}