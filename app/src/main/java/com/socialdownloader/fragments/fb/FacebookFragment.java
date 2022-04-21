package com.socialdownloader.fragments.fb;

import static android.content.Context.CLIPBOARD_SERVICE;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.socialdownloader.Common.Common;
import com.socialdownloader.R;
import com.socialdownloader.databinding.FacebookFragmentBinding;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;

public class FacebookFragment extends Fragment {

    private FacebookFragmentBinding _binder;

    public static FacebookFragment newInstance() {
        return new FacebookFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        _binder = FacebookFragmentBinding.inflate(getLayoutInflater(), container, false);

        setClickListeners();

        return _binder.getRoot();
    }

    private void setClickListeners() {
        _binder.btnPasteFacebook.setOnClickListener(view -> {
            ClipboardManager clipboard = (ClipboardManager) requireActivity().getSystemService(CLIPBOARD_SERVICE);
            _binder.edtLink.setText(clipboard.getPrimaryClip().getItemAt(0).getText());
        });
        _binder.btnDownloadFacebook.setOnClickListener(view -> {
            if (!TextUtils.isEmpty(_binder.edtLink.getText().toString())) {
                _binder.top.setVisibility(View.GONE);
                _binder.webViewSearched.setVisibility(View.VISIBLE);
                webProp(_binder.webViewSearched, _binder.edtLink.getText().toString());
            } else {
                _binder.edtLink.setError("Paste link first");
                _binder.edtLink.requestFocus();
            }
        });
    }


    BroadcastReceiver onComplete = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Toast.makeText(context, "Video Downloaded Successfully", Toast.LENGTH_SHORT).show();
        }

    };


    public void downloadVideo(final String url) {
        Uri downloadUri = Uri.parse(url);
        try {
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
    public void getData(final String pathVideo) {

        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.layout_download_dialog, null, false);
        AlertDialog.Builder downloadDialog = new AlertDialog.Builder(getContext());
        downloadDialog.setView(dialogView);
        downloadDialog.setCancelable(true);
        AlertDialog dialog = downloadDialog.create();
        dialog.show();

        LinearLayout btnDownload = dialogView.findViewById(R.id.download);

        btnDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String finalUrl;
                finalUrl = pathVideo;
                finalUrl = finalUrl.replaceAll("%3A", ":");
                finalUrl = finalUrl.replaceAll("%2F", "/");
                finalUrl = finalUrl.replaceAll("%3F", "?");
                finalUrl = finalUrl.replaceAll("%3D", "=");
                finalUrl = finalUrl.replaceAll("%26", "&");

                //first check permissions
                checkPermission(finalUrl, dialog);
            }
        });
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
    public void webProp(WebView mWebView, String url) {
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
                mWebView.loadUrl(linkScript());
            }

            public void onLoadResource(WebView view, String url) {
                mWebView.loadUrl(linkScript());
            }
        });

        mWebView.loadUrl(url);
    }


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


}