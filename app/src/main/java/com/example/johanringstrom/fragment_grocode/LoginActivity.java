package com.example.johanringstrom.fragment_grocode;

/**
 * Created by Pierre on 2016-11-09.
 */

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.BindView;
import butterknife.ButterKnife;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";
    private static final int REQUEST_SIGNUP = 0;

    @BindView(R.id.input_email) EditText _emailText;
    @BindView(R.id.input_password) EditText _passwordText;
    @BindView(R.id.btn_login) Button _loginButton;
    @BindView(R.id.link_signup) TextView _signupLink;
    Connection conn;
    boolean click;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        conn = new Connection(LoginActivity.this,"");
        _loginButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
//                click=!click;
                login();
//                if(click)
//                    _loginButton.callOnClick();



            }
        });

        _signupLink.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // Start the Signup activity
                Intent intent = new Intent(getApplicationContext(), SignupActivity.class);
                startActivityForResult(intent, REQUEST_SIGNUP);
                finish();
            }
        });
    }

    public void login() {
        Log.d(TAG, "Login");

        if (!validate()) {
            onLoginFailed();
            return;
        }

        _loginButton.setEnabled(false);

        final ProgressDialog progressDialog = new ProgressDialog(LoginActivity.this,
                R.style.AppTheme);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Authenticating...");
        progressDialog.show();
        try {
            conn.loggedin(_emailText.getText().toString(),_passwordText.getText().toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        String email = _emailText.getText().toString();
        conn.clientId=email;
        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        try {

                            new Thread() {
                                public void run() {
                                    conn.subscribeToTopic();
                                    conn.sub=true;
                                }
                            }.start();
//                            boolean res = conn.loggedin(_emailText.getText().toString(), _passwordText.getText().toString());
//                            res = conn.loggedin(_emailText.getText().toString(), _passwordText.getText().toString());
//                            res = conn.loggedin(_emailText.getText().toString(), _passwordText.getText().toString());
//                            res = conn.loggedin(_emailText.getText().toString(), _passwordText.getText().toString());
//                            if (!click) {
//                                click=false;
//                                if (res)
//                                    onLoginSuccess();
//                                else
//                                    onLoginFailed();
//                            }
                            conn.loggedin(_emailText.getText().toString(), _passwordText.getText().toString());
                            conn.loggedin(_emailText.getText().toString(), _passwordText.getText().toString());
                            onLoginSuccess();

                            /*new Thread(){public void run(){conn.subscribeToTopic();}}.start();
                            boolean res = conn.loggedin(_emailText.getText().toString(),_passwordText.getText().toString());
                            if(res)
                                onLoginSuccess();
                            else
                                onLoginFailed();*/
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        // onLoginFailed();
                        progressDialog.dismiss();
                    }
                }, 3000);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_SIGNUP) {
            if (resultCode == RESULT_OK) {

                // TODO: Implement successful signup logic here
                // By default we just finish the Activity and log them in automatically
                this.finish();
            }
        }
    }

    @Override
    public void onBackPressed() {
        // Disable going back to the MainActivity
        moveTaskToBack(true);
    }

    public void onLoginSuccess() {
        _loginButton.setEnabled(true);
        Intent intent = new Intent(getApplicationContext(),MainActivity.class);
        startActivity(intent);
        finish();
    }

    public void onLoginFailed() {
        Toast.makeText(getBaseContext(), "Login failed", Toast.LENGTH_LONG).show();
        _loginButton.setEnabled(true);
        conn.unSubscribe();

        conn.sub=false;

    }
    public void onLoginFailedTest() {
        _loginButton.setEnabled(true);

    }

    public boolean validate() {
        boolean valid = true;

        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _emailText.setError("enter a valid email address");
            valid = false;
        } else {
            _emailText.setError(null);
        }

        if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
            _passwordText.setError("between 4 and 10 alphanumeric characters");
            valid = false;
        } else {
            _passwordText.setError(null);
        }

        return valid;
    }
}
