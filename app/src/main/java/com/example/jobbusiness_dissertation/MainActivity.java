package com.example.jobbusiness_dissertation;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import com.example.jobbusiness_dissertation.Login.LoginActivity;
import com.google.firebase.auth.FirebaseAuth;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import android.os.Handler;
import android.view.MenuInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private LinearLayout buttonSignup, buttonLogin;
    private CardView buttonOne, buttonTwo, buttonThree, buttonFour;
    private MenuInflater menuInflater;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Toolbar toolbar = findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);
        /*FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/
        //Header methods on activity main
        buttonSignup = findViewById(R.id.btn_signup);
        buttonSignup.setOnClickListener(this);
        buttonLogin = findViewById(R.id.btn_login);
        buttonLogin.setOnClickListener(this);

        //Main menu methods on activity_main
        //Implement onClickListener to cardView
        buttonOne = findViewById(R.id.cardViewOne);
        buttonOne.setOnClickListener(this);
        //Implement onClickListener to cardView
        buttonTwo = findViewById(R.id.cardViewTwo);
        buttonTwo.setOnClickListener(this);
        //Implement onClickListener to cardView
        buttonThree = findViewById(R.id.cardViewThree);
        buttonThree.setOnClickListener(this);
        //Implement onClickListener to cardView
        buttonFour = findViewById(R.id.cardViewFour);
        buttonFour.setOnClickListener(this);

        //Toolbar show up overflow menu
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);



        /*Floating action bar
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/
    }


    @Override
    public void onClick(View v) {


        if (v == buttonOne ) {
            Intent nav = new Intent(getApplicationContext(), WelcomeActivity.class);
            startActivity(nav);
        }
        else if (v == buttonTwo) {
            Intent nav = new Intent(this, WelcomeActivity.class);
            startActivity(nav);
        }
        else if (v == buttonThree) {
            Intent nav = new Intent(this, WelcomeActivity.class);
            startActivity(nav);
        }
        else if (v == buttonFour) {
            Intent nav = new Intent(this, WelcomeActivity.class);
            startActivity(nav);
        }

        else if (v == buttonSignup) {
            Intent  nav = new Intent(MainActivity.this, SignupActivity.class);
            startActivity(nav);

        }
        else if (v == buttonLogin) {
            Intent  nav = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(nav);
        }

    }

    // Implement dialog box for user with option either to exit the application
    //code adapted from https://www.youtube.com/watch?v=WpnM1BITW1Y
    @Override
    public void onBackPressed()
    {

        AlertDialog.Builder setDialog = new AlertDialog.Builder(this);
        setDialog.setTitle("Exit Application");
        setDialog.setMessage("Are you sure you want to exit ?");
        setDialog.setCancelable(true);//Applied to dialog box to cancel it or not

        setDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int where) {
                MainActivity.super.onBackPressed();
                finishAffinity();// clear the previous stacks activities


            }
        });
        setDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {

                dialogInterface.cancel();

            }
        });

        //Show alert dialog message
        AlertDialog dialogmessage= setDialog.create();
        dialogmessage.show();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        // getMenuInflater().inflate(R.menu.menu_main, menu);
        menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_main, menu);

        return true; // super.onCreateOptionsMenu(menu)capture the onclick menu when user trigger the overflow menu

    }

    @Override
    // This belongs to menu_main.xml (Hamburger icon navigation)
    // Code adapted from https://www.codingdemos.com/android-options-menu-icon/
    //Toast.makeText(MainActivity.this, item.getTitle().toString(), Toast.LENGTH_SHORT).show();
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        switch (item.getItemId()) {
            case R.id.action_settings_refresh:
                Toast.makeText(MainActivity.this, item.getTitle().toString(), Toast.LENGTH_SHORT).show();
                break;

            case R.id.action_settings_logout:
                // code adapted from https://firebase.google.com/docs/auth/android/custom-auth
                FirebaseAuth.getInstance().signOut();  //Firebase log out
                Toast.makeText(MainActivity.this,"Your have sign out",Toast.LENGTH_SHORT).show();
                Intent Home = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(Home);
                finish();
                break;

            case R.id.action_settings_exit:
                //code adapted from https://stackoverflow.com/questions/36676412/leaked-window-when-exit-app-through-popup-menu
                Handler closeOverflow_menu = new Handler();
                closeOverflow_menu.postDelayed(new Runnable() {
                    public void run() {
                        finishAffinity();
                    }
                },300); //close delay for 3 milliseconds on the app after menu close as to avoid window leaked error
                return true;

            //noinspection SimplifiableIfStatement
            default: return super.onOptionsItemSelected(item);
        }
        return true;
    }
}
