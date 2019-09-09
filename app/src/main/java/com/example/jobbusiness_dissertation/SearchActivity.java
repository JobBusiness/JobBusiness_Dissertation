package com.example.jobbusiness_dissertation;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.jobbusiness_dissertation.Adapter.Recycler_Viewer_Adapter;
import com.example.jobbusiness_dissertation.Models.SearchCompanyDetails;
import com.example.jobbusiness_dissertation.RecylerViewHolder.ViewHolderAdapter;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class SearchActivity extends AppCompatActivity implements View.OnClickListener {

    private DatabaseReference databaseReference;
    ArrayList<SearchCompanyDetails> dataList;

    private RecyclerView recyclerView;
    private SearchView searchView;
    private Button backButton;

    private EditText searchBar;
    private ImageButton imageButton;
    public ProgressBar progressBarLayout;

    private static final String tag = "SearchActivity";

    FirebaseRecyclerOptions<SearchCompanyDetails> RecyclerOptions;
    FirebaseRecyclerAdapter<SearchCompanyDetails, ViewHolderAdapter> RecyclerAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_searchbar);
        //Firebase UI


        progressBarLayout = findViewById(R.id.indeterminate_ProgressBar);



        //Initialize the database Reference for getting the data
        databaseReference = FirebaseDatabase.getInstance().getReference("Company");//get reference of the recycle view .getReference.getChild()

        /*--Recycler Viewer initialisation--*/
        recyclerView = findViewById(R.id.recyclerViewer);
        //Set Layout Manager
        //recyclerView.setLayoutManager(new LinearLayoutManager(this));
        //fixed size
        recyclerView.setHasFixedSize(true);
        /*--Recyler Viewer End--*/


        //searchView = findViewById(R.id.search_View);

        //Toolbar Button initialisation
        backButton = findViewById(R.id.searchjob_Backbutton);
        backButton.setOnClickListener(this);

        //Edit text (Search bar)
        searchBar = findViewById(R.id.edittext_SearchView);

        //Search Button
        imageButton = findViewById(R.id.searching_Button);
        imageButton.setOnClickListener(this);


        // Initialise recyclerViewer
        RecyclerOptions = new FirebaseRecyclerOptions.Builder<SearchCompanyDetails>().setQuery
                (databaseReference, SearchCompanyDetails.class).build();// database reference and model

               //This method initially show the available job search in firebase
                RecyclerAdapter = new FirebaseRecyclerAdapter<SearchCompanyDetails, ViewHolderAdapter>(RecyclerOptions) {
                    @Override
                    protected void onBindViewHolder(@NonNull ViewHolderAdapter viewHolderAdapter, int i, @NonNull SearchCompanyDetails modelSearchCompanyDetails) {

                        //Set the model, text view and image
                        viewHolderAdapter.datatitle.setText(modelSearchCompanyDetails.getJob_title());
                        viewHolderAdapter.dataDescription.setText(modelSearchCompanyDetails.getCompany_description());
                    }

                    @NonNull
                    @Override
                    public ViewHolderAdapter onCreateViewHolder(@NonNull ViewGroup layout, int viewType) {

                        //Implement viewholder to layout
                        View view = LayoutInflater.from(layout.getContext()).inflate(R.layout.activity_viewholder, layout, false);

                        return new ViewHolderAdapter(view); // return to implement the data to recyler viewer
                    }
                };


                LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
                recyclerView.setLayoutManager(layoutManager);
                RecyclerAdapter.startListening();
                recyclerView.setAdapter(RecyclerAdapter);

    }

    // Checking string validation
    private boolean String (String stringSearchText) {
        Log.d(tag, "SearchActivity validation: Checking if string in username, email and password is null");
        if (stringSearchText.isEmpty()) {
            return true; //if empty set error message
        } else {
            return false;//else no error message
        }
    }


    // Code adapted from https://www.youtube.com/watch?v=KOUyvCkwRss
    private void OnSearchClick() {

        //get the string from edittext and remove whitespaces
        final String textSearch = searchBar.getText().toString();

        if (String(textSearch))
        {
            Toast.makeText(getApplicationContext(),"Enter job name to search",Toast.LENGTH_SHORT).show();

        }else {

            //Query implemented
            //code adapted from https://stackoverflow.com/questions/54155576/android-firebase-search-query-not-working-properly
            String capitalizedLetter = textSearch.substring(0, 1).toUpperCase() + textSearch.substring(1);
            Query text = databaseReference.orderByChild("job_title").startAt(capitalizedLetter).endAt(capitalizedLetter + "\uf8ff");//unicode string to enable searching

            text.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot data) {

                    if (data.exists()) {
                        //initialize array list
                        dataList = new ArrayList<>();
                        dataList.clear();
                        for (DataSnapshot snapshot : data.getChildren()) { //start array list of data
                            dataList.add(snapshot.getValue(SearchCompanyDetails.class));//add the string to arraylist from models search company details

                        }

                        Recycler_Viewer_Adapter Adapter = new Recycler_Viewer_Adapter(getApplicationContext(), dataList);  //Set adapter and array list
                        recyclerView.setAdapter(Adapter);
                        Adapter.notifyDataSetChanged();

                    } else{
                        Toast.makeText(getApplicationContext(),"Sorry, the job you search is not available",Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(getApplicationContext(), "Please make sure you have internet connection", Toast.LENGTH_SHORT).show();

                }
            });

        }
    }



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


    @Override
    protected void onStart() {
        super.onStart();

        if(RecyclerAdapter!=null){
            RecyclerAdapter.startListening();
        }

    }

    @Override
    protected void onStop() {
        super.onStop();
        if (RecyclerAdapter!=null){
            RecyclerAdapter.stopListening();

        }
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        if(RecyclerAdapter!=null){
            RecyclerAdapter.startListening();
        }
    }



    //Set on click method for button
    @Override
    public void onClick(View userTrigger) {

        if (userTrigger == backButton) {
            Intent backbutton = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(backbutton);
            finish();
        }
        if(userTrigger == imageButton){
            OnSearchClick();
        }



    }
}
