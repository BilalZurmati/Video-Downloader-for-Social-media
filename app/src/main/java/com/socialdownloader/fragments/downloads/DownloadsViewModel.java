package com.socialdownloader.fragments.downloads;

import static com.socialdownloader.Common.Common.getFolderPath;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.io.File;
import java.util.Arrays;
import java.util.List;

public class DownloadsViewModel extends ViewModel {
    public MutableLiveData<List<File>> downloads = new MutableLiveData();

    public void getDownloads() {
        File folder = new File(getFolderPath());
        if (folder.exists()) {
            File[] items = folder.listFiles();
            if (items != null)
                downloads.postValue(Arrays.asList(items));
        }

    }
}