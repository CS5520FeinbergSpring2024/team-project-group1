package edu.northeastern.new_final.ui.personalGoal;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;


import edu.northeastern.new_final.databinding.FragmentPersonalGoalBinding;

public class PersonalGoalFragment extends Fragment {

    private FragmentPersonalGoalBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        PersonalGoalViewModel personalGoalViewModel =
                new ViewModelProvider(this).get(PersonalGoalViewModel.class);

        binding = FragmentPersonalGoalBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textPersonalGoal;
        personalGoalViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}