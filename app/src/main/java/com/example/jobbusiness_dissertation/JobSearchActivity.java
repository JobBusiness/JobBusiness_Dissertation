package com.example.jobbusiness_dissertation;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


import com.example.jobbusiness_dissertation.ArrayListAdapter.ArrayListAdapter;
import com.example.jobbusiness_dissertation.RecyclerAdapter.Recycler_Viewer_Adapter;
import com.example.jobbusiness_dissertation.Models.SearchCompanyDetails;
import com.example.jobbusiness_dissertation.RecyclerViewHolderAdapter.ViewHolderAdapter;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
//import com.google.android.material.chip.Chip;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;

//import static com.example.jobbusiness_dissertation.ArrayListAdapter.ArrayListAdapter.jobLocation;
public class JobSearchActivity extends AppCompatActivity implements View.OnClickListener,Recycler_Viewer_Adapter.OnViewHolderOnClick {

    //Arrays
    private ArrayList<SearchCompanyDetails> dataList = new ArrayList<SearchCompanyDetails>();
    private ArrayList<String> str = new ArrayList<>();
    private ArrayAdapter<String> locationList, jobTypesList;
    // List<SearchCompanyDetails> list;

    //Overflow menu
    private MenuInflater searchMenuInflater;

    //Recycler Viewer
    private RecyclerView recyclerView;

    //Integer
    private int countJob = 0;

    //For debugging
    private static final String tag = "JobSearchActivity";

    //Others
    private ImageButton backButton, reloadButton;
    private EditText searchBar;
    private ImageButton imageButton, searchFilter;
    public ProgressBar progressBarLayout;
    private TextView textViewMessageOne, textViewMessageTwo, textViewMessageThree;
    //private AutoCompleteTextView searchFilterTextView;
    private AlertDialog.Builder builder;
    private Spinner spinnerJobDistrict, spinnerJobTypes;
    // public Chip tags;
    private String textViewOne, textViewTwo, textViewThree;


    //Firebase
    FirebaseRecyclerOptions<SearchCompanyDetails> RecyclerOptions;
    FirebaseRecyclerAdapter<SearchCompanyDetails, ViewHolderAdapter> RecyclerAdapter;
    private Recycler_Viewer_Adapter recycler_viewer_adapter;
    private DatabaseReference databaseReference;
    private FirebaseStorage databaseStorage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jobsearch);

        // selectItem();
        /*
        --------Button---------
        */
        //Toolbar button
        backButton = findViewById(R.id.searchjob_Backbutton);
        backButton.setOnClickListener(this);
        //Search button
        imageButton = findViewById(R.id.searching_Button);
        imageButton.setOnClickListener(this);
        //Search filter button
        searchFilter = findViewById(R.id.search_Filter);
        searchFilter.setOnClickListener(this);
        //Refresh button
        reloadButton = findViewById(R.id.reload_Button);
        reloadButton.setOnClickListener(this);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());


        //tags = findViewById(R.id.tags);

        //TextView
        textViewMessageOne = findViewById(R.id.textView_Available_Job);
        textViewMessageTwo = findViewById(R.id.textView_JobDistrict);
        textViewMessageThree = findViewById(R.id.textView_JobType);
        //progressBar
        progressBarLayout = findViewById(R.id.indeterminate_ProgressBar);
        /*--Recycler Viewer initialisation--*/
        recyclerView = findViewById(R.id.recyclerViewer);
        //Set Layout Manager
        //recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        //Edit text (Search bar)
        searchBar = findViewById(R.id.edittext_SearchView);

        //Toolbar show up overflow menu
        Toolbar toolbar = findViewById(R.id.searchjobToolbar);
        setSupportActionBar(toolbar);

        //Firebase



        /*
        --------------- RecyclerViewer (Show all available jobs in database) start here-------------------
        */
        //initialize the database reference for getting the data
         databaseReference = FirebaseDatabase.getInstance().getReference("Company");//get reference of the recycle view .getReference.getChild()

        //Initialize random child key to (get key)
        //FirebaseDatabase database = FirebaseDatabase.getInstance();
        //String key = database.getReference("Company").push().getKey();



        //Initialise recyclerViewer
        RecyclerOptions = new FirebaseRecyclerOptions.Builder<SearchCompanyDetails>().setQuery
                (databaseReference, SearchCompanyDetails.class).build();// database reference and model


        //This method initially show the available job search in firebase
        RecyclerAdapter = new FirebaseRecyclerAdapter<SearchCompanyDetails, ViewHolderAdapter>(RecyclerOptions) {
            @Override
            public void onBindViewHolder(@NonNull ViewHolderAdapter viewHolderAdapter, final int itemPosition, @NonNull SearchCompanyDetails modelSearchCompanyDetails) {

                //Get the position of item when user cliked
                // String userGetId = getRef(itemPosition).getKey();

                Log.d(tag, "JobSearchActivity retrieve data: Get data");
                //Set the data to display on view holder
                viewHolderAdapter.datatitle.setText(modelSearchCompanyDetails.getJob_title());
                viewHolderAdapter.dataDescription.setText(modelSearchCompanyDetails.getCompany_description());
                viewHolderAdapter.dataLocation.setText(modelSearchCompanyDetails.getJob_location());
                viewHolderAdapter.dataTypes.setText(modelSearchCompanyDetails.getJob_type());

                //Start Activity new Intent on ViewHolder Adapter
                //Code adapted from https://www.youtube.com/watch?v=vlXZ287Sf9A (Author: CodingCafe)
                viewHolderAdapter.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.d(tag, "JobSearchActivity onClick ViewHolder: Selected");

                        final String userGetId = getRef(itemPosition).getKey();

                        //New activity intent passing
                        Intent recyclerViewHolderPage = new Intent(JobSearchActivity.this, ActivityRecyclerPage.class);
                        recyclerViewHolderPage.putExtra("userGetId", userGetId);

                        startActivity(recyclerViewHolderPage);

                    }
                });


                //Picasso image loading
                // Code adapted from https://square.github.io/picasso/
                //Picasso.get().load(model.get)

                //Indeterminate loading until data completely retrieve
                Log.d(tag, "JobSearchActivity null check: Check data is null or available");
                if (RecyclerOptions == null) {
                    progressBarLayout.setVisibility(View.VISIBLE);

                } else {

                    progressBarLayout.setVisibility(View.INVISIBLE);
                }
            }

            @NonNull
            @Override
            public ViewHolderAdapter onCreateViewHolder(@NonNull ViewGroup layout, int viewType) {

                //Implement view holder to layout
                View viewLayout = LayoutInflater.from(layout.getContext()).inflate(R.layout.activity_viewholder, layout, false);
                return new ViewHolderAdapter(viewLayout); // return to implement the data to recycler viewer

            }
        };

        textViewMessageOne.setVisibility(View.INVISIBLE);
        textViewMessageTwo.setVisibility(View.INVISIBLE);
        textViewMessageThree.setVisibility(View.INVISIBLE);
        recyclerView.setLayoutManager(layoutManager);
        RecyclerAdapter.startListening();
        recyclerView.setAdapter(RecyclerAdapter);

        /*
        --------------- RecyclerViewer (Show all available jobs in database) end here-------------------
        */

        /*
        -----------------Count number of data available in database and set layout visibility start here---------------
        */
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot data) {

                //Return the number of available saved data
                //code adapted from https://developer.android.com/guide/topics/resources/string-resource.html#Plurals-->
                countJob = (int) data.getChildrenCount();
                String integer = Integer.toString(countJob);
                String textViewOne = getString(R.string.allJob, integer);

                //Check the data if exist or not available
                if (data.exists()) {
                    textViewMessageOne.setText(textViewOne);
                    textViewMessageOne.setVisibility(View.VISIBLE);
                    textViewMessageTwo.setVisibility(View.INVISIBLE);
                    textViewMessageThree.setVisibility(View.INVISIBLE);
                    reloadButton.setVisibility(View.INVISIBLE);
                    reloadButton.setVisibility(View.INVISIBLE);

                } else {
                    textViewMessageOne.setText(textViewOne); // Check ME

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

                Toast.makeText(getApplicationContext(), "No data are available", Toast.LENGTH_SHORT).show();
                progressBarLayout.setVisibility(View.INVISIBLE);
            }
        });
    }
     /*
      -----------------Count number of data available in database and set layout visibility end here---------------
     */

    /*
    ----------RecyclerViewer search bar string validation (If string is empty)------------
    */

    private boolean checkString(String stringSearchText) {
        Log.d(tag, "JobSearchActivity.java : Check string if empty");
        if (stringSearchText.isEmpty()) {
            return true; //if empty set error message
        } else {
            return false;//else no error message
        }
    }

    /*
    -----------------Search bar query implementation on job title----------------------
    */

    // Code adapted from https://www.youtube.com/watch?v=KOUyvCkwRss
    private void OnSearchClick() {

        //get value of user input from search bar
        final String textSearch = searchBar.getText().toString();

        //Check string if empty
        if (checkString(textSearch)) {
            recyclerView.setVisibility(View.INVISIBLE);
            textViewMessageOne.setVisibility(View.INVISIBLE);
            textViewMessageTwo.setVisibility(View.INVISIBLE);
            textViewMessageThree.setVisibility(View.INVISIBLE);
            reloadButton.setVisibility(View.VISIBLE);

            Toast.makeText(getApplicationContext(), "Enter a job name to search", Toast.LENGTH_LONG).show();


        } else {

            textViewMessageOne.setVisibility(View.VISIBLE);
            textViewMessageTwo.setVisibility(View.INVISIBLE);
            textViewMessageThree.setVisibility(View.INVISIBLE);
            imageButton.setVisibility(View.VISIBLE);
            reloadButton.setVisibility(View.INVISIBLE);

            //code adapted from https://stackoverflow.com/questions/54155576/android-firebase-search-query-not-working-properly
            //Convert lowercase prefix letter to uppercase
            String capitalizedLetter = textSearch.substring(0, 1).toUpperCase() + textSearch.substring(1);

            /*
            -------Job search filter by job title start here------
            */
            //Search query order by job_title
            Query text = databaseReference.orderByChild("job_title").startAt(capitalizedLetter).endAt(capitalizedLetter + "\uf8ff");//unicode string to enable searching

            text.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshotJobTitle) {

                    countJob = (int) snapshotJobTitle.getChildrenCount();
                    String integer = Integer.toString(countJob);
                    //code adapted from https://developer.android.com/guide/topics/resources/string-resource.html#Plurals-->
                    String textView = getString(R.string.allJob, integer);

                    //if query of user input data available
                    if (snapshotJobTitle.exists()) {
                        //initialize array list
                        recyclerView.setVisibility(View.VISIBLE);
                        textViewMessageOne.setText(textView);
                        dataList = new ArrayList<>();
                        dataList.clear();

                        for (DataSnapshot snapshot : snapshotJobTitle.getChildren()) { //start array list of data
                            dataList.add(snapshot.getValue(SearchCompanyDetails.class));//add the string to arraylist from models search company details
                        }
                        //onViewHolderOnClick.OnViewHolder();

                        //if query of user input data not available
                    } else {
                        Toast.makeText(getApplicationContext(), "Sorry, the job you search for is not available", Toast.LENGTH_LONG).show();
                        recyclerView.setVisibility(View.INVISIBLE);
                        imageButton.setVisibility(View.VISIBLE);
                        reloadButton.setVisibility(View.VISIBLE);
                    }
                    //Set adapter and array list
                    Recycler_Viewer_Adapter Adapter = new Recycler_Viewer_Adapter(dataList, getApplicationContext(), JobSearchActivity.this);
                    recyclerView.setAdapter(Adapter);
                    Adapter.notifyDataSetChanged();

                    //TextView message one string replacement
                    if (countJob <= 1) {
                        textViewOne = getString(R.string.allJob, integer);
                        textViewOne = textViewOne.replace("jobs", "job");
                        textViewMessageOne.setText(textViewOne);

                    } else {
                        //code adapted from https://developer.android.com/guide/topics/resources/string-resource.html#Plurals-->
                        textViewOne = getString(R.string.allJob, integer);
                        textViewOne = textViewOne.replace("jobss", "jobs");
                        textViewMessageOne.setText(textViewOne);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(getApplicationContext(), "Database error, no data are retrieved from database", Toast.LENGTH_SHORT).show();

                }

            });
        }
    }

    /*
    --------------Reload existing data available back to recyclerViewer start here-------------
    */
    public void reloadData() {

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                countJob = (int) dataSnapshot.getChildrenCount();
                String integer = Integer.toString(countJob);
                //code adapted from https://developer.android.com/guide/topics/resources/string-resource.html#Plurals-->
                String reloadTextView = getString(R.string.allJob, integer);

                imageButton.setVisibility(View.VISIBLE);
                reloadButton.setVisibility(View.INVISIBLE);
                recyclerView.setVisibility(View.VISIBLE);
                textViewMessageOne.setText(reloadTextView);
                textViewMessageOne.setVisibility(View.VISIBLE);
                textViewMessageTwo.setVisibility(View.INVISIBLE);
                textViewMessageThree.setVisibility(View.INVISIBLE);

                LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
                recyclerView.setLayoutManager(layoutManager);
                recyclerView.setAdapter(RecyclerAdapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

                Toast.makeText(getApplicationContext(), "Database error, no data are retrieved from database", Toast.LENGTH_LONG).show();
            }
        });
    }
    /*
    --------------Reload existing data available back to recyclerViewer end here-------------
    */


    /*
    ----------Filter dialog box(include spinner for both job filter by district and job filter by job type) start here-------
    */
    public void OnClickFilter() {

        builder = new AlertDialog.Builder(JobSearchActivity.this);
        builder.setTitle("Job Filter");


        //inflate layout
        final LayoutInflater layout = this.getLayoutInflater();
        View viewLayout = layout.inflate(R.layout.activity_searchfilter, null);

        // searchFilterTextView = viewLayout.findViewById(R.id.filter_JobLocation);
        spinnerJobDistrict = viewLayout.findViewById(R.id.spinner_JobDistrict);
        spinnerJobTypes = viewLayout.findViewById(R.id.spinner_JobTypes);


        //Code adapted from https://android--code.blogspot.com/2015/08/android-spinner-hint.html (From URL Link) ,author Saiful Alam
        //Disable the first array in spinner (The dropdown list for filter job by district )
        locationList = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, ArrayListAdapter.jobDistrict) {

            @Override
            public boolean isEnabled(int stringListLocation) {

                return stringListLocation != 0;

            }

            @Override
            public View getDropDownView(int jobLocation, @Nullable View convertView, @NonNull ViewGroup parent) {

                View dropDownView = super.getDropDownView(jobLocation, convertView, parent);
                TextView item = (TextView) dropDownView;

                if (jobLocation == 0) {

                    item.setTextColor(getResources().getColor(R.color.Grey));

                } else {

                    item.setTextColor(getResources().getColor(R.color.black));
                }
                return dropDownView; //return result
            }

        };


        //locationList = new ArrayAdapter<>(this,android.R.layout.simple_spinner_item, ArrayListAdapter.jobLocation);
        locationList.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerJobDistrict.setAdapter(locationList);

        //Code adapted from https://android--code.blogspot.com/2015/08/android-spinner-hint.html (From URL Link) ,author Saiful Alam
        //Disable the first array in spinner(The dropdown list for filter job by job type)
        jobTypesList = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, ArrayListAdapter.jobTypes) {

            @Override
            public boolean isEnabled(int itemOne) {

                return itemOne != 0;
            }

            @Override
            public View getDropDownView(int jobTypes, @Nullable View convertView, @NonNull ViewGroup parent) {

                View dropDownView = super.getDropDownView(jobTypes, convertView, parent);
                TextView item = (TextView) dropDownView;

                if (jobTypes == 0) {

                    item.setTextColor(getResources().getColor(R.color.Grey));

                } else {

                    item.setTextColor(getResources().getColor(R.color.black));
                }
                return dropDownView; //return result
            }

        };

        /*--To avoid user from applying multiply filter as the filter only work once at the time(Multiple filter for future improvement)--*/
        //Code adpated from https://stackoverflow.com/questions/12108893/set-onclicklistener-for-spinner-item
       // locationList = new ArrayAdapter<String>(this,android.R.layout.simple_dropdown_item_1line,ArrayListAdapter.jobDistrict);
        spinnerJobDistrict.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedJobDistrict = spinnerJobDistrict.getItemAtPosition(position).toString();
                if(selectedJobDistrict.matches("BruneiMuara|Belait|Tutong|Temburong")){
                    spinnerJobTypes.setEnabled(false);

                }else{
                    spinnerJobTypes.setEnabled(true);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        jobTypesList.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerJobTypes.setAdapter(jobTypesList);
        builder.setView(viewLayout);// Set layout

        spinnerJobTypes.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedJobType = spinnerJobTypes.getItemAtPosition(position).toString();
                if(selectedJobType.matches("FullTime|PartTime|Internship")){
                    spinnerJobDistrict.setEnabled(false);
                }else{
                    spinnerJobTypes.setEnabled(true);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //Implement dialog with cancel to exit
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        //Implement dialog with apply filter to send the string of user query to filter method
        builder.setPositiveButton("Apply Filter", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                String selectedJobDistrict = spinnerJobDistrict.getSelectedItem().toString();
                if (selectedJobDistrict.equals("Show jobs based on district"))
                {
                    Toast.makeText(getApplicationContext(),"No job district selected", Toast.LENGTH_LONG).show();

                } else if(spinnerJobDistrict.getSelectedItem().toString().matches("BruneiMuara|Belait|Tutong|Temburong")){
                   /*--For filter by job district
                    Convert the selected value from array to string--*/
                    //String jobByLocation = spinnerJobDistrict.getSelectedItem().toString();
                    dialog.dismiss();
                    ApplyFilterJobDistrict(selectedJobDistrict); //Send the string to ApplyFilter method

                }

                //Spinner for filter by job types validation
                String selectedJobType = spinnerJobTypes.getSelectedItem().toString();
                if(selectedJobType.equals("Show based on job types"))
                {
                    Toast.makeText(getApplicationContext(),"No data selected",Toast.LENGTH_SHORT).show();

                   //code adapted from https://stackoverflow.com/questions/10208052/string-equals-with-multiple-conditions-and-one-action-on-result
                }else if(spinnerJobTypes.getSelectedItem().toString().matches("FullTime|PartTime|Internship")){
                    Toast.makeText(getApplicationContext(), spinnerJobTypes.getSelectedItem().toString(), Toast.LENGTH_LONG).show();
                    dialog.dismiss();
                    ApplyFilterJobType(selectedJobType); //Send string to ApplyFilterJobType if string matches
                }

                Log.d(tag, "Apply filter: Filter started success ");
            }

        });
        //Show the dialog box
        builder.show();

    }
     /*
    ----------Filter dialog box(include spinner for both job filter by district and job filter by job type) end here-------
    */


    /*
    -----Continuation for filter dialog box(sending user filter query based on job filter by district and job filter by job type) start here-----
    */
    private void ApplyFilterJobDistrict(final String jobByLocation) {

        //First query filter job based on brunei district (Stored in ArrayListAdapter)
        final Query stringJobLocation = databaseReference.orderByChild("job_location").startAt(jobByLocation).endAt(jobByLocation + "\uf8ff");//unicode string to enable searching
        stringJobLocation.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot data) {

                countJob = (int) data.getChildrenCount();
                String integer = Integer.toString(countJob);
                //code adapted from https://developer.android.com/guide/topics/resources/string-resource.html#Plurals-->
                textViewTwo = getString(R.string.jobLocation, integer, jobByLocation);

                if (data.exists()) {

                    dataList = new ArrayList<>();// include only new array list at given user query
                    //Clear array list
                    //dataList.clear();

                    textViewMessageTwo.setText(textViewTwo);
                    textViewMessageTwo.setVisibility(View.VISIBLE);
                    textViewMessageOne.setVisibility(View.INVISIBLE);
                    textViewMessageThree.setVisibility(View.INVISIBLE);

                    for (DataSnapshot snapshot : data.getChildren()) { //start array list of data
                        dataList.add(snapshot.getValue(SearchCompanyDetails.class));//add the string to arraylist from models search company details
                    }

                    Recycler_Viewer_Adapter Adapter = new Recycler_Viewer_Adapter(dataList, getApplicationContext(), JobSearchActivity.this);  //Set adapter and array list
                    recyclerView.setAdapter(Adapter);
                    Adapter.notifyDataSetChanged();


                } else {
                    Toast.makeText(getApplicationContext(), "Sorry, no job are available for" + jobByLocation, Toast.LENGTH_SHORT).show();
                    textViewMessageTwo.setText(textViewTwo);
                    reloadButton.setVisibility(View.VISIBLE);
                }


                //TextView message two string replacement
                if (countJob <= 1) {

                    textViewTwo = getString(R.string.jobLocation, integer, jobByLocation);
                    textViewTwo = textViewTwo.replace("jobs", "job");
                    textViewMessageTwo.setText(textViewTwo);

                } else {

                    //code adapted from https://developer.android.com/guide/topics/resources/string-resource.html#Plurals-->
                    textViewTwo = getString(R.string.jobLocation, integer, jobByLocation);
                    textViewTwo = textViewTwo.replace("jobss", "jobs");
                    textViewMessageTwo.setText(textViewTwo);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(), "Database error, no data are retrieved from database", Toast.LENGTH_SHORT).show();

            }
        });

    }

    //Filter based on query start here (include filter by job district and filter by job type)
    private void ApplyFilterJobType(final String jobByTypes) {

        //Second query based on job filter by the job type
        Query stringJobTypes = databaseReference.orderByChild("job_type").startAt(jobByTypes).endAt(jobByTypes + "\uf8ff");//unicode string to enable searching
        stringJobTypes.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot data) {

                countJob = (int) data.getChildrenCount();
                String int_Count_JobType = Integer.toString(countJob);
                //code adapted from https://developer.android.com/guide/topics/resources/string-resource.html#Plurals-->
                textViewThree = getString(R.string.jobType,int_Count_JobType, jobByTypes);

                if (data.exists()) {

                    dataList = new ArrayList<>();
                    //Clear array list
                    //dataList.clear();

                    textViewMessageThree.setText(textViewThree);
                    textViewMessageThree.setVisibility(View.VISIBLE);
                    textViewMessageOne.setVisibility(View.INVISIBLE);
                    textViewMessageTwo.setVisibility(View.INVISIBLE);

                    for (DataSnapshot snapshot : data.getChildren()) { //start array list of data
                        dataList.add(snapshot.getValue(SearchCompanyDetails.class));//add the string to arraylist from models search company details
                    }

                    Recycler_Viewer_Adapter Adapter = new Recycler_Viewer_Adapter(dataList, getApplicationContext(), JobSearchActivity.this);  //Set adapter and array list
                    recyclerView.setAdapter(Adapter);
                    Adapter.notifyDataSetChanged();

                } else {
                    Toast.makeText(getApplicationContext(), "Sorry, there are no job type available for" + jobByTypes, Toast.LENGTH_SHORT).show();
                    textViewMessageThree.setText(textViewThree);
                    reloadButton.setVisibility(View.VISIBLE);

                }

                 /*
                ------TextView message two string replacement-------
                */
                if (countJob <= 1) {

                    textViewThree = getString(R.string.jobType, int_Count_JobType, jobByTypes);
                    textViewThree = textViewThree.replace("jobs", "job");
                    textViewMessageThree.setText(textViewThree);

                } else {

                    //code adapted from https://developer.android.com/guide/topics/resources/string-resource.html#Plurals-->
                    textViewThree = getString(R.string.jobType, int_Count_JobType, jobByTypes);
                    textViewThree = textViewThree.replace("jobss", "jobs");
                    textViewMessageThree.setText(textViewThree);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(), "Database error, no data are retrieved from database", Toast.LENGTH_SHORT).show();
            }
        });
    }
     /*
    -----Continuation for filter dialog box(sending user filter query based on job filter by district and job filter by job type) end here-----
    */


    @Override
    protected void onStart() {
        super.onStart();

        if (RecyclerAdapter != null) {
            RecyclerAdapter.startListening();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (RecyclerAdapter != null) {
            RecyclerAdapter.stopListening();
        }
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        if (RecyclerAdapter != null) {
            RecyclerAdapter.startListening();
        }
    }


    //Set OnClick listener to each button and methods
    @Override
    public void onClick(View userTrigger) {

        switch (userTrigger.getId()) {

            case R.id.searchjob_Backbutton:
                Intent backButton = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(backButton);
                finish();
                break;

            case R.id.searching_Button:
                OnSearchClick();
                break;

            case R.id.reload_Button:
                reloadData();
                break;

            case R.id.search_Filter:
                OnClickFilter();
                break;

        }
    }

    //Code adapted from https://www.youtube.com/watch?v=69C1ljfDvl0 (2019,CodingWithMitch)
    //View holder onClick
    @Override
    public void OnViewHolderClick(final int onSetPosition) {

        Log.d(tag, "OnViewHolderClick started: Clicked " + onSetPosition);

        //Redirect user to new page upon onClick event
        Intent recyclerViewHolderPage = new Intent(JobSearchActivity.this, ActivityRecyclerPage.class);
        // databaseReference = FirebaseDatabase.getInstance().getReference("Company");
        recyclerViewHolderPage.putExtra("SelectedJob", dataList.get(onSetPosition)); //is based on array postion
        startActivity(recyclerViewHolderPage);
    }

}

    // @Override
    // public void OnViewHolder(int onSetPosition) {
    // Log.d(tag,"ViewHolder OnClick begin: Started"+ onSetPosition);
    // Toast.makeText(JobSearchActivity.this,"Selected"+ onSetPosition,Toast.LENGTH_LONG).show();

    //dataList.get(onSetPosition);

    //Intent viewHolderOne = new Intent(getApplicationContext(), ActivityRecyclerPage.class);
    //final String userGetId = dataList.get(onSetPosition).getJob_title();

    //New activity intent passing
    //Intent recyclerViewHolderPage = new Intent(JobSearchActivity.this,ActivityRecyclerPage.class);
    //recyclerViewHolderPage.putExtra("");

    //startActivity(recyclerViewHolderPage);


    // startActivity(viewHolderOne);


    //  }






    // Toast.makeText(getApplicationContext(), "You select " + onSetPosition, Toast.LENGTH_LONG).show();

    //databaseReference.addValueEventListener(new ValueEventListener() {
    // @Override
    // public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
    //dataList.clear();
    //dataList = new ArrayList<>();// array list is still old, new array list is not implement as to get the actual old array list when user click based on search filter.
    // String key = dataSnapshot.getKey();

    // String dataKeys="";
    // for (DataSnapshot dataSnapshot1: dataSnapshot.getChildren()) { //get all children
    //    dataList.add(dataSnapshot1.getValue(SearchCompanyDetails.class)); //get the children value
    //  String id = dataSnapshot1.getKey();
    // Log.d(tag, "get" + id);

    // }


//Search
   /* private void Searching(String string){

        ArrayList<SearchCompanyDetails> mylist = new ArrayList<>();
        for (SearchCompanyDetails object : dataList){
            if (object.getJob_title().toLowerCase().contains(string.toLowerCase()))
            {
                mylist.add(object);
            }
        }
        Recycler_Viewer_Adapter recyclerViewerAdapter = new Recycler_Viewer_Adapter(mylist);
        recyclerView.setAdapter(recyclerViewerAdapter);
    }*/
