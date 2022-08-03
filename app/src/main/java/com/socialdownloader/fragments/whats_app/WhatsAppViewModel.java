package com.socialdownloader.fragments.whats_app;

import android.os.Environment;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class WhatsAppViewModel extends ViewModel {
    private static final String WHATS_APP_STATUSES_LOCATION = "/WhatsApp/Media/.Statuses";

    private MutableLiveData<List<File>> listMutableLiveData = new MutableLiveData<>();

    public MutableLiveData<List<File>> getListMutableLiveData() {
        if (listMutableLiveData == null) {
            listMutableLiveData = new MutableLiveData<>();
            loadImagesFromFile(new File(Environment.getExternalStorageDirectory().toString() + WHATS_APP_STATUSES_LOCATION));
        }
        return listMutableLiveData;
    }

    public void loadImagesFromFile(File parentDir) {
        ArrayList<File> inFiles = new ArrayList<>();
        File[] files;
        files = parentDir.listFiles();
        if (files != null) {
            for (File file : files) {
                Log.i("MyPath", "loadImagesFromFile: " + file.getAbsoluteFile());
                if (file.getName().endsWith(".jpg") ||
                        file.getName().endsWith(".gif") ||
                        file.getName().endsWith(".mp4")) {
                    if (!inFiles.contains(file))
                        inFiles.add(file);
                }
            }
        }
        Log.i("MyPath", "loadImagesFromFile: " + inFiles.size());
        listMutableLiveData.setValue(inFiles);
    }
}