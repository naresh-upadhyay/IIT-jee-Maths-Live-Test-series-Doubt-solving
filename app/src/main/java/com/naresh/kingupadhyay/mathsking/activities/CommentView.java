
package com.naresh.kingupadhyay.mathsking.activities;

import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatRatingBar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.apradanas.simplelinkabletext.Link;
import com.apradanas.simplelinkabletext.LinkableEditText;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.Source;
import com.naresh.kingupadhyay.mathsking.captue.ImageCroper;
import com.naresh.kingupadhyay.mathsking.network.LoadComment;
import com.naresh.kingupadhyay.mathsking.network.LoadSubmission;
import com.naresh.kingupadhyay.mathsking.network.UserDetails;
import com.naresh.kingupadhyay.mathsking.R;
import com.naresh.kingupadhyay.mathsking.viewholders.LoadCommentsViewHolder;
import com.naresh.kingupadhyay.mathsking.viewholders.LoadSubmissionViewHolder;
import com.wang.avi.AVLoadingIndicatorView;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;


public class CommentView extends AppCompatActivity{

    private Toolbar toolbar;
    private RecyclerView mBlogList;
    //    private static DatabaseReference mDatabase;
    private static DatabaseReference favourites;
    private static Drawable draw;
    //    private static DatabaseReference favourite;
    protected String NAME="name";
    private String book;
    private String chapter;
    private AdView mAdView;
    private static boolean skip;
    FirestoreRecyclerAdapter<LoadComment, LoadCommentsViewHolder> adapterComment,adapterEditorial;
    FirestoreRecyclerAdapter<LoadSubmission, LoadSubmissionViewHolder> adapterSubmission;

    private Timestamp uploadingTime;
    private String userId;
    private boolean flagConversation;
    private FirebaseFirestore db;
    private RelativeLayout uploadImageLayout_post;
    private AppCompatRatingBar ratingBar;
    private TextView comment;
    private TextView editorial;
    private TextView submission;
    private ImageView commentImage;
    private ImageView editorialImage;
    private ImageView submissionImage;
    private List<Link> links;
    private int tabNumber;//1->comment,2->editorial,3->submission
    private LinkableEditText editTextPost;
    private String currentUserId;
    private boolean imageset;
    private ImageView attachImage;
    private String documentId;
    private String tab;
    private String imageUrl;
    private boolean test;
    private String liveId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment_view);

        MobileAds.initialize(this, "ca-app-pub-6924423095909700~8475665982");

        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);


        SharedPreferences pref = getSharedPreferences("skip", Context.MODE_PRIVATE);
        skip=pref.getBoolean("skip",false);
        SharedPreferences prefUseId = getSharedPreferences("ActivityPREF", Context.MODE_PRIVATE);
        currentUserId=prefUseId.getString("uid","user");
        final SharedPreferences prefEdit = getSharedPreferences("edit", Context.MODE_PRIVATE);
        String sharedText=prefEdit.getString("txt","");

        Bundle bundle = getIntent().getExtras();
        book = bundle.getString("book");
        chapter = bundle.getString("chapter","chapter");
        liveId = bundle.getString("liveId","liveId");
        documentId=bundle.getString("documentId");
        tab=bundle.getString("tab","tab");
        tabNumber = bundle.getInt("tabNumber",1);
        test = bundle.getBoolean("test",false);

        ImageButton back=findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeKeyBoard(v);
                onBackPressed();
            }
        });

        Link linkUsername = new Link(Pattern.compile("(@\\w+)"))
                .setUnderlined(false)
                .setTextColor(R.color.greenBlue)
                .setTextStyle(Link.TextStyle.BOLD)
                .setClickListener(new Link.OnClickListener() {
                    @Override
                    public void onClick(String text) {
                        // do something
                        Toast.makeText(CommentView.this, "Clicked username: " +text, Toast.LENGTH_SHORT).show();

                    }
                });
        Link linkUrl = new Link(Pattern.compile("(([A-Za-z]{3,9}:(?://)?)(?:[-;:&=+$,\\w]+@)?[A-Za-z0-9.-]+|(?:www\\.|[-;:&=+$,\\w]+@)[A-Za-z0-9.-]+)((?:/[+~%/.\\w-]*)?\\??(?:[-+=&;%@.\\w]*)#?(?:[.!/\\\\\\w]*))?"))
                .setUnderlined(true)
                .setTextColor(R.color.star10)
                .setTextStyle(Link.TextStyle.BOLD)
                .setClickListener(new Link.OnClickListener() {
                    @Override
                    public void onClick(String text) {
                        // do something
                        Context context = CommentView.this;
//                        Toast.makeText(context, "Clicked link: " +text, Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(context, Basic_activity.class);
                        intent.putExtra("pdfUrl",text);
                        intent.putExtra("titleNoti","Clicked Link");
                        context.startActivity(intent);

                    }
                });



        links = new ArrayList<>();
        links.add(linkUrl);
        links.add(linkUsername);

        editTextPost = findViewById(R.id.linkableEdit_post_comment);
        editTextPost.addLinks(links);
        editTextPost.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(editTextPost.getText().toString().isEmpty() ||editTextPost.getText().toString()==null)
                    editTextPost.setText(" ", TextView.BufferType.EDITABLE);
                if(b){
                    editTextPost.setPressed(true);
                    editTextPost.setSelection(editTextPost.getText().length());
                }else{
                    closeKeyBoard(view);
                }
            }
        });

        editTextPost.setText(sharedText);
        if(editTextPost.getText().toString().isEmpty() ||editTextPost.getText().toString()==null)
            editTextPost.setText(" ", TextView.BufferType.EDITABLE);
        editTextPost.setPressed(true);
        editTextPost.setSelection(editTextPost.getText().length());

        attachImage = findViewById(R.id.attachImage_post_comment);
        attachImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CommentView.this, ImageCroper.class);
                startActivity(intent);

            }
        });
        final SharedPreferences.Editor edt = prefEdit.edit();
        edt.putString("txt","");
        edt.apply();



        mBlogList=(RecyclerView)findViewById(R.id.commentRecyclerView);
        mBlogList.setHasFixedSize(true);
        mBlogList.setLayoutManager(new LinearLayoutManager(this));


        try{
            if(tabNumber==1){
                addCard(book,chapter,mBlogList);
                adapterComment.startListening();
                attachImage.setVisibility(View.GONE);
            }else if(tabNumber==2){
                addEditorialCard(book,chapter,mBlogList);
                adapterEditorial.startListening();
                attachImage.setVisibility(View.GONE);
            }else if(tabNumber==3){
                addSubmissionCard(book,chapter,mBlogList);
                adapterSubmission.startListening();
                attachImage.setVisibility(View.VISIBLE);
            }
        }catch(Exception e){
            Toast.makeText(CommentView.this,"here", Toast.LENGTH_SHORT).show();
            e.printStackTrace();

        }

        final FirebaseUser firebaseUser= FirebaseAuth.getInstance().getCurrentUser();

        Button postButton = findViewById(R.id.postButton_post_comment);
        postButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences prefEdit2 = getSharedPreferences("edit", Context.MODE_PRIVATE);
                imageset = prefEdit2.getBoolean("image",false);
                imageUrl=prefEdit2.getString("imageUrl","");

                String txt = editTextPost.getText().toString().trim();
                if (txt.isEmpty()) {
                    editTextPost.setError("Write something!");
                    editTextPost.requestFocus();
                    return;
                }

                final FirebaseFirestore db= FirebaseFirestore.getInstance();
                DocumentReference exampleRef;
                if(test){
                    exampleRef = db
                            .collection("test_series").document("branches")
                            .collection(liveId).document(documentId);//userId = user
                }else{
                    exampleRef = db.collection("chapters").document(book)
                            .collection(chapter).document("branches")
                            .collection(tab).document(documentId);//userId = user
                }

                //todo upload data
                DocumentReference commentRef = exampleRef.collection("comments").document();
                DocumentReference editorialCommentRef = exampleRef.collection("editorialcomment").document();
                DocumentReference submission = exampleRef.collection("submission").document();

                if(tabNumber==1){
                    LoadComment loadComment = new LoadComment();
                    loadComment.setText(editTextPost.getText().toString());
                    loadComment.setUserId(firebaseUser.getUid());
                    loadComment.setVotes(0);
                    commentRef.set(loadComment.toMap());

                }else if(tabNumber==2){
                    LoadComment loadComment = new LoadComment();
                    loadComment.setText(editTextPost.getText().toString());
                    loadComment.setUserId(firebaseUser.getUid());
                    loadComment.setVotes(0);
                    editorialCommentRef.set(loadComment.toMap());

                }else if(tabNumber==3){
                    LoadSubmission loadSubmission = new LoadSubmission();
                    loadSubmission.setText(editTextPost.getText().toString());
                    loadSubmission.setUserId(firebaseUser.getUid());
                    if (imageset && !imageUrl.isEmpty() && imageUrl != null)
                    loadSubmission.setTextImage(imageUrl);
                    loadSubmission.setVotes(0);
                    submission.set(loadSubmission);

                }
                editTextPost.setText("");
                if(editTextPost.getText().toString().isEmpty() ||editTextPost.getText().toString()==null)
                    editTextPost.setText(" ", TextView.BufferType.EDITABLE);
                editTextPost.setPressed(true);
                editTextPost.setSelection(editTextPost.getText().length());

                try{
                    SharedPreferences prefEdit1 = getSharedPreferences("edit", Context.MODE_PRIVATE);
                    SharedPreferences.Editor edt11 = prefEdit1.edit();
                    edt11.putBoolean("image",false);
                    edt11.putString("imageUrl","");
                    edt11.apply();
                    attachImage.setColorFilter(getResources().getColor(R.color.black));
                }catch (Exception e){

                }

            }
        });

        // Adding Toolbar to Main screen
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        final Animation rotation = AnimationUtils.loadAnimation(this, R.anim.clockwise_refresh);
        // rotation.setRepeatCount(Animation.INFINITE);
        rotation.setRepeatCount(-1);
        rotation.setDuration(1000);


        final ImageButton imageButton=(ImageButton)findViewById(R.id.refresh_determinants);
        imageButton.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    if(tabNumber==1){
                        addCard(book,chapter,mBlogList);
                        adapterComment.startListening();
                    }else if(tabNumber==2){
                        addEditorialCard(book,chapter,mBlogList);
                        adapterEditorial.startListening();
                    }else if(tabNumber==3){
                        addSubmissionCard(book,chapter,mBlogList);
                        adapterSubmission.startListening();
                    }
                }catch (Exception e){
                }
            }
        });


    }

    @Override
    protected void onResume() {
        super.onResume();
        try{
            SharedPreferences prefEdit2 = getSharedPreferences("edit", Context.MODE_PRIVATE);
            imageset = prefEdit2.getBoolean("image",false);
            imageUrl=prefEdit2.getString("imageUrl","");

            if(imageset){
                attachImage.setColorFilter(getResources().getColor(R.color.blue));
            }else{
                attachImage.setColorFilter(getResources().getColor(R.color.black));
            }
        }catch (Exception e){

        }
    }


    @Override
    protected void onStart() {
        super.onStart();

        try{
            if(tabNumber==1){
                addCard(book,chapter,mBlogList);
                adapterComment.startListening();
            }else if(tabNumber==2){
                addEditorialCard(book,chapter,mBlogList);
                adapterEditorial.startListening();
            }else if(tabNumber==3){
                addSubmissionCard(book,chapter,mBlogList);
                adapterSubmission.startListening();
            }
        }catch (Exception e){
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(adapterComment != null)
            adapterComment.stopListening();
        if(adapterEditorial != null)
            adapterEditorial.stopListening();
        if(adapterSubmission != null)
            adapterSubmission.stopListening();
    }



    private void closeKeyBoard(View view){
        if (view != null){
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public void addCard(String book,String chapter,RecyclerView recyclerView){
        final AVLoadingIndicatorView avLoadingIndicatorView = findViewById(R.id.progress_bar_backdrop);
        avLoadingIndicatorView.show();

        final FirebaseFirestore db= FirebaseFirestore.getInstance();

        CollectionReference chaptersRef;
        if(test){
            chaptersRef = db
                    .collection("test_series").document("branches")
                    .collection(liveId).document(documentId)
                    .collection("comments");
        }else{
            chaptersRef = db.collection("chapters").document(book)
                    .collection(chapter).document("branches")
                    .collection(tab).document(documentId)
                    .collection("comments");
        }

        Query query = chaptersRef.orderBy("votes",Query.Direction.DESCENDING).limit(500);
        FirestoreRecyclerOptions<LoadComment> options = new FirestoreRecyclerOptions.Builder<LoadComment>()
                .setQuery(query, LoadComment.class)
                .build();

        adapterComment = new FirestoreRecyclerAdapter<LoadComment, LoadCommentsViewHolder>(options){
            @Override
            protected void onBindViewHolder(@NonNull final LoadCommentsViewHolder holder, int position, @NonNull LoadComment model) {
                DocumentReference docRef = db.collection("users").document(model.getUserId());
//                DocumentReference docRef = db.collection("users").document("user1");
                docRef.get(Source.SERVER).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        UserDetails userDetails = documentSnapshot.toObject(UserDetails.class);
                        holder.setUserName(userDetails.getName());
                        holder.setUserImage(userDetails.getUserImage());
                    }
                });
                holder.setLikes(model.getVotes());
                holder.setText(model.getText());
                holder.setUserId(model.getUserId());
                String documentId1 = getSnapshots().getSnapshot(position).getId();
                holder.setThisDocumentId(documentId1);

                avLoadingIndicatorView.hide();
                ((ImageButton)findViewById(R.id.refresh_determinants)).clearAnimation();

            }

            @NonNull
            @Override
            public LoadCommentsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.comment_card_view, parent, false);
                return new LoadCommentsViewHolder(view,true,editTextPost);
            }
        };
        recyclerView.setAdapter(adapterComment);
        adapterComment.startListening();
        adapterComment.notifyDataSetChanged();
    }


    public void addEditorialCard(String book,String chapter,RecyclerView recyclerView){
        final AVLoadingIndicatorView avLoadingIndicatorView = findViewById(R.id.progress_bar_backdrop);
        avLoadingIndicatorView.show();

        final FirebaseFirestore db= FirebaseFirestore.getInstance();

        CollectionReference chaptersRef;
        if(test){
            chaptersRef = db
                    .collection("test_series").document("branches")
                    .collection(liveId).document(documentId)
                    .collection("editorialcomment");//editorial
        }else{
            chaptersRef = db.collection("chapters").document(book)
                    .collection(chapter).document("branches")
                    .collection(tab).document(documentId)
                    .collection("editorialcomment");//editorial
        }


        Query query = chaptersRef.orderBy("votes",Query.Direction.DESCENDING).limit(500);
        FirestoreRecyclerOptions<LoadComment> options = new FirestoreRecyclerOptions.Builder<LoadComment>()
                .setQuery(query, LoadComment.class)
                .build();

        adapterEditorial = new FirestoreRecyclerAdapter<LoadComment, LoadCommentsViewHolder>(options){
            @Override
            protected void onBindViewHolder(@NonNull final LoadCommentsViewHolder holder, int position, @NonNull LoadComment model) {
                DocumentReference docRef = db.collection("users").document(model.getUserId());
//                DocumentReference docRef = db.collection("users").document("user1");
                docRef.get(Source.SERVER).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        UserDetails userDetails = documentSnapshot.toObject(UserDetails.class);
                        holder.setUserName(userDetails.getName());
                        holder.setUserImage(userDetails.getUserImage());
                    }
                });
                holder.setLikes(model.getVotes());
                holder.setText(model.getText());
                holder.setUserId(model.getUserId());
                String documentId1 = getSnapshots().getSnapshot(position).getId();
                holder.setThisDocumentId(documentId1);

                avLoadingIndicatorView.hide();
                ((ImageButton)findViewById(R.id.refresh_determinants)).clearAnimation();

            }

            @NonNull
            @Override
            public LoadCommentsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.comment_card_view, parent, false);
                return new LoadCommentsViewHolder(view,true,editTextPost);
            }
        };
        recyclerView.setAdapter(adapterEditorial);
        adapterEditorial.startListening();
        adapterEditorial.notifyDataSetChanged();
    }


    public void addSubmissionCard(String book,String chapter,RecyclerView recyclerView){
        final AVLoadingIndicatorView avLoadingIndicatorView = findViewById(R.id.progress_bar_backdrop);
        avLoadingIndicatorView.show();

        db = FirebaseFirestore.getInstance();

        CollectionReference chaptersRef;
        if(test){
            chaptersRef = db
                    .collection("test_series").document("branches")
                    .collection(liveId).document(documentId)
                    .collection("submission");
        }else{
            chaptersRef = db.collection("chapters").document(book)
                    .collection(chapter).document("branches")
                    .collection(tab).document(documentId)
                    .collection("submission");
        }

        Query query = chaptersRef.orderBy("votes",Query.Direction.DESCENDING).limit(500);

        FirestoreRecyclerOptions<LoadSubmission> options = new FirestoreRecyclerOptions.Builder<LoadSubmission>()
                .setQuery(query, LoadSubmission.class)
                .build();

        adapterSubmission= new FirestoreRecyclerAdapter<LoadSubmission, LoadSubmissionViewHolder>(options){
            @Override
            protected void onBindViewHolder(@NonNull final LoadSubmissionViewHolder holder, int position, @NonNull LoadSubmission model) {
                DocumentReference docRef = db.collection("users").document(model.getUserId());
//                DocumentReference docRef = db.collection("users").document("user1");
                docRef.get(Source.SERVER).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        UserDetails userDetails = documentSnapshot.toObject(UserDetails.class);
                        holder.setUserName(userDetails.getName());
                        holder.setUserImage(userDetails.getUserImage());
                    }
                });
                holder.setUserId(model.getUserId());
                holder.setText(model.getText());
                holder.setSubImage(model.getTextImage());
                holder.setVotes(model.getVotes());
                String documentId1 = getSnapshots().getSnapshot(position).getId();
                holder.setThisDocumentId(documentId1);

                avLoadingIndicatorView.hide();
            }

            @NonNull
            @Override
            public LoadSubmissionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.submission_card_view, parent, false);
                return new LoadSubmissionViewHolder(view,true,editTextPost,attachImage);
            }
        };
        recyclerView.setAdapter(adapterSubmission);
        adapterSubmission.startListening();
        adapterSubmission.notifyDataSetChanged();
    }


    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }


    @Override
    public void onBackPressed(){

        FragmentManager fm = getFragmentManager();
        if (fm.getBackStackEntryCount() > 0) {
            fm.popBackStack();
        } else {
            super.onBackPressed();
        }
    }


}