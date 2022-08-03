package com.socialdownloader.Common;


import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Environment;
import android.util.Log;

import java.text.CharacterIterator;
import java.text.StringCharacterIterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Common {
    public static final String WHATS_APP_STATUSES_LOCATION = "/WhatsApp/Media/.Statuses";
    public static String DIRECTORY_TO_SAVE_Whats_app_MEDIA = "/Whatsapp";
    public static final String SAVED_FILE_NAME = "Social_downloader";
    public static final String pattern = "https://www.instagram.com/";

    public static boolean IsConnected(Activity context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        //we are connected to a network
        return connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED;
    }

    public static boolean checkURL(String url) {

        Pattern r = Pattern.compile(pattern);

        // Now create matcher object.
        Matcher m = r.matcher(url);
        if (m.find()) {
            System.out.println("Found value: " + m.group(0));
            return true;
        } else {
            System.out.println("NO MATCH");
            return false;
        }
    }

    public static String getFolderPath() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
            return Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                    + "/" + Common.SAVED_FILE_NAME;
        else
            return Environment.getExternalStorageDirectory()
                    + "/" + Common.SAVED_FILE_NAME;
    }

    public static String getSizeInReadableForm(long bytes) {
        if (-1000 < bytes && bytes < 1000) {
            return bytes + " B";
        }
        CharacterIterator ci = new StringCharacterIterator("kMGTPE");
        while (bytes <= -999_950 || bytes >= 999_950) {
            bytes /= 1000;
            ci.next();
        }
        return String.format("%.1f %cB", bytes / 1000.0, ci.current());
    }

    public static String getValidLink(String link) {
        String url;
        if (link.contains("?")) {
            url = link.substring(0, link.indexOf("?"));
            return url;
        } else
            return link;
    }


}
