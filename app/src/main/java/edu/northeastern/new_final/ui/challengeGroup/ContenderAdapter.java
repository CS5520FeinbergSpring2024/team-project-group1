package edu.northeastern.new_final.ui.challengeGroup;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import edu.northeastern.new_final.R;

public class ContenderAdapter extends RecyclerView.Adapter<ContenderViewHolder> {

    private List<Contender> contenderList;

    public ContenderAdapter(List<Contender> contenderList) {
        this.contenderList = contenderList;
    }

    @NonNull
    @Override
    public ContenderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.leaderboard_card_view, parent, false);
        return new ContenderViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ContenderViewHolder holder, int position) {
        Contender contender = contenderList.get(position);
        holder.bind(contender);
    }

    @Override
    public int getItemCount(){
        return contenderList.size();
    }

    public void setContenderList(List<Contender> contenderList) {
        this.contenderList = contenderList;
        notifyDataSetChanged();
    }

}
