package com.sharukhhasan.docupload.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.content.Intent;

import com.sharukhhasan.docupload.R;

import com.parse.ParseUser;
import com.parse.ParseAnonymousUtils;

/**
 * Created by Sharukh on 2/21/16.
 */
public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        Thread timerThread = new Thread(){
            public void run()
            {
                try{
                    sleep(3000);
                }catch(InterruptedException e){
                    e.printStackTrace();
                }finally{
                    //ParseUser currentUser = ParseUser.getCurrentUser();
                    if(ParseAnonymousUtils.isLinked(ParseUser.getCurrentUser()) || ParseUser.getCurrentUser() == null)
                    {
                        Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                        startActivity(intent);
                        finish();
                    }
                    else
                    {
                        if(ParseUser.getCurrentUser() != null)
                        {
                            Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        }
                        else
                        {
                            Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    }
                }
            }
        };
        timerThread.start();
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        finish();
    }

}
