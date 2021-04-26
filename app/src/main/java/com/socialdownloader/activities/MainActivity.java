package com.socialdownloader.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.socialdownloader.R;
import com.socialdownloader.adapter.MyViewPagerAdapter;
import com.socialdownloader.fragments.downloads.DownloadsFragment;
import com.socialdownloader.fragments.fb.FacebookFragment;
import com.socialdownloader.fragments.insta.InstaFragment;
import com.socialdownloader.fragments.whats_app.Whats_App_Fragment;

public class MainActivity extends AppCompatActivity {

    TextView txtFacebook, txtInsta, txtWhatsapp, txtDownloads;
    LinearLayout linearFacebook, linearInsta, linearWhatsapp, linearDownloads;
    ViewPager viewPager;
    MyViewPagerAdapter myViewPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();
        setButtonColors(0);
        setClickListeners();
    }

    private void setClickListeners() {
        txtFacebook.setOnClickListener(view -> {
            setButtonColors(0);
            viewPager.setCurrentItem(0);
        });
        txtInsta.setOnClickListener(view -> {
            setButtonColors(1);
            viewPager.setCurrentItem(1);
        });
        txtWhatsapp.setOnClickListener(view -> {
            setButtonColors(2);
            viewPager.setCurrentItem(2);
        });
        txtDownloads.setOnClickListener(view -> {
            setButtonColors(3);
            viewPager.setCurrentItem(3);
        });
    }


    private void initViews() {
        txtFacebook = findViewById(R.id.txt_facebook);
        txtInsta = findViewById(R.id.txt_insta);
        txtWhatsapp = findViewById(R.id.txt_whats_app);
        txtDownloads = findViewById(R.id.txt_downloads);
        linearFacebook = findViewById(R.id.linear_facebook);
        linearInsta = findViewById(R.id.linear_insta);
        linearWhatsapp = findViewById(R.id.linear_whats_app);
        linearDownloads = findViewById(R.id.linear_downloads);
        viewPager = findViewById(R.id.view_pager);

        //add fragments

        myViewPagerAdapter = new MyViewPagerAdapter(getSupportFragmentManager());
        myViewPagerAdapter.addFragment(new FacebookFragment());
        myViewPagerAdapter.addFragment(new InstaFragment());
        myViewPagerAdapter.addFragment(new Whats_App_Fragment());
        myViewPagerAdapter.addFragment(new DownloadsFragment());

        viewPager.setAdapter(myViewPagerAdapter);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
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
            linearFacebook.setBackgroundColor(getResources().getColor(R.color.dark_gray));
            linearInsta.setBackgroundColor(getResources().getColor(R.color.white));
            linearWhatsapp.setBackgroundColor(getResources().getColor(R.color.white));
            linearDownloads.setBackgroundColor(getResources().getColor(R.color.white));

            //text Color
            txtFacebook.setTextColor(getResources().getColor(R.color.white));
            txtInsta.setTextColor(getResources().getColor(R.color.black));
            txtWhatsapp.setTextColor(getResources().getColor(R.color.black));
            txtDownloads.setTextColor(getResources().getColor(R.color.black));

            //set Action bar title
            getSupportActionBar().setTitle("Facebook");
        } else if (position == 1) {
            linearFacebook.setBackgroundColor(getResources().getColor(R.color.white));
            linearInsta.setBackgroundColor(getResources().getColor(R.color.dark_gray));
            linearWhatsapp.setBackgroundColor(getResources().getColor(R.color.white));
            linearDownloads.setBackgroundColor(getResources().getColor(R.color.white));

            //text Color
            txtFacebook.setTextColor(getResources().getColor(R.color.black));
            txtInsta.setTextColor(getResources().getColor(R.color.white));
            txtWhatsapp.setTextColor(getResources().getColor(R.color.black));
            txtDownloads.setTextColor(getResources().getColor(R.color.black));

            //set Action bar title
            getSupportActionBar().setTitle("Instagram");
        } else if (position == 2) {
            linearFacebook.setBackgroundColor(getResources().getColor(R.color.white));
            linearInsta.setBackgroundColor(getResources().getColor(R.color.white));
            linearWhatsapp.setBackgroundColor(getResources().getColor(R.color.dark_gray));
            linearDownloads.setBackgroundColor(getResources().getColor(R.color.white));

            //text Color
            txtFacebook.setTextColor(getResources().getColor(R.color.black));
            txtInsta.setTextColor(getResources().getColor(R.color.black));
            txtWhatsapp.setTextColor(getResources().getColor(R.color.white));
            txtDownloads.setTextColor(getResources().getColor(R.color.black));

            //set Action bar title
            getSupportActionBar().setTitle("WhatsApp");
        } else if (position == 3) {
            linearFacebook.setBackgroundColor(getResources().getColor(R.color.white));
            linearInsta.setBackgroundColor(getResources().getColor(R.color.white));
            linearWhatsapp.setBackgroundColor(getResources().getColor(R.color.white));
            linearDownloads.setBackgroundColor(getResources().getColor(R.color.dark_gray));

            //text Color
            txtFacebook.setTextColor(getResources().getColor(R.color.black));
            txtInsta.setTextColor(getResources().getColor(R.color.black));
            txtWhatsapp.setTextColor(getResources().getColor(R.color.black));
            txtDownloads.setTextColor(getResources().getColor(R.color.white));

            //set Action bar title
            getSupportActionBar().setTitle("Downloads");
        }
    }
}