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
import com.wang.avi.AVLoadingIndicatorView;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class GaveTest extends AppCompatActivity {
    private FirebaseFirestore db;
    private FirestoreRecyclerAdapter<LoadGaveTest, LoadGaveTestViewHolder> adapter;
    private Query queryMain;
    private RecyclerView exampleRecyclerMain;
    private AVLoadingIndicatorView progressbar;
    private CountDownTimer timer;
    private String title;
    private int questions;
    private long timeRemainingMilisec;
    private int testTimingSeconds;
    private String liveId;
    private int marks;
    private int[] marksValueAll;
    private int[] maxMarksValueAll;
    private int[] chaptersCodeAll;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gave_test);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        MobileAds.initialize(this, "ca-app-pub-6924423095909700~8475665982");

        AdView mAdView = findViewById(R.id.adVmain);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);


        Bundle bundle = getIntent().getExtras();
        title = bundle.getString("title","title");
        liveId = bundle.getString("liveId","liveId");
        questions = bundle.getInt("questions",0);
        testTimingSeconds = bundle.getInt("testTimingSeconds",0);
        timeRemainingMilisec = bundle.getLong("timeRemaining",10000);
        marks = bundle.getInt("marks",0);
        SharedPreferences prefLive = getSharedPreferences("live", Context.MODE_PRIVATE);
        final SharedPreferences.Editor edt = prefLive.edit();
        edt.putString("title",title);
        edt.putString("liveId",liveId);
        edt.apply();
        marksValueAll = new int[questions];
        maxMarksValueAll = new int[questions];
        chaptersCodeAll = new int[questions];
        for(int i=0;i<questions;i=i+1){
            marksValueAll[i] = 0;
            maxMarksValueAll[i] = 0;
            chaptersCodeAll[i] = -1;
        }
        SharedPreferences prefscore=getSharedPreferences("score", Context.MODE_PRIVATE);
        final SharedPreferences.Editor edtScore = prefscore.edit();
        for(int i=0; i<35; i=i+1){
            edtScore.putInt(liveId+i,-1);
        }
        edtScore.apply();

        progressbar = findViewById(R.id.progress_bar_backdrop);
        progressbar.show();

        db = FirebaseFirestore.getInstance();
        CollectionReference chaptersRef = db
                .collection("test_series").document("branches")
                .collection(liveId);//todo use liveId
        queryMain = chaptersRef.orderBy("uploadingTime",Query.Direction.DESCENDING).limit(200);

        exampleRecyclerMain = findViewById(R.id.exampleRecyclerView);
        exampleRecyclerMain.setHasFixedSize(true);
        exampleRecyclerMain.setLayoutManager(new LinearLayoutManager(GaveTest.this));
        addExampleCard(queryMain,exampleRecyclerMain,marksValueAll,chaptersCodeAll);
        startTimer(timeRemainingMilisec,1000);
        Button finishbutton = findViewById(R.id.finshtest);
        finishbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setScore();
                setChaptersCodeAllScore();
                updateLeaderBoard();
                showPopupFinishTest();
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
        try{
            timer.cancel();
        }catch (Exception e){
        }
        progressbar.hide();
    }
    public void setChaptersCodeAllScore(){
        int[] actualGet= new int[35];
        int[] chapterMax=new int[35];
        for(int i=0;i<35;i=i+1){
            actualGet[i] = 0;
            chapterMax[i] = 0;
        }

        for(int i=0; i<questions; i=i+1){
            if(chaptersCodeAll[i]>=0){
                if(marksValueAll[i]>=0)
                actualGet[chaptersCodeAll[i]] = actualGet[chaptersCodeAll[i]]+marksValueAll[i];
                chapterMax[chaptersCodeAll[i]] = chapterMax[chaptersCodeAll[i]]+maxMarksValueAll[i];
            }
        }
        SharedPreferences prefs=getSharedPreferences("score", Context.MODE_PRIVATE);
        final SharedPreferences.Editor edt = prefs.edit();
        for(int i=0; i<35; i=i+1){
            if(chapterMax[i]>0){
                int percentch = (int)(Math.round((actualGet[i]*100.0)/(chapterMax[i]*1.0)));
                edt.putInt(liveId+i,percentch);
            }else {
                edt.putInt(liveId+i,-1);
            }
        }
        edt.apply();
    }


    public void setScore(){
        int total=0,markspos=0,marksneg=0;
        for(int i=0;i<questions;i=i+1){
            total =total + marksValueAll[i];
            if(marksValueAll[i]>=0){
                markspos = markspos + marksValueAll[i];
            }else{
                marksneg = marksneg + marksValueAll[i];
            }
        }
        SharedPreferences prefs=getSharedPreferences("score", Context.MODE_PRIVATE);
        final SharedPreferences.Editor edt = prefs.edit();
        edt.putInt(liveId+"liveScoreTotal",total);
        edt.putInt(liveId+"liveScorepos",markspos);
        edt.putInt(liveId+"liveScoreneg",marksneg);
        edt.apply();
        SharedPreferences scoreDoc=getSharedPreferences(liveId, Context.MODE_PRIVATE);
        final SharedPreferences.Editor scorEditor = scoreDoc.edit();
        scorEditor.putString("liveScoreTotal",""+total);
        scorEditor.apply();

    }
    public void updateLeaderBoard(){
        SharedPreferences prefLive = getSharedPreferences("skip", Context.MODE_PRIVATE);
        final SharedPreferences.Editor edtlive = prefLive.edit();
        edtlive.putString("liveId",liveId);
        edtlive.apply();
        SharedPreferences prefs=getSharedPreferences("score", Context.MODE_PRIVATE);
        int total = prefs.getInt(liveId+"liveScoreTotal",0);
        boolean liveCheck = prefLive.getBoolean("live",false);
        SharedPreferences prefUseId = getSharedPreferences("ActivityPREF", Context.MODE_PRIVATE);
        String currentUserId=prefUseId.getString("uid","userkhk");
        if(liveCheck){
            try{
                DocumentReference currentUerRef = db
                        .collection("test_series").document("branches")
                        .collection(liveId).document("users")//todo use liveId
                        .collection("all").document(currentUserId);

                LoadLeaders leaders = new LoadLeaders();
                leaders.setMarks(total);
                leaders.setUserId(currentUserId);
                leaders.setTimestamp(new Timestamp(new Date()));
                currentUerRef.set(leaders.toMap(), SetOptions.merge());
            }catch (Exception e){
            }
        }
    }

    public void startTimer(final long finish, long tick) {
        final TextView timertextview = findViewById(R.id.timertext);
        timer = new CountDownTimer(finish, tick) {
            public void onTick(long millisUntilFinished) {
                // buttonStartVerification.setVisibility(View.INVISIBLE);
                long remainedSecs = millisUntilFinished / 1000;
                timertextview.setText("" + (remainedSecs / 3600) + ":" + ((remainedSecs / 60)%60) + ":" + (remainedSecs % 60));// manage it accordign to you
                if(remainedSecs<=10 && remainedSecs>=8){
                    setScore();
                    setChaptersCodeAllScore();
                    updateLeaderBoard();
                }
            }

            public void onFinish() {
                setScore();
                setChaptersCodeAllScore();
                updateLeaderBoard();
                timertextview.setText(R.string.timeup);
                Toast.makeText(GaveTest.this,"Test Over", Toast.LENGTH_LONG).show();
                cancel();
                Intent intent = new Intent(GaveTest.this,TestResult.class);
                intent.putExtra("title",title);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                onBackPressed();
            }
        }.start();
    }

    public void showPopupFinishTest(){
        final Dialog myDialog = new Dialog(GaveTest.this);
        myDialog.setContentView(R.layout.show_finish_popup);
        myDialog.setCancelable(false);
//        TextView instructon = (TextView) myDialog.findViewById(R.id.instruction);
        Button cancel = (Button) myDialog.findViewById(R.id.cancel_choice);
        Button finish = (Button) myDialog.findViewById(R.id.finish_test);
        myDialog.show();
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myDialog.dismiss();
            }
        });
        finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setScore();
                setChaptersCodeAllScore();
                updateLeaderBoard();

                TextView timertextview = findViewById(R.id.timertext);
                timertextview.setText(R.string.timeup);

                timer.cancel();
                myDialog.dismiss();
                Intent intent = new Intent(GaveTest.this,TestResult.class);
                intent.putExtra("title",title);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                onBackPressed();
//                open new activity
            }
        });

    }


    public void addExampleCard(Query query, RecyclerView recyclerView, final int[] marksValueAll, final int[] chaptersCodeAll){
        progressbar.show();
        FirestoreRecyclerOptions<LoadGaveTest> options = new FirestoreRecyclerOptions.Builder<LoadGaveTest>()
                .setQuery(query, LoadGaveTest.class)
                .build();

        adapter = new FirestoreRecyclerAdapter<LoadGaveTest, LoadGaveTestViewHolder>(options){

            @Override
            protected void onBindViewHolder(@NonNull final LoadGaveTestViewHolder holder, int position, @NonNull LoadGaveTest model) {
                holder.checkBoxA.setOnCheckedChangeListener(null);
                holder.checkBoxB.setOnCheckedChangeListener(null);
                holder.checkBoxC.setOnCheckedChangeListener(null);
                holder.checkBoxD.setOnCheckedChangeListener(null);
                holder.checkBoxA.setSelected(false);
                holder.checkBoxB.setSelected(false);
                holder.checkBoxC.setSelected(false);
                holder.checkBoxD.setSelected(false);


                String documentId = getSnapshots().getSnapshot(position).getId();
                holder.setDocumentId(documentId);
                SharedPreferences prefs=getSharedPreferences("score", Context.MODE_PRIVATE);
                boolean checka = prefs.getBoolean(documentId+"a",false);
                boolean checkb = prefs.getBoolean(documentId+"b",false);
                boolean checkc = prefs.getBoolean(documentId+"c",false);
                boolean checkd = prefs.getBoolean(documentId+"d",false);

                holder.checkBoxA.setSelected(checka);
                holder.checkBoxB.setSelected(checkb);
                holder.checkBoxC.setSelected(checkc);
                holder.checkBoxD.setSelected(checkd);


                holder.setUserId(model.getUserId());
                holder.setMarkspos(model.getMarkspos());
                holder.setMarksneg(model.getMarksneg());
                holder.setNumericval(model.getNumericval());
//                holder.setChapterCode(model.getChapterCode());
                maxMarksValueAll[position] = model.getMarkspos();
                chaptersCodeAll[position]=model.getChapterCode();
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
            public LoadGaveTestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.gave_test_card_view, parent, false);
                return new LoadGaveTestViewHolder(view,marksValueAll);
            }
        };
        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }


}
