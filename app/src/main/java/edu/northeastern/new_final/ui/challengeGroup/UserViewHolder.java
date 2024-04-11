package edu.northeastern.new_final.ui.challengeGroup;

import android.graphics.Color;
import android.widget.TextView;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import edu.northeastern.new_final.R;

public class UserViewHolder extends RecyclerView.ViewHolder  {

    private TextView userNameTextView;
    private CardView cardView;

    public UserViewHolder(@NonNull View itemView) {
        super(itemView);
        userNameTextView = itemView.findViewById(R.id.username_tv);
        cardView = itemView.findViewById(R.id.cardview_users);
    }


    public void bind(String user) {
        userNameTextView.setText(user);
    }


    public void setSelected(boolean isSelected) {
        if (isSelected) {
            cardView.setCardBackgroundColor(Color.LTGRAY);
        } else {
            cardView.setCardBackgroundColor(Color.parseColor("#D0EEC7"));
        }
    }

}
