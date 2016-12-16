package com.example.johanringstrom.fragment_grocode;

/**
 * Created by Pierre on 2016-11-18.
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


public class SignupActivity extends AppCompatActivity {
    private static final String TAG = "SignupActivity";
    private EditText nameText,emailText,passwordText;
    private Button signupButton;
    private TextView loginLink;

    Connection conn;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        conn = new Connection(SignupActivity.this,"");
        conn.unSubscribe();

        // Bind views.
        nameText = (EditText)  this.findViewById(R.id.input_name);
        emailText = (EditText) this.findViewById(R.id.input_email);
        passwordText = (EditText) this.findViewById(R.id.input_password);
        signupButton = (Button) this.findViewById(R.id.btn_signup);
        loginLink = (TextView) this.findViewById(R.id.link_login);

        //runs signup() on click
        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signup();
            }
        });

        // Finish the registration screen and return to the LoginActivity
        loginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }

            });
    }

    /*
     * When running signup():
     * Checks validate,
     * Creates progressdialog,
     * New runnable. runs OnSignupSuccess(),
     * Close progressdialog
     */

    public void signup() {
        Log.d(TAG, "Signup");

        if (!validate()) {
            onSignupFailed();
            return;
        }

        // disables click events
        signupButton.setEnabled(false);

        //Opens new progressdialog.
        final ProgressDialog progressDialog = new ProgressDialog(SignupActivity.this,
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Creating Account...");
        progressDialog.show();

        String name = nameText.getText().toString();
        String email = emailText.getText().toString();
        String password = passwordText.getText().toString();
        conn.clientId=email;
        conn.publish("register",new String[]{"register",email,password,name});

        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        // On complete call onSignupSuccess
                        try {
                            new Thread() {
                                public void run() {
                                    //subscribe to topic Gro/email
                                    conn.subscribeToTopic();
                                }
                            }.start();
                        }catch (Exception e) {
                            e.printStackTrace();
                            }
                        //send a signup request
                        conn.loggedin(emailText.getText().toString(), passwordText.getText().toString());
                        conn.loggedin(emailText.getText().toString(), passwordText.getText().toString());
                        onSignupSuccess();
                        progressDialog.dismiss();
                    }
                }, 3000);
    }

    /*
     * Finish SignupActivity and logs in directly.
     */
    public void onSignupSuccess() {
        signupButton.setEnabled(true);
        Intent intent = new Intent(getApplicationContext(),MainActivity.class);
        startActivity(intent);
        finish();
    }

    public void onSignupFailed() {
        Toast.makeText(getBaseContext(), "Signup failed", Toast.LENGTH_LONG).show();
        // enables click events
        signupButton.setEnabled(true);
    }

    /*
     * Checks if the user have filled in the fields correctly and returns true if that's the case.
     */

    public boolean validate() {
        boolean valid = true;
        String name = nameText.getText().toString();
        String email = emailText.getText().toString();
        String password = passwordText.getText().toString();

        if (name.isEmpty() || name.length() < 3) {
            nameText.setError("at least 3 characters");
            valid = false;
        } else {
            nameText.setError(null);
        }

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

}
