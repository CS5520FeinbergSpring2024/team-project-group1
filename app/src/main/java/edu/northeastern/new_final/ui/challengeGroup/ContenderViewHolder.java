package edu.northeastern.new_final.ui.challengeGroup;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import edu.northeastern.new_final.R;
import edu.northeastern.new_final.ui.profile.HistoricalWorkout;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import javax.annotation.Nonnull;

public class ContenderViewHolder extends RecyclerView.ViewHolder {

    ImageView memberImage;

    TextView memberName;

    TextView memberAmount;

    public ContenderViewHolder(@Nonnull View itemView) {
        super(itemView);

        //initialize views
        memberImage = itemView.findViewById(R.id.userProfileImg);
        memberName = itemView.findViewById(R.id.username);
        memberAmount = itemView.findViewById(R.id.userPoints);
    }

    // Bind data to views
    public void bind(Contender contender) {
        memberName.setText(contender.getMemberName());
        memberAmount.setText(contender.getMemberAmount());
        Glide.with(itemView.getContext())
                .load(contender.getProfileImageUrl())
                .placeholder(R.drawable.profile_icon) // A default placeholder if needed
                .error(R.drawable.profile_icon) // An error placeholder in case the URL fetch fails
                .into(memberImage);
    }
}
