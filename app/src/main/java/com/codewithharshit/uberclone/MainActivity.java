package com.codewithharshit.uberclone;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.parse.LogInCallback;
import com.parse.ParseAnonymousUtils;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;
import com.shashank.sony.fancytoastlib.FancyToast;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    public void onClick(View v) {
        //this method for user to sign anonymously
        if(edtDriverorPassenger.getText().toString().equals("Driver") || edtDriverorPassenger.getText().toString().equals("Passenger")){

            if (ParseUser.getCurrentUser() != null) {
                ParseAnonymousUtils.logIn(new LogInCallback() {
                    @Override
                    public void done(ParseUser user, ParseException e) {
                        if (user != null && e == null) {

                            FancyToast.makeText(MainActivity.this, "We have an anonymous user", Toast.LENGTH_SHORT, FancyToast.SUCCESS, false).show();
                            user.put("as", edtDriverorPassenger.getText().toString());

                            user.saveInBackground(new SaveCallback() {
                                @Override
                                public void done(ParseException e) {
                                    transitionToPassengerActivity();
                                }
                            });
                        }else{
                            FancyToast.makeText(MainActivity.this, "EROOR", Toast.LENGTH_SHORT, FancyToast.ERROR, false).show();
                        }
                    }
                });
            }else{
                FancyToast.makeText(MainActivity.this, "ERerOOR", Toast.LENGTH_SHORT, FancyToast.ERROR, false).show();
            }
        }else{
            FancyToast.makeText(MainActivity.this, "Are you a Driver or a Passenger", Toast.LENGTH_SHORT, FancyToast.INFO, false).show();

        }

    }

    enum State{
        SIGNUP, LOGIN
    }

    private State state;
    private Button btnSignUpLogin, btnOneTimeLogin;
    private RadioButton driverRadioButton, passengerRadioButton;
    private EditText edtUserName, edtPassword, edtDriverorPassenger;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnSignUpLogin = findViewById(R.id.btnSignUpLogin);
        btnOneTimeLogin = findViewById(R.id.btnOneTimeLogin);
        driverRadioButton = findViewById(R.id.rdbDriver);
        passengerRadioButton = findViewById(R.id.rdbPassenger);

        btnOneTimeLogin.setOnClickListener(this);

        state = State.SIGNUP;

        edtUserName = findViewById(R.id.edtUserName);
        edtPassword = findViewById(R.id.edtPassword);
        edtDriverorPassenger = findViewById(R.id.edtDorP);

        btnSignUpLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(state == State.SIGNUP){

                    if(driverRadioButton.isChecked() == false && passengerRadioButton.isChecked() == false){
                        FancyToast.makeText(MainActivity.this, "Are you a Driver or a Passenger", Toast.LENGTH_SHORT, FancyToast.INFO, false).show();
                        return;   //this return statement means that do not execute other codes after this return statement
                    }

                    ParseUser appUser = new ParseUser();
                    appUser.setUsername(edtUserName.getText().toString());
                    appUser.setPassword(edtPassword.getText().toString());
                    if (driverRadioButton.isChecked()){
                        appUser.put("as", "Driver");
                    } else if (passengerRadioButton.isChecked()) {
                        appUser.put("as", "Passenger");
                    }
                    appUser.signUpInBackground(new SignUpCallback() {
                        @Override
                        public void done(ParseException e) {
                            if(e == null){
                                FancyToast.makeText(MainActivity.this, "Signed Up", Toast.LENGTH_SHORT, FancyToast.SUCCESS, false).show();
                                transitionToPassengerActivity();
                            }
                        }
                    });
                } else if (state == State.LOGIN) {


                    ParseUser.logInInBackground(edtUserName.getText().toString(), edtPassword.getText().toString(), new LogInCallback() {
                        @Override
                        public void done(ParseUser user, ParseException e) {
                            if (user != null && e == null) {
                                FancyToast.makeText(MainActivity.this, "User Logged in.", Toast.LENGTH_SHORT, FancyToast.SUCCESS, false).show();
                                transitionToPassengerActivity();
                            }
                        }
                    });
                }
            }
        });

        if (ParseUser.getCurrentUser() != null) {//this mean the user has already signed up or logged in
            transitionToPassengerActivity();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.my_menu, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()){

            case R.id.loginItem:

                if(state == State.SIGNUP){
                    state = State.LOGIN;
                    item.setTitle("Sign Up");
                    btnSignUpLogin.setText("LOGIN");
                }else if (state == State.LOGIN){
                    state = State.SIGNUP;
                    item.setTitle("Login");
                    btnSignUpLogin.setText("SIGN UP");
                }

        }

        return super.onOptionsItemSelected(item);
    }

    private void transitionToPassengerActivity(){
        if (ParseUser.getCurrentUser() != null){
            if (ParseUser.getCurrentUser().get("as").equals("Passenger")){

                Intent intent = new Intent(MainActivity.this, PassengerActivity.class);
                startActivity(intent);
            }
        }
    }
}
