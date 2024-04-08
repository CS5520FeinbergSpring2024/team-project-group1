package edu.northeastern.new_final.ui.profile;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import edu.northeastern.new_final.R;


public class HistoricalWorkoutAdapter extends RecyclerView.Adapter<HistoricalWorkoutViewHolder> {

    private List<HistoricalWorkout> historicalWorkoutList;

    // Constructor
    public HistoricalWorkoutAdapter(List<HistoricalWorkout> historicalWorkoutList) {
        this.historicalWorkoutList = historicalWorkoutList;
    }

    @NonNull
    @Override
    public HistoricalWorkoutViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the layout for each item
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.workouts_cardview, parent, false);
        return new HistoricalWorkoutViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull HistoricalWorkoutViewHolder holder, int position) {
        // Bind data to each ViewHolder
        HistoricalWorkout historicalWorkout = historicalWorkoutList.get(position);
        holder.bind(historicalWorkout);

        // Check if the activity is "Run" and set the appropriate icon
        if ("Run".equals(historicalWorkout.getActivity())) {
            holder.activityIcon.setImageResource(R.drawable.run_icon);
        } else {
            // Set a default icon if the activity is not "Run"
            holder.activityIcon.setImageResource(R.drawable.walk_icon);
        }
    }

    @Override
    public int getItemCount() {
        // Return the number of items in the RecyclerView
        return historicalWorkoutList.size();
    }

    // Method to set the personal goals list
    public void setHistoricalWorkoutList(List<HistoricalWorkout> historicalWorkoutList) {
        this.historicalWorkoutList = historicalWorkoutList;
        notifyDataSetChanged(); // Notify the adapter that the data set has changed
    }
}
