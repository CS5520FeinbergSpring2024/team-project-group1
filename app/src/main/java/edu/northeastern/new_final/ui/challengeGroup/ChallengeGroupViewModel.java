package edu.northeastern.new_final.ui.challengeGroup;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ChallengeGroupViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public ChallengeGroupViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is the challenge group fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}