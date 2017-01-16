package com.example.johanringstrom.fragment_grocode;

/**
 * Created by Pierre on 2016-11-09.
 */

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";
    private EditText emailText,passwordText;
    private Button loginButton;
    private TextView signupLink;
    Connection conn;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //init the connection here with an empty client id
        conn = new Connection(LoginActivity.this,"");

        //bind views
        emailText = (EditText) this.findViewById(R.id.input_email);
        emailText.setTextColor(getColor(R.color.iron));
        passwordText = (EditText) this.findViewById(R.id.input_password);
        passwordText.setTextColor(getColor(R.color.black));
        loginButton = (Button) this.findViewById(R.id.btn_login);
        signupLink = (TextView) this.findViewById(R.id.link_signup);
        signupLink.setTextColor(getColor(R.color.iron));
        //runs login() on click
        loginButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                login();
            }
        });
        //starts new SignupActivity on click.
        signupLink.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SignupActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    /*
         * When running login():
         * Checks validate,
         * Creates progressdialog,
         * New runnable. runs OnLoginSucess(),
         * Close progressdialog
         */

    public void login() {
        Log.d(TAG, "Login");

        // if not validate(), returns onLoginFailed().
        if (!validate()) {
            onLoginFailed();
            return;
        }
        // disables click events
        loginButton.setEnabled(false);

        //Opens new progressdialog.
        final ProgressDialog progressDialog = new ProgressDialog(LoginActivity.this,
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Authenticating...");
        progressDialog.show();
        // send a login request, extra to also create kinda delay for the messageArrived to get the reply"
        //
        try {
            conn.loggedin(emailText.getText().toString(),passwordText.getText().toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        //set the client ID to the provided email
        String email = emailText.getText().toString();
        conn.clientId = email;

        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        try {

                            new Thread() {
                                public void run() {
                                    //subscribe to topic Gro/email
                                    conn.subscribeToTopic();
                                }
                            }.start();
                            //send a login request
                            conn.loggedin(emailText.getText().toString(), passwordText.getText().toString());
                            conn.loggedin(emailText.getText().toString(), passwordText.getText().toString());
                            //move on to the main activity, we'll check of the failure/success of the login there then we'll either stay or get back here
                            onLoginSuccess();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        progressDialog.dismiss();
                    }
                }, 3000);
    }

    // Disable going back to the MainActivity
    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

    // Starts new MainActivity.
    public void onLoginSuccess() {
        // enables click events
        loginButton.setEnabled(true);
        Intent intent = new Intent(getApplicationContext(),MainActivity.class);
        startActivity(intent);
        finish();
    }

    public void onLoginFailed() {
        Toast.makeText(getBaseContext(), "Login failed", Toast.LENGTH_LONG).show();
        // enables click events
        loginButton.setEnabled(true);
        //unsubscribe from the topic if login fails
        conn.unSubscribe();
    }

    //Checks if the user have filled in the fields correctly and returns true if that's the case.
    public boolean validate() {
        boolean valid = true;
        String email = emailText.getText().toString();
        String password = passwordText.getText().toString();

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailText.setError("enter a valid email address");
            valid = false;
        } else {
            emailText.setError(null);
        }

        if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
            passwordText.setError("between 4 and 10 alphanumeric characters");
            valid = false;
        } else {
            passwordText.setError(null);
        }

        return valid;
    }

    //Fixes leaked ServiceConnection by setting connection to null.
    @Override
    public void finish() {
        conn = null;
    }
}
