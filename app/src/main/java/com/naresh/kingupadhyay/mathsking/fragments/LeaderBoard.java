package com.naresh.kingupadhyay.mathsking.fragments;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import androidx.fragment.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.Source;
import com.naresh.kingupadhyay.mathsking.R;
import com.naresh.kingupadhyay.mathsking.UiMaterial.LoginOption;
import com.naresh.kingupadhyay.mathsking.activities.PostConcept;
import com.naresh.kingupadhyay.mathsking.activities.PostExample;
import com.naresh.kingupadhyay.mathsking.network.LoadLeaders;
import com.naresh.kingupadhyay.mathsking.network.UserDetails;
import com.naresh.kingupadhyay.mathsking.viewholders.LoadConceptViewHolder;
import com.naresh.kingupadhyay.mathsking.network.LoadConcept;
import com.naresh.kingupadhyay.mathsking.viewholders.LoadLeaderBoardViewHolder;
import com.wang.avi.AVLoadingIndicatorView;


public class LeaderBoard extends Fragment {

    private FirebaseFirestore db;
    private FirestoreRecyclerAdapter<LoadLeaders, LoadLeaderBoardViewHolder> adapter;
    private Query queryMain;
    private RecyclerView recyclerViewMain;
    private AVLoadingIndicatorView progressbar;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View viewFragment = inflater.inflate(R.layout.fragment_leader_board, container, false);

        SharedPreferences prefLive = viewFragment.getContext().getSharedPreferences("skip", Context.MODE_PRIVATE);
        String liveid = prefLive.getString("liveId","liveId");
        boolean liveCheck = prefLive.getBoolean("live",false);
        boolean leaderboardCheck = prefLive.getBoolean("leaderboard",false);
        TextView participantsNames = viewFragment.findViewById(R.id.participantsNames);
        TextView participantsRanks = viewFragment.findViewById(R.id.participantsRanks);
        TextView onlyForLive = viewFragment.findViewById(R.id.onlyforLive);
        onlyForLive.setText(R.string.onlyLive);
        participantsNames.setText(R.string.participant);
        participantsRanks.setText(R.string.rank);
        progressbar = viewFragment.findViewById(R.id.progress_bar_backdrop);
        progressbar.show();
        db = FirebaseFirestore.getInstance();
        CollectionReference chaptersRef = db
                .collection("test_series").document("branches")
                .collection(liveid).document("users")//todo use liveId
                .collection("all");
        queryMain = chaptersRef.orderBy("marks",Query.Direction.DESCENDING).limit(10000);

//        loadConceptData = downloadData(book,chapter);conceptRecyclerView
        recyclerViewMain = viewFragment.findViewById(R.id.conceptRecyclerView);
        if(liveCheck || leaderboardCheck){
            addConceptCard(queryMain,recyclerViewMain);
            onlyForLive.setVisibility(View.GONE);
        }else{
            if(adapter!=null){
                adapter.stopListening();
            }
            onlyForLive.setVisibility(View.VISIBLE);
            progressbar.hide();
        }

        recyclerViewMain.setHasFixedSize(true);
        recyclerViewMain.setLayoutManager(new LinearLayoutManager(getContext()));

        return viewFragment;
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

    public void addConceptCard(Query query,RecyclerView recyclerView){
        progressbar.show();
        FirestoreRecyclerOptions<LoadLeaders> options = new FirestoreRecyclerOptions.Builder<LoadLeaders>()
                .setQuery(query, LoadLeaders.class)
                .build();

        adapter = new FirestoreRecyclerAdapter<LoadLeaders, LoadLeaderBoardViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final LoadLeaderBoardViewHolder loadLeaderBoardViewHolder, int i, @NonNull final LoadLeaders loadLeaders) {
                DocumentReference docRef = db.collection("users").document(loadLeaders.getUserId());
                docRef.get(Source.SERVER).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        UserDetails userDetails = documentSnapshot.toObject(UserDetails.class);
                        loadLeaderBoardViewHolder.setUserName(userDetails.getName());
                        loadLeaderBoardViewHolder.setUserImage(userDetails.getUserImage());
                    }
                });
                loadLeaderBoardViewHolder.setRank(i+1);
                loadLeaderBoardViewHolder.setUserId(loadLeaders.getUserId());
                progressbar.hide();

            }

            @NonNull
            @Override
            public LoadLeaderBoardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.leader_board_card_view, parent, false);
                return new LoadLeaderBoardViewHolder(view);
            }
        };
        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }

}
