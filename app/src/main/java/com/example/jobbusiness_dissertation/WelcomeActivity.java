package com.example.jobbusiness_dissertation;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ProgressBar;

public class WelcomeActivity extends AppCompatActivity {

    int timeout = 5000;  // Activity welcome screen set delay duration to 5 milliseconds
    private ImageView welcomeScreenLogo;
     ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        // Code adapted from http://devdeeds.com/how-to-create-a-5-seconds-splash-screen-in-android/
        // Code adapted from http://www.itcuties.com/android/how-to-create-android-splash-screen/ (updated)

            welcomeScreenLogo= findViewById(R.id.welcomescreen_Logo);
            progressBar= findViewById(R.id.progressbar_circular) ;

            //Load animation
            Animation logofade_In = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.anim_welcome_activity);
            //animation for logo fade in set delay duration to 2 milliseconds
            logofade_In.setDuration(2000);
            //implement the animation for ImageView
            welcomeScreenLogo.startAnimation(logofade_In);

            Animation progressbar_Fadein = AnimationUtils.loadAnimation(WelcomeActivity.this,R.anim.anim_welcome_activity);
            //animation for progressbar circular fade in set delay duration to 1 milliseconds
            progressbar_Fadein.setDuration(3000);
            //implement the animation for ProgressBar
            progressBar.startAnimation(progressbar_Fadein);


            //full screen without an action bar
            this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
            mainActivityStart Activity = new mainActivityStart();
            Activity.start();
        }

        // private class for activity for implement the delay time on welcome screen activity
        private class mainActivityStart extends Thread{

            @Override
            public void run() {
                {
                    try {
                        Thread.sleep(timeout);
                        welcomeScreenLogo.setVisibility(View.VISIBLE);

                    }
                    catch (Exception e)
                    {
                        e.printStackTrace(); //for debugging purpose not for user display
                    }
                    finally {
                        Intent welcomeScreen = new Intent(WelcomeActivity.this, MainActivity.class);//from currently runnin activity to MainActivity
                        startActivity(welcomeScreen);

                    }
                }
            }
        }
    }

