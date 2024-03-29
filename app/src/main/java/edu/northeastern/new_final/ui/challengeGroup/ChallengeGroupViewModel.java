package edu.northeastern.new_final.ui.challengeGroup;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import edu.northeastern.new_final.R;

public class ChallengeGroupViewModel extends ViewModel {

    private final MutableLiveData<Integer> layout;

    public ChallengeGroupViewModel() {
        layout = new MutableLiveData<>();
        layout.setValue(R.layout.create_group);
    }

    public LiveData<Integer> getLayout() {
        return layout;
    }
}