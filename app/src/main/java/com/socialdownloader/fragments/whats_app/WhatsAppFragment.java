package com.socialdownloader.fragments.whats_app;

import static android.os.Build.VERSION.SDK_INT;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;

import android.os.Environment;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.socialdownloader.Common.Common;
import com.socialdownloader.adapter.MyWhatsAppAdapter;
import com.socialdownloader.databinding.WhatsAppFragmentBinding;

import java.io.File;

public class WhatsAppFragment extends Fragment {

    private WhatsAppViewModel mViewModel;

    private WhatsAppFragmentBinding _binder;


    private final ActivityResultLauncher<String> requestPermissionLauncher = registerForActivityResult(
            new ActivityResultContracts.RequestPermission(),
            new ActivityResultCallback<Boolean>() {
                @Override
                public void onActivityResult(Boolean result) {
                    if (result) {
                        mViewModel.loadImagesFromFile(new
                                File(Environment.getExternalStorageDirectory().toString() + Common.WHATS_APP_STATUSES_LOCATION));
                    }
                }
            }
    );

    // You can do the assignment inside onAttach or onCreate, i.e, before the activity is displayed
    ActivityResultLauncher<Intent> activityLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        if (SDK_INT >= Build.VERSION_CODES.R) {
                            if (Environment.isExternalStorageManager()) {
                                mViewModel.loadImagesFromFile(new
                                        File(Environment.getExternalStorageDirectory().toString() + Common.WHATS_APP_STATUSES_LOCATION));
                            } else {
                                Toast.makeText(requireContext(), "Allow permission for storage access!", Toast.LENGTH_SHORT).show();
                            }
                        }

                        // There are no request codes

                    }
                }
            });

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        _binder = WhatsAppFragmentBinding.inflate(getLayoutInflater(), container, false);

        initViews();


        return _binder.getRoot();
    }

    private void initViews() {
        _binder.recyclerViewWhatsApp.setLayoutManager(new GridLayoutManager(getContext(), 3));
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(WhatsAppViewModel.class);

        if (isPermissionGranted()) {
            mViewModel.loadImagesFromFile(new
                    File(Environment.getExternalStorageDirectory().toString() + Common.WHATS_APP_STATUSES_LOCATION));
        }
        if (SDK_INT >= Build.VERSION_CODES.R) {
            Intent intent = new Intent();
            intent.setAction(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
            activityLauncher.launch(intent);
        } else
            requestPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);

        mViewModel.getListMutableLiveData().observe(getViewLifecycleOwner(), files -> {
            MyWhatsAppAdapter myWhatsAppAdapter = new MyWhatsAppAdapter(requireContext(), files);
            _binder.recyclerViewWhatsApp.setAdapter(myWhatsAppAdapter);
        });
    }

    public Boolean isPermissionGranted() {
        if (SDK_INT >= Build.VERSION_CODES.R) {
            return Environment.isExternalStorageManager();
        } else
            return ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }
}