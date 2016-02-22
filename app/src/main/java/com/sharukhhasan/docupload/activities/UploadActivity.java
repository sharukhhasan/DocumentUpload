package com.sharukhhasan.docupload.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.app.Fragment;
import android.app.FragmentManager;
import android.view.Window;
import android.view.WindowManager;

import com.sharukhhasan.docupload.R;
import com.sharukhhasan.docupload.models.Document;
import com.sharukhhasan.docupload.fragments.NewDocumentFragment;

/**
 * Created by Sharukh on 2/21/16.
 */
public class UploadActivity extends AppCompatActivity {
    private Document doc;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        doc = new Document();
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);

        // Begin with main data entry view,
        setContentView(R.layout.activity_upload);
        FragmentManager manager = getFragmentManager();
        Fragment fragment = manager.findFragmentById(R.id.fragmentContainer);

        if(fragment == null)
        {
            fragment = new NewDocumentFragment();
            manager.beginTransaction().add(R.id.fragmentContainer, fragment).commit();
        }
    }

    public Document getCurrentDocument()
    {
        return doc;
    }
}
