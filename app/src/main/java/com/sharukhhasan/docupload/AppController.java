package com.sharukhhasan.docupload;

import android.app.Application;

import com.parse.ParseFacebookUtils;
import com.parse.Parse;
import com.parse.ParseUser;
import com.parse.ParseACL;
import com.parse.ParseObject;

import com.sharukhhasan.docupload.models.Document;


/**
 * Created by Sharukh on 2/21/16.
 */
public class AppController extends Application {

    @Override
    public void onCreate()
    {
        super.onCreate();

        ParseObject.registerSubclass(Document.class);
        Parse.enableLocalDatastore(this);
        Parse.initialize(this, "9elS8VcUvCA1lVClGQumHZhXXzl1ZtLbm0DeJpKM", "EV5UAegvZW41hLT0OmYh9BtkrE6frO1imVhpKbti");

        //ParseFacebookUtils.initialize(this);

        ParseUser.enableAutomaticUser();
        ParseACL defaultACL = new ParseACL();

        defaultACL.setPublicReadAccess(true);
        ParseACL.setDefaultACL(defaultACL, true);
    }

}
