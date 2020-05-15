package com.naresh.kingupadhyay.mathsking.activities;

import android.content.Context;
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

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.naresh.kingupadhyay.mathsking.R;
import com.naresh.kingupadhyay.mathsking.network.LoadInstructions;
import com.naresh.kingupadhyay.mathsking.viewholders.LoadInstructionsViewHolder;
import com.wang.avi.AVLoadingIndicatorView;


public class ReadInstructions extends AppCompatActivity{

    private FirebaseRecyclerAdapter<LoadInstructions, LoadInstructionsViewHolder> fireBaseRecyclerAdapter;
    private AVLoadingIndicatorView progressbar;
    private Toolbar toolbar;
    private RecyclerView mBlogList;
    private AdView mAdView;
    private Query query;
    private boolean flagval;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read_instructions);

        MobileAds.initialize(this, "ca-app-pub-6924423095909700~8475665982");

        mAdView = findViewById(R.id.adVmain);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);


        Bundle bundle = getIntent().getExtras();
        flagval = bundle.getBoolean("flag",true);

        mBlogList=(RecyclerView)findViewById(R.id.fav_myrecyclerview);
        mBlogList.setHasFixedSize(true);
        mBlogList.setLayoutManager(new LinearLayoutManager(this));
        progressbar = findViewById(R.id.progress_bar_backdrop);
        progressbar.show();

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
        titleb.setText("Instructions");

    }


    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        favouritesLoad(progressbar);
    }

    public void favouritesLoad(final AVLoadingIndicatorView progressbar){
        if(flagval){
            query= FirebaseDatabase.getInstance().getReference()
                    .child("admininstructions")
                    .limitToLast(50);

        }else{
            query = FirebaseDatabase.getInstance().getReference()
                    .child("instructions")
                    .limitToLast(50);
        }


        FirebaseRecyclerOptions<LoadInstructions> options =
                new FirebaseRecyclerOptions.Builder<LoadInstructions>()
                        .setQuery(query, LoadInstructions.class)
                        .build();
        fireBaseRecyclerAdapter=new FirebaseRecyclerAdapter <LoadInstructions, LoadInstructionsViewHolder>
                (options){
            @Override
            protected void onBindViewHolder(@NonNull LoadInstructionsViewHolder holder, int position, @NonNull LoadInstructions model) {
                holder.setText(model.getTextmsg());
                progressbar.hide();
            }

            @NonNull
            @Override
            public LoadInstructionsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.content_determinants, parent, false);
                return new LoadInstructionsViewHolder(view);
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