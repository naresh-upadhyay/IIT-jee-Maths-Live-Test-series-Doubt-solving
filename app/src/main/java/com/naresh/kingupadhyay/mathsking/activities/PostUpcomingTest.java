package com.naresh.kingupadhyay.mathsking.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatRatingBar;

import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.apradanas.simplelinkabletext.Link;
import com.apradanas.simplelinkabletext.LinkableEditText;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.firestore.Source;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.naresh.kingupadhyay.mathsking.R;
import com.naresh.kingupadhyay.mathsking.captue.ImageCroper;
import com.naresh.kingupadhyay.mathsking.network.LoadEditorial;
import com.naresh.kingupadhyay.mathsking.network.LoadUpcomingTestSeries;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class PostUpcomingTest extends AppCompatActivity {

    private FirebaseFirestore db;
    private List<Link> links;
    private LinkableEditText nameTest;
    private LinkableEditText questionTest;
    private LinkableEditText marksTest;
    private LinkableEditText minutestest;
    private String nameTesttxtval;
    private String questionTesttxtval;
    private String marksTesttxtval;
    private String minutestesttxtval;
    private TimePicker timePicker;
    private DatePicker datePicker;
    private String currentUserId;
    private String documentId;
    private DocumentReference upcomingRef;
    private Calendar calendar = Calendar.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_upcoming_test);

        SharedPreferences prefUseId = getSharedPreferences("ActivityPREF", Context.MODE_PRIVATE);
        currentUserId=prefUseId.getString("uid","user");

        try{
            Bundle bundle = getIntent().getExtras();
            nameTesttxtval = bundle.getString("title","");
            questionTesttxtval = bundle.getString("questions","");
            marksTesttxtval = bundle.getString("marks","");
            minutestesttxtval = bundle.getString("time","");
            documentId = bundle.getString("documentId","");
        }catch (Exception e){
            documentId = "";
        }

        db= FirebaseFirestore.getInstance();
        nameTest = findViewById(R.id.nameTestLinkableEdit);
        questionTest = findViewById(R.id.questionsLinkableEdit);
        marksTest = findViewById(R.id.marksLinkableEdit1);
        minutestest = findViewById(R.id.minutesLinkableEdit2);

        try{
            nameTest.setText(nameTesttxtval);
            questionTest.setText(questionTesttxtval);
            marksTest.setText(marksTesttxtval);
            minutestest.setText(minutestesttxtval);
        }catch (Exception e){
        }

        Link linkUsername = new Link(Pattern.compile("(@\\w+)"))
                .setUnderlined(false)
                .setTextColor(R.color.blue)
                .setTextStyle(Link.TextStyle.BOLD)
                .setClickListener(new Link.OnClickListener() {
                    @Override
                    public void onClick(String text) {
                        // do something
                        Toast.makeText(PostUpcomingTest.this, "Clicked username: " +text, Toast.LENGTH_SHORT).show();

                    }
                });

        links = new ArrayList<>();
        links.add(linkUsername);
        nameTest.addLinks(links);
        nameTest.setText(nameTesttxtval,TextView.BufferType.EDITABLE);


        //name
        nameTest.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(nameTest.getText().toString().isEmpty() ||nameTest.getText().toString()==null)
                    nameTest.setText(" ", TextView.BufferType.EDITABLE);
                if(b){
                    nameTest.setPressed(true);
                    nameTest.setSelection(nameTest.getText().length());
                }else{
                    closeKeyBoard(view);
                }
            }
        });

        //questions
        questionTest.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(questionTest.getText().toString().isEmpty() ||questionTest.getText().toString()==null)
                    questionTest.setText(" ", TextView.BufferType.EDITABLE);
                if(b){
                    questionTest.setPressed(true);
                    questionTest.setSelection(questionTest.getText().length());
                }else{
                    closeKeyBoard(view);
                }
            }
        });
        //marks
        marksTest.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(marksTest.getText().toString().isEmpty() ||marksTest.getText().toString()==null)
                    marksTest.setText(" ", TextView.BufferType.EDITABLE);
                if(b){
                    marksTest.setPressed(true);
                    marksTest.setSelection(marksTest.getText().length());
                }else{
                    closeKeyBoard(view);
                }
            }
        });
        //marks
        minutestest.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(minutestest.getText().toString().isEmpty() ||minutestest.getText().toString()==null)
                    minutestest.setText(" ", TextView.BufferType.EDITABLE);
                if(b){
                    minutestest.setPressed(true);
                    minutestest.setSelection(minutestest.getText().length());
                }else{
                    closeKeyBoard(view);
                }
            }
        });

        Button postButton = findViewById(R.id.postButton_post_example);
        postButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String nametestxt = nameTest.getText().toString().trim();
                String questiontxt = questionTest.getText().toString().trim();
                String markstxt = marksTest.getText().toString().trim();
                String minutestxt = minutestest.getText().toString().trim();
                if(nametestxt.isEmpty() || questiontxt.isEmpty()|| markstxt.isEmpty()|| minutestxt.isEmpty()){
                    if (nametestxt.isEmpty()) {
                        nameTest.setError("Write something!");
                    }
                    if (questiontxt.isEmpty()) {
                        questionTest.setError("Write something!");
                    }
                    if (markstxt.isEmpty()) {
                        marksTest.setError("Write something!");
                    }
                    if (minutestxt.isEmpty()) {
                        minutestest.setError("Write something!");
                    }

                    if (nametestxt.isEmpty()) {
                        nameTest.requestFocus();
                    }else if(questiontxt.isEmpty()){
                        questionTest.requestFocus();
                    }else if(markstxt.isEmpty()){
                        marksTest.requestFocus();
                    }else{
                        minutestest.requestFocus();
                    }
                    return;
                }

                try{
                    try{
                        FirebaseFirestore db= FirebaseFirestore.getInstance();
                        if(!documentId.isEmpty() && documentId != null){
                            upcomingRef= db
                                    .collection("test_series").document("branches")
                                    .collection("upcoming").document(documentId);
                        }else {
                            upcomingRef = db
                                    .collection("test_series").document("branches")
                                    .collection("upcoming").document();
                        }
                        }catch (Exception e){
                        Toast.makeText(PostUpcomingTest.this, "Report 1" , Toast.LENGTH_SHORT).show();

                    }

                    try{
                        //todo upload data
                        timePicker = findViewById(R.id.timePicker);
                        datePicker = findViewById(R.id.datePicker1);
                        calendar = new GregorianCalendar(datePicker.getYear(),
                                datePicker.getMonth(),
                                datePicker.getDayOfMonth(),
                                timePicker.getCurrentHour(),
                                timePicker.getCurrentMinute());

                    }catch (Exception e){
                        Toast.makeText(PostUpcomingTest.this, "Report 2" , Toast.LENGTH_SHORT).show();
                    }

                    try{

                        LoadUpcomingTestSeries loadUpcomingTestSeries = new LoadUpcomingTestSeries();
                        loadUpcomingTestSeries.setTitle(nametestxt);
                        loadUpcomingTestSeries.setQuestions(Integer.parseInt(questiontxt));
                        loadUpcomingTestSeries.setMarks(Integer.parseInt(markstxt));
                        loadUpcomingTestSeries.setTimestamp(new Timestamp(calendar.getTime()));
                        loadUpcomingTestSeries.setTimeSeconds(Integer.parseInt(minutestxt)*60);
                        upcomingRef.set(loadUpcomingTestSeries.toMap(), SetOptions.merge());

                    }catch (Exception e){
                        Toast.makeText(PostUpcomingTest.this, "Report 3" , Toast.LENGTH_SHORT).show();
                    }

                    onBackPressed();

                }catch (Exception e){
                }


            }
        });



    }
    @Override
    protected void onResume() {
        super.onResume();
    }


    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }


    private void closeKeyBoard(View view){
        if (view != null){
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
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
