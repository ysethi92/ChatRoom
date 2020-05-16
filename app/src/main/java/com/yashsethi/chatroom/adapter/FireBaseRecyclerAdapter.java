package com.yashsethi.chatroom.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseUser;
import com.yashsethi.chatroom.R;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.content.ContentValues.TAG;

public class FireBaseRecyclerAdapter extends RecyclerView.Adapter<FireBaseRecyclerAdapter.ViewHolder> {

    ArrayList<String> message;
    FirebaseUser name;
    String image;
    Context context;

    public FireBaseRecyclerAdapter(Context context, ArrayList<String> message, FirebaseUser name, String image) {
        super();
        this.context = context;
        this.message = message;
        this.name = name;
        this.image = image;
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
        holder.MessageText.setText(message.get(position));
        holder.NameText.setText(name.getDisplayName());
        //holder.Image.setImageResource(image.get(position));
        //Use glide.
    }

    @Override
    public int getItemCount() {
        return message.size();
    }

      class ViewHolder extends RecyclerView.ViewHolder {
        TextView MessageText;
        TextView NameText;
        CircleImageView Image;
        ViewHolder(@NonNull View itemView) {
            super(itemView);
            MessageText = (TextView) itemView.findViewById(R.id.message);
            NameText = (TextView) itemView.findViewById(R.id.name);
            Image = (CircleImageView) itemView.findViewById(R.id.profile_pic);
        }
    }
}
