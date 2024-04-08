package edu.northeastern.new_final.ui.profile;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import edu.northeastern.new_final.R;

public class GroupViewHolder extends RecyclerView.ViewHolder {

    TextView groupName;
    TextView imageName;

    TextView energyPoints;



    public GroupViewHolder(@NonNull View itemView) {
        super(itemView);

        //initialize views
        groupName = itemView.findViewById(R.id.textView_groupName);
        //imageName = itemView.findViewById(R.id.imageView_groupIcon);
        energyPoints = itemView.findViewById(R.id.textView_EP);

    }

    // Bind data to views
    public void bind(Group group) {
        groupName.setText(group.getGroupName());
        //imageName.setText(group.getImageName());
        energyPoints.setText(group.getEnergyPoints());
    }
}
