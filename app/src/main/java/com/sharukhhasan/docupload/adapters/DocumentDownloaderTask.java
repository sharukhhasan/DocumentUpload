package com.sharukhhasan.docupload.adapters;

import android.graphics.Bitmap;
import android.os.AsyncTask;

import com.parse.ParseImageView;

import java.lang.ref.WeakReference;

/**
 * Created by Sharukh on 2/21/16.
 */
public class DocumentDownloaderTask extends AsyncTask<String, Void, Bitmap> {
    private final WeakReference<ParseImageView> imageViewReference;

    public DocumentDownloaderTask(ParseImageView imageView)
    {
        imageViewReference = new WeakReference<ParseImageView>(imageView);
    }

    @Override
    protected Bitmap doInBackground(String... params)
    {
        return null;
    }
}
