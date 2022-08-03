package com.socialdownloader.fragments.fb;


import static com.socialdownloader.Common.Common.getFolderPath;
import static com.socialdownloader.utils.AppUtil.isPermissionGranted;
import static com.socialdownloader.utils.AppUtil.requestPermission;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.socialdownloader.Common.Common;
import com.socialdownloader.R;
import com.socialdownloader.databinding.FacebookFragmentBinding;
import com.socialdownloader.databinding.LayoutDownloadDialogBinding;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.net.URISyntaxException;

public class FacebookFragment extends Fragment {

    private FacebookFragmentBinding _binder;


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
            String mBaseFolderPath = getFolderPath() + File.separator;


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

            _binder.downloadFab.setOnClickListener(view -> {
                Log.i("MyKey", "getData: " + finalUrl);  //video url

                showDownloadDialog(finalUrl);
            });


        } catch (JSONException e) {
            Log.i("MyException", "getData: " + e.getMessage());
        }
    }

    private void showDownloadDialog(String finalUrl) {
        LayoutDownloadDialogBinding dialogViewBinding = LayoutDownloadDialogBinding.inflate(getLayoutInflater());
        AlertDialog.Builder downloadDialog = new AlertDialog.Builder(getContext());
        downloadDialog.setView(dialogViewBinding.getRoot());
        downloadDialog.setCancelable(true);
        AlertDialog dialog = downloadDialog.create();
        dialog.show();


        Glide.with(requireActivity())
                .load(finalUrl)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(dialogViewBinding.thumbnail);

        dialogViewBinding.download.setOnClickListener(view -> {
            //first check permissions
            if (isPermissionGranted(requireContext()))
                downloadVideo(finalUrl);
            else
                requestPermission(requireActivity(), 111);


            dialog.dismiss();
        });
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