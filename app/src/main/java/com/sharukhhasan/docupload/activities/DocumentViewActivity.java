package com.sharukhhasan.docupload.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;
import android.widget.TextView;
import android.widget.ImageView;

import com.bumptech.glide.Glide;


import com.sharukhhasan.docupload.R;

/**
 * Created by Sharukh on 2/22/16.
 */
public class DocumentViewActivity extends AppCompatActivity {
    TextView docTitle;
    TextView docType;
    ImageView docImage;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_document_view);

        // get data from DocumentListAdapter on click
        Intent listViewIntent = getIntent();

        // get document title, type, and image URL
        String title = listViewIntent.getStringExtra("DocumentTitle");
        String type = listViewIntent.getStringExtra("DocumentType");
        String imgURL = listViewIntent.getStringExtra("DocumentImageURL");

        docTitle = (TextView) findViewById(R.id.docTitleView);
        docType = (TextView) findViewById(R.id.docTypeView);
        docImage = (ImageView) findViewById(R.id.docImgView);

        // set document title to TextView
        docTitle.setText(title);

        // set document type to TextView
        docType.setText(type);

        // download and set image to ParseImageView
        Glide.with(this).load(imgURL).into(docImage);
    }
}
