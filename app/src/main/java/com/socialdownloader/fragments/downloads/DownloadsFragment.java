package com.socialdownloader.fragments.downloads;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.socialdownloader.R;
import com.socialdownloader.adapter.DownloadsAdapter;
import com.socialdownloader.databinding.DownloadsFragmentBinding;

import java.io.File;
import java.util.List;

public class DownloadsFragment extends Fragment {

    private DownloadsViewModel mViewModel;

    private DownloadsFragmentBinding _binder;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        _binder = DownloadsFragmentBinding.inflate(getLayoutInflater(), container, false);


        return _binder.getRoot();
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(DownloadsViewModel.class);
        mViewModel.getDownloads();

        mViewModel.downloads.observe(getViewLifecycleOwner(), files -> {
            Log.i("MyKey", "onChanged: " + files.size());
            DownloadsAdapter adapter = new DownloadsAdapter(requireActivity(), files);
            _binder.recyclerViewDownloads.setAdapter(adapter);
        });
    }
}