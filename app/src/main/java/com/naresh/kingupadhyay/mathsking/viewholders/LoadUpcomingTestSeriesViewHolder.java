package com.naresh.kingupadhyay.mathsking.viewholders;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.naresh.kingupadhyay.mathsking.R;
import com.naresh.kingupadhyay.mathsking.activities.PostUpcomingTest;
import com.naresh.kingupadhyay.mathsking.activities.ViewCardTestSeriesDataUpload;
import com.naresh.kingupadhyay.mathsking.network.LoadLiveTestSeries;
import com.naresh.kingupadhyay.mathsking.network.LoadPreviousTestSeries;

import java.text.SimpleDateFormat;
import java.util.Date;

import static java.lang.Math.abs;

public class LoadUpcomingTestSeriesViewHolder extends RecyclerView.ViewHolder {
    private View view;
    private String topic;
    private String title;
    private int questions;
    private int marks;
    private int timeSeconds;
    private Timestamp timestamp;
    private String documentId;

    public LoadUpcomingTestSeriesViewHolder(View itemView) {
        super(itemView);
        view = itemView;
        SharedPreferences prefAdmin = view.getContext().getSharedPreferences("ActivityPREF", Context.MODE_PRIVATE);
        String id=prefAdmin.getString("id","");
        TextView editUpcoming = view.findViewById(R.id.editUpcoming);
        editUpcoming.setText(R.string.edit);
        if(id.equals("vinayupadhyay02001@gmail.com") || id.equals("naresh03961999@gmail.com") || id.equals("rakshitaupadhyay1497@gmail.com") ) {
            editUpcoming.setVisibility(View.VISIBLE);
        }else{
            editUpcoming.setVisibility(View.GONE);
        }
        editUpcoming.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popupMenuEditDelete(view);
            }
        });
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
                            editActivity(v);
                        }catch (Exception e){
                        }
                        break;
                    case R.id.delete_popup :
                        try {
                            FirebaseFirestore db= FirebaseFirestore.getInstance();
                            final DocumentReference upcomingRef = db
                                    .collection("test_series").document("branches")
                                    .collection("upcoming").document(documentId);
                                    upcomingRef.delete();
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
        Intent intent = new Intent(context, PostUpcomingTest.class);
        intent.putExtra("title",title);
        intent.putExtra("questions",""+questions);
        intent.putExtra("marks",""+marks);
        intent.putExtra("time",""+((int)(timeSeconds/60)));
        intent.putExtra("documentId",documentId);
        context.startActivity(intent);
    }

    public void fillDataActivity(View v){
        Context context = v.getContext();
        Intent intent = new Intent(context, ViewCardTestSeriesDataUpload.class);
        intent.putExtra("title",title);
        intent.putExtra("questions",questions);
        intent.putExtra("liveId",documentId);
        intent.putExtra("marks",marks);
        context.startActivity(intent);
    }



    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
        Button starttest = itemView.findViewById(R.id.attempt_Button);
        SimpleDateFormat sfd = new SimpleDateFormat("EEEE MMM dd, hh:mm a");
        String strDate = sfd.format(timestamp.toDate());
        starttest.setText(strDate);
        starttest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences prefAdmin = view.getContext().getSharedPreferences("ActivityPREF", Context.MODE_PRIVATE);
                String id=prefAdmin.getString("id","");
                if(id.equals("vinayupadhyay02001@gmail.com") || id.equals("naresh03961999@gmail.com") || id.equals("rakshitaupadhyay1497@gmail.com") ) {
                    fillDataActivity(view);
                }
            }
        });
        try{
            Date dateServer = timestamp.toDate();//(Dates are in decreasing order)
            Date dateNow = new Date();
            long diff = dateServer.getTime() - dateNow.getTime();
//                        Toast.makeText(viewFragment.getContext(),""+diff, Toast.LENGTH_LONG).show();

            if((diff/1000)<=0){
                //todo test started
                if(abs(diff/1000)>=timeSeconds){
                    LoadPreviousTestSeries loadPreviousTestSeries = new LoadPreviousTestSeries();
                    try{
                        FirebaseFirestore db= FirebaseFirestore.getInstance();
                        DocumentReference liveTest= db
                                .collection("test_series").document("branches")
                                .collection("previous").document(documentId);
                        loadPreviousTestSeries.setTitle(title);
                        loadPreviousTestSeries.setTimestamp(new Timestamp(new Date()));
                        loadPreviousTestSeries.setQuestions(questions);
                        loadPreviousTestSeries.setMarks(marks);
                        loadPreviousTestSeries.setTimeSeconds(timeSeconds);
                        liveTest.set(loadPreviousTestSeries.toMap(), SetOptions.merge());

                    }catch (Exception e){
                    }

                    FirebaseFirestore db= FirebaseFirestore.getInstance();
                    DocumentReference previous= db
                            .collection("test_series").document("branches")
                            .collection("upcoming").document(documentId);
                    previous.delete();

                }else{
                    String liveMsg = "Live Now, check in Live section";
                    starttest.setText(liveMsg);
                }
            }

        }catch (Exception e){}

    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
        SharedPreferences prefLive = view.getContext().getSharedPreferences("live", Context.MODE_PRIVATE);
        final SharedPreferences.Editor edt = prefLive.edit();
        edt.putString("liveId",documentId);
        edt.apply();

    }

    public void setQuestions(int questions) {
        this.questions = questions;
        TextView questionval = view.findViewById(R.id.questionsval);
        TextView questiontext = view.findViewById(R.id.questions);
        questiontext.setText(R.string.question);
        String txt = Integer.toString(questions);
        questionval.setText(txt);
    }

    public void setMarks(int marks) {
        this.marks = marks;
        TextView marksval = view.findViewById(R.id.marksval);
        TextView mark = view.findViewById(R.id.marks);
        mark.setText(R.string.marks);
        String txt = Integer.toString(marks);
        marksval.setText(txt);
    }

    public void setTimeSeconds(int timeSeconds) {
        this.timeSeconds = timeSeconds;
        TextView timeval = view.findViewById(R.id.timeval);
        TextView time1 = view.findViewById(R.id.time1);
        time1.setText(R.string.timem);
        String txt = Long.toString(Math.round((timeSeconds*1.0)/(60*1.0)));
        timeval.setText(txt);
    }

    public void setTopic(int topicin) {
        this.topic = Integer.toString(topicin);
        if(topicin==1){
            //todo update live
            LoadLiveTestSeries loadLiveTestSeries = new LoadLiveTestSeries();

            try{
                FirebaseFirestore db= FirebaseFirestore.getInstance();
                DocumentReference liveTest= db
                        .collection("test_series").document("branches")
                        .collection("live").document("live");

                loadLiveTestSeries.setLiveId(documentId);
                loadLiveTestSeries.setTitle(title);
                loadLiveTestSeries.setTimestamp(timestamp);
                loadLiveTestSeries.setQuestions(questions);
                loadLiveTestSeries.setMarks(marks);
                loadLiveTestSeries.setTimeSeconds(timeSeconds);
                liveTest.set(loadLiveTestSeries.toMap(), SetOptions.merge());

            }catch (Exception e){
            }

        }
        TextView test1 = view.findViewById(R.id.test1);
        TextView topicnum = view.findViewById(R.id.topicNum);
        topicnum.setText(topic);
        test1.setText(R.string.test);

    }

    public void setTitle(String title) {
        this.title = title;
        TextView titleconcept = view.findViewById(R.id.conceptTitle);
        titleconcept.setText(title);
    }

}

