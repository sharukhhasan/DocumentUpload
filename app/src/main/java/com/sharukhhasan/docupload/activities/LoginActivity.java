package com.sharukhhasan.docupload.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Build;
import android.view.View;
import android.content.Intent;
import android.widget.TextView;
import android.widget.Button;
import android.widget.EditText;
import android.text.TextUtils;
import android.widget.Toast;
import android.app.AlertDialog;
import android.content.DialogInterface;

import java.util.Locale;

import butterknife.ButterKnife;
import butterknife.InjectView;

import com.parse.ParseUser;
import com.parse.ParseException;
import com.parse.LogInCallback;
import com.parse.ParseFacebookUtils;

import com.facebook.FacebookSdk;

import com.sharukhhasan.docupload.R;

/**
 * Created by Sharukh on 2/21/16.
 */
public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";
    private static final int REQUEST_SIGNUP = 0;
    //private ParseUser currentUser;

    // Bind UI components
    @InjectView(R.id.input_username) EditText usernameInput;
    @InjectView(R.id.input_password) EditText passwordInput;
    @InjectView(R.id.btn_login)
    Button _loginButton;
    @InjectView(R.id.link_signup)
    TextView _signupLink;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        //FacebookSdk.sdkInitialize(this.getApplicationContext());
        setContentView(R.layout.activity_login);

        ButterKnife.inject(this);

        View myView = this.findViewById(android.R.id.content);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
        {
            myView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }

        // onClick action of login button
        _loginButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v)
            {
                String username = usernameInput.getText().toString();
                String password = passwordInput.getText().toString();
                validLogin();
                //loginUser(username.toLowerCase(Locale.getDefault()), password);
            }
        });

        // onClick action of sign up button
        _signupLink.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(getApplicationContext(), SignupActivity.class);
                startActivityForResult(intent, REQUEST_SIGNUP);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        //ParseFacebookUtils.onActivityResult(requestCode, resultCode, data);
    }

    public void validLogin()
    {
        String username = usernameInput.getText().toString();
        String password = passwordInput.getText().toString();

        boolean incorrect = false;
        View focusView = null;

        // Checks if password meets conditions
        if(TextUtils.isEmpty(password))
        {
            passwordInput.setError(getString(R.string.error_field_required));
            focusView = passwordInput;
            incorrect = true;
        }
        else if(password.length() < 4)
        {
            passwordInput.setError(getString(R.string.error_invalid_password));
            focusView = passwordInput;
            incorrect = true;
        }

        // Checks if username field is empty
        if (TextUtils.isEmpty(username))
        {
            usernameInput.setError(getString(R.string.error_field_required));
            focusView = usernameInput;
            incorrect = true;
        }

        if (incorrect) {
            focusView.requestFocus();
        } else {
            loginUser(username.toLowerCase(Locale.getDefault()), password);
        }
    }

    private void loginUser(String username, String password)
    {
        // Attempt to log users in, if credentials are correct
        ParseUser.logInInBackground(username, password, new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException e) {
                if (user != null) {
                    Intent loggedIn = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(loggedIn);
                } else {
                    Toast.makeText(getApplicationContext(), "", Toast.LENGTH_SHORT).show();

                    AlertDialog alertDialog = new AlertDialog.Builder(getApplicationContext()).create();
                    alertDialog.setTitle("Login");
                    alertDialog.setMessage("Username or Password is incorrect");
                    alertDialog.setButton("Ok", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    });

                    alertDialog.show();
                }
            }
        });
    }
}
