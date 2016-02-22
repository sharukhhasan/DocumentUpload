package com.sharukhhasan.docupload.activities;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ImageButton;
import android.view.Window;
import android.content.Intent;
import android.widget.ListView;
import android.os.AsyncTask;
import android.app.ProgressDialog;

import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseFile;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

import com.sharukhhasan.docupload.R;
import com.sharukhhasan.docupload.adapters.DocumentListAdapter;
import com.sharukhhasan.docupload.models.Document;

/**
 * Created by Sharukh on 2/21/16.
 */
public class MainActivity extends AppCompatActivity implements AbsListView.OnScrollListener {
    ImageButton uploadButton;
    ListView listViewDocs;
    ArrayList<Document> docList = new ArrayList<>();
    DocumentListAdapter docAdapter;
    ProgressDialog mProgressDialog;

    private boolean viewBusy = false;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActionBar supportActionBar = getSupportActionBar();
        supportActionBar.setDisplayShowTitleEnabled(false);
        supportActionBar.setDisplayUseLogoEnabled(true);

        listViewDocs = (ListView) findViewById(R.id.list);

        uploadButton = (ImageButton) findViewById(R.id.uploadButton);
        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Intent uploadIntent = new Intent(getApplicationContext(), UploadActivity.class);
                startActivity(uploadIntent);
            }
        });

        new RemoteDataTask().execute();
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState)
    {
        switch (scrollState) {
            case AbsListView.OnScrollListener.SCROLL_STATE_IDLE:
                viewBusy = false;
                docAdapter.notifyDataSetChanged();
                break;
            case AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL:
                viewBusy = true;
                break;
            case AbsListView.OnScrollListener.SCROLL_STATE_FLING:
                viewBusy = true;
                break;
        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount)
    {

    }

    public boolean isViewBusy()
    {
        return viewBusy;
    }

    private class RemoteDataTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Create a progressdialog
            mProgressDialog = new ProgressDialog(MainActivity.this);
            // Set progressdialog title
            mProgressDialog.setTitle("DocUpload");
            // Set progressdialog message
            mProgressDialog.setMessage("Loading...");
            mProgressDialog.setIndeterminate(false);
            // Show progressdialog
            mProgressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            List<ParseObject> docObjs;
            try {
                // Locate the class table named "TestLimit" in Parse.com
                ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("Document");
                query.whereEqualTo("username", ParseUser.getCurrentUser().getUsername());
                query.orderByAscending("createdAt");
                docObjs = query.find();
                for (ParseObject objs : docObjs) {
                    Document doc = new Document();
                    doc.setTitle((String) objs.get("DocumentTitle"));
                    doc.setDocumentType((String) objs.get("DocumentType"));
                    ParseFile imageFile = objs.getParseFile("DocumentImage");
                    doc.setPhotoFile(imageFile);
                    docList.add(doc);
                }
            } catch (com.parse.ParseException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            // Pass the results into ListViewAdapter.java
            docAdapter = new DocumentListAdapter(MainActivity.this, docList);
            // Binds the Adapter to the ListView
            listViewDocs.setAdapter(docAdapter);
            // Close the progressdialog
            mProgressDialog.dismiss();
        }
    }
}
