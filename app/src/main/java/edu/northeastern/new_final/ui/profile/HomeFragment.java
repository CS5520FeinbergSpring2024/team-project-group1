package edu.northeastern.new_final.ui.profile;

import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import edu.northeastern.new_final.LogWorkoutActivity;
import edu.northeastern.new_final.R;
import edu.northeastern.new_final.ui.logWorkout.LogWorkoutFragment;

public class HomeFragment extends Fragment {

    private HomeViewModel mViewModel;

    public static HomeFragment newInstance() {
        return new HomeFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        // TODO: Use the ViewModel
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Button buttonLogWorkout = view.findViewById(R.id.button_logWorkout); // Assuming the button ID is button_log_workout
        buttonLogWorkout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startLogWorkoutActivity(v);
            }
        });
    }



    public void startLogWorkoutActivity(View view) {
        Intent intent = new Intent(getContext(), LogWorkoutActivity.class);
        startActivity(intent);
    }



}