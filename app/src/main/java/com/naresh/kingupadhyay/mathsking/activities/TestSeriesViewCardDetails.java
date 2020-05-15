package com.naresh.kingupadhyay.mathsking.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.widget.CompoundButtonCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.apradanas.simplelinkabletext.Link;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.Source;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.naresh.kingupadhyay.mathsking.Database.CountryData;
import com.naresh.kingupadhyay.mathsking.Database.PDFTools;
import com.naresh.kingupadhyay.mathsking.R;
import com.naresh.kingupadhyay.mathsking.UiMaterial.LoginOption;
import com.naresh.kingupadhyay.mathsking.network.LoadComment;
import com.naresh.kingupadhyay.mathsking.network.LoadEditorial;
import com.naresh.kingupadhyay.mathsking.network.LoadSubmission;
import com.naresh.kingupadhyay.mathsking.network.UserDetails;
import com.naresh.kingupadhyay.mathsking.viewholders.LoadCommentsViewHolder;
import com.naresh.kingupadhyay.mathsking.viewholders.LoadSubmissionViewHolder;
import com.wang.avi.AVLoadingIndicatorView;


import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class TestSeriesViewCardDetails extends AppCompatActivity{

    private Toolbar toolbar;
    private RecyclerView mBlogList;
    //    private static DatabaseReference mDatabase;
    private static DatabaseReference favourites;
    private static Drawable draw;
    //    private static DatabaseReference favourite;
    protected String NAME="name";
    private AdView mAdView;
    private static boolean skip;
    FirestoreRecyclerAdapter<LoadComment, LoadCommentsViewHolder> adapterComment,adapterEditorial;
    FirestoreRecyclerAdapter<LoadSubmission, LoadSubmissionViewHolder> adapterSubmission;

    private String tags;
    private String text;
    private String textImageUrl;
    private boolean option;//true-> objective , false -> subjective questions
    private boolean optionA;
    private boolean optionB;
    private boolean optionC;
    private boolean optionD;

    private FirebaseFirestore db;
    private TextView comment;
    private TextView editorial;
    private TextView submission;
    private ImageView commentImage;
    private ImageView editorialImage;
    private ImageView submissionImage;
    private List<Link> links;
    private int tabNumber;//1->comment,2->editorial,3->submission
    private String documentId;
    private String currentUserId;
    private String questionImageUrl="";
    private String answerImageUrl="";
    private String answertext="";
    private String liveId;
    private int markspos;
    private int marksneg;
    private int chapterCode;
    private int numericval;
    private String title;
    private int cardnumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_series_view_card_details);
//        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        MobileAds.initialize(this, "ca-app-pub-6924423095909700~8475665982");

        mAdView = findViewById(R.id.adVmain);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);


        SharedPreferences pref = getSharedPreferences("skip", Context.MODE_PRIVATE);
        skip=pref.getBoolean("skip",false);
        SharedPreferences prefUseId = getSharedPreferences("ActivityPREF", Context.MODE_PRIVATE);
        currentUserId=prefUseId.getString("uid","userkhk");
        db= FirebaseFirestore.getInstance();
        SharedPreferences prefLive = getSharedPreferences("live", Context.MODE_PRIVATE);
        title = prefLive.getString("title",title);
        liveId = prefLive.getString("liveId",liveId);


        Bundle bundle = getIntent().getExtras();

        liveId = bundle.getString("liveId","liveId");
        markspos = bundle.getInt("markspos",0);
        marksneg = bundle.getInt("marksneg",0);
        chapterCode = bundle.getInt("chapterCode",0);
        numericval = bundle.getInt("numericval",0);

        tags=bundle.getString("tags","none");
        text=bundle.getString("text");
        textImageUrl=bundle.getString("textImageUrl");
        option=bundle.getBoolean("option",false);
        optionA=bundle.getBoolean("optionA", false);
        optionB=bundle.getBoolean("optionB", false);
        optionC=bundle.getBoolean("optionC", false);
        optionD=bundle.getBoolean("optionD", false);
        tabNumber = bundle.getInt("tabNumber",1);
        documentId=bundle.getString("documentId");
        cardnumber = bundle.getInt("cardnumber",1);

        SharedPreferences intentPref = getSharedPreferences("intent", Context.MODE_PRIVATE);
        SharedPreferences.Editor edtIntent = intentPref.edit();
        edtIntent.putString("book","book");
        edtIntent.putString("chapter","chapter");
        edtIntent.putString("liveId",liveId);
        edtIntent.putString("documentId",documentId);
        edtIntent.putString("tab","tab");
        edtIntent.putInt("tabNumber",tabNumber);
        edtIntent.putBoolean("test",true);
        edtIntent.apply();

        try{
            defaultCardSettings();
            setDocumentId(documentId);
            setMarkspos(markspos);
            setMarksneg(marksneg);
            setNumericval(numericval);
            setTags(tags);
            setText(text);
            setTextImageUrl(textImageUrl);
            setOption(option);
            setOptions(optionA,optionB,optionC,optionD);
            setCardnumber(cardnumber);

        }catch (Exception e){
//            Toast.makeText(TestSeriesViewCardDetails.this,"p1", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
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

        SharedPreferences prefAdmin = getSharedPreferences("ActivityPREF", Context.MODE_PRIVATE);
        final String id=prefAdmin.getString("id","");
        ImageView reportedit = findViewById(R.id.report_bar);
        reportedit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(id.equals("vinayupadhyay02001@gmail.com") || id.equals("naresh03961999@gmail.com") || id.equals("rakshitaupadhyay1497@gmail.com") ) {
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
                    newActivityOpen(view,tabNumber);
                else{
                    Toast.makeText(TestSeriesViewCardDetails.this, "First authenticate yourself", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(TestSeriesViewCardDetails.this, LoginOption.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }
            }
        });


        try{

            comment = findViewById(R.id.comment);
            editorial= findViewById(R.id.editorialText);
            submission= findViewById(R.id.submissionText);
            comment.setText(R.string.comment);
            editorial.setText(R.string.editorial);
            submission.setText(R.string.submission);
            commentImage = findViewById(R.id.commentImage);
            editorialImage = findViewById(R.id.editorialImage);
            submissionImage = findViewById(R.id.submissionImage);
        }catch (Exception e){
//            Toast.makeText(TestSeriesViewCardDetails.this,"p5", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
        final CardView editorialcardview;
        editorialcardview= findViewById(R.id.card_view_editorial);
        try{

            editorialcardview.setVisibility(View.GONE);
            if(tabNumber==1){
                comment.setTextColor(getResources().getColor(R.color.blue));
                commentImage.setColorFilter(getResources().getColor(R.color.blue));
                addCard(liveId,documentId,mBlogList);
                adapterComment.startListening();
            }else if(tabNumber==2){
                editorial.setTextColor(getResources().getColor(R.color.blue));
                editorialImage.setColorFilter(getResources().getColor(R.color.blue));
                editorialcardview.setVisibility(View.VISIBLE);
                addEditorialCard(liveId,documentId,mBlogList);
                adapterEditorial.startListening();

            }else if(tabNumber==3){
                submission.setTextColor(getResources().getColor(R.color.blue));
                submissionImage.setColorFilter(getResources().getColor(R.color.blue));
                addSubmissionCard(liveId,documentId,mBlogList);
                adapterSubmission.startListening();
            }
        }catch(Exception e){
//            Toast.makeText(TestSeriesViewCardDetails.this,"p6", Toast.LENGTH_SHORT).show();
            e.printStackTrace();

        }


        // Adding Toolbar to Main screen
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        TextView titleb = findViewById(R.id.titleb);
        titleb.setText(title);

        final Animation rotation = AnimationUtils.loadAnimation(this, R.anim.clockwise_refresh);
        // rotation.setRepeatCount(Animation.INFINITE);
        rotation.setRepeatCount(-1);
        rotation.setDuration(1000);


        final ImageButton imageButton=(ImageButton)findViewById(R.id.refresh_determinants);
        imageButton.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(tabNumber==1){
                    addCard(liveId,documentId,mBlogList);
                    adapterComment.startListening();
                }else if(tabNumber==2){
                    addEditorialCard(liveId,documentId,mBlogList);
                    adapterEditorial.startListening();

                }else if(tabNumber==3){
                    addSubmissionCard(liveId,documentId,mBlogList);
                    adapterSubmission.startListening();
                }
            }
        });


        Spinner spinner=(Spinner) findViewById(R.id.spinnerNumbers);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(TestSeriesViewCardDetails.this,
                android.R.layout.simple_spinner_item, CountryData.numberNames);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setVisibility(View.GONE);


        try{

            LinearLayout commentsLayout = findViewById(R.id.comments);
            commentsLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    editorialcardview.setVisibility(View.GONE);
                    comment.setTextColor(getResources().getColor(R.color.blue));
                    editorial.setTextColor(getResources().getColor(R.color.white));
                    submission.setTextColor(getResources().getColor(R.color.white));
                    commentImage.setColorFilter(getResources().getColor(R.color.blue));
                    editorialImage.setColorFilter(getResources().getColor(R.color.white));
                    submissionImage.setColorFilter(getResources().getColor(R.color.white));
                    if(adapterEditorial != null)
                        adapterEditorial.stopListening();
                    if(adapterSubmission != null)
                        adapterSubmission.stopListening();
                    addCard(liveId,documentId,mBlogList);
                    adapterComment.startListening();
                    tabNumber = 1;

                    SharedPreferences intentPref = getSharedPreferences("intent", Context.MODE_PRIVATE);
                    SharedPreferences.Editor edtIntent = intentPref.edit();
                    edtIntent.putString("book","book");
                    edtIntent.putString("chapter","chapter");
                    edtIntent.putString("liveId",liveId);
                    edtIntent.putString("documentId",documentId);
                    edtIntent.putString("tab","tab");
                    edtIntent.putInt("tabNumber",tabNumber);
                    edtIntent.putBoolean("test",true);
                    edtIntent.apply();

                }
            });
            LinearLayout editorialLayout = findViewById(R.id.editorial);
            editorialLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    editorialcardview.setVisibility(View.VISIBLE);
                    comment.setTextColor(getResources().getColor(R.color.white));
                    editorial.setTextColor(getResources().getColor(R.color.blue));
                    submission.setTextColor(getResources().getColor(R.color.white));
                    commentImage.setColorFilter(getResources().getColor(R.color.white));
                    editorialImage.setColorFilter(getResources().getColor(R.color.blue));
                    submissionImage.setColorFilter(getResources().getColor(R.color.white));

                    if(adapterComment != null)
                        adapterComment.stopListening();
                    if(adapterSubmission != null)
                        adapterSubmission.stopListening();
                    addEditorialCard(liveId,documentId,mBlogList);
                    adapterEditorial.startListening();
                    tabNumber = 2;

                    SharedPreferences intentPref = getSharedPreferences("intent", Context.MODE_PRIVATE);
                    SharedPreferences.Editor edtIntent = intentPref.edit();
                    edtIntent.putString("book","book");
                    edtIntent.putString("chapter","chapter");
                    edtIntent.putString("liveId",liveId);
                    edtIntent.putString("documentId",documentId);
                    edtIntent.putString("tab","tab");
                    edtIntent.putInt("tabNumber",tabNumber);
                    edtIntent.putBoolean("test",true);
                    edtIntent.apply();

                }
            });
            LinearLayout submissionLayout = findViewById(R.id.submission);
            submissionLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    editorialcardview.setVisibility(View.GONE);
                    comment.setTextColor(getResources().getColor(R.color.white));
                    editorial.setTextColor(getResources().getColor(R.color.white));
                    submission.setTextColor(getResources().getColor(R.color.blue));
                    commentImage.setColorFilter(getResources().getColor(R.color.white));
                    editorialImage.setColorFilter(getResources().getColor(R.color.white));
                    submissionImage.setColorFilter(getResources().getColor(R.color.blue));

                    if(adapterComment != null)
                        adapterComment.stopListening();
                    if(adapterEditorial != null)
                        adapterEditorial.stopListening();
                    addSubmissionCard(liveId,documentId,mBlogList);
                    adapterSubmission.startListening();
                    tabNumber = 3;

                    SharedPreferences intentPref = getSharedPreferences("intent", Context.MODE_PRIVATE);
                    SharedPreferences.Editor edtIntent = intentPref.edit();
                    edtIntent.putString("book","book");
                    edtIntent.putString("chapter","chapter");
                    edtIntent.putString("liveId",liveId);
                    edtIntent.putString("documentId",documentId);
                    edtIntent.putString("tab","tab");
                    edtIntent.putInt("tabNumber",tabNumber);
                    edtIntent.putBoolean("test",true);
                    edtIntent.apply();

                }
            });
            RelativeLayout part3Rl = findViewById(R.id.part3);
            part3Rl.setVisibility(View.VISIBLE);

        }catch (Exception e){
//            Toast.makeText(TestSeriesViewCardDetails.this,"p7", Toast.LENGTH_SHORT).show();

        }
    }

    private static int getDefaultCheckBoxButtonDrawableResourceId(Context context) {
        // pre-Honeycomb has a different way of setting the CheckBox button drawable
        if (Build.VERSION.SDK_INT <= 10) return Resources.getSystem().getIdentifier("checkbox_empty", "drawable", "android");
        // starting with Honeycomb, retrieve the theme-based indicator as CheckBox button drawable
        TypedValue value = new TypedValue();
        context.getTheme().resolveAttribute(android.R.attr.listChoiceIndicatorMultiple, value, true);
        return value.resourceId;
    }

    public void setCardnumber(int cardnumberin) {
        this.cardnumber = cardnumberin;
        TextView Quest1 = findViewById(R.id.Quest);
        Quest1.setVisibility(View.VISIBLE);
        TextView cardNUm = findViewById(R.id.cardNum);
        cardNUm.setVisibility(View.VISIBLE);
        String txt = Integer.toString(cardnumberin);
        Quest1.setText(R.string.quest);
        cardNUm.setText(txt);
    }

    public void setMarkspos(int markspos) {
        this.markspos = markspos;
        String txt = Integer.toString(markspos);
        TextView markpostext = findViewById(R.id.marksobtainpositive);
        markpostext.setText(txt);
    }

    public void setMarksneg(int marksneg) {
        this.marksneg = marksneg;
        String txt = Integer.toString(marksneg);
        TextView marknegtext = findViewById(R.id.marksobtainNegative);
        marknegtext.setText(txt);
    }

    public void setNumericval(int numericval) {
        this.numericval = numericval;
        SharedPreferences prefs=getSharedPreferences("score", Context.MODE_PRIVATE);
        TextView select = findViewById(R.id.select);
        int index = prefs.getInt(documentId+"s",0);
        String spinNumber = CountryData.numberNames[index];
        String txt = "Your selection :" +spinNumber+"  Actual :"+numericval;
        select.setText(txt);
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
                            SharedPreferences prefLive = getSharedPreferences("live", Context.MODE_PRIVATE);
                            String liveId = prefLive.getString("liveId","live");
                            final DocumentReference userRef = db
                                    .collection("test_series").document("branches")
                                    .collection(liveId).document(documentId);//userId = user

                            //todo editorial data
                            DocumentReference editorialRef = userRef.collection("editorial").document("live");
                            editorialRef.get(Source.SERVER).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                @Override
                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                    LoadEditorial loadEditorial = documentSnapshot.toObject(LoadEditorial.class);
                                    String ansImageUrl = loadEditorial.getAnswerImage();
                                    answerImageUrl = ansImageUrl;
                                    Uri uri2 =  Uri.parse(ansImageUrl);
                                    String path2 = uri2.getPath();
                                    int cut2 = path2.lastIndexOf('/');
                                    String imageFilename2="";
                                    if (cut2 != -1) {
                                        imageFilename2 = path2.substring(cut2 + 1);
                                    }
                                    deleteStoredImage("images/examples/submissions",imageFilename2);
                                    userRef.delete();
                                }
                            });
                            onBackPressed();
                        }catch (Exception e){
                        }
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
        Intent intent = new Intent(context, PostTestData.class);
        SharedPreferences prefLive = getSharedPreferences("live", Context.MODE_PRIVATE);
        String liveId = prefLive.getString("liveId","live");

        intent.putExtra("liveId", liveId);
        intent.putExtra("markspos",""+ markspos);
        intent.putExtra("marksneg", ""+marksneg);
        intent.putExtra("chapterCode", chapterCode);
        intent.putExtra("tags", tags);
        intent.putExtra("text", text);
        intent.putExtra("textImageUrl", questionImageUrl);
        intent.putExtra("textAns", answertext);
        intent.putExtra("textImageUrlAns", answerImageUrl);
        intent.putExtra("option", option);
        intent.putExtra("numericval", (numericval+1));
        intent.putExtra("optionA", optionA);
        intent.putExtra("optionB", optionB);
        intent.putExtra("optionC", optionC);
        intent.putExtra("optionD", optionD);
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


    public void newActivityOpen(View v,int tabNumberVal){
        Context context = v.getContext();
        Intent intent = new Intent(context, CommentView.class);
        intent.putExtra("book","book");
        intent.putExtra("chapter", "chapter");
        intent.putExtra("liveId", liveId);
        intent.putExtra("tags", tags);
        intent.putExtra("text", text);
        intent.putExtra("textImageUrl", textImageUrl);
        intent.putExtra("option", option);
        intent.putExtra("optionA", optionA);
        intent.putExtra("optionB", optionB);
        intent.putExtra("optionC", optionC);
        intent.putExtra("optionD", optionD);
        intent.putExtra("tabNumber",tabNumberVal);
        intent.putExtra("documentId",documentId);
        intent.putExtra("tab","live");
        intent.putExtra("test", true);
        context.startActivity(intent);

    }


    @Override
    protected void onStart() {
        super.onStart();
        if(tabNumber==1){
//            addCard(book,chapter,mBlogList);
            adapterComment.startListening();
        }else if(tabNumber==2){
//            addEditorialCard(book,chapter,mBlogList);
            adapterEditorial.startListening();

        }else if(tabNumber==3){
//            addSubmissionCard(book,chapter,mBlogList);
            adapterSubmission.startListening();
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


    public void setDocumentId(String documentId) {
        this.documentId = documentId;
        SharedPreferences prefs=getSharedPreferences("score", Context.MODE_PRIVATE);
        TextView solve = findViewById(R.id.solvepercent);
        int marks = prefs.getInt(documentId,0);
        String totalscore = "Score :" +marks;
        solve.setText(totalscore);
    }

    public void setTags(String tags) {
        this.tags = tags;
        TextView tagsval = findViewById(R.id.tagsValues);
        tagsval.setText(tags);
    }

    public void setText(String text) {
        this.text = text;
        TextView textView = findViewById(R.id.conceptTitle);
        textView.setText(text);
    }

    public void setTextImageUrl(String textImageUrl) {
        this.textImageUrl = textImageUrl;
        final ImageView questionImg=findViewById(R.id.questionImage);
        AVLoadingIndicatorView progressBar=findViewById(R.id.progress_bar_questionImage);
        progressBar.show();
        PDFTools.picassoLoadImageAvl(questionImg , textImageUrl,progressBar);
        questionImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PDFTools.showPopupImage(questionImg,view);
            }
        });
    }



    public void setOption(boolean option) {
        this.option = option;
        RelativeLayout relativeLayout = findViewById(R.id.partspinner);
        LinearLayout linearLayoutOptions = findViewById(R.id.part2Linear);
        if(option){
            linearLayoutOptions.setVisibility(View.VISIBLE);
            relativeLayout.setVisibility(View.GONE);
        }
        else{
            linearLayoutOptions.setVisibility(View.GONE);
            relativeLayout.setVisibility(View.VISIBLE);
        }
    }

    public void setOptions(boolean optionA,boolean optionB,boolean optionC,boolean optionD) {
        this.optionA = optionA;
        this.optionB = optionB;
        this.optionC = optionC;
        this.optionD = optionD;
        SharedPreferences prefs=getSharedPreferences("score", Context.MODE_PRIVATE);

        CheckBox checkBoxA = findViewById(R.id.checkBoxA);
        CheckBox checkBoxB = findViewById(R.id.checkBoxB);
        CheckBox checkBoxC = findViewById(R.id.checkBoxC);
        CheckBox checkBoxD = findViewById(R.id.checkBoxD);

        checkBoxA.setButtonDrawable(optionA ? R.drawable.checkbox_tick : getDefaultCheckBoxButtonDrawableResourceId(TestSeriesViewCardDetails.this));
        CompoundButtonCompat.setButtonTintList(checkBoxA, optionA ? ColorStateList.valueOf(getResources().getColor(R.color.green)) :ColorStateList.valueOf(getResources().getColor(R.color.black)));

        checkBoxB.setButtonDrawable(optionB ? R.drawable.checkbox_tick : getDefaultCheckBoxButtonDrawableResourceId(TestSeriesViewCardDetails.this));
        CompoundButtonCompat.setButtonTintList(checkBoxB, optionB ? ColorStateList.valueOf(getResources().getColor(R.color.green)) :ColorStateList.valueOf(getResources().getColor(R.color.black)));

        checkBoxC.setButtonDrawable(optionC ? R.drawable.checkbox_tick : getDefaultCheckBoxButtonDrawableResourceId(TestSeriesViewCardDetails.this));
        CompoundButtonCompat.setButtonTintList(checkBoxC, optionC ? ColorStateList.valueOf(getResources().getColor(R.color.green)) :ColorStateList.valueOf(getResources().getColor(R.color.black)));

        checkBoxD.setButtonDrawable(optionD ? R.drawable.checkbox_tick : getDefaultCheckBoxButtonDrawableResourceId(TestSeriesViewCardDetails.this));
        CompoundButtonCompat.setButtonTintList(checkBoxD, optionD ? ColorStateList.valueOf(getResources().getColor(R.color.green)) :ColorStateList.valueOf(getResources().getColor(R.color.black)));

        boolean checka = prefs.getBoolean(documentId+"a",false);
        boolean checkb = prefs.getBoolean(documentId+"b",false);
        boolean checkc = prefs.getBoolean(documentId+"c",false);
        boolean checkd = prefs.getBoolean(documentId+"d",false);
        if(checka && checka != optionA){
            checkBoxA.setButtonDrawable(checka ? R.drawable.checkbox_cross : getDefaultCheckBoxButtonDrawableResourceId(TestSeriesViewCardDetails.this));
            CompoundButtonCompat.setButtonTintList(checkBoxA, checka ? ColorStateList.valueOf(getResources().getColor(R.color.red)) :ColorStateList.valueOf(getResources().getColor(R.color.black)));
        }
        if(checkb && checkb != optionB){
            checkBoxB.setButtonDrawable(checkb ? R.drawable.checkbox_cross : getDefaultCheckBoxButtonDrawableResourceId(TestSeriesViewCardDetails.this));
            CompoundButtonCompat.setButtonTintList(checkBoxB, checkb ? ColorStateList.valueOf(getResources().getColor(R.color.red)) :ColorStateList.valueOf(getResources().getColor(R.color.black)));
        }

        if(checkc && checkc != optionC){
            checkBoxC.setButtonDrawable(checkc ? R.drawable.checkbox_cross : getDefaultCheckBoxButtonDrawableResourceId(TestSeriesViewCardDetails.this));
            CompoundButtonCompat.setButtonTintList(checkBoxC, checkc ? ColorStateList.valueOf(getResources().getColor(R.color.red)) :ColorStateList.valueOf(getResources().getColor(R.color.black)));
        }

        if(checkd && checkd != optionD){
            checkBoxD.setButtonDrawable(checkd ? R.drawable.checkbox_cross : getDefaultCheckBoxButtonDrawableResourceId(TestSeriesViewCardDetails.this));
            CompoundButtonCompat.setButtonTintList(checkBoxD, checkd ? ColorStateList.valueOf(getResources().getColor(R.color.red)) :ColorStateList.valueOf(getResources().getColor(R.color.black)));
        }


    }


    public void defaultCardSettings(){

        TextView marks = findViewById(R.id.marks);
        TextView signpos = findViewById(R.id.signpositive);
        TextView markpos = findViewById(R.id.marksobtainpositive);
        TextView slash = findViewById(R.id.marksobtainspace);
        TextView signneg = findViewById(R.id.signNegative);
        TextView markneg = findViewById(R.id.marksobtainNegative);

//        TextView solve = view.findViewById(R.id.solvepercent);
        TextView percentsign = findViewById(R.id.percent);
        TextView tagname = findViewById(R.id.tagsName);
        TextView a = findViewById(R.id.optionTextA);
        TextView b = findViewById(R.id.optionTextB);
        TextView c = findViewById(R.id.optionTextC);
        TextView d = findViewById(R.id.optionTextD);
        TextView comment = findViewById(R.id.comment);
        TextView editorial = findViewById(R.id.editorialText);
        TextView submission = findViewById(R.id.submissionText);
        TextView select = findViewById(R.id.select);
        RelativeLayout tagslyout = findViewById(R.id.tagslayout);

        marks.setText(R.string.markss);
        signpos.setText(R.string.positive);
        markpos.setText(R.string.like);
        slash.setText(R.string.slash);
        signneg.setText(R.string.negative);
        markneg.setText(R.string.like);

        tagname.setText(R.string.tagsName);
//        solve.setText(R.string.solve);
        percentsign.setText(R.string.percentsign);
        a.setText("A");
        b.setText("B");
        c.setText("C");
        d.setText("D");
        comment.setText(R.string.comment);
        editorial.setText(R.string.editorial);
        submission.setText(R.string.submission);
        select.setText(R.string.selectSpinner);
        SharedPreferences prefAdmin = getSharedPreferences("ActivityPREF", Context.MODE_PRIVATE);
        String id=prefAdmin.getString("id","");
        if(id.equals("vinayupadhyay02001@gmail.com") || id.equals("naresh03961999@gmail.com") || id.equals("rakshitaupadhyay1497@gmail.com") ) {
            tagslyout.setVisibility(View.VISIBLE);
        }else{
            tagslyout.setVisibility(View.GONE);
        }

        // find username
        Link linkUsername = new Link(Pattern.compile("(@\\w+)"))
                .setUnderlined(false)
                .setTextColor(R.color.blue)
                .setTextStyle(Link.TextStyle.BOLD)
                .setClickListener(new Link.OnClickListener() {
                    @Override
                    public void onClick(String text) {
                        // do something
                        Toast.makeText(TestSeriesViewCardDetails.this, "Clicked username: " +text, Toast.LENGTH_SHORT).show();

                    }
                });

        links = new ArrayList<>();
        links.add(linkUsername);

    }

    private void setEditorialText(String etext){
        AVLoadingIndicatorView progressBar=findViewById(R.id.progress_bar_user_image_editoral);
        progressBar.hide();
        TextView etxt = findViewById(R.id.text_editoral);
        etxt.setText(etext);
    }
    private void setEditorialImage(String eImage){
        final ImageView solutionImg=findViewById(R.id.solutionImage_editoral);
        AVLoadingIndicatorView progressBar=findViewById(R.id.progress_bar_solutionImage_editoral);
        progressBar.show();
        PDFTools.picassoLoadImageAvl(solutionImg , eImage,progressBar);
        solutionImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PDFTools.showPopupImage(solutionImg,view);
            }
        });

    }



    public void addCard(String liveIdval,String documentIdval,RecyclerView recyclerView){
        final AVLoadingIndicatorView avLoadingIndicatorView = findViewById(R.id.progress_bar_nested_recyclerview);
        avLoadingIndicatorView.show();

        final FirebaseFirestore db= FirebaseFirestore.getInstance();

        CollectionReference chaptersRef = db
                .collection("test_series").document("branches")
                .collection(liveIdval).document(documentIdval)
                .collection("comments");
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
                return new LoadCommentsViewHolder(view,false,null);
            }
        };
        recyclerView.setAdapter(adapterComment);
        adapterComment.startListening();
        adapterComment.notifyDataSetChanged();
    }


    public void addEditorialCard(String liveIdval,String documentIdval,RecyclerView recyclerView){
        final AVLoadingIndicatorView avLoadingIndicatorView = findViewById(R.id.progress_bar_nested_recyclerview);
        avLoadingIndicatorView.show();

        final FirebaseFirestore db= FirebaseFirestore.getInstance();

        DocumentReference userRef = db
                .collection("test_series").document("branches")
                .collection(liveIdval).document(documentIdval);

        //todo editorial data
        DocumentReference editorialRef = userRef.collection("editorial").document("live");
        editorialRef.get(Source.SERVER).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                LoadEditorial loadEditorial = documentSnapshot.toObject(LoadEditorial.class);
                setEditorialText(loadEditorial.getText());
                setEditorialImage(loadEditorial.getAnswerImage());
                avLoadingIndicatorView.hide();
            }
        });

        CollectionReference editorialCommentRef = userRef.collection("editorialcomment");

        //todo editorial comments
        Query query = editorialCommentRef.orderBy("votes",Query.Direction.DESCENDING).limit(500);
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
                return new LoadCommentsViewHolder(view,false,null);
            }
        };
        recyclerView.setAdapter(adapterEditorial);
        adapterEditorial.startListening();
        adapterEditorial.notifyDataSetChanged();
    }


    public void addSubmissionCard(String liveIdval,String documentIdval,RecyclerView recyclerView){
        final AVLoadingIndicatorView avLoadingIndicatorView = findViewById(R.id.progress_bar_nested_recyclerview);
        avLoadingIndicatorView.show();

        db = FirebaseFirestore.getInstance();
        CollectionReference chaptersRef = db
                .collection("test_series").document("branches")
                .collection(liveIdval).document(documentIdval)
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