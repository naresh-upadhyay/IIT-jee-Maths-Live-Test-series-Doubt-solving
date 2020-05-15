package com.naresh.kingupadhyay.mathsking.fragments;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import com.naresh.kingupadhyay.mathsking.activities.PostDoubt;
import com.naresh.kingupadhyay.mathsking.activities.PostExample;
import com.naresh.kingupadhyay.mathsking.network.LoadDoubts;
import com.naresh.kingupadhyay.mathsking.network.LoadExamples;
import com.naresh.kingupadhyay.mathsking.network.UserDetails;
import com.naresh.kingupadhyay.mathsking.viewholders.LoadDoubtsViewHolder;
import com.naresh.kingupadhyay.mathsking.viewholders.LoadExamplesViewHolder;
import com.wang.avi.AVLoadingIndicatorView;

/**
 * A simple {@link Fragment} subclass.
 */
public class AskSolveDoubts extends Fragment {

    private FirebaseFirestore db;
    private FirestoreRecyclerAdapter<LoadDoubts, LoadDoubtsViewHolder> adapter;
    private Query queryMain;
    private RecyclerView exampleRecyclerMain;
    private AVLoadingIndicatorView progressbar;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View viewFragment = inflater.inflate(R.layout.fragment_examples, container, false);

        SharedPreferences pref = getActivity().getSharedPreferences("course", Context.MODE_PRIVATE);
        final String book = pref.getString("book","book");
        final String chapter = pref.getString("chapter","chapter");

        progressbar = viewFragment.findViewById(R.id.progress_bar_backdrop);
        progressbar.show();
        db = FirebaseFirestore.getInstance();
        CollectionReference chaptersRef = db
                .collection("chapters").document(book)
                .collection(chapter).document("branches")
                .collection("doubts");//.document("course")
        queryMain = chaptersRef.orderBy("uploadingTime",Query.Direction.DESCENDING).limit(300);
//        queryMain = chaptersRef.orderBy("uploadingTime",Query.Direction.ASCENDING).limit(50);

        exampleRecyclerMain = viewFragment.findViewById(R.id.exampleRecyclerView);
        exampleRecyclerMain.setHasFixedSize(true);
        exampleRecyclerMain.setLayoutManager(new LinearLayoutManager(getContext()));
        addExampleCard(queryMain,exampleRecyclerMain);
        final FloatingActionButton fab = (FloatingActionButton) viewFragment.findViewById(R.id.uploadExample);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences prefUpdate = view.getContext().getSharedPreferences("skip", Context.MODE_PRIVATE);
                boolean skip=prefUpdate.getBoolean("skip",false);
                if(!skip){
                    Intent intent = new Intent(view.getContext(), PostDoubt.class);
                    intent.putExtra("book",book);
                    intent.putExtra("chapter", chapter);
                    intent.putExtra("tab","doubts");
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

    public void addExampleCard(Query query,RecyclerView recyclerView){
        progressbar.show();
        FirestoreRecyclerOptions<LoadDoubts> options = new FirestoreRecyclerOptions.Builder<LoadDoubts>()
                .setQuery(query, LoadDoubts.class)
                .build();

        adapter = new FirestoreRecyclerAdapter<LoadDoubts, LoadDoubtsViewHolder>(options){

            @Override
            protected void onBindViewHolder(@NonNull final LoadDoubtsViewHolder holder, int position, @NonNull LoadDoubts model) {
                holder.setUserId(model.getUserId());
                //DocumentReference docRef = db.collection("users").document(model.getUserId());
                DocumentReference docRef = db.collection("users").document(model.getUserId());
                docRef.get(Source.SERVER).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        UserDetails userDetails = documentSnapshot.toObject(UserDetails.class);
                        holder.setUserName(userDetails.getName());
                        holder.setUserImage(userDetails.getUserImage());
                        holder.setUserRating(userDetails.getRating());
                    }
                });
                holder.setVotes(model.getVotes());
                holder.setTags(model.getTags());
                holder.setText(model.getText());
                holder.setTextImageUrl(model.getTextImage());
                String documentId1 = getSnapshots().getSnapshot(position).getId();
                holder.setDocumentId(documentId1);
                progressbar.hide();
            }

            @NonNull
            @Override
            public LoadDoubtsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.ask_solve_doubts_card_view, parent, false);
                return new LoadDoubtsViewHolder(view);
            }
        };
        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }

}
