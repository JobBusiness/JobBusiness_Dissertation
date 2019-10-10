package com.example.jobbusiness_dissertation;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import com.example.jobbusiness_dissertation.Login.LoginActivity;
import com.example.jobbusiness_dissertation.Signup.SignupActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

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

    public LinearLayout buttonSignup, buttonLogin;
    public  CardView buttonOne, buttonTwo, buttonThree, buttonFour;
    public  MenuInflater menuInflater;

    private FirebaseAuth databaseAuthentication;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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

      //  FirebaseUser user = databaseAuthentication.getCurrentUser();
        //String userUID = user.getUid();
       // String userID = user.getDisplayName();
       // databaseReference = FirebaseDatabase.getInstance().getReference("Jobseeker").child().











    }

    /*--Floating action button (maybe can used for future improvement--*/
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
         /*Floating action bar
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/







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

    /*
    ----------Overflow menu----------
    */
    @Override
    public boolean onCreateOptionsMenu(Menu mainOverflowMenu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        // getMenuInflater().inflate(R.menu.menu_main, menu);
        menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_main, mainOverflowMenu);

        return true; // super.onCreateOptionsMenu(menu)capture the onclick menu when user trigger the overflow menu

    }

    @Override
    // Code adapted from https://www.codingdemos.com/android-options-menu-icon/
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_settings_refresh:
                Toast.makeText(MainActivity.this, item.getTitle().toString(), Toast.LENGTH_SHORT).show();
                break;

            case R.id.action_settings_logout:
                // code adapted from https://firebase.google.com/docs/auth/android/custom-auth
                FirebaseAuth.getInstance().signOut();
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


    //SetOnClick listener to android cardView
    @Override
    public void onClick(View button) {

        switch (button.getId()){
            case R.id.cardViewOne:
                Intent navigationOne = new Intent(MainActivity.this, JobSearchActivity.class);
                startActivity(navigationOne);
                break;

            case R.id.cardViewTwo:
                Intent navigationTwo = new Intent(this, JobSearchActivity.class);
                startActivity(navigationTwo);
                break;

            case R.id.cardViewThree:
                break;

            case R.id.cardViewFour:
                break;

            case R.id.btn_signup:
                Intent  navigationSignUp = new Intent(MainActivity.this, SignupActivity.class);
                startActivity(navigationSignUp);
                break;

            case R.id.btn_login:
                Intent  navigationLogIn = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(navigationLogIn);
                break;
        }
    }

}
