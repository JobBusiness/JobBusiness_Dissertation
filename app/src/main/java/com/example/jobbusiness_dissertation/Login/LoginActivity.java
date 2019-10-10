package com.example.jobbusiness_dissertation.Login;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.LinkMovementMethod;
import android.text.method.PasswordTransformationMethod;
import android.text.style.ClickableSpan;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jobbusiness_dissertation.MainActivity;
import com.example.jobbusiness_dissertation.R;
import com.example.jobbusiness_dissertation.Signup.SignupActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    // logcat message of the application during runtime
    private static final String tag = "LoginActivity";

    private EditText loginPassword, loginEmail, currentEmail;
    private ProgressBar progressBar;
    private CheckBox showhidepassword;
    private LinearLayout login_linearLayout;
    private TextView passwordforget;

    //widgets
    Button loginButton,loginAuthentication, closeButton, resetButtonSendLink,loginBackButton ;

    //pop up window
    private LinearLayout linearPopUp,linearBackgroundPopUp;
    private RelativeLayout loginRelativeButton, loginRelativeProgressbar;

    //Show hide password textView
    private TextView loginPasswordShowHide;

 /*
---------------Firebase----------------
*/
    private FirebaseAuth firebaseAuthentication;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;


/*
 -------------Animation---------------
*/
    private Animation setErrorAnim_LogIn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Log.d(tag, "Login onCreate: started ");

        //Code adapted from https://www.tutorialspoint.com/how-to-show-shaking-wobble-view-animation-in-android
        //Initialize animation
        setErrorAnim_LogIn = AnimationUtils.loadAnimation(LoginActivity.this, R.anim.anim_error);

        //Toolbar
        loginBackButton = findViewById(R.id.login_Backbutton);
        loginBackButton.setOnClickListener(this);

        //Login main body
        login_linearLayout = findViewById(R.id.login_LinearLayout);
        loginPassword = findViewById(R.id.login_Password);
        loginEmail = findViewById(R.id.login_Email);
        loginButton = findViewById(R.id.login_Button);
        loginButton.setOnClickListener(this);

        //Pop up window for forget password set the layout visibility
        linearBackgroundPopUp = findViewById(R.id.linear_background_popup);
        linearPopUp = findViewById(R.id.linear_window_popup);
        linearPopUp.setVisibility(View.INVISIBLE);
        linearBackgroundPopUp.setVisibility(View.INVISIBLE);
        passwordforget = findViewById(R.id.login_passwordForget);

        //Reset password page
        resetButtonSendLink = findViewById(R.id.reset_Button);
        closeButton = findViewById(R.id.window_Close_Button);
        currentEmail = findViewById(R.id.currentlogin_Email);

        //Login button layout visibility
        loginRelativeButton = findViewById(R.id.login_Button_Layout);
        loginRelativeProgressbar = findViewById(R.id.login_Progress_Circular_Layout);

        //Initially hide the progressbar layout before login
        loginRelativeProgressbar.setVisibility(View.INVISIBLE);

        //Firebase
        firebaseSetup();
        firebaseAuthentication = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Jobseeker");//maybe login the job seeker

        //Text view password show hide
        loginPasswordShowHide = findViewById(R.id.login_PasswordShowHide);
        loginPasswordShowHide.setOnClickListener(this);



/*
--------Clickable Spannable text view start here------------
*/

        ClickableSpan setClickable;
        // Code adapted from https://www.youtube.com/watch?v=E4xSjGZWR3E (From URL links)
        // Identify textView id for implement the method
        TextView loginTextView = findViewById(R.id.login_Signup);

        // get string from string.xml (signup_loginOption)
        final String text = getString(R.string.login_signupOption);
        SpannableString textViewStringSelect = new SpannableString(text);
        setClickable = new ClickableSpan() {


            @Override
            public void onClick(View view) {
                Intent jump = new Intent(getApplicationContext(), SignupActivity.class);
                startActivity(jump);
            }
            // may using below method if applying language
            //@Override
            // public void updateDrawState(TextPaint apply) {
            //super.updateDrawState(apply);
            //apply.setUnderlineText(true);
            //}
        };

        // span for textview is maximum to 28
        textViewStringSelect.setSpan(setClickable, 21, 28, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        //if apply dfferent language may changed
        //code adapted from https://developer.android.com/guide/topics/text/spans#java
        //underline the span text link
        textViewStringSelect.setSpan(new UnderlineSpan(), 21, 28, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        loginTextView.setText(textViewStringSelect);
        loginTextView.setMovementMethod(LinkMovementMethod.getInstance());

        loginTextView.setText(textViewStringSelect);
        loginTextView.setHighlightColor(Color.TRANSPARENT);
        loginTextView.setPaintFlags(loginTextView.getPaintFlags());
/*
-----------Clickable spannable textView methods end here---------------
*/


/*
-----------TextWatcher password for edittext start here (text clear if password greater than 10)-----------
*/

        loginPasswordShowHide.setVisibility(View.INVISIBLE);

        /*--Check text change within edit text--*/
        loginPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(loginPassword.getText().length()>0){
                    loginPasswordShowHide.setVisibility(View.VISIBLE);//Set textView visible if exceed 0
                }else
                    loginPasswordShowHide.setVisibility(View.INVISIBLE);
            }

            // if password exceed 10 clear the text
            @Override
            public void afterTextChanged(Editable s) {

                if (loginPassword.length() > 10) {
                    loginPassword.getText().clear();
                    onPause();
                }
            }
        });

/*
-----------TextWatcher password for edittext end here------------------
*/



/*
----------Password check box start here------------------
*/

        // show and hide password(user trigger checkbox)
       /* showhidepassword.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton button, boolean userCheck) {

                if (userCheck) {
                    // implement string to password hide when user trigger the checkbox
                    showhidepassword.setText(R.string.loginPasswordhide);
                    //initially set password field value visible to user
                    editTextLoginPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());//Show the string within the password field

                } else {
                    showhidepassword.setText(R.string.loginPasswordshow);
                    editTextLoginPassword.setTransformationMethod(PasswordTransformationMethod.getInstance()); //Hide the string within the password field

                }

            }
        });*/


 /*
-----------Username validation start here------------------
*/
                //Edit text Username validation
               /* if (loginUsername.length() == 0)    //Validation if the edittext box is empty
                // code adapted from https://www.youtube.com/watch?v=QLPasDQc7qc
                {
                    loginUsername.setError("Username field can't be empty");
                    linearLayout.startAnimation(setErrorAnim_LogIn);
                } else {
                    loginUsername.setError(null);
                    //String email = loginUsername.getText().toString();
                    // code adapted from https://www.youtube.com/watch?v=QLPasDQc7qc

                }*/
            //}
       // });
/*
-----------Username validation end here------------------
*/


/*
---------Password forget anchor start here (Reset password by user email)-----------
*/

        //Implement set on click listener to textview
        passwordforget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View passwordOnClick) {
                linearBackgroundPopUp.setVisibility(View.VISIBLE);
                linearPopUp.setVisibility(View.VISIBLE);
                login_linearLayout.setVisibility(View.INVISIBLE);
                //login_linearLayout.setOnClickListener(this);

                loginRelativeButton.setVisibility(View.INVISIBLE);
                loginRelativeProgressbar.setVisibility(View.INVISIBLE);
            }
        });

        //Password close button
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View closeOnClick) {
                linearBackgroundPopUp.setVisibility(View.INVISIBLE);
                linearPopUp.setVisibility(View.INVISIBLE);
                login_linearLayout.setVisibility(View.VISIBLE);
                loginRelativeButton.setVisibility(View.VISIBLE);
            }
        });

        resetButtonSendLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //String validation
                final String currentLoginEmail = currentEmail.getText().toString();
                if(currentLoginEmail.isEmpty()){
                    Toast.makeText(getApplicationContext(),"Enter your current login email",Toast.LENGTH_SHORT).show();
                }
                else {
                    firebaseAuthentication.sendPasswordResetEmail(currentLoginEmail).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> currentEmailSend) {

                            if (currentEmailSend.isSuccessful()){

                                Toast.makeText(getApplicationContext(),"We have send a link for you to reset at your email address " + currentLoginEmail,Toast.LENGTH_LONG).show();
                            }
                            else{
                                String errorMessage = currentEmailSend.getException().getMessage();
                                Toast.makeText(getApplicationContext(),"Reset link is not successful send to your email. Error found: " + errorMessage, Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }
            }
        });
/*
---------Password forget anchor text end here-----------
*/
    }


/*
 ---------Password editext text watcher start here-----------
*/
    public void loginPasswordTextChanged(){
        if(loginPasswordShowHide.getText() == "Show password"){
            loginPassword.setInputType(InputType.TYPE_CLASS_TEXT);
            // seTransformation code adapted from https://medium.com/@droidbyme/show-hide-password-in-edittext-in-android-c4c3db44f734
            loginPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());


        }else if(loginPasswordShowHide.getText().equals("Hide password")){
            loginPasswordShowHide.setText(R.string.signupPasswordshow);
            loginPassword.setInputType(InputType.TYPE_CLASS_TEXT);
            // seTransformation code adapted from https://medium.com/@droidbyme/show-hide-password-in-edittext-in-android-c4c3db44f734
            loginPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());// Hide


        }else{
            loginPasswordShowHide.setText(R.string.signupPasswordhide);
            loginPassword.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
            // seTransformation code adapted from https://medium.com/@droidbyme/show-hide-password-in-edittext-in-android-c4c3db44f734
            loginPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());// Show

        }

    }
/*
---------Password editext text watcher start here-----------
*/


/*
----------private string empty validation start here----------
*/
         private boolean String (String string) {
           Log.d(tag, " Login Validation: Check if string email and password is null");
            if (string.isEmpty()) {
              return true;//if null set error message
            } else {
              return false;// if not null no error message
         }
    }

/*
----------private string empty validation start here----------
*/


 /*
--------Firebase methods---------
*/
        public void loginButton() {


            // Email validation parameter
            String emailRegex = "[a-zA-Z0-9\\+\\_\\%\\-\\+]{1,256}" +

                    "\\@" + "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +

                    "(" + "\\." + "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" + ")+";

            //Password validation parameter
            String passwordRegex = ("^" + "(?=\\S+$)" + ".{5,}" + "$");


            final String login_email = loginEmail.getText().toString().trim();
            final String login_password = loginPassword.getText().toString().trim();

            //Set validation parameter to respective id
            Matcher loginEmailValidation = Pattern.compile(emailRegex).matcher(login_email);
            Matcher loginPasswordValidation = Pattern.compile(passwordRegex).matcher(login_password);

            //Edit text Password validation
            if (String(login_email) && String(login_password)) {
                loginEmail.setError("Email address field can't be empty");
                loginPassword.setError("Password field password field can't be empty");
                login_linearLayout.startAnimation(setErrorAnim_LogIn);
                return;

            } else if (loginEmailValidation.matches() && loginPasswordValidation.matches()) {
                //set drawable within the edit text
                //code adapted from https://developer.android.com/reference/android/widget/TextView
                loginEmail.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.ic_check,0);
                loginPassword.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_check, 0);

            } else {
                loginEmail.setError("Please make sure you enter your valid email only!");
                loginPassword.setError("Password too weak, please enter least 6 characters and without white spaces");
                return;
            }


            //initializing button for login
            //loginAuthentication = findViewById(R.id.login_Button);
            //loginAuthentication.setOnClickListener(new View.OnClickListener() {
            //@Override
            // public void onClick(View v) {
            Log.d(tag, "FirebaseAuth Login: attempt to login.");

            // find the id of the edit text in activity_login.xml
            // loginEmail = findViewById(R.id.login_Email);
            //loginPassword = findViewById(R.id.login_Password);


            loginRelativeProgressbar.setVisibility(View.VISIBLE);
            loginRelativeButton.setVisibility(View.INVISIBLE);

   /*
   -------------------------Firebase authentication start here-------------------------
   */
            firebaseAuthentication.signInWithEmailAndPassword(login_email, login_password)
                    .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            Log.d(tag, "signInWithEmail:success:" + task.isSuccessful());
                            if (task.isSuccessful()) {
                                Log.d(tag, "signInWithEmail:success:successful login");
                                //Put intent here
                                Toast.makeText(LoginActivity.this, R.string.authentication_success, Toast.LENGTH_SHORT).show();
                                Intent MainMenu = new Intent(getApplicationContext(), MainActivity.class);
                                startActivity(MainMenu);
                                loginEmail.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.ic_check,0);
                                loginPassword.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_check, 0);
                                loginRelativeProgressbar.setVisibility(View.INVISIBLE);
                                loginRelativeButton.setVisibility(View.VISIBLE);

                            } else {
                                // Sign in success, update UI with the signed-in user's information
                                Log.w(tag, "signInWithEmail:failed", task.getException());
                                Toast.makeText(LoginActivity.this, R.string.authentication_failed, Toast.LENGTH_SHORT).show();
                                loginEmail.setCompoundDrawablesWithIntrinsicBounds(0,0,0,0);
                                loginPassword.setCompoundDrawablesWithIntrinsicBounds(0, 0,0, 0);
                                loginRelativeProgressbar.setVisibility(View.INVISIBLE);
                                loginRelativeButton.setVisibility(View.VISIBLE);
                            }
                        }
                    });

            // if user login not null, navigate to mainActivity
            if(firebaseAuthentication.getCurrentUser()!= null){
                Intent intent =new Intent(LoginActivity.this,MainActivity.class);
                startActivity(intent);
                finish();
            }



            //Username

        }


        private void firebaseSetup() {
            Log.d(tag, "firebase setup: setting up firebase auth");

            firebaseAuthentication = FirebaseAuth.getInstance();
            //Check the authentication state
            authStateListener = new FirebaseAuth.AuthStateListener() {
                @Override
                public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                    FirebaseUser user = firebaseAuth.getCurrentUser();
                    //code adapted from https://firebase.google.com/docs/auth/android/manage-users
                    if (user != null) {
                        // If user sign in
                        Log.d(tag, "AuthLogin:User log in, user log in is : " + user.getUid());
                    } else {
                        //If user no signed in
                        Log.d(tag, "AuthLogin:No user is log in");
                    }
                }
            };

        }

        @Override
        protected void onStart() {
            super.onStart();
            // Check if user is signed in (non-null) and update UI accordingly.
            firebaseAuthentication.addAuthStateListener(authStateListener);

        }

    //@Override
        //protected void onStop () {
           // super.onStop();
           // if (authStateListener != null) {
                //firebaseAuth.removeAuthStateListener(authStateListener);
            //}
       // }


    /**
     *
     * @param button
     */
        @Override
        public void onClick (View button){
            switch(button.getId() ){

                case R.id.login_Backbutton:
                    Intent Home = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(Home);
                    finish();
                    break;

                case R.id.login_Button:
                    loginButton();
                    break;

                case R.id.login_PasswordShowHide:
                    loginPasswordTextChanged();
                    break;
            }
            
        }
}

