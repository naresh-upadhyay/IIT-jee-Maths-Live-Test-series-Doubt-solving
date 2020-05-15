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

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.naresh.kingupadhyay.mathsking.R;
import com.naresh.kingupadhyay.mathsking.activities.GaveTest;
import com.naresh.kingupadhyay.mathsking.activities.PostUpcomingTest;
import com.naresh.kingupadhyay.mathsking.activities.TestResult;

public class LoadPreviousTestSeriesViewHolder extends RecyclerView.ViewHolder {
    private View view;
    private String topic;
    private String title;
    private int questions;
    private int marks;
    private int timeSeconds;
    private int score;
    private String documentId;

    public LoadPreviousTestSeriesViewHolder(View itemView) {
        super(itemView);
        view = itemView;
        TextView scoreval = view.findViewById(R.id.scoredval);
        TextView score = view.findViewById(R.id.score);
        scoreval.setText("0");
        score.setText(R.string.score);
        SharedPreferences pref = view.getContext().getSharedPreferences("skip", Context.MODE_PRIVATE);
        final SharedPreferences.Editor edt = pref.edit();
        edt.putBoolean("live",false);
        edt.apply();

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


//        MaterialButton
        Button starttest = itemView.findViewById(R.id.attempt_Button);
        starttest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edt.putString("liveId",documentId);
                edt.putBoolean("leaderboard",false);
                edt.apply();

                SharedPreferences prefsScore=view.getContext().getSharedPreferences("score", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = prefsScore.edit();
                editor.clear().apply();

                Context context = v.getContext();
                Intent intent = new Intent(context, GaveTest.class);
                intent.putExtra("title",title);
                intent.putExtra("testTimingSeconds",timeSeconds);
                intent.putExtra("timeRemaining",(long)(timeSeconds*1000));
                intent.putExtra("liveId",documentId);
                intent.putExtra("questions",questions);
                intent.putExtra("marks",marks);
                context.startActivity(intent);
            }
        });

        //leaderboard
        Button leaderboard = itemView.findViewById(R.id.leaderboard);
        leaderboard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                edt.putString("liveId",documentId);
                edt.putBoolean("leaderboard",true);
                edt.apply();
                Intent intent = new Intent(view.getContext(), TestResult.class);
                intent.putExtra("title",title);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                view.getContext().startActivity(intent);

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
                                    .collection("previous").document(documentId);
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


    public void setDocumentId(String documentId) {
        this.documentId = documentId;
        SharedPreferences scoreDoc=view.getContext().getSharedPreferences(documentId, Context.MODE_PRIVATE);
        String sval = scoreDoc.getString("liveScoreTotal","0");
        TextView scoreval = view.findViewById(R.id.scoredval);
        scoreval.setText(sval);

    }

    public void setQuestions(int questions) {
        TextView questionval = view.findViewById(R.id.questionsval);
        TextView questiontext = view.findViewById(R.id.questions);
        questiontext.setText(R.string.question);
        String txt = Integer.toString(questions);
        questionval.setText(txt);
        this.questions = questions;
    }

    public void setMarks(int marks) {
        TextView marksval = view.findViewById(R.id.marksval);
        TextView mark = view.findViewById(R.id.marks);
        mark.setText(R.string.marks);
        String txt = Integer.toString(marks);
        marksval.setText(txt);
        this.marks = marks;
    }

    public void setTimeSeconds(int timeSeconds) {
        TextView timeval = view.findViewById(R.id.timeval);
        TextView time1 = view.findViewById(R.id.time1);
        time1.setText(R.string.timem);
        String txt = Long.toString(Math.round((timeSeconds*1.0)/(60*1.0)));
        timeval.setText(txt);
        this.timeSeconds = timeSeconds;
    }

    public void setTopic(int topicin) {
        this.topic = Integer.toString(topicin);
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

    public void setScore(int score) {
        this.score = score;
    }
}
