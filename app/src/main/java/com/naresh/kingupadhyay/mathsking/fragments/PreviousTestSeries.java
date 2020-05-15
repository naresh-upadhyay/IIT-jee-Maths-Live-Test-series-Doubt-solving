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
import com.naresh.kingupadhyay.mathsking.network.LoadPreviousTestSeries;
import com.naresh.kingupadhyay.mathsking.viewholders.LoadConceptViewHolder;
import com.naresh.kingupadhyay.mathsking.network.LoadConcept;
import com.naresh.kingupadhyay.mathsking.viewholders.LoadPreviousTestSeriesViewHolder;
import com.wang.avi.AVLoadingIndicatorView;



/**
 * A placeholder fragment containing a simple view. test_series_previous_card_view
 */
public class PreviousTestSeries extends Fragment {

    private FirebaseFirestore db;
    private FirestoreRecyclerAdapter<LoadPreviousTestSeries, LoadPreviousTestSeriesViewHolder> adapter;
    private Query queryMain;
    private RecyclerView recyclerViewMain;
    private AVLoadingIndicatorView progressbar;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View viewFragment = inflater.inflate(R.layout.fragment_test_series, container, false);

        SharedPreferences pref = getActivity().getSharedPreferences("course", Context.MODE_PRIVATE);

        progressbar = viewFragment.findViewById(R.id.progress_bar_backdrop);
        progressbar.show();
        db = FirebaseFirestore.getInstance();
        CollectionReference chaptersRef = db
                .collection("test_series").document("branches")
                .collection("previous");
        queryMain = chaptersRef.orderBy("timestamp",Query.Direction.DESCENDING).limit(1000);

//        loadConceptData = downloadData(book,chapter);
        recyclerViewMain = viewFragment.findViewById(R.id.conceptRecyclerView);
        addConceptCard(queryMain,recyclerViewMain);
        recyclerViewMain.setHasFixedSize(true);
        recyclerViewMain.setLayoutManager(new LinearLayoutManager(getContext()));
        SharedPreferences prefAdmin = viewFragment.getContext().getSharedPreferences("ActivityPREF", Context.MODE_PRIVATE);
        String id=prefAdmin.getString("id","");

        final FloatingActionButton fab = (FloatingActionButton) viewFragment.findViewById(R.id.uploadExample);
        fab.setVisibility(View.GONE);

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
        FirestoreRecyclerOptions<LoadPreviousTestSeries> options = new FirestoreRecyclerOptions.Builder<LoadPreviousTestSeries>()
                .setQuery(query, LoadPreviousTestSeries.class)
                .build();

        adapter = new FirestoreRecyclerAdapter<LoadPreviousTestSeries, LoadPreviousTestSeriesViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull LoadPreviousTestSeriesViewHolder loadPreviousTestSeriesViewHolder, int i, @NonNull LoadPreviousTestSeries loadPreviousTestSeries) {
                String documentId1 = getSnapshots().getSnapshot(i).getId();
                loadPreviousTestSeriesViewHolder.setDocumentId(documentId1);
                loadPreviousTestSeriesViewHolder.setTopic(i+1);
                loadPreviousTestSeriesViewHolder.setTitle(loadPreviousTestSeries.getTitle());
                loadPreviousTestSeriesViewHolder.setQuestions(loadPreviousTestSeries.getQuestions());
                loadPreviousTestSeriesViewHolder.setMarks(loadPreviousTestSeries.getMarks());
                loadPreviousTestSeriesViewHolder.setTimeSeconds(loadPreviousTestSeries.getTimeSeconds());
                progressbar.hide();

            }

            @NonNull
            @Override
            public LoadPreviousTestSeriesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.test_series_previous_card_view, parent, false);
                return new LoadPreviousTestSeriesViewHolder(view);
            }
        };
        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }

}
