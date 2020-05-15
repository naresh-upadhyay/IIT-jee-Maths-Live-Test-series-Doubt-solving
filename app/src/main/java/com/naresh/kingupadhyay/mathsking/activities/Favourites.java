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
import androidx.viewpager.widget.ViewPager;

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
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.naresh.kingupadhyay.mathsking.R;
import com.naresh.kingupadhyay.mathsking.network.LoadConcept;
import com.naresh.kingupadhyay.mathsking.viewholders.LoadConceptViewHolder;
import com.wang.avi.AVLoadingIndicatorView;


public class Favourites extends AppCompatActivity{

    private FirebaseRecyclerAdapter<LoadConcept, LoadConceptViewHolder> fireBaseRecyclerAdapter;
    private AVLoadingIndicatorView progressbar;
    private Toolbar toolbar;
    private RecyclerView mBlogList;
    private DatabaseReference mDatabase;
    private AdView mAdView;
    private FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favourites);


        MobileAds.initialize(this, "ca-app-pub-6924423095909700~8475665982");

        mAdView = findViewById(R.id.adVmain);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);


        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        mDatabase=FirebaseDatabase.getInstance().getReference().child("users").child(firebaseUser.getUid()).child("favourites");
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference().child("users").child(firebaseUser.getUid());

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
        titleb.setText("Favourites");

        rootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild("favourites")) {

                }else{
                    Toast.makeText(getApplicationContext(), "Favourites is empty", Toast.LENGTH_SHORT).show();
                    progressbar.hide();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(), databaseError.getMessage(), Toast.LENGTH_SHORT).show();

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
                .child("users").child(firebaseUser.getUid()).child("favourites")
                .limitToLast(500);
        FirebaseRecyclerOptions<LoadConcept> options =
                new FirebaseRecyclerOptions.Builder<LoadConcept>()
                        .setQuery(query, LoadConcept.class)
                        .build();
        fireBaseRecyclerAdapter=new FirebaseRecyclerAdapter <LoadConcept, LoadConceptViewHolder>
                (options){
            @Override
            protected void onBindViewHolder(@NonNull LoadConceptViewHolder holder, int position, @NonNull LoadConcept model) {
                holder.setTopic(position+1);
                holder.setTitle(model.getTitle());
                holder.setConceptPdfUrl(model.getConceptPdfUrl());
                holder.setTime(model.getTime());
                progressbar.hide();

            }

            @NonNull
            @Override
            public LoadConceptViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.concept_card_view, parent, false);
                return new LoadConceptViewHolder(view);
            }
        };

        mBlogList.setAdapter(fireBaseRecyclerAdapter);

    }



    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }


}