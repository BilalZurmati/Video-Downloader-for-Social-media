package com.socialdownloader.fragments.downloads;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.socialdownloader.R;

public class DownloadsFragment extends Fragment {

    private DownloadsViewModel mViewModel;

    RecyclerView recyclerView;

    public static DownloadsFragment newInstance() {
        return new DownloadsFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.downloads_fragment, container, false);
        initViews(root);


        return root;
    }

    private void initViews(View root) {
        recyclerView = root.findViewById(R.id.recycler_view_downloads);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(DownloadsViewModel.class);
        // TODO: Use the ViewModel
    }

}