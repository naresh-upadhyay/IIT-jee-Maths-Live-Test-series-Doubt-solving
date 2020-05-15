package com.naresh.kingupadhyay.mathsking.activities;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatRatingBar;
import androidx.appcompat.widget.PopupMenu;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.FragmentManager;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.Source;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.naresh.kingupadhyay.mathsking.Database.PDFTools;
import com.naresh.kingupadhyay.mathsking.R;
import com.naresh.kingupadhyay.mathsking.UiMaterial.LoginOption;
import com.naresh.kingupadhyay.mathsking.network.LoadEditorial;
import com.naresh.kingupadhyay.mathsking.network.LoadSubmission;
import com.naresh.kingupadhyay.mathsking.network.UserDetails;
import com.naresh.kingupadhyay.mathsking.viewholders.LoadSubmissionViewHolder;
import com.wang.avi.AVLoadingIndicatorView;

public class ViewCardDoubts extends AppCompatActivity {

    private Toolbar toolbar;
    private RecyclerView mBlogList;

    private String book;
    private String chapter;
    private Timestamp uploadingTime;
    private String userId;
    private String userName;
    private int userRating;
    private String userImage;

    private String tags;
    private String text;
    private String textImageUrl;
    private AppCompatRatingBar ratingBar;
    private String documentId;
    private String currentUserId;
    private String questionImageUrl="";
    private FirebaseFirestore db;
    private LinearLayout upvoteLayout;
    private LinearLayout downvoteLayout;
    private LinearLayout replyLayout;
    private boolean skip;
    private ImageView upvoteImage;
    private ImageView downvoteImage;
    private boolean flagUpVote = false;
    private boolean flagDownVote = false;
    private TextView upvote;
    private TextView downvote;
    private TextView voteText;
    private String votes;
    private TextView reply;
    private ImageView replyImage;
    FirestoreRecyclerAdapter<LoadSubmission, LoadSubmissionViewHolder> adapterSubmission;
    private String tab;
    private AdView mAdView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_card_doubts);

        MobileAds.initialize(this, "ca-app-pub-6924423095909700~8475665982");

        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);



        db= FirebaseFirestore.getInstance();

        SharedPreferences prefUseId = getSharedPreferences("ActivityPREF", Context.MODE_PRIVATE);
        currentUserId=prefUseId.getString("uid","userkhk");

        SharedPreferences prefskip = getSharedPreferences("skip", Context.MODE_PRIVATE);
        skip=prefskip.getBoolean("skip",false);


        Bundle bundle = getIntent().getExtras();
        book = bundle.getString("book");
        chapter = bundle.getString("chapter","chapter");
        userId=bundle.getString("userId");
        userName=bundle.getString("userName", "Private");
        userRating=bundle.getInt("userRating",1);
        userImage=bundle.getString("userImage");
        tags=bundle.getString("tags","none");
        text=bundle.getString("text");
        textImageUrl=bundle.getString("textImageUrl");
        votes=bundle.getString("votes");
        documentId=bundle.getString("documentId");
        tab = bundle.getString("tab","doubts");

        SharedPreferences intentPref = getSharedPreferences("intent", Context.MODE_PRIVATE);
        SharedPreferences.Editor edtIntent = intentPref.edit();
        edtIntent.putString("book",book);
        edtIntent.putString("chapter",chapter);
        edtIntent.putString("tab",tab);
        edtIntent.putString("documentId",documentId);
        edtIntent.putInt("tabNumber",3);
        edtIntent.putString("liveId","live");
        edtIntent.putBoolean("test",false);
        edtIntent.apply();

        final FirebaseFirestore db= FirebaseFirestore.getInstance();
        final CollectionReference votesRef = db
                .collection("chapters").document(book)
                .collection(chapter).document("branches")
                .collection(tab);//userId = user


        try{
            defaultCardSettings();
            setUserImage(userImage);
            setUserName(userName);
            setUserRating(userRating);
            setTags(tags);
            setText(text);
            setTextImageUrl(textImageUrl);
            setDocumentId(documentId);
            setVotes(votes);
        }catch (Exception e){
        }


        ImageButton back=findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


        mBlogList=(RecyclerView)findViewById(R.id.myrecyclerview);
        mBlogList.setHasFixedSize(true);
        mBlogList.setLayoutManager(new LinearLayoutManager(this));


        ImageView reportedit = findViewById(R.id.report_bar);
        reportedit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(userId.equals(currentUserId)){
                    popupMenuEditDelete(view);
                }else{
                    popupMenuReport(view);
                }

            }
        });

        ImageView userpic = findViewById(R.id.userImage_post_comment2);
        AVLoadingIndicatorView process = findViewById(R.id.progress_bar_user_image_post_comment2);
        String userImage = prefUseId.getString("userImage","userImage");
        PDFTools.picassoLoadImageAvl(userpic,userImage,process);
        TextView textViewpost_comment_box = findViewById(R.id.post_Comment_view);
        textViewpost_comment_box.setText(R.string.thought);
        CardView cardViewPost = findViewById(R.id.editTextCard_post_comment2);
        cardViewPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!skip)
                    newActivityOpen(view);
                else{
                    Toast.makeText(ViewCardDoubts.this, "First authenticate yourself", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(ViewCardDoubts.this, LoginOption.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }
            }
        });

        reply.setTextColor(getResources().getColor(R.color.blue));
        replyImage.setColorFilter(getResources().getColor(R.color.blue));
        addSubmissionCard(book,chapter,mBlogList);
        adapterSubmission.startListening();

        // Adding Toolbar to Main screen
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        TextView titleb = findViewById(R.id.titleb);
        if(tab.equals("suggestions")){
            titleb.setText(R.string.suggestion);
        }else{
            titleb.setText(R.string.doubt);
        }


        final Animation rotation = AnimationUtils.loadAnimation(this, R.anim.clockwise_refresh);
        // rotation.setRepeatCount(Animation.INFINITE);
        rotation.setRepeatCount(-1);
        rotation.setDuration(1000);


        final ImageButton imageButton=(ImageButton)findViewById(R.id.refresh_determinants);
        imageButton.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addSubmissionCard(book,chapter,mBlogList);
                adapterSubmission.startListening();
            }
        });



        upvoteLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!skip){
                    SharedPreferences prefliked = view.getContext().getSharedPreferences("ActivityPREF", Context.MODE_PRIVATE);
                    flagUpVote = prefliked.getBoolean(documentId+"upvote",false);
                    flagDownVote = prefliked.getBoolean(documentId+"downvote",false);

                    downvoteImage.setColorFilter(view.getContext().getResources().getColor(R.color.white));
                    downvote.setTextColor(view.getResources().getColor(R.color.white));
                    if(!flagUpVote){
                        if(flagDownVote){
                            votes = Integer.toString(Integer.parseInt(votes)+1);
                        }
                        votes = Integer.toString(Integer.parseInt(votes)+1);
                        voteText.setText(votes);

                        upvoteImage.setColorFilter(view.getContext().getResources().getColor(R.color.blue));
                        upvote.setTextColor(view.getResources().getColor(R.color.blue));
                        flagUpVote = true;
                    }else{
                        if(flagDownVote){
                            votes = Integer.toString(Integer.parseInt(votes)+1);
                        }
                        votes = Integer.toString(Integer.parseInt(votes)-1);
                        voteText.setText(votes);

                        upvoteImage.setColorFilter(view.getContext().getResources().getColor(R.color.white));
                        upvote.setTextColor(view.getResources().getColor(R.color.white));
                        flagUpVote = false;
                    }
                    flagDownVote = false;
                    SharedPreferences.Editor likedit = prefliked.edit();
                    likedit.putBoolean(documentId+"upvote",flagUpVote);
                    likedit.putBoolean(documentId+"downvote",flagDownVote);
                    likedit.apply();

                    try {
                        votesRef.document(documentId).update("votes",Integer.parseInt(votes));
                    }catch (Exception e){
                    }
                    if(flagUpVote){
                        upvoteImage.setColorFilter(view.getContext().getResources().getColor(R.color.blue));
                        upvote.setTextColor(view.getResources().getColor(R.color.blue));
                    }else{
                        upvoteImage.setColorFilter(view.getContext().getResources().getColor(R.color.white));
                        upvote.setTextColor(view.getResources().getColor(R.color.white));
                    }
                    if(flagDownVote){
                        downvote.setTextColor(view.getResources().getColor(R.color.blue));
                        downvoteImage.setColorFilter(view.getContext().getResources().getColor(R.color.blue));
                    }else{
                        downvote.setTextColor(view.getResources().getColor(R.color.white));
                        downvoteImage.setColorFilter(view.getContext().getResources().getColor(R.color.white));
                    }



                }else{
                    Toast.makeText(view.getContext(), "First authenticate yourself", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(view.getContext(), LoginOption.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    view.getContext().startActivity(intent);

                }
            }
        });
        if(tab.equals("suggestions")){
            downvoteLayout.setVisibility(View.INVISIBLE);
        }
        downvoteLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!skip){
                    SharedPreferences prefliked = view.getContext().getSharedPreferences("ActivityPREF", Context.MODE_PRIVATE);
                    flagUpVote = prefliked.getBoolean(documentId+"upvote",false);
                    flagDownVote = prefliked.getBoolean(documentId+"downvote",false);

                    upvoteImage.setColorFilter(view.getContext().getResources().getColor(R.color.white));
                    upvote.setTextColor(view.getResources().getColor(R.color.white));
                    if(!flagDownVote){
                        if(flagUpVote){
                            votes = Integer.toString(Integer.parseInt(votes)-1);
                        }
                        votes = Integer.toString(Integer.parseInt(votes)-1);
                        voteText.setText(votes);

                        downvote.setTextColor(view.getResources().getColor(R.color.blue));
                        downvoteImage.setColorFilter(view.getContext().getResources().getColor(R.color.blue));
                        flagDownVote = true;
                    }else{
                        if(flagUpVote){
                            votes = Integer.toString(Integer.parseInt(votes)-1);
                        }
                        votes = Integer.toString(Integer.parseInt(votes)+1);
                        voteText.setText(votes);

                        downvote.setTextColor(view.getResources().getColor(R.color.white));
                        downvoteImage.setColorFilter(view.getContext().getResources().getColor(R.color.white));
                        flagDownVote = false;
                    }
                    flagUpVote = false;
                    SharedPreferences.Editor likedit = prefliked.edit();
                    likedit.putBoolean(documentId+"upvote",flagUpVote);
                    likedit.putBoolean(documentId+"downvote",flagDownVote);
                    likedit.apply();

                    try {
                        votesRef.document(documentId).update("votes",Integer.parseInt(votes));
                    }catch (Exception e){
                    }

                    if(flagUpVote){
                        upvoteImage.setColorFilter(view.getContext().getResources().getColor(R.color.blue));
                        upvote.setTextColor(view.getResources().getColor(R.color.blue));
                    }else{
                        upvoteImage.setColorFilter(view.getContext().getResources().getColor(R.color.white));
                        upvote.setTextColor(view.getResources().getColor(R.color.white));
                    }
                    if(flagDownVote){
                        downvote.setTextColor(view.getResources().getColor(R.color.blue));
                        downvoteImage.setColorFilter(view.getContext().getResources().getColor(R.color.blue));
                    }else{
                        downvote.setTextColor(view.getResources().getColor(R.color.white));
                        downvoteImage.setColorFilter(view.getContext().getResources().getColor(R.color.white));
                    }


                }else{
                    Toast.makeText(view.getContext(), "First authenticate yourself", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(view.getContext(), LoginOption.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    view.getContext().startActivity(intent);
                }
            }
        });

    }


    @Override
    protected void onStart() {
        super.onStart();
        if(adapterSubmission != null){
            adapterSubmission.startListening();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(adapterSubmission != null)
            adapterSubmission.stopListening();

    }




    public void setVotes(String votesInput) {
//        votes = Integer.toString(votesInput);
        voteText= findViewById(R.id.voteText);
        voteText.setText(votesInput);
    }

    public void defaultCardSettings(){
        TextView tagname = findViewById(R.id.tagsName);
        upvote = findViewById(R.id.upvoteText);
        downvote = findViewById(R.id.downText);
        reply = findViewById(R.id.replyTextsub);
        upvoteLayout = findViewById(R.id.upvoteLayout);
        downvoteLayout = findViewById(R.id.downvoteLayout);
        replyLayout = findViewById(R.id.replyLayoutsub);
        voteText= findViewById(R.id.voteText);
        replyImage = findViewById(R.id.replyImagesub);
        upvoteImage = findViewById(R.id.upvoteImage);
        downvoteImage = findViewById(R.id.downImage);

        tagname.setText(R.string.tagsName);
        upvote.setText(R.string.upvote);
        downvote.setText(R.string.downvote);
        reply.setText(R.string.reply);
    }


    public void setDocumentId(String documentId) {
        SharedPreferences prefliked = getSharedPreferences("ActivityPREF", Context.MODE_PRIVATE);
        flagUpVote = prefliked.getBoolean(documentId+"upvote",false);
        flagDownVote = prefliked.getBoolean(documentId+"downvote",false);
        upvoteImage.setColorFilter(getResources().getColor(R.color.white));
        upvote.setTextColor(getResources().getColor(R.color.white));
        downvoteImage.setColorFilter(getResources().getColor(R.color.white));
        downvote.setTextColor(getResources().getColor(R.color.white));

        if(flagUpVote){
            upvoteImage.setColorFilter(getResources().getColor(R.color.blue));
            upvote.setTextColor(getResources().getColor(R.color.blue));
        }else{
            upvoteImage.setColorFilter(getResources().getColor(R.color.white));
            upvote.setTextColor(getResources().getColor(R.color.white));
        }
        if(flagDownVote){
            downvote.setTextColor(getResources().getColor(R.color.blue));
            downvoteImage.setColorFilter(getResources().getColor(R.color.blue));
        }else{
            downvote.setTextColor(getResources().getColor(R.color.white));
            downvoteImage.setColorFilter(getResources().getColor(R.color.white));
        }
    }

    public void setUserName(String userName) {
        TextView textViewUserName = findViewById(R.id.user_name);
        textViewUserName.setText(userName);
        this.userName = userName;
    }

    public void setUserRating(int userRating) {
        ratingBar= findViewById(R.id.ratingbar);
        ratingBar.setNumStars(userRating);
        ratingBar.setRating(userRating);
        setCurrentRating(userRating);
        this.userRating = userRating;
    }

    private void setCurrentRating(float rating) {
        LayerDrawable drawable = (LayerDrawable)ratingBar.getProgressDrawable();
        switch (Math.round(rating)) {
            case 1:
                setRatingStarColor(drawable.getDrawable(2), ContextCompat.getColor(this, R.color.star1));
                break;
            case 2:
                setRatingStarColor(drawable.getDrawable(2), ContextCompat.getColor(this, R.color.star2));
                break;
            case 3:
                setRatingStarColor(drawable.getDrawable(2), ContextCompat.getColor(this, R.color.star3));
                break;
            case 4:
                setRatingStarColor(drawable.getDrawable(2), ContextCompat.getColor(this, R.color.star4));
                break;
            case 5:
                setRatingStarColor(drawable.getDrawable(2), ContextCompat.getColor(this, R.color.star5));
                break;
            case 6:
                setRatingStarColor(drawable.getDrawable(2), ContextCompat.getColor(this, R.color.star6));
                break;
            case 7:
                setRatingStarColor(drawable.getDrawable(2), ContextCompat.getColor(this, R.color.star7));
                break;
            case 8:
                setRatingStarColor(drawable.getDrawable(2), ContextCompat.getColor(this, R.color.star8));
                break;
            case 9:
                setRatingStarColor(drawable.getDrawable(2), ContextCompat.getColor(this, R.color.star9));
                break;
            case 10:
                setRatingStarColor(drawable.getDrawable(2), ContextCompat.getColor(this, R.color.star10));
                break;
        }
        setRatingStarColor(drawable.getDrawable(1), ContextCompat.getColor(this, R.color.line_background));
        setRatingStarColor(drawable.getDrawable(0), ContextCompat.getColor(this, R.color.line_background));
    }

    private void setRatingStarColor(Drawable drawable, @ColorInt int color)
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        {
            DrawableCompat.setTint(drawable, color);
        }
        else
        {
            drawable.setColorFilter(color, PorterDuff.Mode.SRC_IN);
        }
    }
    public void setUserImage(String userImage) {
        ImageView dp = findViewById(R.id.userImage);
        AVLoadingIndicatorView progressBar=findViewById(R.id.progress_bar_user_image);
        progressBar.show();
        PDFTools.picassoLoadImageAvl(dp , userImage,progressBar);
        this.userImage = userImage;
    }


    public void setUploadingTime(Timestamp uploadingTime) {
        this.uploadingTime = uploadingTime;
    }

    public void setUserId(String userId) {
        // user Image , name ,rating will be here
        this.userId = userId;
    }



    public void setTags(String tags) {
        this.tags = tags;
        TextView tagsval = findViewById(R.id.tagsValues);
        tagsval.setText(tags);
    }

    public void setText(String text) {
        this.text = text;
        final TextView textView = findViewById(R.id.conceptTitle);
        textView.setText(text);
        textView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                ClipboardManager clipboard = (ClipboardManager) view.getContext().getSystemService(CLIPBOARD_SERVICE);
                ClipData cData = ClipData.newPlainText("text", textView.getText());
                clipboard.setPrimaryClip(cData);
                Toast.makeText(view.getContext(), "Copied", Toast.LENGTH_SHORT).show();
                return true;
            }
        });

    }

    public void setTextImageUrl(String textImageUrl) {
        this.textImageUrl = textImageUrl;
        ImageView questionImg=findViewById(R.id.questionImage);
        AVLoadingIndicatorView progressBar=findViewById(R.id.progress_bar_user_image);
//        progressBar.show();
        PDFTools.picassoLoadImageAvl(questionImg , textImageUrl,progressBar);
    }


    private void popupMenuEditDelete(final View v) {
        //Creating the instance of PopupMenu
        PopupMenu popup = new PopupMenu(v.getContext(),v );
        // to inflate the menu resource (defined in XML) into the PopupMenu
        popup.getMenuInflater().inflate(R.menu.options_edit_delete, popup.getMenu());
        //popup with OnMenuItemClickListener
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()){
                    case R.id.edit_popup :
                        try {
                            questionImageUrl = textImageUrl;
                            onBackPressed();
                            editActivity(v);
                        }catch (Exception e){
                        }
                        break;
                    case R.id.delete_popup :
                        try {
                            questionImageUrl = textImageUrl;
                            Uri uri =  Uri.parse(textImageUrl);
                            String path = uri.getPath();
                            int cut = path.lastIndexOf('/');
                            String imageFilename="";
                            if (cut != -1) {
                                imageFilename = path.substring(cut + 1);
                            }
                            deleteStoredImage("images/examples/submissions",imageFilename);
                        }catch (Exception e){
                        }
                        final DocumentReference userRef = db
                                .collection("chapters").document(book)
                                .collection(chapter).document("branches")
                                .collection(tab).document(documentId);//userId = user
                        userRef.delete();
                        onBackPressed();

                        break;
                    default:
                        break;
                }
                return true;
            }
        });
        popup.show();//show the popup menu
    }

    public void editActivity(View v){
        Context context = v.getContext();
        Intent intent = new Intent(context, PostDoubt.class);
        intent.putExtra("book",book);
        intent.putExtra("chapter", chapter);
        intent.putExtra("tags", tags);
        intent.putExtra("text", text);
        intent.putExtra("textImageUrl", questionImageUrl);
        intent.putExtra("votes", Integer.parseInt(votes));
        intent.putExtra("tab",tab);
        intent.putExtra("documentId",documentId);
        context.startActivity(intent);

    }
    public void deleteStoredImage(String path,String filename){
        FirebaseStorage storage = FirebaseStorage.getInstance();
        // Create a storage reference from our app
        StorageReference storageRef = storage.getReference();
        StorageReference conceptImagesRef = storageRef.child(path+"/"+filename);

        // Delete the file
        conceptImagesRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                // File deleted successfully
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Uh-oh, an error occurred!
            }
        });
    }

    private void popupMenuReport(final View v) {
        //Creating the instance of PopupMenu
        PopupMenu popup = new PopupMenu(v.getContext(),v );
        // to inflate the menu resource (defined in XML) into the PopupMenu
        popup.getMenuInflater().inflate(R.menu.option_report, popup.getMenu());
        //popup with OnMenuItemClickListener
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()){
                    case R.id.report_popup :
                        Toast.makeText(v.getContext(),"report", Toast.LENGTH_SHORT).show();
                        break;
                    default:
                        break;
                }
                return false;
            }
        });
        popup.show();//show the popup menu
    }

    public void newActivityOpen(View v){
        Context context = v.getContext();
        Intent intent = new Intent(context, CommentView.class);
        intent.putExtra("book",book);
        intent.putExtra("chapter", chapter);
        intent.putExtra("documentId",documentId);
        intent.putExtra("tab",tab);
        intent.putExtra("tabNumber",3);
        intent.putExtra("liveId", "live");
        intent.putExtra("test", false);
        context.startActivity(intent);

    }

    public void addSubmissionCard(String book,String chapter,RecyclerView recyclerView){
        final AVLoadingIndicatorView avLoadingIndicatorView = findViewById(R.id.progress_bar_nested_recyclerview);
        avLoadingIndicatorView.show();

        db = FirebaseFirestore.getInstance();
        CollectionReference chaptersRef = db
                .collection("chapters").document(book)
                .collection(chapter).document("branches")
                .collection(tab).document(documentId)
                .collection("submission");
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
                return new LoadSubmissionViewHolder(view,false,null,null);
            }
        };
        recyclerView.setAdapter(adapterSubmission);
        adapterSubmission.startListening();
        adapterSubmission.notifyDataSetChanged();
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
