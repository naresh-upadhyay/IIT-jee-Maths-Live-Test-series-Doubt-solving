package com.naresh.kingupadhyay.mathsking.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.firestore.Source;
import com.naresh.kingupadhyay.mathsking.R;
import com.naresh.kingupadhyay.mathsking.UiMaterial.LoginOption;
import com.naresh.kingupadhyay.mathsking.fragments.LeaderBoard;
import com.naresh.kingupadhyay.mathsking.network.LoadExamples;
import com.naresh.kingupadhyay.mathsking.network.LoadGaveTest;
import com.naresh.kingupadhyay.mathsking.network.LoadLeaders;
import com.naresh.kingupadhyay.mathsking.network.LoadPreviousTestSeries;
import com.naresh.kingupadhyay.mathsking.network.UserDetails;
import com.naresh.kingupadhyay.mathsking.viewholders.LoadExamplesViewHolder;
import com.naresh.kingupadhyay.mathsking.viewholders.LoadGaveTestViewHolder;
import com.naresh.kingupadhyay.mathsking.viewholders.LoadReviewTestViewHolder;
import com.wang.avi.AVLoadingIndicatorView;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ReviewTest extends AppCompatActivity {
    private FirebaseFirestore db;
    private FirestoreRecyclerAdapter<LoadGaveTest, LoadReviewTestViewHolder> adapter;
    private Query queryMain;
    private RecyclerView exampleRecyclerMain;
    private AVLoadingIndicatorView progressbar;
    private String title;
    private String liveId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_card_test_series_data_upload);

        MobileAds.initialize(this, "ca-app-pub-6924423095909700~8475665982");

        AdView mAdView = findViewById(R.id.adVmain);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);


        SharedPreferences prefLive = getSharedPreferences("live", Context.MODE_PRIVATE);
        title = prefLive.getString("title",title);
        liveId = prefLive.getString("liveId",liveId);

        ImageButton back=findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        progressbar = findViewById(R.id.progress_bar_nested_recyclerview);
        progressbar.show();
        db = FirebaseFirestore.getInstance();
        CollectionReference chaptersRef = db
                .collection("test_series").document("branches")
                .collection(liveId);//todo use liveId
        queryMain = chaptersRef.orderBy("uploadingTime",Query.Direction.DESCENDING).limit(200);
//        Toast.makeText(GaveTest.this,""+timeRemainingMilisec, Toast.LENGTH_LONG).show();
        exampleRecyclerMain = findViewById(R.id.myrecyclerview);
        exampleRecyclerMain.setHasFixedSize(true);
        exampleRecyclerMain.setLayoutManager(new LinearLayoutManager(ReviewTest.this));
        addExampleCard(queryMain,exampleRecyclerMain);

        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.uploadExample);
        fab.setVisibility(View.GONE);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences prefUpdate = view.getContext().getSharedPreferences("skip", Context.MODE_PRIVATE);
                boolean skip=prefUpdate.getBoolean("skip",false);
                if(!skip){
                    editActivity(view);
                }else{
                    Toast.makeText(view.getContext(), "First authenticate yourself", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(view.getContext(), LoginOption.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }
            }
        });


        TextView titleb = findViewById(R.id.titleb);
        titleb.setText(title);
    }

    @Override
    public void onStart() {
        super.onStart();
        if(adapter != null){
            adapter.startListening();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if(adapter != null){
            adapter.stopListening();
        }
        progressbar.hide();
    }

    public void editActivity(View v){
        Context context = v.getContext();
        Intent intent = new Intent(context, PostTestData.class);
        SharedPreferences prefLive = getSharedPreferences("live", Context.MODE_PRIVATE);
        String liveId = prefLive.getString("liveId","live");

        intent.putExtra("liveId", liveId);
        intent.putExtra("markspos","");
        intent.putExtra("marksneg", "");
        intent.putExtra("chapterCode", 0);
        intent.putExtra("tags", "");
        intent.putExtra("text", "");
        intent.putExtra("textImageUrl", "");
        intent.putExtra("textAns", "");
        intent.putExtra("textImageUrlAns", "");
        intent.putExtra("option", false);
        intent.putExtra("numericval", 0);
        intent.putExtra("optionA", false);
        intent.putExtra("optionB", false);
        intent.putExtra("optionC", false);
        intent.putExtra("optionD", false);
        intent.putExtra("documentId","");
        context.startActivity(intent);

    }


    public void addExampleCard(Query query, RecyclerView recyclerView){
        progressbar.show();
        FirestoreRecyclerOptions<LoadGaveTest> options = new FirestoreRecyclerOptions.Builder<LoadGaveTest>()
                .setQuery(query, LoadGaveTest.class)
                .build();

        adapter = new FirestoreRecyclerAdapter<LoadGaveTest, LoadReviewTestViewHolder>(options){

            @Override
            protected void onBindViewHolder(@NonNull final LoadReviewTestViewHolder holder, int position, @NonNull LoadGaveTest model) {
                String documentId1 = getSnapshots().getSnapshot(position).getId();
                holder.setDocumentId(documentId1);

                holder.setUserId(model.getUserId());
                holder.setMarkspos(model.getMarkspos());
                holder.setMarksneg(model.getMarksneg());
                holder.setNumericval(model.getNumericval());
                holder.setSolvepercent(model.getSolvepercent());
                holder.setTags(model.getTags());
                holder.setText(model.getText());
                holder.setTextImageUrl(model.getTextImage());
                holder.setOption(model.isOption());
                holder.setOptions(model.isOptionA(),model.isOptionB(),model.isOptionC(),model.isOptionD());
                holder.setCardnumber(position+1);
                progressbar.hide();
            }

            @NonNull
            @Override
            public LoadReviewTestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.review_test_card_view, parent, false);
                return new LoadReviewTestViewHolder(view);
            }
        };
        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }


}
