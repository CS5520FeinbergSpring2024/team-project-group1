package edu.northeastern.new_final.ui.logWorkout;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import edu.northeastern.new_final.R;

public class LogWorkoutViewModel extends ViewModel {
    private final MutableLiveData<Integer> layout;

    public LogWorkoutViewModel() {
        layout = new MutableLiveData<>();
        layout.setValue(R.layout.log_workout);
    }

    public LiveData<Integer> getLayout() {
        return layout;
    }
}
