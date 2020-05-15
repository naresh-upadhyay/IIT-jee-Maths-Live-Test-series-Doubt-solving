package com.naresh.kingupadhyay.mathsking.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.naresh.kingupadhyay.mathsking.R;
import com.naresh.kingupadhyay.mathsking.UiMaterial.LoginOption;
import com.naresh.kingupadhyay.mathsking.network.LoadShortNotes;
import com.naresh.kingupadhyay.mathsking.viewholders.LoadShotNotesViewHolder;
import com.wang.avi.AVLoadingIndicatorView;


public class Short_note extends AppCompatActivity{

    private FirebaseRecyclerAdapter<LoadShortNotes, LoadShotNotesViewHolder> fireBaseRecyclerAdapter;
    private AVLoadingIndicatorView progressbar;
    private Toolbar toolbar;
    private RecyclerView mBlogList;
    private AdView mAdView;
    public String book;
    public String chapter;
    private String activityName = "shortnote";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favourites);

        MobileAds.initialize(this, "ca-app-pub-6924423095909700~8475665982");

        mAdView = findViewById(R.id.adVmain);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);


        Bundle bundle = getIntent().getExtras();
        book = bundle.getString("book");
        chapter = bundle.getString("chapter");
        SharedPreferences pref = getSharedPreferences("course", Context.MODE_PRIVATE);
        SharedPreferences.Editor edt = pref.edit();
        edt.putString("book",book);
        edt.putString("chapter",chapter);
        edt.putString("activity",activityName);
        edt.apply();

        mBlogList=(RecyclerView)findViewById(R.id.fav_myrecyclerview);
        mBlogList.setHasFixedSize(true);
        mBlogList.setLayoutManager(new LinearLayoutManager(this));
        progressbar = findViewById(R.id.progress_bar_backdrop);
        progressbar.show();

        favouritesLoad(progressbar);
        // Adding Toolbar to Main screen
        ImageButton back=findViewById(R.id.backConcept);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        // Adding Toolbar to Main screen
        toolbar = (Toolbar) findViewById(R.id.conceptToolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        TextView titleb = findViewById(R.id.titleb);
        titleb.setText("Formulas");

        SharedPreferences prefAdmin = getSharedPreferences("ActivityPREF", Context.MODE_PRIVATE);
        String id=prefAdmin.getString("id","");

        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.uploadExample);
        if(id.equals("vinayupadhyay02001@gmail.com") || id.equals("naresh03961999@gmail.com") || id.equals("rakshitaupadhyay1497@gmail.com") ) {
            fab.setVisibility(View.VISIBLE);
        }else{
            fab.setVisibility(View.GONE);
        }
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences prefUpdate = view.getContext().getSharedPreferences("skip", Context.MODE_PRIVATE);
                boolean skip=prefUpdate.getBoolean("skip",false);
                if(!skip){
                    Intent intent = new Intent(view.getContext(), PostShortNotes.class);
                    intent.putExtra("book",book);
                    intent.putExtra("chapter", chapter);
                    intent.putExtra("tab", activityName);
                    startActivity(intent);
                }else{
                    Toast.makeText(view.getContext(), "First authenticate yourself", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(view.getContext(), LoginOption.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }
            }
        });


    }


    @Override
    protected void onStart() {
        super.onStart();
        if(fireBaseRecyclerAdapter !=null)
            fireBaseRecyclerAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(fireBaseRecyclerAdapter !=null)
            fireBaseRecyclerAdapter.stopListening();
    }


    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }


    public void favouritesLoad(final AVLoadingIndicatorView progressbar){

        Query query = FirebaseDatabase.getInstance().getReference()
                .child("chapters").child(book).child(chapter).child(activityName).child("newdata")
                .limitToLast(100);

        FirebaseRecyclerOptions<LoadShortNotes> options =
                new FirebaseRecyclerOptions.Builder<LoadShortNotes>()
                        .setQuery(query, LoadShortNotes.class)
                        .build();
        fireBaseRecyclerAdapter=new FirebaseRecyclerAdapter <LoadShortNotes, LoadShotNotesViewHolder>
                (options){
            @Override
            protected void onBindViewHolder(@NonNull LoadShotNotesViewHolder holder, int position, @NonNull LoadShortNotes model) {
                holder.setTopic(position+1);
                holder.setTitle(model.getTitle());
                holder.setConceptPdfUrl(model.getConceptPdfUrl());
                holder.setTime(model.getTime());
                progressbar.hide();

            }

            @NonNull
            @Override
            public LoadShotNotesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.concept_card_view, parent, false);
                return new LoadShotNotesViewHolder(view);
            }
        };

        mBlogList.setAdapter(fireBaseRecyclerAdapter);
        fireBaseRecyclerAdapter.startListening();
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }



}