package edu.northeastern.new_final.ui.challengeGroup;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import edu.northeastern.new_final.R;

public class UserAdapter extends RecyclerView.Adapter<UserViewHolder> {

    private List<String> userList;
    private List<String> selectedUsers;


    public UserAdapter(List<String> userList) {
        this.userList = userList;
        this.selectedUsers = new ArrayList<>();

    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.users_cardview, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        String user = userList.get(position);
        holder.bind(user);


        // Set selected state
        holder.itemView.setSelected(selectedUsers.contains(user));

        // Toggle selection on click
        holder.itemView.setOnClickListener(v -> {
            toggleSelection(user);
            notifyItemChanged(position);
        });
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public void setUserList(List<String> userList) {
        this.userList = userList;
        notifyDataSetChanged();
    }


    public List<String> getSelectedUsers() {
        return selectedUsers;
    }

    private void toggleSelection(String user) {
        if (selectedUsers.contains(user)) {
            selectedUsers.remove(user);
        } else {
            selectedUsers.add(user);
        }
    }

}
