package com.socialdownloader.fragments.fb;


import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.socialdownloader.Common.Common;
import com.socialdownloader.R;
import com.socialdownloader.databinding.FacebookFragmentBinding;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.net.URISyntaxException;

public class FacebookFragment extends Fragment {

    private FacebookFragmentBinding _binder;

    public static FacebookFragment newInstance() {
        return new FacebookFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        _binder = FacebookFragmentBinding.inflate(getLayoutInflater(), container, false);

        webProp(_binder.webViewSearched);


        return _binder.getRoot();
    }


    BroadcastReceiver onComplete = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Toast.makeText(context, "Video Downloaded Successfully", Toast.LENGTH_SHORT).show();
        }

    };


    public void downloadVideo(final String url) {

        try {
            Uri downloadUri = Uri.parse(url);
            String mBaseFolderPath = android.os.Environment
                    .getExternalStorageDirectory()
                    + File.separator
                    + Common.SAVED_FILE_NAME + File.separator;


            if (!new File(mBaseFolderPath).exists()) {
                new File(mBaseFolderPath).mkdirs();
            }
            String mFilePath = "file://" + mBaseFolderPath + "/" + "Facebook " + System.currentTimeMillis() + ".mp4";

            DownloadManager.Request req = new DownloadManager.Request(downloadUri);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                req.setDestinationInExternalFilesDir(requireContext(), Environment.DIRECTORY_DOWNLOADS, mFilePath);
            } else {
                req.setDestinationUri(Uri.parse(mFilePath));
            }

            req.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
            DownloadManager dm = (DownloadManager) requireActivity().getSystemService(Context.DOWNLOAD_SERVICE);
            dm.enqueue(req);
            Toast.makeText(requireContext(), "Download Started", Toast.LENGTH_LONG).show();
            requireActivity().registerReceiver(onComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
        } catch (Exception e) {
            Toast.makeText(getContext(), "Download Failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    @JavascriptInterface
    public void getData(final String json) {
        try {
            JSONObject jsonObject = new JSONObject(json);


            String videoUrl = jsonObject.getString("src");
            videoUrl = videoUrl.replaceAll("%3A", ":");
            videoUrl = videoUrl.replaceAll("%2F", "/");
            videoUrl = videoUrl.replaceAll("%3F", "?");
            videoUrl = videoUrl.replaceAll("%3D", "=");
            videoUrl = videoUrl.replaceAll("%26", "&");

            final String finalUrl = videoUrl;


            _binder.downloadFab.setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.black, null));

            _binder.downloadFab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(requireContext(), finalUrl, Toast.LENGTH_SHORT).show();
                    Log.i("MyKey", "getData: " + finalUrl);  //video url
                }
            });


//            View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.layout_download_dialog, null, false);
//            AlertDialog.Builder downloadDialog = new AlertDialog.Builder(getContext());
//            downloadDialog.setView(dialogView);
//            downloadDialog.setCancelable(true);
//            AlertDialog dialog = downloadDialog.create();
//            dialog.show();
//
//            LinearLayout btnDownload = dialogView.findViewById(R.id.download);
//            ImageView imageThumbnail = dialogView.findViewById(R.id.thumbnail);
//            Glide.with(requireActivity()).load(finalUrl)
//                    .diskCacheStrategy(DiskCacheStrategy.ALL)
//                    .into(imageThumbnail);
//
//            btnDownload.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    //first check permissions
//                    checkPermission(finalUrl, dialog);
//                }
//            });


        } catch (JSONException e) {
            Log.i("MyException", "getData: " + e.getMessage());
        }
    }

    private void checkPermission(String finalUrl, AlertDialog dialog) {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            dialog.dismiss();
            downloadVideo(finalUrl);
        } else {
            dialog.dismiss();
            ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 111);
        }

    }

    @SuppressLint({"AddJavascriptInterface", "SetJavaScriptEnabled"})
    public void webProp(WebView mWebView) {
        mWebView.getSettings().setDisplayZoomControls(false);
        mWebView.addJavascriptInterface(this, "mJava");
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setLoadWithOverviewMode(true);
        mWebView.getSettings().setUseWideViewPort(true);
        mWebView.getSettings().setBuiltInZoomControls(true);
        mWebView.getSettings().setPluginState(WebSettings.PluginState.ON);

        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (url.startsWith("http") || url.startsWith("https")) {
                    return false;
                }
                if (url.startsWith("intent")) {
                    try {
                        Intent intent = Intent.parseUri(url, Intent.URI_INTENT_SCHEME);
                        String fallbackUrl = intent.getStringExtra("browser_fallback_url");
                        if (fallbackUrl != null) {
                            mWebView.loadUrl(fallbackUrl);
                            return true;
                        }
                    } catch (URISyntaxException e) {
                        //not an intent uri
                    }
                }
                return true;//do nothing in other cases
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                mWebView.loadUrl(playScript());
            }

            public void onLoadResource(WebView view, String url) {
//                mWebView.loadUrl(playScript());
            }
        });

        CookieSyncManager.createInstance(requireContext());
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.setAcceptCookie(true);
        CookieSyncManager.getInstance().startSync();
        mWebView.loadUrl("https://www.facebook.com/");
    }


    //doesn't requires user to be logged in but won't work on devices above android 10
    private String linkScript() {
        return "javascript:(function() { "
                + "var el = document.querySelectorAll('div[data-sigil]');"
                + "for(var i=0;i<el.length; i++)"
                + "{"
                + "var sigil = el[i].dataset.sigil;"
                + "if(sigil.indexOf('inlineVideo') > -1){"
                + "delete el[i].dataset.sigil;"
                + "var jsonData = JSON.parse(el[i].dataset.store);"
                + "el[i].setAttribute('onClick', 'mJava.getData(\"'+jsonData['src']+'\");');"
                + "}" + "}" + "})()";
    }

    //Works on all devices but requires user to log in to web view
    private String playScript() {
        return "javascript:(function() {" +
                "document.addEventListener(\"play\",(e)=>{\n" +
                "var videos = document.querySelectorAll(\"video\");\n" +
                "videos.forEach((el)=>{\n" +
                "var parentElement = el.parentElement;\n" +
                "var data = parentElement.getAttribute(\"data-store\");\n" +
                "mJava.getData(data.toString());\n" +
                "})\n" +
                "},true)" +
                "})()";
    }


}