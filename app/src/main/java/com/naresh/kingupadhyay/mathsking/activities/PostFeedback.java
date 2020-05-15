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
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.Source;
import com.naresh.kingupadhyay.mathsking.R;
import com.naresh.kingupadhyay.mathsking.UiMaterial.LoginOption;
import com.naresh.kingupadhyay.mathsking.network.LoadComment;
import com.naresh.kingupadhyay.mathsking.network.LoadConcept;
import com.naresh.kingupadhyay.mathsking.network.LoadFeedback;
import com.naresh.kingupadhyay.mathsking.network.LoadShortNotes;
import com.naresh.kingupadhyay.mathsking.network.UserDetails;
import com.naresh.kingupadhyay.mathsking.viewholders.LoadCommentsViewHolder;
import com.naresh.kingupadhyay.mathsking.viewholders.LoadConceptViewHolder;
import com.naresh.kingupadhyay.mathsking.viewholders.LoadFeedbackViewHolder;
import com.naresh.kingupadhyay.mathsking.viewholders.LoadShotNotesViewHolder;
import com.wang.avi.AVLoadingIndicatorView;


public class PostFeedback extends AppCompatActivity{

    private FirestoreRecyclerAdapter<LoadFeedback, LoadFeedbackViewHolder> fireBaseRecyclerAdapter;
    private AVLoadingIndicatorView progressbar;
    private Toolbar toolbar;
    private RecyclerView mBlogList;
    private AdView mAdView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favourites);

        mBlogList=(RecyclerView)findViewById(R.id.fav_myrecyclerview);
        mBlogList.setHasFixedSize(true);
        mBlogList.setLayoutManager(new LinearLayoutManager(this));
        progressbar = findViewById(R.id.progress_bar_backdrop);
        progressbar.show();

        addFeedBackCard(mBlogList);
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
        titleb.setText("Feedback");

        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.uploadExample);
        fab.setVisibility(View.GONE);

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

    public void addFeedBackCard(RecyclerView recyclerView){
        progressbar.show();
        final FirebaseFirestore db= FirebaseFirestore.getInstance();
        CollectionReference chaptersRef = db
                    .collection("feedback").document("live")
                    .collection("app");

        com.google.firebase.firestore.Query query = chaptersRef.orderBy("uploadingTime", Query.Direction.DESCENDING).limit(500);
        FirestoreRecyclerOptions<LoadFeedback> options = new FirestoreRecyclerOptions.Builder<LoadFeedback>()
                .setQuery(query, LoadFeedback.class)
                .build();

        fireBaseRecyclerAdapter = new FirestoreRecyclerAdapter<LoadFeedback, LoadFeedbackViewHolder>(options){
            @Override
            protected void onBindViewHolder(@NonNull final LoadFeedbackViewHolder holder, int position, @NonNull LoadFeedback model) {
                String documentId1 = getSnapshots().getSnapshot(position).getId();
                holder.setDocumentId(documentId1);
                DocumentReference docRef = db.collection("users").document(model.getUserId());
                docRef.get(Source.SERVER).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        UserDetails userDetails = documentSnapshot.toObject(UserDetails.class);
                        holder.setUserName(userDetails.getName());
                        holder.setUserImage(userDetails.getUserImage());
                        holder.setUid(userDetails.getUid());
                    }
                });
                holder.setTags(model.getTestlevel());
                holder.setText(model.getTextmsg());
                progressbar.hide();
            }

            @NonNull
            @Override
            public LoadFeedbackViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.feedback_card, parent, false);
                return new LoadFeedbackViewHolder(view);
            }
        };
        recyclerView.setAdapter(fireBaseRecyclerAdapter);
        fireBaseRecyclerAdapter.startListening();
    }





}