package com.nsromapa.uchat.databases;

import android.app.DownloadManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

public class StickersSoundBackground extends AsyncTask<String, Void, String> {

    private static final String TAG = "StickerSoundBackground";
    private Context context;

    public StickersSoundBackground(Context context) {
        this.context = context;
    }

    @Override
    protected String doInBackground(String... strings) {

        final String fileName = strings[0];
        String fileUrl = strings[1];
        final String localLocation = strings[2];

        DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);

        Uri uri = Uri.parse(fileUrl);
        DownloadManager.Request request = new DownloadManager.Request(uri);

        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_ONLY_COMPLETION);
        request.setDestinationInExternalFilesDir(context, localLocation, fileName);
        request.setVisibleInDownloadsUi(false);

        downloadManager.enqueue(request);

        Log.d(TAG, "doInBackground: executing");

//                Glide.with(context)
//                        .asBitmap()
//                        .load(imageUrl)
//                        .into(new SimpleTarget<Bitmap>() {
//                            @Override
//                            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
//                                saveImage(resource, stickerName, localLocation);
//                            }
//                        });

        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return "doInBackground: onefile downloaded....." + localLocation + "........" + fileName;
    }


    private void saveImage(Bitmap image, String imageFileName, String directory) {
        File storageDir = new File(String.valueOf(context.getExternalFilesDir(directory)));
        boolean success = true;
        if (!storageDir.exists()) {
            success = storageDir.mkdirs();
        }
        if (success) {
            File imageFile = new File(storageDir, imageFileName + ".png");
            try {
                OutputStream fOut = new FileOutputStream(imageFile);
                image.compress(Bitmap.CompressFormat.PNG, 100, fOut);
                fOut.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            Toast.makeText(context, "saved " + imageFileName, Toast.LENGTH_SHORT).show();
        }
    }


    private void downloadAndSaveSound(String url, String soundName, String localLocation) {
        DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);

        Uri uri = Uri.parse(url);
        DownloadManager.Request request = new DownloadManager.Request(uri);

        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_ONLY_COMPLETION);
        request.setDestinationInExternalFilesDir(context, localLocation, soundName);

        downloadManager.enqueue(request);


    }


    @Override
    protected void onPostExecute(String s) {
        Log.d(TAG, s);
        super.onPostExecute(s);
    }
}
