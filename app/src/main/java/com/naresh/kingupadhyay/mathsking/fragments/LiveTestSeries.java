package com.naresh.kingupadhyay.mathsking.fragments;

import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.apradanas.simplelinkabletext.Link;
import com.apradanas.simplelinkabletext.LinkableEditText;
import com.apradanas.simplelinkabletext.LinkableTextView;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.firestore.Source;
import com.naresh.kingupadhyay.mathsking.R;
import com.naresh.kingupadhyay.mathsking.activities.Basic_activity;
import com.naresh.kingupadhyay.mathsking.activities.CourseDetails;
import com.naresh.kingupadhyay.mathsking.activities.GaveTest;
import com.naresh.kingupadhyay.mathsking.activities.ReadInstructions;
import com.naresh.kingupadhyay.mathsking.activities.Short_note;
import com.naresh.kingupadhyay.mathsking.activities.TestResult;
import com.naresh.kingupadhyay.mathsking.network.LoadInstructions;
import com.naresh.kingupadhyay.mathsking.network.LoadLiveTestSeries;
import com.naresh.kingupadhyay.mathsking.network.UserDetails;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import static android.content.Context.CLIPBOARD_SERVICE;
import static java.lang.Math.abs;

public class LiveTestSeries extends Fragment {

    private String title;
    private Timestamp timestamp;
    private String liveId;
    private int testTimingSeconds;
    private int questions;
    private int marks;
    private CountDownTimer timer;
    private long diff;
    private Button startondate;
    private Button startnow;
    private View viewFragment;
    private String instuctionData = "Wait Instructions are Loading....";

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        viewFragment = inflater.inflate(R.layout.test_series_live_card_view, container, false);
        startondate = viewFragment.findViewById(R.id.startondate);
        startnow = viewFragment.findViewById(R.id.startnow);
        startnow.setVisibility(View.GONE);
        final SharedPreferences pref = viewFragment.getContext().getSharedPreferences("skip", Context.MODE_PRIVATE);
        final SharedPreferences.Editor edt = pref.edit();
        edt.putBoolean("live",false);
        edt.apply();
        startondate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(viewFragment.getContext(),"Please wait for Right Time", Toast.LENGTH_LONG).show();

            }
        });
        startnow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //server computation
                boolean finish = pref.getBoolean("liveStartFlag"+liveId,false);
                if(!finish){
                    edt.putBoolean("live",true);
                    edt.putString("liveId",liveId);
                    edt.putBoolean("liveStartFlag"+liveId,true);
                    edt.apply();
                    Date dateServer = timestamp.toDate();//(Dates are in decreasing order)
                    Date dateNow = new Date();
                    diff = dateServer.getTime() - dateNow.getTime();

                    long timeRemaining = testTimingSeconds*1000+(diff)+10000;
                    Intent intent = new Intent(viewFragment.getContext(), GaveTest.class);
                    intent.putExtra("title",title);
                    intent.putExtra("testTimingSeconds",testTimingSeconds);
                    intent.putExtra("timeRemaining",timeRemaining);
                    intent.putExtra("liveId",liveId);
                    intent.putExtra("questions",questions);
                    intent.putExtra("marks",marks);
                    startActivity(intent);

                }else{
                    final String nextL = "Wait for Next Live Exam";
                    startnow.setText(nextL);
                    Toast.makeText(viewFragment.getContext(),"You have finished this test", Toast.LENGTH_LONG).show();
                }
            }
        });
        Button instructon = viewFragment.findViewById(R.id.instruction);
        instructon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try{
                    timer.cancel();
                }catch(Exception e){
                }
                SharedPreferences prefAdmin = view.getContext().getSharedPreferences("ActivityPREF", Context.MODE_PRIVATE);
                String id=prefAdmin.getString("id","");
                if(id.equals("vinayupadhyay02001@gmail.com") || id.equals("naresh03961999@gmail.com") || id.equals("rakshitaupadhyay1497@gmail.com") ) {
                    showPopupWriteInstruction(view);
                }else{
                    Context context = view.getContext();
                    Intent intent = new Intent(context, ReadInstructions.class);
                    intent.putExtra("flag",false);
                    context.startActivity(intent);
                }

            }
        });

        return viewFragment;
    }

    public void startTimer(final long finish, long tick, final Button startondate, final Button startnow) {
        timer = new CountDownTimer(finish, tick) {
            public void onTick(long millisUntilFinished) {
                // buttonStartVerification.setVisibility(View.INVISIBLE);
                long remainedSecs = millisUntilFinished / 1000;
                startondate.setText("Start in " + (remainedSecs / 3600) + ":" + ((remainedSecs / 60)%60) + ":" + (remainedSecs % 60));// manage it accordign to you
            }

            public void onFinish() {
                startnow.setVisibility(View.VISIBLE);
                startondate.setVisibility(View.GONE);
                cancel();
            }
        }.start();
    }

    @Override
    public void onResume() {
        super.onResume();
        final String maxtime = "Update in Max 5 hours";
        startondate.setText(maxtime);
        startnow.setText(R.string.startnow);
        try{
            final FirebaseFirestore db= FirebaseFirestore.getInstance();
            final DocumentReference docRef = db
                    .collection("test_series").document("branches")
                    .collection("live").document("live");

            docRef.get(Source.SERVER).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    LoadLiveTestSeries loadLiveTestSeries = documentSnapshot.toObject(LoadLiveTestSeries.class);
                    title = loadLiveTestSeries.getTitle();
                    timestamp = loadLiveTestSeries.getTimestamp();
                    liveId = loadLiveTestSeries.getLiveId();
                    testTimingSeconds = loadLiveTestSeries.getTimeSeconds();
                    questions = loadLiveTestSeries.getQuestions();
                    marks = loadLiveTestSeries.getMarks();
                    try{
                        SimpleDateFormat sfd = new SimpleDateFormat("EEEE MMM dd, hh:mm a");
                        String strDate = sfd.format(timestamp.toDate());
                        String txt = "Start on "+strDate;
                        startondate.setText(txt);
                        Date dateServer = timestamp.toDate();//(Dates are in decreasing order)
                        Date dateNow = new Date();
                        diff = dateServer.getTime() - dateNow.getTime();
//                        Toast.makeText(viewFragment.getContext(),""+diff, Toast.LENGTH_LONG).show();

                        if(abs(diff/1000)<=testTimingSeconds && (diff/1000)<=0){
                            final SharedPreferences pref = viewFragment.getContext().getSharedPreferences("skip", Context.MODE_PRIVATE);
                            boolean finish = pref.getBoolean("liveStartFlag"+liveId,false);
                            if(finish){
                                String nextL = "Wait for Next Live Exam";
                                startnow.setText(nextL);
                            }
                            startnow.setVisibility(View.VISIBLE);
                            startondate.setVisibility(View.GONE);
                        }else if((diff/1000)<=(24*60*60) && (diff/1000)>=0){
                            startTimer(diff,1000,startondate,startnow);
                        }else if(abs(diff/1000)>testTimingSeconds && (diff/1000)<0){
                            startondate.setText(maxtime);
                            startnow.setVisibility(View.GONE);
                            startondate.setVisibility(View.VISIBLE);
                        }

                    }catch (Exception e){}

                }
            });
        }catch (Exception e){}

    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
        try{
            timer.cancel();
        }catch (Exception e){}
    }

    public void showPopupWriteInstruction(final View view){
        final Dialog myDialog = new Dialog(view.getContext());
        myDialog.setContentView(R.layout.popup_instrucitons_test_series);
        myDialog.setCancelable(true);
        Link linkUrl = new Link(Pattern.compile("(([A-Za-z]{3,9}:(?://)?)(?:[-;:&=+$,\\w]+@)?[A-Za-z0-9.-]+|(?:www\\.|[-;:&=+$,\\w]+@)[A-Za-z0-9.-]+)((?:/[+~%/.\\w-]*)?\\??(?:[-+=&;%@.\\w]*)#?(?:[.!/\\\\\\w]*))?"))
                .setUnderlined(true)
                .setTextColor(R.color.star10)
                .setTextStyle(Link.TextStyle.BOLD)
                .setClickListener(new Link.OnClickListener() {
                    @Override
                    public void onClick(String text) {
                        // do something
                        Context context = view.getContext();
//                        Toast.makeText(context, "Clicked link: " +text, Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(context, Basic_activity.class);
                        intent.putExtra("pdfUrl",text);
                        intent.putExtra("titleNoti","Clicked Link");
                        context.startActivity(intent);

                    }
                });


        List<Link> links = new ArrayList<>();
        links.add(linkUrl);
        final CardView editTextCard = (CardView) myDialog.findViewById(R.id.editTextInstruciton);
        final LinkableTextView textView = myDialog.findViewById(R.id.textLinkableTextViewInstruciton);
        final LinkableEditText editText = (LinkableEditText)myDialog.findViewById(R.id.editTextLinkableEditInstruciton);
        textView.setVisibility(View.GONE);
        editTextCard.setVisibility(View.VISIBLE);
        editText.addLinks(links);

        editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(editText.getText().toString().isEmpty() ||editText.getText().toString()==null)
                    editText.setText(" ", TextView.BufferType.EDITABLE);
                if(b){
                    editText.setPressed(true);
                    editText.setSelection(editText.getText().length());
                }else{
                    closeKeyBoard(view);
                }
            }
        });

        Button postButton = (Button) myDialog.findViewById(R.id.postButton);
        postButton.setText("Send");

        myDialog.show();
        postButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadInstruction(editText,myDialog);
            }
        });

    }

    public void uploadInstruction(LinkableEditText linkableEditText,Dialog dialog){

        String tagstxt = linkableEditText.getText().toString().trim();
        if (tagstxt.isEmpty()) {
            linkableEditText.setError("Write something!");
            linkableEditText.requestFocus();
            return;
        }

        try{
            DatabaseReference favourites= FirebaseDatabase.getInstance().getReference().child("instructions");
            favourites.child("live").child("textmsg").setValue(tagstxt);
            dialog.dismiss();

        }catch (Exception e){
        }
    }



    private void closeKeyBoard(View view){
        if (view != null){
            InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }




}
