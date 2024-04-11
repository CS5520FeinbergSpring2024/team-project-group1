package edu.northeastern.new_final.ui.challengeGroup;

import android.widget.TextView;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import edu.northeastern.new_final.R;

public class UserViewHolder extends RecyclerView.ViewHolder  {

    private TextView userNameTextView;

    public UserViewHolder(@NonNull View itemView) {
        super(itemView);
        userNameTextView = itemView.findViewById(R.id.username_tv);
    }


    public void bind(String user) {
        userNameTextView.setText(user);
    }
}
