package edu.northeastern.new_final.ui.profile;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import edu.northeastern.new_final.R;

public class PersonalGoalAdapter extends RecyclerView.Adapter<PersonalGoalViewHolder>{

    private List<PersonalGoal> personalGoalsList;

    // Constructor
    public PersonalGoalAdapter(List<PersonalGoal> personalGoalsList) {
        this.personalGoalsList = personalGoalsList;
    }

    @NonNull
    @Override
    public PersonalGoalViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the layout for each item
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.personal_goal_cardview, parent, false);
        return new PersonalGoalViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull PersonalGoalViewHolder holder, int position) {
        // Bind data to each ViewHolder
        PersonalGoal personalGoal = personalGoalsList.get(position);
        holder.bind(personalGoal);
    }

    @Override
    public int getItemCount() {
        // Return the number of items in the RecyclerView
        return personalGoalsList.size();
    }

    // Method to set the personal goals list
    public void setPersonalGoalsList(List<PersonalGoal> personalGoalsList) {
        this.personalGoalsList = personalGoalsList;
        notifyDataSetChanged(); // Notify the adapter that the data set has changed
    }
}

