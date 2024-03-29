package edu.northeastern.new_final.ui.personalGoal;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class PersonalGoalViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public PersonalGoalViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("");
    }

    public LiveData<String> getText() {
        return mText;
    }
}