package com.sandeep.smsspamfilter.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.bumptech.glide.request.target.ImageViewTarget;
import com.bumptech.glide.request.transition.Transition;
import com.sandeep.smsspamfilter.R;
import com.sandeep.smsspamfilter.model.Message;

import java.util.List;

public class SMSAdapter extends RecyclerView.Adapter<SMSAdapter.SMSViewHolder> {

    private List<Message> smsList;
    private OnItemClickListener clickListener;

    public SMSAdapter(List<Message> smsList) {
        this.smsList = smsList;
    }

    public void setSmsList(List<Message> smsList) {
        this.smsList = smsList;
    }

    public void setOnItemClickListener(OnItemClickListener clickListener) {
        this.clickListener = clickListener;
    }

    @NonNull
    @Override
    public SMSViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_sms, parent, false);
        return new SMSViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull SMSViewHolder holder, int position) {
        Message sms = smsList.get(holder.getAdapterPosition());
        holder.messageTextView.setText(sms.getBody());
        holder.addressTextView.setText(sms.getAddress());
        switch (sms.getResult())
        {
            case -1: holder.status.setImageResource(R.drawable.unknown);
            break;
            case 0: holder.status.setImageResource(R.drawable.accept);
            break;
            case 1: holder.status.setImageResource(R.drawable.multiply);
            break;
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (clickListener != null) {
                    clickListener.onItemClick(v, holder.getAdapterPosition(),holder.status);
                }
            }
        });
    }


    @Override
    public int getItemCount() {
        return smsList.size();
    }

    static class SMSViewHolder extends RecyclerView.ViewHolder {
        TextView messageTextView;
        TextView addressTextView;

        ImageView status;

        SMSViewHolder(View itemView) {
            super(itemView);
            messageTextView = itemView.findViewById(R.id.messageTextView);
            addressTextView = itemView.findViewById(R.id.addressTextView);
            status = itemView.findViewById(R.id.statusView);
        }
    }


    public interface OnItemClickListener {
        void onItemClick(View view, int position,ImageView imageView);
    }
}
