package com.sharukhhasan.docupload.activities;

import android.os.Bundle;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.view.Window;
import android.widget.AbsListView;
import android.content.Intent;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.os.AsyncTask;
import android.view.View;
import android.content.res.Configuration;
import android.widget.AdapterView;

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
    private ListView listViewDocs;
    private ArrayList<Document> docList = new ArrayList<>();
    private DocumentListAdapter docAdapter;
    private ActionBarDrawerToggle mDrawerToggle;
    private Button uploadButton;

    private boolean viewBusy = false;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Initialize upload button (used only if user has no previous documents)
        uploadButton = (Button) findViewById(R.id.btn_upload);

        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Intent uploadIntent = new Intent(getApplicationContext(), UploadActivity.class);
                startActivity(uploadIntent);
            }
        });

        // Find our drawer view
        DrawerLayout mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        // String array of choices in navigation drawer
        String[] mNavChoices = getResources().getStringArray(R.array.nav_array);

        // Initalizing navigation drawer, and onClick functionality
        ListView mDrawerList = (ListView) findViewById(R.id.left_drawer);
        mDrawerList.setAdapter(new ArrayAdapter<String>(this, R.layout.drawer_list_item, mNavChoices));
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

        // Setting action of navigation drawer
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close){

            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view)
            {
                super.onDrawerClosed(view);
            }

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView)
            {
                super.onDrawerOpened(drawerView);
            }
        };

        // Set the drawer toggle as the DrawerListener
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        listViewDocs = (ListView) findViewById(R.id.list);

        new RemoteDataTask().execute();
    }

    // onClick for ListView in the navigation drawer
    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id)
        {
            Intent intent = null;

            switch(position)
            {
                case 0:
                    intent = new Intent(getApplicationContext(), MainActivity.class);
                    break;
                case 1:
                    intent = new Intent(getApplicationContext(), UploadActivity.class);
                    break;
                case 2:
                    ParseUser.logOut();
                    intent = new Intent(getApplicationContext(), LoginActivity.class);
                    break;
                default:
                    intent = new Intent(getApplicationContext(), MainActivity.class);
            }

            if(intent != null)
            {
                startActivity(intent);
            }
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState)
    {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig)
    {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    // determines when list has been scrolled, which leads to data pull
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

    // viewBusy is true when list is scrolling
    public boolean isViewBusy()
    {
        return viewBusy;
    }

    // asynchronously grab data from parse
    private class RemoteDataTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            List<ParseObject> docObjs;
            try {
                // Locate the class table named "TestLimit" in Parse.com
                ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("Document");
                query.whereEqualTo("username", ParseUser.getCurrentUser().getUsername());
                query.orderByAscending("createdAt");
                docObjs = query.find();

                for(ParseObject objs : docObjs)
                {
                    Document doc = new Document();
                    doc.setTitle((String) objs.get("DocumentTitle"));
                    doc.setDocumentType((String) objs.get("DocumentType"));
                    ParseFile imageFile = objs.getParseFile("DocumentImage");
                    doc.setPhotoURL(imageFile.getUrl());
                    doc.setPhotoFile(imageFile);
                    docList.add(doc);
                }
            } catch (com.parse.ParseException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result)
        {
            // Pass the results into ListViewAdapter.java
            docAdapter = new DocumentListAdapter(MainActivity.this, docList);
            // Binds the Adapter to the ListView
            listViewDocs.setAdapter(docAdapter);
        }
    }
}
