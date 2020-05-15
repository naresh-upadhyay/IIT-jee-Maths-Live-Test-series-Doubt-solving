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
import com.naresh.kingupadhyay.mathsking.viewholders.LoadConceptViewHolder;
import com.naresh.kingupadhyay.mathsking.network.LoadConcept;
import com.wang.avi.AVLoadingIndicatorView;



/**
 * A placeholder fragment containing a simple view.
 */
public class ConceptsFragment extends Fragment {

    private FirebaseFirestore db;
    private FirestoreRecyclerAdapter<LoadConcept, LoadConceptViewHolder> adapter;
    private Query queryMain;
    private RecyclerView recyclerViewMain;
    private AVLoadingIndicatorView progressbar;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View viewFragment = inflater.inflate(R.layout.fragment_concepts, container, false);

        SharedPreferences pref = getActivity().getSharedPreferences("course", Context.MODE_PRIVATE);
        final String book = pref.getString("book","book");
        final String chapter = pref.getString("chapter","chapter");

        progressbar = viewFragment.findViewById(R.id.progress_bar_backdrop);
        progressbar.show();
        db = FirebaseFirestore.getInstance();
        CollectionReference chaptersRef = db
                .collection("chapters").document(book)
                .collection(chapter).document("branches")
                .collection("concepts");//.document("course")
        queryMain = chaptersRef.orderBy("topicNum",Query.Direction.ASCENDING).limit(100);

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
                    Intent intent = new Intent(view.getContext(), PostConcept.class);
                    intent.putExtra("book",book);
                    intent.putExtra("chapter", chapter);
                    startActivity(intent);
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
        FirestoreRecyclerOptions<LoadConcept> options = new FirestoreRecyclerOptions.Builder<LoadConcept>()
                .setQuery(query, LoadConcept.class)
                .build();

        adapter = new FirestoreRecyclerAdapter<LoadConcept, LoadConceptViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull LoadConceptViewHolder loadConceptViewHolder, int i, @NonNull LoadConcept loadConcept) {
                String documentId1 = getSnapshots().getSnapshot(i).getId();
                loadConceptViewHolder.setDocumentId(documentId1);
                loadConceptViewHolder.setTopic(i+1);
                loadConceptViewHolder.setTitle(loadConcept.getTitle());
                loadConceptViewHolder.setConceptPdfUrl(loadConcept.getConceptPdfUrl());
                loadConceptViewHolder.setTime(loadConcept.getTime());
                progressbar.hide();

            }

            @NonNull
            @Override
            public LoadConceptViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.concept_card_view, parent, false);
                return new LoadConceptViewHolder(view);
            }
        };
        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }

}
