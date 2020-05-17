package com.yashsethi.chatroom.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.yashsethi.chatroom.R;
import com.yashsethi.chatroom.bean.MessageContainer;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.content.ContentValues.TAG;

public class FireBaseRecyclerAdapter extends RecyclerView.Adapter<FireBaseRecyclerAdapter.ViewHolder> {

    ArrayList<MessageContainer> messageContainerArrayList;
    Context context;

    public FireBaseRecyclerAdapter(Context context, ArrayList<MessageContainer> messageContainerArrayList) {
        super();
        this.context = context;
        this.messageContainerArrayList = messageContainerArrayList;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.d(TAG , "viewHolder OnCreateViewHolder");
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item , parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.MessageText.setText(messageContainerArrayList.get(position).getMessage());
        holder.NameText.setText(messageContainerArrayList.get(position).getName());
        holder.TimeStamp.setText(getDate(Long.parseLong(messageContainerArrayList.get(position).getTimeStamp())));
        Glide.with(this.context)
                .load(messageContainerArrayList.get(position).getImage())
                .into(holder.Image);
    }

    @Override
    public int getItemCount() {
        return messageContainerArrayList.size();
    }

      class ViewHolder extends RecyclerView.ViewHolder {
        TextView MessageText;
          TextView NameText, TimeStamp;
        CircleImageView Image;
        ViewHolder(@NonNull View itemView) {
            super(itemView);
            MessageText = (TextView) itemView.findViewById(R.id.message);
            NameText = (TextView) itemView.findViewById(R.id.name);
            Image = (CircleImageView) itemView.findViewById(R.id.profile_pic);
            TimeStamp = (TextView) itemView.findViewById(R.id.timeStamp);
        }
    }

    private String getDate(long time) {
        Date date = new Date(time);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm");
        return simpleDateFormat.format(date);
    }
}
