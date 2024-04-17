package edu.northeastern.new_final.ui.challengeGroup;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import edu.northeastern.new_final.R;

public class GroupMessageAdapter extends RecyclerView.Adapter<GroupMessageViewHolder> {

    private List<GroupMessage> messageList;

    public GroupMessageAdapter(List<GroupMessage> messageList) {
        this.messageList = messageList;
    }

    @NonNull
    @Override
    public GroupMessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_cardview, parent, false);
        return new GroupMessageViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull GroupMessageViewHolder holder, int position) {
        GroupMessage message = messageList.get(position);
        holder.bind(message);
    }

    @Override
    public int getItemCount(){
        return messageList.size();
    }

    public void setMessageList(List<GroupMessage> messageList) {
        this.messageList = messageList;
        notifyDataSetChanged();
    }

}