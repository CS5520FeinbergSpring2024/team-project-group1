package edu.northeastern.new_final.ui.profile;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.view.View;
import android.widget.TextView;

import org.w3c.dom.Text;

import edu.northeastern.new_final.R;

public class PersonalGoalViewHolder extends RecyclerView.ViewHolder {

    TextView activity;
    TextView metric_amount;
    TextView metric_type;
    TextView date;

    public PersonalGoalViewHolder(@NonNull View itemView) {
        super(itemView);

        //initialize views
        activity = itemView.findViewById(R.id.activityType);
        metric_amount = itemView.findViewById(R.id.amount);
        metric_type = itemView.findViewById(R.id.metric);
        date = itemView.findViewById(R.id.dueDate);

    }

    // Bind data to views
    public void bind(PersonalGoal personalGoal) {
        activity.setText(personalGoal.getActivity());
        metric_amount.setText(personalGoal.getMetricAmount());
        metric_type.setText(personalGoal.getMetricType());
        date.setText(personalGoal.getDate());
    }
}


