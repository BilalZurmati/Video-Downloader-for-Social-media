package com.socialdownloader.adapter;

import static com.socialdownloader.Common.Common.getFolderPath;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.socialdownloader.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URLConnection;
import java.nio.channels.FileChannel;
import java.util.List;

public class MyWhatsAppAdapter extends RecyclerView.Adapter<MyWhatsAppAdapter.MyViewHolder> {

    private Context context;
    private List<File> fileList;

    public MyWhatsAppAdapter(Context context, List<File> fileList) {
        this.context = context;
        this.fileList = fileList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.layout_whatsapp_item,
                parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        File currentFile = fileList.get(holder.getAdapterPosition());

        holder.imgDownload.setOnClickListener(this.downloadMediaItem(currentFile));

        holder.imgShare.setOnClickListener(view -> shareFile(currentFile));


        Glide.with(context).load(currentFile.getAbsoluteFile()).into(holder.imgWhatsapp);
    }

    public View.OnClickListener downloadMediaItem(final File sourceFile) {

        return v -> ((Runnable) () -> {
            try {
                copyFile(sourceFile, new File(getFolderPath() + File.separator + sourceFile.getName()));
                Toast.makeText(context, "Saved successfully", Toast.LENGTH_SHORT).show();

            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(context, "Saved failed", Toast.LENGTH_SHORT).show();
            }
        }).run();
    }

    public static void copyFile(File sourceFile, File destFile) throws IOException {
        if (!destFile.getParentFile().exists())
            destFile.getParentFile().mkdirs();

        if (!destFile.exists()) {
            destFile.createNewFile();
        }

        FileChannel source = null;
        FileChannel destination = null;

        try {
            source = new FileInputStream(sourceFile).getChannel();
            destination = new FileOutputStream(destFile).getChannel();
            destination.transferFrom(source, 0, source.size());
        } finally {
            if (source != null) {
                source.close();
            }
            if (destination != null) {
                destination.close();
            }
        }
    }

    private void shareFile(File file) {

        Intent intentShareFile = new Intent(Intent.ACTION_SEND);

        intentShareFile.setType(URLConnection.guessContentTypeFromName(file.getName()));
        Uri imageUri = FileProvider.getUriForFile(
                context,
                "com.socialdownloader.provider", //(use your app signature + ".provider" )
                file);
        intentShareFile.putExtra(Intent.EXTRA_STREAM, imageUri);

        //if you need
        //intentShareFile.putExtra(Intent.EXTRA_SUBJECT,"Sharing File Subject);
        //intentShareFile.putExtra(Intent.EXTRA_TEXT, "Sharing File Description");

        context.startActivity(Intent.createChooser(intentShareFile, "Share File"));

    }

    @Override
    public int getItemCount() {
        return fileList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView imgWhatsapp, imgDownload, imgShare;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            imgWhatsapp = itemView.findViewById(R.id.img_whats_app);
            imgDownload = itemView.findViewById(R.id.img_download);
            imgShare = itemView.findViewById(R.id.img_share);
        }
    }
}
