package com.sharukhhasan.docupload.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.Toolbar;
import android.support.design.widget.NavigationView;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ImageButton;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.Menu;
import android.content.Intent;
import android.widget.ListView;
import android.os.AsyncTask;
import android.app.Fragment;

import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseFile;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

import com.sharukhhasan.docupload.R;
import com.sharukhhasan.docupload.adapters.DocumentListAdapter;
import com.sharukhhasan.docupload.models.Document;
import com.sharukhhasan.docupload.fragments.SettingsFragment;

/**
 * Created by Sharukh on 2/21/16.
 */
public class MainActivity extends AppCompatActivity implements AbsListView.OnScrollListener {
    ImageButton uploadButton;
    ListView listViewDocs;
    ArrayList<Document> docList = new ArrayList<>();
    DocumentListAdapter docAdapter;
    DrawerLayout mDrawer;
    Toolbar toolbar;

    private boolean viewBusy = false;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Find our drawer view
        mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        listViewDocs = (ListView) findViewById(R.id.list);

        uploadButton = (ImageButton) findViewById(R.id.uploadButton);
        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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

    private void setupDrawerContent(NavigationView navigationView)
    {
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem)
            {
                selectDrawerItem(menuItem);
                return true;
            }
        });
    }

    public void selectDrawerItem(MenuItem menuItem)
    {
        // Create a new fragment
        //Fragment fragment = null;
        Intent intent = null;

        //Class fragmentClass;
        switch(menuItem.getItemId())
        {
            case R.id.documents_action:
                intent = new Intent(getApplicationContext(), MainActivity.class);
                break;
            case R.id.upload_action:
                //fragmentClass = SecondFragment.class;
                intent = new Intent(getApplicationContext(), UploadActivity.class);
                break;
            case R.id.settings_action:
                //fragmentClass = ThirdFragment.class;
                intent = new Intent(getApplicationContext(), SettingsFragment.class);
                break;
            default:
                intent = new Intent(getApplicationContext(), MainActivity.class);
        }

        try {
            //fragment = (Fragment) fragmentClass.newInstance();
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Insert the fragment by replacing any existing fragment
        //FragmentManager fragmentManager = getSupportFragmentManager();
        //fragmentManager.beginTransaction().replace(R.id.flContent, fragment).commit();

        // Highlight the selected item, update the title, and close the drawer
        menuItem.setChecked(true);
        setTitle(menuItem.getTitle());
        mDrawer.closeDrawers();
    }

    public boolean isViewBusy()
    {
        return viewBusy;
    }

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
                for (ParseObject objs : docObjs) {
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
        protected void onPostExecute(Void result) {
            // Pass the results into ListViewAdapter.java
            docAdapter = new DocumentListAdapter(MainActivity.this, docList);
            // Binds the Adapter to the ListView
            listViewDocs.setAdapter(docAdapter);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        if(item.getItemId() == R.id.action_logout)
        {
            ParseUser.logOut();
            Intent loginIntent = new Intent(this, LoginActivity.class);
            startActivity(loginIntent);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState)
    {
        super.onPostCreate(savedInstanceState);
    }
}
