package com.socialdownloader.fragments.insta;

import androidx.lifecycle.ViewModelProvider;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.URLUtil;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.socialdownloader.Common.Common;
import com.socialdownloader.R;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class InstaFragment extends Fragment {

    private InstaViewModel mViewModel;

    Button btnPaste, btnDownload;
    private boolean type;
    private Bitmap bitmap, thumbnailBitmap;
    EditText edtLink;
    AlertDialog alertDialog;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.insta_fragment, container, false);
        initViews(root);

        return root;
    }

    private void initViews(View root) {
        btnDownload = root.findViewById(R.id.btn_download_insta);
        btnPaste = root.findViewById(R.id.btn_paste_insta);
        edtLink = root.findViewById(R.id.edt_link);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(InstaViewModel.class);
        // TODO: Use the ViewModel

        setClickListeners();
    }

    private void setClickListeners() {
        btnPaste.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        btnDownload.setOnClickListener(view -> {
            if (Common.IsConnected(requireActivity())) {
                if (TextUtils.isEmpty(edtLink.getText().toString())) {
                    Toast.makeText(getContext(), "Paste your url first", Toast.LENGTH_SHORT).show();
                } else if (!URLUtil.isValidUrl(edtLink.getText().toString())) {
                    Toast.makeText(getContext(), "Url is not valid url", Toast.LENGTH_SHORT).show();
                } else if (Common.checkURL(edtLink.getText().toString())) {
                    new ValidateFileFromURL(Common.getValidLink(edtLink.getText().toString())).execute();
                } else {
                    Toast.makeText(getContext(), "Url is not valid url", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(getContext(), "Sorry your Network is not connected", Toast.LENGTH_SHORT).show();
            }
        });
    }

    class ValidateFileFromURL extends AsyncTask<Void, String, String> {
        ProgressDialog progressDialog;
        String updatedUrl = "", url = "";

        public ValidateFileFromURL(String url) {

            this.updatedUrl = url + "?__a=1";
        }

        @Override
        protected void onPreExecute() {
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("Getting File Info ...");
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();
        }

        @Override
        protected String doInBackground(Void... params) {
            try {
                Log.i("MyUrl", "doInBackground: "+updatedUrl);

                Document document = Jsoup.connect(updatedUrl).ignoreContentType(true).get();
                url = Html.fromHtml(document.html()).toString();

                Log.i("MyUrl", "doInBackground: "+url);
                type = false;
                String checkType, imageUrl, videoUrl, thumbnail_url, isPrivate, start;

                start = url.substring(url.indexOf("is_private") + 12);
                isPrivate = start.substring(0, 4);
                if (isPrivate.equals("true")) {
                    getActivity().runOnUiThread(() ->
                            Toast.makeText(getContext(), "Sorry this account is private", Toast.LENGTH_SHORT).show()
                    );
                } else {
                    start = "";
                    start = url.substring(url.indexOf("is_video") + 10);
                    checkType = start.substring(0, 4);
                    if (checkType.equals("true")) {
                        type = true;
                    }
                    //set image url
                    imageUrl = url.substring(url.indexOf("display_url") + 14, url.lastIndexOf("display_resources") - 3);
                    imageUrl = imageUrl.replace("\\u0026", "&");
                    //checking if the file is image or video

                    if (type) {
                        //set video url
                        start = "";
                        videoUrl = url.substring(url.indexOf("video_url") + 12, url.lastIndexOf("video_view_count") - 3);
                        videoUrl = videoUrl.replace("\\u0026", "&");

                        thumbnail_url = url.substring(url.indexOf("thumbnail_src") + 16,
                                url.lastIndexOf("clips_music_attribution_info") - 3);
                        thumbnail_url = thumbnail_url.replace("\\u0026", "&");
                        Log.i("MyUrl", "doInBackground: " + thumbnail_url);
                        getThumbnail(videoUrl, thumbnail_url);
                    } else {
                        getImage(imageUrl);
                    }

                }

                return url;
            } catch (IOException e) {
                e.printStackTrace();
            }
            return url;
        }

        private void getImage(String imageUrl) {
            HttpURLConnection connection = null;
            try {
                connection = (HttpURLConnection) new URL(imageUrl).openConnection();
                connection.connect();
                bitmap = BitmapFactory.decodeStream(connection.getInputStream());
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        private void getThumbnail(String videoUrl, String thumbnailUrl) {
            HttpURLConnection connection = null;
            try {
                connection = (HttpURLConnection) new URL(thumbnailUrl).openConnection();
                connection.connect();
                thumbnailBitmap = BitmapFactory.decodeStream(connection.getInputStream());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


        @Override
        protected void onPostExecute(String response) {
            super.onPostExecute(response);
            progressDialog.dismiss();
            getActivity().runOnUiThread(() -> {
                if (bitmap != null || thumbnailBitmap != null) {
                    showDialog(bitmap, thumbnailBitmap);
                    //Clear bitmap so it will not inflate next time
                    bitmap = null;
                    thumbnailBitmap = null;
                } else {
                    Toast.makeText(getContext(), "An error occurred during fetching", Toast.LENGTH_SHORT).show();
                }
            });

        }
    }


    private class downloadFile extends AsyncTask<String, Integer, String> {
        String url = "";
        ProgressDialog progressDialog;

        private downloadFile(String url) {
            this.url = url;
        }

        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("Downloading ...");
            progressDialog.setProgressStyle(1);
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.setMax(100);
            progressDialog.show();
        }

        protected String doInBackground(String... args) {
            type = false;
            String checkType, imageUrl, videoUrl, thumbnail_url, isPrivate, start, end;

            start = url.substring(url.indexOf("is_private") + 12);
            isPrivate = start.substring(0, 4);
            if (isPrivate.equals("true")) {
                getActivity().runOnUiThread(() ->
                        Toast.makeText(getContext(), "Sorry this account is private", Toast.LENGTH_SHORT).show()
                );
            } else {
                start = "";
                end = "";
                start = url.substring(url.indexOf("is_video") + 10);
                checkType = start.substring(0, 4);
                if (checkType.equals("true")) {
                    type = true;
                }
                //set image url
                imageUrl = url.substring(url.indexOf("display_url") + 14, url.lastIndexOf("display_resources") - 3);
                imageUrl = imageUrl.replace("\\u0026", "&");
                //checking if the file is image or video

                if (type) {
                    //set video url
                    start = "";
                    end = "";
                    videoUrl = url.substring(url.indexOf("video_url") + 12, url.lastIndexOf("video_view_count") - 3);
                    videoUrl = videoUrl.replace("\\u0026", "&");

                    thumbnail_url = url.substring(url.indexOf("thumbnail_src") + 16, url.lastIndexOf("?"));
                    thumbnail_url = thumbnail_url.replace("\\u0026", "&");
                    getThumbnail(videoUrl, thumbnail_url);
                } else {
                    getImage(imageUrl);
                }

            }

            return null;
        }

        private void getImage(String imageUrl) {
            HttpURLConnection connection = null;
            try {
                connection = (HttpURLConnection) new URL(imageUrl).openConnection();
                connection.connect();
                bitmap = BitmapFactory.decodeStream(connection.getInputStream());
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        private void getThumbnail(String videoUrl, String thumbnailUrl) {
            HttpURLConnection connection = null;
            try {
                connection = (HttpURLConnection) new URL(thumbnailUrl).openConnection();
                connection.connect();
                thumbnailBitmap = BitmapFactory.decodeStream(connection.getInputStream());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

//        private void downloadVideo(String videoUrl) {
//            URL urlToDownload;
//            int count;
//            URLConnection connection = null;
//            try {
//                urlToDownload = new URL(videoUrl);
//                connection = urlToDownload.openConnection();
//                connection.connect();
//                // getting file length
//                int lengthOfFile = connection.getContentLength();
//
//                // input stream to read file - with 8k buffer
//                InputStream input = new BufferedInputStream(urlToDownload.openStream(), 8192);
//
//                // Output stream to write file
//
//
//                File direct = new File(Environment.getExternalStorageDirectory().toString() + File.separator
//                        + Common.SAVED_FILE_NAME + File.separator + "Instagram" + File.separator + "Posts");
//
//                if (!direct.exists()) {
//                    direct.mkdirs();
//                }
//
//                String fileName = null;
//                fileName = Common.SAVED_FILE_NAME + "-" + System.currentTimeMillis() + ".mp4";
//                file = new File(direct, fileName);
//                if (file.exists()) {
//                    file.delete();
//                }
//
//                OutputStream output = new FileOutputStream(file);
//
//                byte data[] = new byte[1024];
//
//                long total = 0;
//
//                while ((count = input.read(data)) != -1) {
//                    total += count;
//                    // publishing the progress....
//                    // After this onProgressUpdate will be called
//                    publishProgress(Integer.valueOf("" + ((total * 100) / lengthOfFile)));
//
//                    // writing data to file
//                    output.write(data, 0, count);
//                }
//
//                // flushing output
//                output.flush();
//
//                // closing streams
//                output.close();
//                input.close();
//
//                type = false;
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//
//        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            progressDialog.setProgress(values[0].intValue());
        }

        protected void onPostExecute(String response) {
            super.onPostExecute(response);
            edtLink.setText("");
            progressDialog.dismiss();
            getActivity().runOnUiThread(() -> {
                if (bitmap != null || thumbnailBitmap != null) {
                    showDialog(bitmap, thumbnailBitmap);
                    //Clear bitmap so it will not inflate next time
                    bitmap = null;
                    thumbnailBitmap = null;
                } else {
                    Log.i("MyData", "onPostExecute: reaching down");
                    Toast.makeText(getContext(), "An error occurred", Toast.LENGTH_SHORT).show();
                }
            });

        }

    }

    private void showDialog(Bitmap bitmap, Bitmap thumbnailBitmap) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        View view = LayoutInflater.from(getContext()).inflate(R.layout.layout_insta_dialog, null);

        ImageView img = view.findViewById(R.id.img_download);
        TextView txtDownload = view.findViewById(R.id.txt_download_insta);
        ImageView imgPlay = view.findViewById(R.id.img_play);
        LinearLayout linearDownload = view.findViewById(R.id.linear_downlad_insta);

        linearDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getContext(), "Not implemented yet", Toast.LENGTH_SHORT).show();
            }
        });

        if (bitmap != null) {
            txtDownload.setText("Download Image");
            Glide.with(getContext()).load(bitmap).into(img);
        } else {
            imgPlay.setVisibility(View.VISIBLE);
            txtDownload.setText("Download Video");
            Glide.with(getContext()).load(thumbnailBitmap).into(img);
        }
        builder.setView(view);

        alertDialog = builder.create();
        alertDialog.show();
    }
}