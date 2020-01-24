package com.ayan.architectureapp.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import com.ayan.architectureapp.R;

import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;

public class ImageDownloader {

    private final WeakReference<ImageView> imageViewReference;
    private ImageView imageView;

    public ImageDownloader(ImageView imageView) {
        imageViewReference = new WeakReference<ImageView>(imageView);
    }

    private static Bitmap downloadBitmap(String url) {
        HttpURLConnection urlConnection = null;
        try {
            URL uri = new URL(url);
            urlConnection = (HttpURLConnection) uri.openConnection();
            int statusCode = urlConnection.getResponseCode();
            if (statusCode != HttpURLConnection.HTTP_OK) {
                return null;
            }

            InputStream inputStream = urlConnection.getInputStream();
            if (inputStream != null) {
                return BitmapFactory.decodeStream(inputStream);
            }
        } catch (Exception e) {
            assert urlConnection != null;
            urlConnection.disconnect();
            Log.e("ImageDownloader", "Error downloading image from " + url);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
        return null;
    }

    public void loadImage(String url) {

        new DownloadImage(imageViewReference).execute(url);
    }

    private static class DownloadImage extends AsyncTask<String, Void, Bitmap> {

        private final WeakReference<ImageView> imageViewReference;

        public DownloadImage(WeakReference<ImageView> imageViewReference) {
            this.imageViewReference = imageViewReference;
        }

        @Override
        protected Bitmap doInBackground(String... strings) {
            return downloadBitmap(strings[0]);
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);

            ImageView imageView = imageViewReference.get();
            if (imageView != null) {
                if (bitmap != null) {
                    imageView.setImageBitmap(bitmap);
                } else {
                    Drawable placeholder = imageView.getContext().getResources().getDrawable(R.drawable.ic_map, null);
                    imageView.setImageDrawable(placeholder);
                }
            }

        }
    }
}
