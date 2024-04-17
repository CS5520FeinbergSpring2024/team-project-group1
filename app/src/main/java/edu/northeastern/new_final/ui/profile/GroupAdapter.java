package edu.northeastern.new_final.ui.profile;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import edu.northeastern.new_final.R;
import edu.northeastern.new_final.ui.challengeGroup.ChallengeGroupMain;
import edu.northeastern.new_final.ui.challengeGroup.ChallengeGroupMessaging;

public class GroupAdapter  extends RecyclerView.Adapter<GroupViewHolder> {

    private List<Group> groupList;

    // Constructor
    public GroupAdapter(List<Group> groupList) {
        this.groupList = groupList;
    }

    @NonNull
    @Override
    public GroupViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the layout for each item
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.groups_cardview, parent, false);
        return new GroupViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull GroupViewHolder holder, int position) {
        // Bind data to each ViewHolder
        Group group = groupList.get(position);
        holder.bind(group);

        //Add OnClickListener to each card
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Start new activity for the selected group
                Intent intent = new Intent(v.getContext(), ChallengeGroupMain.class);
                intent.putExtra("groupName", group.getGroupName());
                v.getContext().startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        // Return the number of items in the RecyclerView
        return groupList.size();
    }

    // Method to set the personal goals list
    public void setGroupList(List<Group> groupList) {
        this.groupList = groupList;
        notifyDataSetChanged(); // Notify the adapter that the data set has changed
    }
}

