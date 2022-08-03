package com.socialdownloader.adapter;

import static com.socialdownloader.Common.Common.getSizeInReadableForm;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.socialdownloader.R;

import java.io.File;
import java.util.List;

public class DownloadsAdapter extends RecyclerView.Adapter<DownloadsAdapter.DownloadsHolder> {

    Context context;
    List<File> files;

    public DownloadsAdapter(Context context, List<File> files) {
        this.context = context;
        this.files = files;
    }

    @NonNull
    @Override
    public DownloadsHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new DownloadsHolder(LayoutInflater.from(context).inflate(R.layout.layout_download_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull DownloadsHolder holder, int position) {
        Glide.with(context).load(files.get(position)).into(holder.imgItem);

        if (files.get(position).getName().endsWith(".mp4"))
            holder.playIcon.setVisibility(View.VISIBLE);
        else
            holder.playIcon.setVisibility(View.GONE);


        holder.txtTitle.setText(files.get(position).getName());
        holder.txtSize.setText(getSizeInReadableForm(files.get(position).length()));
    }

    @Override
    public int getItemCount() {
        return files.size();
    }

    public static class DownloadsHolder extends RecyclerView.ViewHolder {

        ImageView imgItem, playIcon;
        TextView txtTitle, txtSize;

        public DownloadsHolder(@NonNull View itemView) {
            super(itemView);
            imgItem = itemView.findViewById(R.id.img);
            playIcon = itemView.findViewById(R.id.play);
            txtTitle = itemView.findViewById(R.id.txt_title);
            txtSize = itemView.findViewById(R.id.size);
        }
    }
}
