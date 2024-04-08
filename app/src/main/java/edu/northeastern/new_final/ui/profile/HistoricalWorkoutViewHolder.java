package edu.northeastern.new_final.ui.profile;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import edu.northeastern.new_final.R;


public class HistoricalWorkoutViewHolder extends RecyclerView.ViewHolder {

    TextView activity;
    TextView amount;
    TextView amountCategory;
    TextView date;
    TextView energyPoints;

    ImageView activityIcon;

    public HistoricalWorkoutViewHolder(@NonNull View itemView) {
        super(itemView);

        //initialize views
        activityIcon = itemView.findViewById(R.id.activityIcon);
        activity = itemView.findViewById(R.id.activityType);
        amount = itemView.findViewById(R.id.amount);
        amountCategory = itemView.findViewById(R.id.metric);
        date = itemView.findViewById(R.id.dueDate);
        energyPoints = itemView.findViewById(R.id.EP_amount);

    }

    // Bind data to views
    public void bind(HistoricalWorkout historicalWorkout) {
        activity.setText(historicalWorkout.getActivity());
        amount.setText(historicalWorkout.getAmount());
        amountCategory.setText(historicalWorkout.getAmountCategory());
        date.setText(historicalWorkout.getDate());
        energyPoints.setText(historicalWorkout.getEnergyPoints());
    }
}




