package com.socialdownloader.fragments.whats_app;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.socialdownloader.Common.Common;
import com.socialdownloader.R;
import com.socialdownloader.adapter.MyWhatsAppAdapter;

import java.io.File;
import java.util.List;

public class Whats_App_Fragment extends Fragment {

    private WhatsAppViewModel mViewModel;

    RecyclerView recyclerView;
    MyWhatsAppAdapter myWhatsAppAdapter;

    public static Whats_App_Fragment newInstance() {
        return new Whats_App_Fragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.whats__app__fragment, container, false);
        initViews(root);

        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 111);
        }
        return root;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 111 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            mViewModel.loadImagesFromFile(new
                    File(Environment.getExternalStorageDirectory().toString() + Common.WHATS_APP_STATUSES_LOCATION));
        }
    }

    private void initViews(View root) {
        recyclerView = root.findViewById(R.id.recycler_view_whats_app);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(WhatsAppViewModel.class);

        mViewModel.getErrorMessage().observe(getViewLifecycleOwner(), s -> Toast.makeText(getContext(), s, Toast.LENGTH_SHORT).show());

        mViewModel.getListMutableLiveData().observe(getViewLifecycleOwner(), files -> {
            myWhatsAppAdapter = new MyWhatsAppAdapter(getContext(), files);
            recyclerView.setAdapter(myWhatsAppAdapter);
        });

    }

}