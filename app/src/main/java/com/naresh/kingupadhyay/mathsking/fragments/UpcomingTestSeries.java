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
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.naresh.kingupadhyay.mathsking.R;
import com.naresh.kingupadhyay.mathsking.UiMaterial.LoginOption;
import com.naresh.kingupadhyay.mathsking.activities.PostConcept;
import com.naresh.kingupadhyay.mathsking.activities.PostExample;
import com.naresh.kingupadhyay.mathsking.activities.PostUpcomingTest;
import com.naresh.kingupadhyay.mathsking.network.LoadPreviousTestSeries;
import com.naresh.kingupadhyay.mathsking.network.LoadUpcomingTestSeries;
import com.naresh.kingupadhyay.mathsking.viewholders.LoadConceptViewHolder;
import com.naresh.kingupadhyay.mathsking.network.LoadConcept;
import com.naresh.kingupadhyay.mathsking.viewholders.LoadPreviousTestSeriesViewHolder;
import com.naresh.kingupadhyay.mathsking.viewholders.LoadUpcomingTestSeriesViewHolder;
import com.wang.avi.AVLoadingIndicatorView;



public class UpcomingTestSeries extends Fragment {

    private FirebaseFirestore db;
    private FirestoreRecyclerAdapter<LoadUpcomingTestSeries, LoadUpcomingTestSeriesViewHolder> adapter;
    private Query queryMain;
    private RecyclerView recyclerViewMain;
    private AVLoadingIndicatorView progressbar;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View viewFragment = inflater.inflate(R.layout.fragment_test_series, container, false);

        progressbar = viewFragment.findViewById(R.id.progress_bar_backdrop);
        progressbar.show();
        db = FirebaseFirestore.getInstance();
        CollectionReference chaptersRef = db
                .collection("test_series").document("branches")
                .collection("upcoming");
        queryMain = chaptersRef.orderBy("timestamp",Query.Direction.ASCENDING).limit(300);

//        loadConceptData = downloadData(book,chapter);
        recyclerViewMain = viewFragment.findViewById(R.id.conceptRecyclerView);
        addConceptCard(queryMain,recyclerViewMain);
        recyclerViewMain.setHasFixedSize(true);
        recyclerViewMain.setLayoutManager(new LinearLayoutManager(getContext()));
        SharedPreferences prefAdmin = viewFragment.getContext().getSharedPreferences("ActivityPREF", Context.MODE_PRIVATE);
        String id=prefAdmin.getString("id","");

        final FloatingActionButton fab = (FloatingActionButton) viewFragment.findViewById(R.id.uploadExample);
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
                    editActivity(view);
                }else{
                    Toast.makeText(view.getContext(), "First authenticate yourself", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(view.getContext(), LoginOption.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }
            }
        });

        return viewFragment;
    }

    public void editActivity(View v){
        Context context = v.getContext();
        Intent intent = new Intent(context, PostUpcomingTest.class);
        intent.putExtra("title","");
        intent.putExtra("questions","");
        intent.putExtra("marks","");
        intent.putExtra("time","");
        intent.putExtra("documentId","");
        context.startActivity(intent);
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
        FirestoreRecyclerOptions<LoadUpcomingTestSeries> options = new FirestoreRecyclerOptions.Builder<LoadUpcomingTestSeries>()
                .setQuery(query, LoadUpcomingTestSeries.class)
                .build();

        adapter = new FirestoreRecyclerAdapter<LoadUpcomingTestSeries, LoadUpcomingTestSeriesViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull LoadUpcomingTestSeriesViewHolder loadUpcomingTestSeriesViewHolder, int i, @NonNull LoadUpcomingTestSeries loadUpcomingTestSeries) {
                String documentId1 = getSnapshots().getSnapshot(i).getId();
                loadUpcomingTestSeriesViewHolder.setDocumentId(documentId1);
                loadUpcomingTestSeriesViewHolder.setTitle(loadUpcomingTestSeries.getTitle());
                loadUpcomingTestSeriesViewHolder.setQuestions(loadUpcomingTestSeries.getQuestions());
                loadUpcomingTestSeriesViewHolder.setMarks(loadUpcomingTestSeries.getMarks());
                loadUpcomingTestSeriesViewHolder.setTimeSeconds(loadUpcomingTestSeries.getTimeSeconds());
                loadUpcomingTestSeriesViewHolder.setTimestamp(loadUpcomingTestSeries.getTimestamp());
                loadUpcomingTestSeriesViewHolder.setTopic(i+1);
                progressbar.hide();

            }

            @NonNull
            @Override
            public LoadUpcomingTestSeriesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.test_series_upcoming_card_view, parent, false);
                return new LoadUpcomingTestSeriesViewHolder(view);
            }
        };
        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }

}
