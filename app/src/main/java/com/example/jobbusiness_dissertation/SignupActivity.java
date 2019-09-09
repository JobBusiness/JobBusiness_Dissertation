package com.example.jobbusiness_dissertation;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
//import androidx.constraintlayout.widget.ConstraintLayout;

//import android.app.ActivityOptions;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
//import android.graphics.BitmapFactory;
import android.graphics.Color;
//import android.os.AsyncTask;
import android.os.Bundle;
import android.renderscript.ScriptGroup;
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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
//import android.widget.RelativeLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jobbusiness_dissertation.Database.StringHandling;
import com.example.jobbusiness_dissertation.Login.LoginActivity;
import com.example.jobbusiness_dissertation.Models.EmployeeDetails;
import com.example.jobbusiness_dissertation.Models.JobSeekerDetails;
import com.google.android.gms.tasks.OnCompleteListener;
//import com.google.android.gms.tasks.OnFailureListener;
//import com.google.android.gms.tasks.OnSuccessListener;

//Firebase
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


//Password and email validation
import java.util.regex.Matcher;
import java.util.regex.Pattern;

//import br.com.simplepass.loading_button_lib.customViews.CircularProgressButton;


public class SignupActivity extends AppCompatActivity implements View.OnClickListener {

    RadioButton jobseeker, employee;
    private static final String tag = "SignupActivity";

    //Toolbar home button
    private Button backButton;
    AlertDialog dialog;


    /*
----------------------Notes------------------------
..code adapted from ..... (From URL link)
*/


/*
----------------------JobSeeker layout------------------------
*/
    private EditText jobSeekerUsername,jobSeekerEmail,jobSeekerPassword;
    private Button jobSeekerSignupButton;
    private CheckBox jobSeekerShowHidePassword;
    private LinearLayout jobSeekerLinearLayout;


    ScrollView jobseekerLayout;
    RelativeLayout jobseekerRelativeButton,jobseekerRelativeProgressBar ;
/*
----------------------Employee layout------------------------
*/
    private EditText employeeEmail,employeePassword, employeeUsername, employeeCompany;
    private Button employeeSignupButton;
   //private CheckBox employeeShowHidePassword;
    private LinearLayout employeeLinearLayout;
    private TextView employeePasswordShowHide, jobseekerPasswordShowHide;


    ScrollView employeeLayout;
    RelativeLayout employeeRelativeButton,employeeRelativeProgressBar;
/*
----------------------Animation------------------------
*/
    //Animation for jobseeker and employee layout
    private Animation setErrorAnim_SignUp;

    //private CircularProgressButton circularProgressButton;
    //TextView Signup_login;


/*
----------------------Firebase------------------------
*/
    private FirebaseAuth firebaseAuthentication;
    private DatabaseReference jobseekerDatabaseReference, employeeDatabaseReference;
    FirebaseDatabase firebaseDatabase;
    private FirebaseAuth.AuthStateListener authStateListener;
    private String jobseeker_username;
    private FirebaseUser firebaseUser;

   // private FirebaseFirestore firestore;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        Log.d(tag, "Login onCreate: started ");

        //Set layout visibility on both jobseeker and eployee layout
        jobseekerLayout = findViewById(R.id.jobseeker_Layout);
        jobseekerLayout.setVisibility(jobseekerLayout.VISIBLE);
        jobseekerRelativeButton = findViewById(R.id.jobseeker_Relative_Button);
        jobseekerRelativeButton.setVisibility(jobseekerRelativeButton.VISIBLE);
        employeeLayout = findViewById(R.id.employee_Layout);
        employeeLayout.setVisibility(employeeLayout.INVISIBLE);
        employeeRelativeButton = findViewById(R.id.employee_Relative_Button);
        employeeRelativeButton.setVisibility(employeeRelativeButton.INVISIBLE);

        //Radio button
        jobseeker = findViewById(R.id.radiobtn_Jobseeker);
        employee = findViewById(R.id.radiobtn_Employee);

        //firestore = FirebaseFirestore.getInstance();

        //Implement for sign up toolbar button
        backButton = findViewById(R.id.signup_Backbutton);
        backButton.setOnClickListener(this);

        // Register the xml edittext, button and linear id to designated class file
        //signupUsername = findViewById(R.id.signup_Username);

        //Jobseeker editText
        jobSeekerEmail = findViewById(R.id.jobseeker_Signup_Email);
        jobSeekerPassword = findViewById(R.id.jobseeker_Signup_Password);
        jobSeekerUsername = findViewById(R.id.jobseeker_Signup_Username);

        //Jobseeker layout and button
        jobSeekerSignupButton = findViewById(R.id.jobseeker_Signup_Button);
        jobSeekerSignupButton.setOnClickListener(this);
        jobSeekerLinearLayout = findViewById(R.id.jobseeker_LinearLayout);
        jobseekerRelativeProgressBar = findViewById(R.id.jobseeker_Progress_Circular_Layout);

        //Jobseeeker TextView for hide and show password
        jobseekerPasswordShowHide = findViewById(R.id.jobseeker_PasswordShowHide);
        jobseekerPasswordShowHide.setOnClickListener(this);


        //Employee editText
        employeeEmail = findViewById(R.id.employee_Signup_Email);
        employeePassword = findViewById(R.id.employee_Signup_Password);
        employeeUsername = findViewById(R.id.employee_Signup_Username);
        employeeCompany = findViewById(R.id.employee_Signup_Company);

        //Employee TextView show and hide password
        employeePasswordShowHide = findViewById(R.id.employee_PasswordShowHide);
        employeePasswordShowHide.setOnClickListener(this);


        //Employee layout and button
        employeeSignupButton = findViewById(R.id.employee_Signup_Button);
        employeeSignupButton.setOnClickListener(this);
        employeeLinearLayout = findViewById(R.id.employee_LinearLayout);
        employeeRelativeButton = findViewById(R.id.employee_Relative_Button);

        //Progress bar layout
        employeeRelativeProgressBar = findViewById(R.id.employee_Progress_Circular_Layout);

        //Initially hide progressBar layout
        employeeRelativeProgressBar.setVisibility(View.INVISIBLE);
        jobseekerRelativeProgressBar.setVisibility(View.INVISIBLE);

        //Initialise animation
        setErrorAnim_SignUp = AnimationUtils.loadAnimation(SignupActivity.this, R.anim.anim_error);

        //Initialise firebase
        firebaseAuthentication = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        jobseekerDatabaseReference = firebaseDatabase.getReference("Jobseeker");
        employeeDatabaseReference = firebaseDatabase.getReference("Employee");



/*
------------------------------Clickable Span textView methods Start here-------------------------------------
*/

        // Set the span of the textView to implement link based on selected textView
        ClickableSpan setClickable;

        // Code adapted from https://www.youtube.com/watch?v=E4xSjGZWR3E
        // Implement (getString ) from string.xml (signup_loginOption)
        final String text = getString(R.string.signup_loginOption);
        SpannableString stringSelect = new SpannableString(text);
        setClickable = new ClickableSpan() {

            @Override
            public void onClick(View jd) {
                //Implement intent from ActivitySignup to ActivityLogin class (This refer to the link below button)
                Intent activityLogin = new Intent(SignupActivity.this, LoginActivity.class);
                startActivity(activityLogin);
                //startActivity(new Intent(AboutActivity.this, LoginActivity.class));
            }
            // may using below method if applying language
            //@Override
            // public void updateDrawState(TextPaint apply) {
            //super.updateDrawState(apply);
            //apply.setUnderlineText(true);
            //}
        };

        stringSelect.setSpan(setClickable, 23, 29, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        //if apply dfferent language may changed
        //code adapted from https://developer.android.com/guide/topics/text/spans#java
        //underline the span text link
        stringSelect.setSpan(new UnderlineSpan(), 23, 29, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        // Code adapted from https://www.youtube.com/watch?v=E4xSjGZWR3E
        // Identify textView id for implement the method
        TextView textView = findViewById(R.id.employee_Signup_Login);
        TextView textView1 = findViewById(R.id.jobseeker_Signup_Login);

        textView.setText(stringSelect);
        textView.setMovementMethod(LinkMovementMethod.getInstance());
        textView.setText(stringSelect);
        textView.setHighlightColor(Color.TRANSPARENT);
        textView.setPaintFlags(textView.getPaintFlags());


        textView1.setText(stringSelect);
        textView1.setMovementMethod(LinkMovementMethod.getInstance());
        textView1.setText(stringSelect);
        textView1.setHighlightColor(Color.TRANSPARENT);
        textView1.setPaintFlags(textView.getPaintFlags());

        //ForegroundColorSpan foregroundSpan = new ForegroundColorSpan(Color.BLUE);
        //stringSelect.setSpan(foregroundSpan, 23, stringSelect.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        //stringSelect.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG );
        // Signup_login= (TextView) findViewById(R.id.Signup_login);

/*
------------------------------Clickable Span textView methods end here-------------------------------------
*/

        firebaseSet();


/*
------------------------------Show hide password for jobseeker start here-------------------------------------
*/

        //Checkbox (initiate) Show and hide password option
        /*jobSeekerShowHidePassword = findViewById(R.id.jobseeker_password_ShowHide);
        // Dynamic string between show and hide password when user trigger checkbox
        jobSeekerShowHidePassword.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton button, boolean userCheck) {

                if (userCheck) {
                    jobSeekerShowHidePassword.setText(R.string.signupPasswordshow); // implement string to password hide when user trigger the checkbox
                    jobSeekerPassword.setInputType(InputType.TYPE_CLASS_TEXT);
                    jobSeekerPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());// Hide
                }
                else {
                    //implement string to password show when user trigger the checkbox
                    jobSeekerPassword.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    jobSeekerPassword.setTransformationMethod(PasswordTransformationMethod.getInstance()); //Show
                }
            }
        });*/
/*
------------------------------Text Watcher for employee password start here-------------------------------------
*/
        employeePasswordShowHide.setVisibility(View.INVISIBLE);

        /*--Check text change within edit text--*/
        employeePassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(employeePassword.getText().length()>0){
                    employeePasswordShowHide.setVisibility(View.VISIBLE);//Set textView visible if exceed 0
                }else
                    employeePasswordShowHide.setVisibility(View.INVISIBLE);
            }

            @Override
            public void afterTextChanged(Editable s) {

                if (employeePassword.length() > 10) { // if password exceed 10 clear the text
                    employeePassword.getText().clear();
                    onPause();//
                }
            }
        });

/*
------------------------------Text Watcher for jobseeker password start here-------------------------------------
*/
        jobseekerPasswordShowHide.setVisibility(View.INVISIBLE);

        /*--Check text change within edit text--*/
        jobSeekerPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(jobSeekerPassword.getText().length()>0){
                    jobseekerPasswordShowHide.setVisibility(View.VISIBLE);//Set textView visible if exceed 0
                }else
                    jobseekerPasswordShowHide.setVisibility(View.INVISIBLE);
            }

            @Override
            public void afterTextChanged(Editable s) {

                if (jobSeekerPassword.length() > 10) { // if password exceed 10 clear the text
                    employeePassword.getText().clear();
                    onPause();//
                }
            }
        });

/*
------------------------------Layout visibility start here-------------------------------------
*/



        // if user select the jobseeker radio button belw methods will execute
        jobseeker.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(jobseeker.isChecked()){
                    //Jobseeker scrollview layout and button layout are visible to user
                    jobseekerLayout.setVisibility(jobseekerLayout.VISIBLE);
                    jobseekerRelativeButton.setVisibility(jobseekerRelativeButton.VISIBLE);
                    //Employee scrollview layout and button layout are hide to user
                    employeeLayout.setVisibility(employeeLayout.INVISIBLE);
                    employeeRelativeButton.setVisibility(employeeRelativeButton.INVISIBLE);
                }
            }
        });

        //If user select the employee radio button below methods will execute
        employee.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(employee.isChecked()){
                    //Employee scrollview layout and button layout are visible to user
                    employeeLayout.setVisibility(employeeLayout.VISIBLE);
                    employeeRelativeButton.setVisibility(employeeRelativeButton.VISIBLE);
                    //Jobseeker scrollview layout and button layout are visible to user
                    jobseekerLayout.setVisibility(jobseekerLayout.INVISIBLE);
                    jobseekerRelativeButton.setVisibility(jobseekerRelativeButton.INVISIBLE);//employee button layout
                }
            }
        });
    }


    // Code adapted from https://www.youtube.com/watch?v=md3eVaRzdIM
    //Hide and show employee methods
    // Code adapted from https://www.youtube.com/watch?v=md3eVaRzdIM
    public void TextChanged(){
        if(employeePasswordShowHide.getText() == "Show password"){
            employeePassword.setInputType(InputType.TYPE_CLASS_TEXT);
            // seTransformation code adapted from https://medium.com/@droidbyme/show-hide-password-in-edittext-in-android-c4c3db44f734
            employeePassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
            onPause();

        }else if(employeePasswordShowHide.getText().equals("Hide password")){
            employeePasswordShowHide.setText(R.string.signupPasswordshow);
            employeePassword.setInputType(InputType.TYPE_CLASS_TEXT);
            // seTransformation code adapted from https://medium.com/@droidbyme/show-hide-password-in-edittext-in-android-c4c3db44f734
            employeePassword.setTransformationMethod(PasswordTransformationMethod.getInstance());// Hide
            onPause();

        }else{
            employeePasswordShowHide.setText(R.string.signupPasswordhide);
            employeePassword.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
            // seTransformation code adapted from https://medium.com/@droidbyme/show-hide-password-in-edittext-in-android-c4c3db44f734
            employeePassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());// Show
            onPause();
        }
    }


    public void employeePasswordTextChnaged(){
        if(jobseekerPasswordShowHide.getText() == "Show password"){
            jobSeekerPassword.setInputType(InputType.TYPE_CLASS_TEXT);
            // seTransformation code adapted from https://medium.com/@droidbyme/show-hide-password-in-edittext-in-android-c4c3db44f734
            jobSeekerPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());


        }else if(jobseekerPasswordShowHide.getText().equals("Hide password")){
            jobseekerPasswordShowHide.setText(R.string.signupPasswordshow);
            jobSeekerPassword.setInputType(InputType.TYPE_CLASS_TEXT);
            // seTransformation code adapted from https://medium.com/@droidbyme/show-hide-password-in-edittext-in-android-c4c3db44f734
            jobSeekerPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());// Hide


        }else{
            jobseekerPasswordShowHide.setText(R.string.signupPasswordhide);
            jobSeekerPassword.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
            // seTransformation code adapted from https://medium.com/@droidbyme/show-hide-password-in-edittext-in-android-c4c3db44f734
            jobSeekerPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());// Show

        }

    }


    /*---Check again code adapted from https://stackoverflow.com/questions/8122625/getextractedtext-on-inactive-inputconnection-warning-on-android (antoniom,2014)--*/
   // To hide keyboard
    @Override
    public void onPause() {

        // hide the keyboard in order to avoid getTextBeforeCursor on inactive InputConnection
        InputMethodManager Hidekeyboard = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        Hidekeyboard.hideSoftInputFromWindow(employeePasswordShowHide.getWindowToken(), 0);
        super.onPause();
    }



    // Checking string validation
    private boolean String (String stringEditText) {
     Log.d(tag, "String null validation: Checking if string in username, email and password is null");
       if (stringEditText.isEmpty()) {
          return true; //if empty set error message
        } else {
          return false;//else no error message
        }
     }

// Attempt on checking if username already exist during sign up account  (Please check again!)
    public boolean usernameCheck(String jobseeker_username, DataSnapshot dataSnapshot){

        Log.d(tag,"Sign up username check: Checking" +jobseeker_username+"already exist");
    //Retrieve data from firebase
    JobSeekerDetails jobSeekerDetails = new JobSeekerDetails();

    //check based on loop on each node stored in database, check if data exist in database
    for(DataSnapshot ds: dataSnapshot.getChildren()){
        Log.d(tag,"Checking username if exist" + ds);

            // jobseeker username will contain in user object
           jobSeekerDetails.setJobseeker_username(ds.getValue(JobSeekerDetails.class).getJobseeker_username());
        Log.d(tag,"Checking username if exist" + jobSeekerDetails.getJobseeker_username());

        //if(StringHandling.checkusername(jobSeekerDetails.getJobseeker_username()).equals(jobseeker_username)){
            //Log.d(tag,"Usernmae match found" + jobSeekerDetails.getJobseeker_username());
           //return true;
         }

        return true;

    //within databse spaces in username will store in with period
    }

/*
------------------------------JobSeeker signup methods--------------------------------------
*/
    //Implement method for user registration which this method is called at line 314
    public void jobSeekerRegister() {
         //Code adapted from
        // Email validation
        String emailRegex = "[a-zA-Z0-9\\+\\_\\%\\-\\+]{1,256}" +

                "\\@" + "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +

                "(" + "\\." + "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" + ")+";

        // Password validation
        String passwordRegex = ("^" + "(?=\\S+$)" + ".{6,}" + "$");


        //Get the content from the editable text then convert to string format to store
        final String jobseeker_email = jobSeekerEmail.getText().toString().trim();
        final String jobseeker_password = jobSeekerPassword.getText().toString().trim();
        final String jobseeker_username = jobSeekerUsername.getText().toString().trim();


        Matcher jobSeekerEmailValidation = Pattern.compile(emailRegex).matcher(jobseeker_email);
        Matcher jobSeekerPasswordValidation = Pattern.compile(passwordRegex).matcher(jobseeker_password);

        //The validation errors are implement consistently by using only .setError for each editText
        // code adapted from https://www.youtube.com/watch?v=QLPasDQc7qc
        if (String(jobseeker_username) && String(jobseeker_email) && String(jobseeker_password)) {
            jobSeekerUsername.setError("Username field can't be empty");
            jobSeekerEmail.setError("Email address field can't be empty");
            jobSeekerPassword.setError("Password field password field can't be empty");
            jobSeekerLinearLayout.startAnimation(setErrorAnim_SignUp);
            return;

        } else if (jobSeekerEmailValidation.matches() && jobSeekerPasswordValidation.matches()) {
            jobSeekerEmail.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_check, 0);
            jobSeekerPassword.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_check, 0);
            jobSeekerUsername.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.ic_check,0);
        }
        else {
            jobSeekerEmail.setError("Please make sure you enter your valid email only!");
            jobSeekerPassword.setError("Password too weak, please enter least 6 characters and without white spaces");
            return;
        }



        /*--------------------------------Firebase methods----------------------------------------*/

        //Set relative visibility
        jobseekerRelativeProgressBar.setVisibility(View.VISIBLE);
        jobseekerRelativeButton.setVisibility(View.INVISIBLE);

        //String to store in the real time database
        final JobSeekerDetails jobSeekerUser = new JobSeekerDetails(jobseeker_username,jobseeker_email); //String stored in realtime database

        //Set the database reference
        FirebaseDatabase.getInstance().getReference("Jobseeker").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //code adapted from https://www.youtube.com/watch?v=oEKUMW4_2GA (From URL link)
                if(dataSnapshot.child(jobSeekerUser.getJobseeker_username()).exists()) { //get jobseeker username string and check if exist in the database
                    Toast.makeText(SignupActivity.this, "The username is already been used, Please try another username", Toast.LENGTH_LONG).show();
                    jobseekerRelativeProgressBar.setVisibility(View.INVISIBLE);
                    jobseekerRelativeButton.setVisibility(View.VISIBLE);
                    jobSeekerEmail.setCompoundDrawablesWithIntrinsicBounds(0,0,0,0);
                    jobSeekerPassword.setCompoundDrawablesWithIntrinsicBounds(0, 0,0, 0);
                    jobSeekerUsername.setCompoundDrawablesWithIntrinsicBounds(0,0,0,0);

                }else{
                    // if username string unique saved user email and password
                    firebaseAuthentication.createUserWithEmailAndPassword(jobseeker_email, jobseeker_password)
                      .addOnCompleteListener(SignupActivity.this, new OnCompleteListener<AuthResult>() {

                        @Override
                        public void onComplete(@NonNull Task<AuthResult> authentication) {
                            //If task authentication is successful stored user details to realtime database
                            if (authentication.isSuccessful()){
                                jobseekerDatabaseReference.child(jobSeekerUser.getJobseeker_username()).setValue(jobSeekerUser); //store jobseeker username in database
                                jobseekerRelativeProgressBar.setVisibility(View.INVISIBLE);
                                jobseekerRelativeButton.setVisibility(View.VISIBLE);
                                //Toast message if authentication and validation of username and email success
                                Toast.makeText(SignupActivity.this, "Your account is successfully registered", Toast.LENGTH_SHORT).show();
                            } else {
                                //authenticate if email use by another account
                                Toast.makeText(getApplicationContext(), "The email is already used by another account, Please try again", Toast.LENGTH_SHORT).show();
                                jobseekerRelativeProgressBar.setVisibility(View.INVISIBLE);
                                jobseekerRelativeButton.setVisibility(View.VISIBLE);
                                jobSeekerEmail.setCompoundDrawablesWithIntrinsicBounds(0,0,0,0);
                                jobSeekerPassword.setCompoundDrawablesWithIntrinsicBounds(0, 0,0, 0);
                                jobSeekerUsername.setCompoundDrawablesWithIntrinsicBounds(0,0,0,0);
                            }
                            //if (authentication.getException() instanceof FirebaseAuthUserCollisionException) {
                            // Toast.makeText(getApplicationContext(), "The email is already used by another account", Toast.LENGTH_LONG).show();
                            // employeeRelativeProgressBar.setVisibility(View.INVISIBLE);
                            //}//else {
                            //  Toast.makeText(SignupActivity.this,authentication.getException().getMessage(),Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

               Toast.makeText(getApplicationContext(),"Please check your internet connectivity",Toast.LENGTH_SHORT).show();
            }
        });
    }


        //Code adapted from https://www.youtube.com/watch?v=7Yc3Pt37coM (Simplfied Coding, 2018)
        // Code adapted from https://www.youtube.com/watch?v=7Yc3Pt37coM&t=22s




/*
------------------------------Employee Register methods -------------------------------------
*/

        public void employeeRegister() {

            final String employee_username = employeeUsername.getText().toString().trim();
            final String employee_email = employeeEmail.getText().toString().trim();
            final String employee_company = employeeCompany.getText().toString().trim();
            final String employee_password = employeePassword.getText().toString().trim();


            // Email validation parameter
            // Code adapted from
            String emailRegex = "[a-zA-Z0-9\\+\\_\\%\\-\\+]{1,256}" +

                    "\\@" + "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +

                    "(" + "\\." + "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" + ")+";

            // Password validation
            String passwordRegex = ("^" + "(?=\\S+$)" + ".{6,}" + "$");


            Matcher employeeEmailValidation = Pattern.compile(emailRegex).matcher(employee_email);
            Matcher employeePasswordValidation = Pattern.compile(passwordRegex).matcher(employee_password);


            if (String(employee_username) && String(employee_email) && String(employee_password)) {
                employeeUsername.setError("Username field can't be empty");
                employeeEmail.setError("Email address field can't be empty");
                employeePassword.setError("Password field password field can't be empty");
                employeeLinearLayout.startAnimation(setErrorAnim_SignUp);
                return;

            } else if (employeeEmailValidation.matches() && employeePasswordValidation.matches()) {
                employeeEmail.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_check, 0);
                employeePassword.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_check, 0);
                employeeCompany.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_check, 0);
                employeeUsername.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_check, 0);

            } else {
                employeeEmail.setError("Please make sure you enter your valid email only!");
                employeePassword.setError("Password too weak, please enter at least 6 characters and without white spaces");
                return;
            }


            // Set visibility of the layout before registering account
            employeeRelativeProgressBar.setVisibility(View.VISIBLE);
            employeeRelativeButton.setVisibility(View.INVISIBLE);


         /*--------------------------------Firebase methods----------------------------------------*/

        //String to store in the real time database
        final EmployeeDetails employeeUser = new EmployeeDetails(employee_username,employee_email,employee_company); //String stored in realtime database

        //Set the database reference
        FirebaseDatabase.getInstance().getReference("Employee").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //code adapted from https://www.youtube.com/watch?v=oEKUMW4_2GA (From URL link)
                if(dataSnapshot.child(employeeUser.getEmployee_username()).exists()) { //get employee username string and check if exist in the database
                    Log.d(tag, "firebase datasnaphot: Username found exist in database"+jobseeker_username);
                    Toast.makeText(SignupActivity.this, "Username is already been used, Please try another username", Toast.LENGTH_LONG).show();
                    employeeRelativeProgressBar.setVisibility(View.INVISIBLE);
                    employeeRelativeButton.setVisibility(View.VISIBLE);
                    employeeEmail.setCompoundDrawablesWithIntrinsicBounds(0,0,0,0);
                    employeePassword.setCompoundDrawablesWithIntrinsicBounds(0, 0,0, 0);
                    employeeUsername.setCompoundDrawablesWithIntrinsicBounds(0,0,0,0);
                    employeeCompany.setCompoundDrawablesWithIntrinsicBounds(0, 0,0, 0);

                }else{
                    // if username string unique saved user email and password
                    firebaseAuthentication.createUserWithEmailAndPassword(employee_email, employee_password)
                      .addOnCompleteListener(SignupActivity.this, new OnCompleteListener<AuthResult>() {

                        @Override
                        public void onComplete(@NonNull Task<AuthResult> authentication) {
                            //If task authentication is successful stored user details to realtime database
                            if (authentication.isSuccessful()){
                                Log.d(tag, "firebase authentication: authentication successful");
                                employeeDatabaseReference.child(employeeUser.getEmployee_username()).setValue(employeeUser); //store jobseeker username in the object after checking if exist or not in database
                                employeeRelativeProgressBar.setVisibility(View.INVISIBLE);
                                employeeRelativeButton.setVisibility(View.VISIBLE);
                                //Toast message if authentication and validation of username and email success
                                Toast.makeText(SignupActivity.this, "Your account is successfully registered", Toast.LENGTH_SHORT).show();
                            } else {
                                //authenticate if email use by another account
                                Log.w(tag, "firebase authentication: authentication unsuccessful");
                                Toast.makeText(getApplicationContext(), "The email is already used by another account, Please try again", Toast.LENGTH_SHORT).show();
                                employeeRelativeProgressBar.setVisibility(View.INVISIBLE);
                                employeeRelativeButton.setVisibility(View.VISIBLE);
                                employeeEmail.setCompoundDrawablesWithIntrinsicBounds(0,0,0,0);
                                employeePassword.setCompoundDrawablesWithIntrinsicBounds(0, 0,0, 0);
                                employeeUsername.setCompoundDrawablesWithIntrinsicBounds(0,0,0,0);
                                employeeCompany.setCompoundDrawablesWithIntrinsicBounds(0, 0,0, 0);

                            }
                        }
                    });
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
               Toast.makeText(getApplicationContext(),"Please check your internet connectivity",Toast.LENGTH_SHORT).show();
            }
        });
    }


    public void firebaseSet() {
        Log.d(tag, "firebase setup: setting up firebase auth in sign in");


        //Check the authentication state
        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                //code adapted from https://firebase.google.com/docs/auth/android/manage-users (From URL Link)
                if (user != null) {
                    // If user sign in
                    Log.d(tag, "AuthSignUp:user sign in " + user.getUid());

                     jobseekerDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                         @Override
                         public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                             //Checking in database reference if username is not already use
                             if(usernameCheck(jobseeker_username,dataSnapshot)){
                                 Toast.makeText(SignupActivity.this,"Username already exist",Toast.LENGTH_SHORT).show();
                                 //append = databaseReference.push().getKey().substring(3,10);//get UID of node jobseeker child
                                 Log.d(tag, "On changed: username already exist append string to random ");
                             }
                         }

                         @Override
                         public void onCancelled(@NonNull DatabaseError databaseError) {

                         }
                     });
                } else {
                    //If user no signed in
                    Log.d(tag, "AuthSignUp:No user is sign up");
                }
            }
        };

    }

    //super.onDestroy(); // to enable the app have a final chance to clean things up before the activity does get destroyed
//handler.removeCallbacksAndMessages(null);



/*
----------------------------------------Custom toast-------------------------------------
*/

    // Show custom success Toast message  //code adapted from https://www.youtube.com/watch?v=sZ1fLi4QZ-g
    public void successToast() {
        //Toast toast = Toast.makeText(this, "Your account is successfully registered", Toast.LENGTH_LONG);
        LayoutInflater layout = getLayoutInflater();
        View toastCustom = layout.inflate(R.layout.toastcustom_success, (ViewGroup) findViewById(R.id.toast_Success));
        Toast toast = new Toast(SignupActivity.this);
        // toast.setGravity(Gravity.BOTTOM, 0, 7);
        toast.setDuration(Toast.LENGTH_LONG); //Set duration of showing the toast message
        toast.setView(toastCustom);
        toast.show();//
    }

    public void ErrorToast(){

        // Enter
    }


/*-----------------------------------------OnClick Button (Upon user trigger)----------------------------------------

*/

    /**
     *
     * @param userTrigger**This is used for onClick button**
     */
    @Override
    public void onClick (View userTrigger) {

        //int selected = userTrigger.getId();
        if (userTrigger == jobSeekerSignupButton) {    // method called from line 314, public void userRegister()
            jobSeekerRegister();
        }
            if (userTrigger == employeeSignupButton) {
                employeeRegister();
            }
               if(userTrigger == employeePasswordShowHide){
                 TextChanged();
                }
                  if(userTrigger == jobseekerPasswordShowHide){
                      employeePasswordTextChnaged();
                  }
                 //if(userTrigger == jobSeekerTextView){
                    // jobSeekerOnTextChanged();
                // }
                   if (userTrigger == backButton) {
                      Intent backHome = new Intent(getApplicationContext(), MainActivity.class); // getApplicationContext will live through the entire process and thus it does not matter if you store a static reference to it anywhere since it will always be there during the runtime of your app (and outlive any objects/singletons instantiated by it).
                      // http://www.christianpeeters.com/android-tutorials/tutorial-activity-slide-animation/
                      //Bundle anim = ActivityOptions.makeCustomAnimation(this, R.anim.anim_slideleft, R.anim.anim_slide_right).toBundle();
                      startActivity(backHome);
                      finish();
                      //overridePendingTransition(R.anim.anim_slideleft,R.anim.anim_slide_right);
                    }
       }
}

    /*public void loadingButton(){
        circularProgressButton = findViewById(R.id.signup_Button);
        circularProgressButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AsyncTask<String,String,String> Starting = new AsyncTask<String, String, String>() {
                    @Override
                    protected String doInBackground(String... params) {
                        try {
                            Thread.sleep(3000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        return "done";
                    }

                    @Override
                    protected void onPostExecute(String s) {

                        if (s.equals("done")){
                            Toast.makeText(SignupActivity.this,"Done",Toast.LENGTH_SHORT).show();
                            circularProgressButton.doneLoadingAnimation(Color.parseColor("#333639"), BitmapFactory.decodeResource(getResources(),R.drawable.ic_check));
                        }

                    }
                };
                circularProgressButton.startAnimation();
                Starting.execute();


            }
        });
    }*/

// https://firebase.google.com/docs/auth/android/custom-auth( To logout )

