package edu.northeastern.new_final.ui.personalGoal;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class PersonalGoalViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public PersonalGoalViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is the add personal goal fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}