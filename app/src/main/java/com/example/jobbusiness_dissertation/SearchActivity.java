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
import android.os.Bundle;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
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
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

//import static com.example.jobbusiness_dissertation.ArrayListAdapter.ArrayListAdapter.jobLocation;
public class SearchActivity extends AppCompatActivity implements View.OnClickListener,Recycler_Viewer_Adapter.OnViewHolderOnClick {

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
    private static final String tag = "SearchActivity";

    //Others
    private ImageButton backButton, reloadButton;
    private EditText searchBar;
    private ImageButton imageButton, searchFilter;
    public ProgressBar progressBarLayout;
    private TextView textViewMessageOne, textViewMessageTwo;
    //private AutoCompleteTextView searchFilterTextView;
    private AlertDialog.Builder builder;
    private Spinner spinnerJobDistrict, spinnerJobTypes;
    // public Chip tags;
    private String textViewOne, textViewTwo;

    //Firebase
    FirebaseRecyclerOptions<SearchCompanyDetails> RecyclerOptions;
    FirebaseRecyclerAdapter<SearchCompanyDetails, ViewHolderAdapter> RecyclerAdapter;
    private Recycler_Viewer_Adapter recycler_viewer_adapter;
    private DatabaseReference databaseReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_searchbar);

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
        textViewMessageOne = findViewById(R.id.available_Job);
        textViewMessageTwo = findViewById(R.id.location_Job);
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


        /*
        --------------- RecyclerViewer (All jobs)-------------------
        */
        //Firebase, initialize the database Reference for getting the data
        databaseReference = FirebaseDatabase.getInstance().getReference("Company");//get reference of the recycle view .getReference.getChild()

        //Initialise recyclerViewer
        RecyclerOptions = new FirebaseRecyclerOptions.Builder<SearchCompanyDetails>().setQuery
                (databaseReference, SearchCompanyDetails.class).build();// database reference and model


        //This method initially show the available job search in firebase
        RecyclerAdapter = new FirebaseRecyclerAdapter<SearchCompanyDetails, ViewHolderAdapter>(RecyclerOptions) {
            @Override
            public void onBindViewHolder(@NonNull ViewHolderAdapter viewHolderAdapter, final int itemPosition, @NonNull SearchCompanyDetails modelSearchCompanyDetails) {

                //Get the position of item when user cliked
                // String userGetId = getRef(itemPosition).getKey();

                Log.d(tag, "SearchActivity retrieve data: Get data");
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
                        Log.d(tag, "SearchActivity onClick ViewHolder: Selected");

                        final String userGetId = getRef(itemPosition).getKey();

                        //New activity intent passing
                        Intent recyclerViewHolderPage = new Intent(SearchActivity.this, ActivityRecyclerPage.class);
                        recyclerViewHolderPage.putExtra("userGetId", userGetId);

                        startActivity(recyclerViewHolderPage);


                    }

                });


                //Picasso image loading
                // Code adapted from https://square.github.io/picasso/
                //Picasso.get().load(model.get)

                //Indeterminate loading until data completely retrieve
                Log.d(tag, "SearchActivity null check: Check data is null or available");
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
        recyclerView.setLayoutManager(layoutManager);
        RecyclerAdapter.startListening();
        recyclerView.setAdapter(RecyclerAdapter);


        /*
        -----------------Implementation of count number of data available and layout visibility---------------
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
                    reloadButton.setVisibility(View.INVISIBLE);
                    reloadButton.setVisibility(View.INVISIBLE);

                } else {
                    textViewMessageOne.setText(textViewOne);

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
    ----------RecyclerViewer search bar string validation------------
    */

    private boolean checkString(String stringSearchText) {
        Log.d(tag, "SearchActivity.java : Check string if empty");
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

        //if empty
        if (checkString(textSearch)) {
            recyclerView.setVisibility(View.INVISIBLE);
            textViewMessageOne.setVisibility(View.INVISIBLE);
            textViewMessageTwo.setVisibility(View.INVISIBLE);
            reloadButton.setVisibility(View.VISIBLE);

            Toast.makeText(getApplicationContext(), "Enter a job name to search", Toast.LENGTH_LONG).show();


        } else {

            textViewMessageOne.setVisibility(View.VISIBLE);
            textViewMessageTwo.setVisibility(View.INVISIBLE);
            imageButton.setVisibility(View.VISIBLE);
            reloadButton.setVisibility(View.INVISIBLE);

            //code adapted from https://stackoverflow.com/questions/54155576/android-firebase-search-query-not-working-properly
            //Convert lowercase prefix letter to uppercase
            String capitalizedLetter = textSearch.substring(0, 1).toUpperCase() + textSearch.substring(1);

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
                    Recycler_Viewer_Adapter Adapter = new Recycler_Viewer_Adapter(dataList, getApplicationContext(), SearchActivity.this);
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
    --------------Reload data back to recyclerViewer-------------
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
    ------Search filter based on job location-------
    */

    public void OnClickFilter() {

        //Query by Job Location
        builder = new AlertDialog.Builder(SearchActivity.this);
        builder.setTitle("Job Filter");

        //inflate layout
        final LayoutInflater layout = this.getLayoutInflater();
        View viewLayout = layout.inflate(R.layout.activity_searchfilter, null);

        // searchFilterTextView = viewLayout.findViewById(R.id.filter_JobLocation);
        spinnerJobDistrict = viewLayout.findViewById(R.id.spinner_JobDistrict);
        spinnerJobTypes = viewLayout.findViewById(R.id.spinner_JobTypes);


        //Code adapted from https://android--code.blogspot.com/2015/08/android-spinner-hint.html (From URL Link) ,author Saiful Alam
        locationList = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, ArrayListAdapter.jobLocation) {

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


        //spinner array list adapter
        //locationList = new ArrayAdapter<>(this,android.R.layout.simple_spinner_item, ArrayListAdapter.jobLocation);
        locationList.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerJobDistrict.setAdapter(locationList);
        jobTypesList.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerJobTypes.setAdapter(jobTypesList);
        builder.setView(viewLayout);// Set layout

        //Implement dialog with cancel to exit
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        //Implement dialog with apply filter to start searching
        builder.setPositiveButton("Apply Filter", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                if (spinnerJobDistrict.getSelectedItem().toString().equals("Show jobs based on district")) {
                    Toast.makeText(getApplicationContext(), spinnerJobDistrict.getSelectedItem().toString(), Toast.LENGTH_LONG).show();
                    dialog.dismiss();
                }

                if (spinnerJobTypes.getSelectedItem().toString().equals("Job type")) {
                    Toast.makeText(getApplicationContext(), spinnerJobTypes.getSelectedItem().toString(), Toast.LENGTH_LONG).show();
                    dialog.dismiss();
                }

                String jobByLocation = spinnerJobDistrict.getSelectedItem().toString();
                ApplyFilter(jobByLocation);
                String jobByTypes = spinnerJobTypes.getSelectedItem().toString();
                ApplyFilter(jobByTypes);
                Log.d(tag, "Apply filter: Filter started success ");
            }

        });
        //Show the dialog
        builder.show();


        /*---Chip group---*/
       /* final ChipGroup group = viewLayout.findViewById(R.id.chipGroup);
        //Create the autocomplete string based on array adapter
        locationList = new ArrayAdapter<>(this, android.R.layout.select_dialog_item, ArrayListAdapter.jobLocation);
        searchFilterTextView.setAdapter(locationList);

        //When user click the autocomplete string itappear into chipgroup
        searchFilterTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Remove string when first string is input
                searchFilterTextView.setText("");

                //Chip groups begin
                Chip chip = (Chip) layout.inflate(R.layout.activity_tag, null, false);
                //Set text and get text from autocomplete
                chip.setText(((TextView) view).getText());
                //Enable chip to close upon user click
                chip.setOnCloseIconClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View layout) {
                        group.removeView(layout);
                    }

                });
                group.addView(chip);
            }
        });
        //Set both chip gropu and editext autocomplete appear in dialog
        dialog.setView(viewLayout);
        //Implement dialog with cancel to exit
        dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        //Implement dialog with apply filter to start searching
        dialog.setPositiveButton("Apply Filter", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                stringList = new ArrayList<>();
                StringBuilder filter = new StringBuilder("");
                //Chip group
                for (int a = 0; a < group.getChildCount(); a++) {

                    Chip chip = (Chip) group.getChildAt(a);
                    stringList.add(chip.getText().toString());// filter and take the string to chip group
                    //Sort filter of string
                    Collections.sort(stringList);

                    //convert the arraylist to string
                    for (String list : stringList) {
                        filter.append(list).append(",");//append the comma in between the list
                    }
                    filter.setLength(filter.length() - 1);//remove the comma in the end of the list

                }
                //After string format is complete
                //Pass the string to start filter the user query
                String name = filter.toString();
                ApplyFilter(name);
                Log.d(tag, "Apply filter: Filter started ");
            }
        });
        //Show the dialog
        dialog.show();*/

    }

    public void ApplyFilter(final String name) {

        //Query by Job Location (First Query)
        Query stringJobLocation = databaseReference.orderByChild("job_location").startAt(name).endAt(name + "\uf8ff");//unicode string to enable searching
        stringJobLocation.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot data) {

                countJob = (int) data.getChildrenCount();
                String integer = Integer.toString(countJob);
                //code adapted from https://developer.android.com/guide/topics/resources/string-resource.html#Plurals-->
                textViewTwo = getString(R.string.jobLocation, integer, name);

                if (data.exists()) {

                    dataList = new ArrayList<>();
                    //Clear array list
                    //dataList.clear();

                    textViewMessageTwo.setText(textViewTwo);
                    textViewMessageTwo.setVisibility(View.VISIBLE);
                    textViewMessageOne.setVisibility(View.INVISIBLE);

                    for (DataSnapshot snapshot : data.getChildren()) { //start array list of data
                        dataList.add(snapshot.getValue(SearchCompanyDetails.class));//add the string to arraylist from models search company details
                    }

                    Recycler_Viewer_Adapter Adapter = new Recycler_Viewer_Adapter(dataList, getApplicationContext(), SearchActivity.this);  //Set adapter and array list
                    recyclerView.setAdapter(Adapter);
                    Adapter.notifyDataSetChanged();


                } else {
                    Toast.makeText(getApplicationContext(), "Sorry, no job are available in" + name, Toast.LENGTH_SHORT).show();
                    textViewMessageTwo.setText(textViewTwo);
                    reloadButton.setVisibility(View.VISIBLE);
                }

                /*
                ----TextView message two string replacement---
                */
                if (countJob <= 1) {

                    textViewTwo = getString(R.string.jobLocation, integer, name);
                    textViewTwo = textViewTwo.replace("jobs", "job");
                    textViewMessageTwo.setText(textViewTwo);

                } else {

                    //code adapted from https://developer.android.com/guide/topics/resources/string-resource.html#Plurals-->
                    textViewTwo = getString(R.string.jobLocation, integer, name);
                    textViewTwo = textViewTwo.replace("jobss", "jobs");
                    textViewMessageTwo.setText(textViewTwo);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(), "Please make sure you have internet connection", Toast.LENGTH_SHORT).show();

            }
        });

        //Second jobfilter by JobTypes
        Query stringJobTypes = databaseReference.orderByChild("job_type").startAt(name).endAt(name + "\uf8ff");//unicode string to enable searching
        stringJobTypes.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot data) {

                countJob = (int) data.getChildrenCount();
                String integer = Integer.toString(countJob);
                //code adapted from https://developer.android.com/guide/topics/resources/string-resource.html#Plurals-->
                textViewTwo = getString(R.string.jobLocation, integer, name);

                if (data.exists()) {

                    dataList = new ArrayList<>();
                    //Clear array list
                    //dataList.clear();

                    textViewMessageTwo.setText(textViewTwo);
                    textViewMessageTwo.setVisibility(View.VISIBLE);
                    textViewMessageOne.setVisibility(View.INVISIBLE);

                    for (DataSnapshot snapshot : data.getChildren()) { //start array list of data
                        dataList.add(snapshot.getValue(SearchCompanyDetails.class));//add the string to arraylist from models search company details
                    }

                    Recycler_Viewer_Adapter Adapter = new Recycler_Viewer_Adapter(dataList, getApplicationContext(), SearchActivity.this);  //Set adapter and array list
                    recyclerView.setAdapter(Adapter);
                    Adapter.notifyDataSetChanged();


                } else {
                    Toast.makeText(getApplicationContext(), "Sorry, no job are available in" + name, Toast.LENGTH_SHORT).show();
                    textViewMessageTwo.setText(textViewTwo);
                    reloadButton.setVisibility(View.VISIBLE);
                }

                 /*
                ----TextView message two string replacement---
                */
                if (countJob <= 1) {

                    textViewTwo = getString(R.string.jobLocation, integer, name);
                    textViewTwo = textViewTwo.replace("jobs", "job");
                    textViewMessageTwo.setText(textViewTwo);

                } else {

                    //code adapted from https://developer.android.com/guide/topics/resources/string-resource.html#Plurals-->
                    textViewTwo = getString(R.string.jobLocation, integer, name);
                    textViewTwo = textViewTwo.replace("jobss", "jobs");
                    textViewMessageTwo.setText(textViewTwo);
                }
            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(), "Please make sure you have internet connection", Toast.LENGTH_SHORT).show();
            }
        });
    }


    /*
    --------Recycler adapter--------- (Check me again)
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
                Intent backbutton = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(backbutton);
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


    // @Override
    // public void OnViewHolder(int onSetPosition) {
    // Log.d(tag,"ViewHolder OnClick begin: Started"+ onSetPosition);
    // Toast.makeText(SearchActivity.this,"Selected"+ onSetPosition,Toast.LENGTH_LONG).show();

    //dataList.get(onSetPosition);

    //Intent viewHolderOne = new Intent(getApplicationContext(), ActivityRecyclerPage.class);
    //final String userGetId = dataList.get(onSetPosition).getJob_title();

    //New activity intent passing
    //Intent recyclerViewHolderPage = new Intent(SearchActivity.this,ActivityRecyclerPage.class);
    //recyclerViewHolderPage.putExtra("");

    //startActivity(recyclerViewHolderPage);


    // startActivity(viewHolderOne);


    //  }


    //Code adapted from https://www.youtube.com/watch?v=69C1ljfDvl0 (2019,CodingWithMitch)
    //View holder onClick
    @Override
    public void OnViewHolderClick(final int onSetPosition) {

        Log.d(tag, "OnViewHolderClick started: Clicked " + onSetPosition);

        //Passing data from Search to ActivityRecylerPage
        Intent recyclerViewHolderPage = new Intent(SearchActivity.this, ActivityRecyclerPage.class);
        // databaseReference = FirebaseDatabase.getInstance().getReference("Company");
        recyclerViewHolderPage.putExtra("SelectedJob", dataList.get(onSetPosition)); //is based on array postion
        startActivity(recyclerViewHolderPage);
    }
}


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
