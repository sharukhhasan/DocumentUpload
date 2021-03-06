package com.sharukhhasan.docupload.activities;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.content.Intent;

import java.util.Locale;

import com.parse.ParseUser;
import com.parse.ParseException;
import com.parse.SignUpCallback;

import butterknife.ButterKnife;
import butterknife.InjectView;

import com.sharukhhasan.docupload.R;

/**
 * Created by Sharukh on 2/21/16.
 */
public class SignupActivity extends AppCompatActivity {
    // Bind user inputs
    @InjectView(R.id.input_name) EditText _usernameText;
    @InjectView(R.id.input_email) EditText _emailText;
    @InjectView(R.id.input_password) EditText _passwordText;
    @InjectView(R.id.input_confirmPassword) EditText _confirmPasswordText;
    @InjectView(R.id.btn_signup) Button _signupButton;
    @InjectView(R.id.link_login) TextView _loginLink;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        ButterKnife.inject(this);

        // onClick functionality for sign up button
        _signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Sign up user
                signup();
            }
        });

        // onClick functionality for login button
        _loginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Finish the registration screen and return to the Login activity
                finish();
            }
        });
    }

    // Signup function
    public void signup()
    {
        if(!validate())
        {
            onSignupFailed();
            return;
        }

        _signupButton.setEnabled(false);

        final ProgressDialog progressDialog = new ProgressDialog(SignupActivity.this, R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Creating Account...");
        progressDialog.show();

        String username = _usernameText.getText().toString();
        String userEmail = _emailText.getText().toString();
        String userPassword = _passwordText.getText().toString();

        createNewUser(userEmail, username.toLowerCase(Locale.getDefault()), userPassword);

        new android.os.Handler().postDelayed(new Runnable() {
            public void run() {
                onSignupSuccess();
                progressDialog.dismiss();
            }
        }, 3000);
    }


    public void onSignupSuccess()
    {
        _signupButton.setEnabled(true);
        setResult(RESULT_OK, null);
        finish();
    }

    public void onSignupFailed()
    {
        Toast.makeText(getBaseContext(), "Login failed", Toast.LENGTH_LONG).show();

        _signupButton.setEnabled(true);
    }

    // check user inputs validity
    public boolean validate()
    {
        boolean valid = true;

        String name = _usernameText.getText().toString();
        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();

        if(name.isEmpty() || name.length() < 3)
        {
            _usernameText.setError("at least 3 characters");
            valid = false;
        }
        else
        {
            _usernameText.setError(null);
        }

        if(email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches())
        {
            _emailText.setError("enter a valid email address");
            valid = false;
        }
        else
        {
            _emailText.setError(null);
        }

        if(password.isEmpty() || password.length() < 4 || password.length() > 10)
        {
            _passwordText.setError("between 4 and 10 alphanumeric characters");
            valid = false;
        }
        else
        {
            _passwordText.setError(null);
        }

        return valid;
    }

    // Create new user
    private void createNewUser(String email, String username, String password)
    {
        ParseUser user = new ParseUser();   // Initialize new parse user
        user.setEmail(email);               // Set user inputted email
        user.setUsername(username);         // Set user inputted username
        user.setPassword(password);         // Set user inputted password

        // attempt to sign up user with given credentials
        user.signUpInBackground(new SignUpCallback() {
            public void done(ParseException e)
            {
                if(e == null)
                {
                    signUpAlert("Account Created Successfully");
                    Intent in = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(in);
                }
                else
                {
                    signUpAlert("Account already taken.");
                }
            }
        });
    }

    protected void signUpAlert(String msg)
    {
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
    }
}
