package com.socialdownloader.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;

import com.socialdownloader.R;
import com.socialdownloader.adapter.MyViewPagerAdapter;
import com.socialdownloader.databinding.ActivityMainBinding;
import com.socialdownloader.fragments.downloads.DownloadsFragment;
import com.socialdownloader.fragments.fb.FacebookFragment;
import com.socialdownloader.fragments.insta.InstaFragment;
import com.socialdownloader.fragments.whats_app.Whats_App_Fragment;

public class MainActivity extends AppCompatActivity {

    MyViewPagerAdapter myViewPagerAdapter;

    private ActivityMainBinding _binder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        _binder = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(_binder.getRoot());

        initViews();
        setButtonColors(0);
        setClickListeners();
    }

    private void setClickListeners() {
        _binder.txtFacebook.setOnClickListener(view -> {
            setButtonColors(0);
            _binder.viewPager.setCurrentItem(0);
        });
        _binder.txtInsta.setOnClickListener(view -> {
            setButtonColors(1);
            _binder.viewPager.setCurrentItem(1);
        });
        _binder.txtWhatsApp.setOnClickListener(view -> {
            setButtonColors(2);
            _binder.viewPager.setCurrentItem(2);
        });
        _binder.txtDownloads.setOnClickListener(view -> {
            setButtonColors(3);
            _binder.viewPager.setCurrentItem(3);
        });
    }


    private void initViews() {

        //add fragments

        myViewPagerAdapter = new MyViewPagerAdapter(getSupportFragmentManager());
        myViewPagerAdapter.addFragment(new FacebookFragment());
        myViewPagerAdapter.addFragment(new InstaFragment());
        myViewPagerAdapter.addFragment(new Whats_App_Fragment());
        myViewPagerAdapter.addFragment(new DownloadsFragment());

        _binder.viewPager.setAdapter(myViewPagerAdapter);

        _binder.viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                setButtonColors(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void setButtonColors(int position) {
        if (position == 0) {
            _binder.linearFacebook.setBackgroundColor(getResources().getColor(R.color.dark_gray));
            _binder.linearInsta.setBackgroundColor(getResources().getColor(R.color.white));
            _binder.linearWhatsApp.setBackgroundColor(getResources().getColor(R.color.white));
            _binder.linearDownloads.setBackgroundColor(getResources().getColor(R.color.white));

            //text Color
            _binder.txtFacebook.setTextColor(getResources().getColor(R.color.white));
            _binder.txtInsta.setTextColor(getResources().getColor(R.color.black));
            _binder.txtWhatsApp.setTextColor(getResources().getColor(R.color.black));
            _binder.txtDownloads.setTextColor(getResources().getColor(R.color.black));

            //set Action bar title
            setTitle("Facebook");
        } else if (position == 1) {
            _binder.linearFacebook.setBackgroundColor(getResources().getColor(R.color.white));
            _binder.linearInsta.setBackgroundColor(getResources().getColor(R.color.dark_gray));
            _binder.linearWhatsApp.setBackgroundColor(getResources().getColor(R.color.white));
            _binder.linearDownloads.setBackgroundColor(getResources().getColor(R.color.white));

            //text Color
            _binder.txtFacebook.setTextColor(getResources().getColor(R.color.black));
            _binder.txtInsta.setTextColor(getResources().getColor(R.color.white));
            _binder.txtWhatsApp.setTextColor(getResources().getColor(R.color.black));
            _binder.txtDownloads.setTextColor(getResources().getColor(R.color.black));

            //set Action bar title
            setTitle("Instagram");
        } else if (position == 2) {
            _binder.linearFacebook.setBackgroundColor(getResources().getColor(R.color.white));
            _binder.linearInsta.setBackgroundColor(getResources().getColor(R.color.white));
            _binder.linearWhatsApp.setBackgroundColor(getResources().getColor(R.color.dark_gray));
            _binder.linearDownloads.setBackgroundColor(getResources().getColor(R.color.white));

            //text Color
            _binder.txtFacebook.setTextColor(getResources().getColor(R.color.black));
            _binder.txtInsta.setTextColor(getResources().getColor(R.color.black));
            _binder.txtWhatsApp.setTextColor(getResources().getColor(R.color.white));
            _binder.txtDownloads.setTextColor(getResources().getColor(R.color.black));

            //set Action bar title
            setTitle("WhatsApp");
        } else if (position == 3) {
            _binder.linearFacebook.setBackgroundColor(getResources().getColor(R.color.white));
            _binder.linearInsta.setBackgroundColor(getResources().getColor(R.color.white));
            _binder.linearWhatsApp.setBackgroundColor(getResources().getColor(R.color.white));
            _binder.linearDownloads.setBackgroundColor(getResources().getColor(R.color.dark_gray));

            //text Color
            _binder.txtFacebook.setTextColor(getResources().getColor(R.color.black));
            _binder.txtInsta.setTextColor(getResources().getColor(R.color.black));
            _binder.txtWhatsApp.setTextColor(getResources().getColor(R.color.black));
            _binder.txtDownloads.setTextColor(getResources().getColor(R.color.white));

            //set Action bar title
            setTitle("Downloads");
        }
    }

    private void setTitle(String title) {
        if (getSupportActionBar() != null)
            getSupportActionBar().setTitle(title);
    }
}