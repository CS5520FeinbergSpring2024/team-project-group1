package edu.northeastern.new_final.ui.challengeGroup;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import edu.northeastern.new_final.R;

import androidx.recyclerview.widget.RecyclerView;

import javax.annotation.Nonnull;

public class GroupMessageViewHolder extends RecyclerView.ViewHolder {


    TextView username;

    TextView date;
    TextView message;

    public GroupMessageViewHolder(@Nonnull View itemView) {
        super(itemView);

        username = itemView.findViewById(R.id.usernameTextView);
        date = itemView.findViewById(R.id.dateTextView);
        message = itemView.findViewById(R.id.messageTextView);
    }

    public void bind(GroupMessage groupMessage) {
        username.setText(groupMessage.getUsername());
        String dateAndTime = groupMessage.getDate() + "  " + groupMessage.getTime();
        date.setText(dateAndTime);
        message.setText(groupMessage.getMessage());
    }
}
