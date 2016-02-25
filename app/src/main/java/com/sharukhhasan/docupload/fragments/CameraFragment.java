package com.sharukhhasan.docupload.fragments;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import android.app.Fragment;
import android.app.FragmentManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import com.sharukhhasan.docupload.R;
import com.sharukhhasan.docupload.activities.UploadActivity;

/**
 * Created by Sharukh on 2/21/16.
 */
public class CameraFragment extends Fragment {
    public static final String TAG = "CameraFragment";
    private String document_Name;
    private Camera camera;
    private SurfaceView surfaceView;
    private ParseFile photoFile;
    private ImageButton photoButton;
    private ImageButton saveButton;
    private ImageButton rotateButton;
    private byte[] img_data;
    private float angle = 90;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState)
    {
        View v = inflater.inflate(R.layout.fragment_camera, parent, false);

        photoButton = (ImageButton) v.findViewById(R.id.camera_photo_button);
        photoButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (camera == null) {
                    return;
                }

                camera.takePicture(new Camera.ShutterCallback() {

                    @Override
                    public void onShutter() {
                        // nothing to do
                    }

                }, null, new Camera.PictureCallback() {

                    @Override
                    public void onPictureTaken(byte[] data, Camera camera)
                    {
                        img_data = data;
                        saveButton.setVisibility(View.VISIBLE);
                        rotateButton.setVisibility(View.VISIBLE);
                        //saveScaledPhoto(data);
                    }

                });

            }
        });

        saveButton = (ImageButton) v.findViewById(R.id.save_photo_button);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (img_data != null) {
                    saveScaledPhoto(img_data);
                }
            }
        });
        saveButton.setVisibility(View.GONE);

        rotateButton = (ImageButton) v.findViewById(R.id.rotate_photo_button);
        rotateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                angle += 90;

            }
        });
        rotateButton.setVisibility(View.GONE);

        if(camera == null)
        {
            try {
                camera = Camera.open();
                photoButton.setEnabled(true);
            } catch (Exception e) {
                Log.e(TAG, "No camera with exception: " + e.getMessage());
                photoButton.setEnabled(false);
                Toast.makeText(getActivity(), "No camera detected", Toast.LENGTH_LONG).show();
            }
        }

        document_Name = getArguments().getString("documentName");

        surfaceView = (SurfaceView) v.findViewById(R.id.camera_surface_view);
        SurfaceHolder holder = surfaceView.getHolder();
        holder.addCallback(new Callback() {

            public void surfaceCreated(SurfaceHolder holder)
            {
                try {
                    if(camera != null)
                    {
                        camera.setDisplayOrientation(90);
                        camera.setPreviewDisplay(holder);
                        camera.startPreview();
                    }
                } catch (IOException e) {
                    Log.e(TAG, "Error setting up preview", e);
                }
            }

            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height)
            {
                // nothing to do here
            }

            public void surfaceDestroyed(SurfaceHolder holder)
            {
                // nothing here
            }

        });

        return v;
    }

    private void saveScaledPhoto(byte[] data)
    {
        // Resize photo from camera byte array
        Bitmap documentImage = BitmapFactory.decodeByteArray(data, 0, data.length);
        Bitmap documentImageScaled = Bitmap.createScaledBitmap(documentImage, 200, 200 * documentImage.getHeight() / documentImage.getWidth(), false);

        // Override Android default landscape orientation and save portrait
        Matrix matrix = new Matrix();

        matrix.postRotate(angle);
        Bitmap rotatedScaledDocImage = Bitmap.createBitmap(documentImageScaled, 0, 0, documentImageScaled.getWidth(), documentImageScaled.getHeight(), matrix, true);

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        rotatedScaledDocImage.compress(Bitmap.CompressFormat.JPEG, 100, bos);

        byte[] scaledData = bos.toByteArray();

        // Save the scaled image to Parse
        String fileNameBuilder = ParseUser.getCurrentUser().getUsername() + "_" + document_Name + ".jpg";
        photoFile = new ParseFile(fileNameBuilder, scaledData);
        photoFile.saveInBackground(new SaveCallback() {

            public void done(ParseException e)
            {
                if(e != null)
                {
                    Toast.makeText(getActivity(), "Error saving: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
                else
                {
                    addPhotoToDocumentsAndReturn(photoFile);
                }
            }
        });
    }

    private void addPhotoToDocumentsAndReturn(ParseFile photoFile)
    {
        ((UploadActivity) getActivity()).getCurrentDocument().setPhotoFile(photoFile);
        FragmentManager fm = getActivity().getFragmentManager();
        fm.popBackStack("NewDocumentFragment", FragmentManager.POP_BACK_STACK_INCLUSIVE);
    }

    @Override
    public void onResume()
    {
        super.onResume();
        if(camera == null)
        {
            try {
                camera = Camera.open();
                photoButton.setEnabled(true);
            } catch (Exception e) {
                Log.i(TAG, "No camera: " + e.getMessage());
                photoButton.setEnabled(false);
                Toast.makeText(getActivity(), "No camera detected", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onPause()
    {
        if(camera != null)
        {
            camera.stopPreview();
            camera.release();
        }
        super.onPause();
    }
}
