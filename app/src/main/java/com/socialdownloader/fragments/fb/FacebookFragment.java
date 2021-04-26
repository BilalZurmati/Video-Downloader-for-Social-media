package com.socialdownloader.fragments.fb;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
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


    Button btnPaste, btnDownload;
    EditText edtLink;
    WebView webView;
    LinearLayout mainLayout;
    File file;

    public static FacebookFragment newInstance() {
        return new FacebookFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.facebook_fragment, container, false);

        initViews(root);
        setClickListeners();

        return root;
    }

    private void setClickListeners() {
        btnPaste.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        btnDownload.setOnClickListener(view -> {
            if (!TextUtils.isEmpty(edtLink.getText().toString())) {
                mainLayout.setVisibility(View.GONE);
                webView.setVisibility(View.VISIBLE);
                webProp(webView, getContext(), edtLink.getText().toString());
            } else {
                edtLink.setError("Paste link first");
                edtLink.requestFocus();
            }
        });
    }

    private void initViews(View root) {
        btnDownload = root.findViewById(R.id.btn_download_facebook);
        btnPaste = root.findViewById(R.id.btn_paste_facebook);
        edtLink = root.findViewById(R.id.edt_link);
        webView = root.findViewById(R.id.webViewSearched);
        mainLayout = root.findViewById(R.id.top);
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
                new File(mBaseFolderPath).mkdir();
            }
            String mFilePath = "file://" + mBaseFolderPath + "/" + "Facebook " + System.currentTimeMillis() + ".mp4";

            DownloadManager.Request req = new DownloadManager.Request(downloadUri);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                req.setDestinationInExternalFilesDir(getContext(), Environment.DIRECTORY_DOWNLOADS, mFilePath);
            } else {
                req.setDestinationUri(Uri.parse(mFilePath));
            }

            req.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
            DownloadManager dm = (DownloadManager) getActivity().getSystemService(Context.DOWNLOAD_SERVICE);
            dm.enqueue(req);
            Toast.makeText(getContext(), "Download Started", Toast.LENGTH_LONG).show();
            getActivity().registerReceiver(onComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
        } catch (Exception e) {
            Toast.makeText(getContext(), "Download Failed: " + e.toString(), Toast.LENGTH_LONG).show();
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
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            dialog.dismiss();
            downloadVideo(finalUrl);
        } else {
            dialog.dismiss();
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 111);
        }

    }

    @SuppressLint({"AddJavascriptInterface", "SetJavaScriptEnabled"})
    public void webProp(WebView mwebview, Context context, String url) {
        mwebview.getSettings().setDisplayZoomControls(false);
        mwebview.addJavascriptInterface(this, "mJava");
        mwebview.getSettings().setJavaScriptEnabled(true);
        mwebview.getSettings().setLoadWithOverviewMode(true);
        mwebview.getSettings().setUseWideViewPort(true);
        mwebview.getSettings().setLoadWithOverviewMode(true);
        mwebview.getSettings().setUseWideViewPort(true);
        mwebview.getSettings().setBuiltInZoomControls(true);
        mwebview.getSettings().setPluginState(WebSettings.PluginState.ON);

        mwebview.setWebViewClient(new WebViewClient() {
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
                            mwebview.loadUrl(fallbackUrl);
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
                mwebview.loadUrl("javascript:" +
                        "var e=0;\n" +
                        "window.onscroll=function()\n" +
                        "{\n" +
                        "\tvar ij=document.querySelectorAll(\"video\");\n" +
                        "\t\tfor(var f=0;f<ij.length;f++)\n" +
                        "\t\t{\n" +
                        "\t\t\tif((ij[f].parentNode.querySelectorAll(\"img\")).length==0)\n" +
                        "\t\t\t{\n" +
                        "\t\t\t\tvar nextimageWidth=ij[f].nextSibling.style.width;\n" +
                        "\t\t\t\tvar nextImageHeight=ij[f].nextSibling.style.height;\n" +
                        "\t\t\t\tvar Nxtimgwd=parseInt(nextimageWidth, 10);\n" +
                        "\t\t\t\tvar Nxtimghght=parseInt(nextImageHeight, 10); \n" +
                        "\t\t\t\tvar DOM_img = document.createElement(\"img\");\n" +
                        "\t\t\t\t\tDOM_img.height=\"68\";\n" +
                        "\t\t\t\t\tDOM_img.width=\"68\";\n" +
                        "\t\t\t\t\tDOM_img.style.top=(Nxtimghght/2-20)+\"px\";\n" +
                        "\t\t\t\t\tDOM_img.style.left=(Nxtimgwd/2-20)+\"px\";\n" +
                        "\t\t\t\t\tDOM_img.style.position=\"absolute\";\n" +
                        "\t\t\t\t\tDOM_img.src = \"https://image.ibb.co/kobwsk/one.png\"; \n" +
                        "\t\t\t\t\tij[f].parentNode.appendChild(DOM_img);\n" +
                        "\t\t\t}\t\t\n" +
                        "\t\t\tij[f].remove();\n" +
                        "\t\t} \n" +
                        "\t\t\te++;\n" +
                        "};" +
                        "var a = document.querySelectorAll(\"a[href *= 'video_redirect']\");\n" +
                        "for (var i = 0; i < a.length; i++) {\n" +
                        "    var mainUrl = a[i].getAttribute(\"href\");\n" +
                        "  a[i].removeAttribute(\"href\");\n" +
                        "\tmainUrl=mainUrl.split(\"/video_redirect/?src=\")[1];\n" +
                        "\tmainUrl=mainUrl.split(\"&source\")[0];\n" +
                        "    var threeparent = a[i].parentNode.parentNode.parentNode;\n" +
                        "    threeparent.setAttribute(\"src\", mainUrl);\n" +
                        "    threeparent.onclick = function() {\n" +
                        "        var mainUrl1 = this.getAttribute(\"src\");\n" +
                        "         mJava.getData(mainUrl1);\n" +
                        "    };\n" +
                        "}" +
                        "var k = document.querySelectorAll(\"div[data-store]\");\n" +
                        "for (var j = 0; j < k.length; j++) {\n" +
                        "    var h = k[j].getAttribute(\"data-store\");\n" +
                        "    var g = JSON.parse(h);\nvar jp=k[j].getAttribute(\"data-sigil\");\n" +
                        "    if (g.type === \"video\") {\n" +
                        "if(jp==\"inlineVideo\")" +
                        "{" +
                        "   k[j].removeAttribute(\"data-sigil\");" +
                        "}\n" +
                        "        var url = g.src;\n" +
                        "        k[j].setAttribute(\"src\", g.src);\n" +
                        "        k[j].onclick = function() {\n" +
                        "            var mainUrl = this.getAttribute(\"src\");\n" +
                        "               mJava.getData(mainUrl);\n" +
                        "        };\n" +
                        "    }\n" +
                        "\n" +
                        "}");
            }

//            public void onLoadResource(WebView view, String url) {
//                mwebview.loadUrl("javascript:" +
//                        "var e=document.querySelectorAll(\"span\"); " +
//                        "if(e[0]!=undefined)" +
//                        "{" +
//                        "var fbforandroid=e[0].innerText;" +
//                        "if(fbforandroid.indexOf(\"Facebook\")!=-1)" +
//                        "{ " +
//                        "var h =e[0].parentNode.parentNode.parentNode.style.display=\"none\";" +
//                        "} " +
//                        "}" +
//                        "var installfb=document.querySelectorAll(\"a\");\n" +
//                        "for (var hardwares = 0; hardwares < installfb.length; hardwares++) \n" +
//                        "{\n" +
//                        "\tif(installfb[hardwares].text.indexOf(\"Install\")!=-1)\n" +
//                        "\t{\n" +
//                        "\t\tvar soft=installfb[hardwares].parentNode.style.display=\"none\";\n" +
//                        "\n" +
//                        "\t}\n" +
//                        "}\n");
//                mwebview.loadUrl("javascript:" +
//                        "var e=0;\n" +
//                        "window.onscroll=function()\n" +
//                        "{\n" +
//                        "\tvar ij=document.querySelectorAll(\"video\");\n" +
//                        "\t\tfor(var f=0;f<ij.length;f++)\n" +
//                        "\t\t{\n" +
//                        "\t\t\tif((ij[f].parentNode.querySelectorAll(\"img\")).length==0)\n" +
//                        "\t\t\t{\n" +
//                        "\t\t\t\tvar nextimageWidth=ij[f].nextSibling.style.width;\n" +
//                        "\t\t\t\tvar nextImageHeight=ij[f].nextSibling.style.height;\n" +
//                        "\t\t\t\tvar Nxtimgwd=parseInt(nextimageWidth, 10);\n" +
//                        "\t\t\t\tvar Nxtimghght=parseInt(nextImageHeight, 10); \n" +
//                        "\t\t\t\tvar DOM_img = document.createElement(\"img\");\n" +
//                        "\t\t\t\t\tDOM_img.height=\"68\";\n" +
//                        "\t\t\t\t\tDOM_img.width=\"68\";\n" +
//                        "\t\t\t\t\tDOM_img.style.top=(Nxtimghght/2-20)+\"px\";\n" +
//                        "\t\t\t\t\tDOM_img.style.left=(Nxtimgwd/2-20)+\"px\";\n" +
//                        "\t\t\t\t\tDOM_img.style.position=\"absolute\";\n" +
//                        "\t\t\t\t\tDOM_img.src = \"https://image.ibb.co/kobwsk/one.png\"; \n" +
//                        "\t\t\t\t\tij[f].parentNode.appendChild(DOM_img);\n" +
//                        "\t\t\t}\t\t\n" +
//                        "\t\t\tij[f].remove();\n" +
//                        "\t\t} \n" +
//                        "\t\t\te++;\n" +
//                        "};" +
//                        "var a = document.querySelectorAll(\"a[href *= 'video_redirect']\");\n" +
//                        "for (var i = 0; i < a.length; i++) {\n" +
//                        "    var mainUrl = a[i].getAttribute(\"href\");\n" +
//                        "  a[i].removeAttribute(\"href\");\n" +
//                        "\tmainUrl=mainUrl.split(\"/video_redirect/?src=\")[1];\n" +
//                        "\tmainUrl=mainUrl.split(\"&source\")[0];\n" +
//                        "    var threeparent = a[i].parentNode.parentNode.parentNode;\n" +
//                        "    threeparent.setAttribute(\"src\", mainUrl);\n" +
//                        "    threeparent.onclick = function() {\n" +
//                        "        var mainUrl1 = this.getAttribute(\"src\");\n" +
//                        "         mJava.getData(mainUrl1);\n" +
//                        "    };\n" +
//                        "}" +
//                        "var k = document.querySelectorAll(\"div[data-store]\");\n" +
//                        "for (var j = 0; j < k.length; j++) {\n" +
//                        "    var h = k[j].getAttribute(\"data-store\");\n" +
//                        "    var g = JSON.parse(h);var jp=k[j].getAttribute(\"data-sigil\");\n" +
//                        "    if (g.type === \"video\") {\n" +
//                        "if(jp==\"inlineVideo\")" +
//                        "{" +
//                        "   k[j].removeAttribute(\"data-sigil\");" +
//                        "}\n" +
//                        "        var url = g.src;\n" +
//                        "        k[j].setAttribute(\"src\", g.src);\n" +
//                        "        k[j].onclick = function() {\n" +
//                        "            var mainUrl = this.getAttribute(\"src\");\n" +
//                        "               mJava.getData(mainUrl);\n" +
//                        "        };\n" +
//                        "    }\n" +
//                        "\n" +
//                        "}");
//            }
        });

        mwebview.loadUrl(url);
    }


}