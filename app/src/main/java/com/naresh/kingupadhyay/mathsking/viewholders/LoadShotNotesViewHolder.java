package com.naresh.kingupadhyay.mathsking.viewholders;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.TypedValue;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.PopupMenu;
import androidx.core.widget.CompoundButtonCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.naresh.kingupadhyay.mathsking.Database.PDFTools;
import com.naresh.kingupadhyay.mathsking.R;
import com.naresh.kingupadhyay.mathsking.UiMaterial.LoginOption;
import com.naresh.kingupadhyay.mathsking.activities.Basic_activity;
import com.naresh.kingupadhyay.mathsking.activities.PostConcept;
import com.naresh.kingupadhyay.mathsking.activities.PostShortNotes;
import com.naresh.kingupadhyay.mathsking.activities.PostUpcomingTest;

import static android.net.Uri.fromFile;
import static com.naresh.kingupadhyay.mathsking.Database.PDFTools.openPDF;
import static com.naresh.kingupadhyay.mathsking.Database.PDFTools.tempFile;

public class LoadShotNotesViewHolder extends RecyclerView.ViewHolder {
    private View view;
    private int topic;
    private String title;
    private String conceptPdfUrl;
    private String time;
    private boolean skip;
    private String NAME="name";
    //    private String KEY;
    private DatabaseReference favourites;
    private Drawable draw;



    public LoadShotNotesViewHolder(View itemView) {
        super(itemView);
        view = itemView;
        TextView hours = view.findViewById(R.id.hours);
        TextView hoursh = view.findViewById(R.id.hoursh);
        TextView editConcept = view.findViewById(R.id.editConcept);
        hours.setText(R.string.time);
        hoursh.setText(R.string.h);
        editConcept.setText(R.string.edit);
        SharedPreferences prefAdmin = view.getContext().getSharedPreferences("ActivityPREF", Context.MODE_PRIVATE);
        String id=prefAdmin.getString("id","");
        if(id.equals("vinayupadhyay02001@gmail.com") || id.equals("naresh03961999@gmail.com") || id.equals("rakshitaupadhyay1497@gmail.com") ) {
            editConcept.setVisibility(View.VISIBLE);
        }else{
            editConcept.setVisibility(View.GONE);
        }

        editConcept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popupMenuEditDelete(view);
            }
        });
//        Context contextParent = itemView.getContext();
        RelativeLayout concept = itemView.findViewById(R.id.part1);
        concept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = v.getContext();

                if ( tempFile(context,title).isFile()) {
                    // If we have downloaded the file before, just go ahead and show it.
                    openPDF( context, fromFile( tempFile(context,title) ) );
                }else{

                    Intent intent = new Intent(context, Basic_activity.class);
                    intent.putExtra("pdfUrl",conceptPdfUrl);
                    intent.putExtra("titleNoti",title);
                    context.startActivity(intent);
                }

            }
        });
        final SharedPreferences pref = view.getContext().getSharedPreferences("skip", Context.MODE_PRIVATE);
        skip=pref.getBoolean("skip",false);

        final ImageButton favourite = itemView.findViewById(R.id.add_favorite);
        favourite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!skip){
                    final SharedPreferences prefs=view.getContext().getSharedPreferences(NAME, Context.MODE_PRIVATE);
                    boolean check = prefs.getBoolean(title,false);
                    SharedPreferences.Editor edt = prefs.edit();

                    FirebaseUser firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
                    favourites= FirebaseDatabase.getInstance().getReference().child("users").child(firebaseUser.getUid()).child("favourites");
                    favourites.push().child("title").setValue(title);

                    if(!check){
                        draw = view.getResources().getDrawable(R.drawable.favourite_fill);
                        favourite.setImageDrawable(draw);

                        Query applesQuery = favourites.orderByChild("title").equalTo(title);
                        applesQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                for (DataSnapshot appleSnapshot: dataSnapshot.getChildren()) {
                                    appleSnapshot.getRef().child("title").setValue(title);
                                    appleSnapshot.getRef().child("conceptPdfUrl").setValue(conceptPdfUrl);
                                    appleSnapshot.getRef().child("time").setValue(time);
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                            }
                        });

                        edt.putBoolean(title,true);
                        edt.apply();
                    }else {
                        draw = view.getResources().getDrawable(R.drawable.favourite_blank);
                        favourite.setImageDrawable(draw);

                        Query applesQuery = favourites.orderByChild("title").equalTo(title);
                        applesQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                for (DataSnapshot appleSnapshot: dataSnapshot.getChildren()) {
                                    appleSnapshot.getRef().removeValue();
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                            }
                        });

                        edt.putBoolean(title,false);
                        edt.apply();
                    }

                }else{
                    Toast.makeText(view.getContext(), "First authenticate yourself", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(view.getContext(), LoginOption.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    view.getContext().startActivity(intent);
                }

            }
        });
        final ImageButton share = itemView.findViewById(R.id.share);
        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if ( tempFile(view.getContext(),title).isFile()) {
                    // If we have downloaded the file before, just go ahead and show it.
                    PDFTools.sharePdf(view.getContext(),tempFile(view.getContext(),title));
                }else{
                    if(PDFTools.isDownloadManagerAvailable(view.getContext()) ){
                        if ( PDFTools.isPDFSupported( view.getContext() ) )
                            PDFTools.downloadAndSharePDF(view.getContext(),title, conceptPdfUrl);
                        else
                            Toast.makeText(view.getContext(),"Phone is not supported",Toast.LENGTH_LONG).show();

                    }
                    else{
                        Toast.makeText(view.getContext(),"Phone is not supported",Toast.LENGTH_LONG).show();
                    }
                }
            }
        });
        ImageButton download = itemView.findViewById(R.id.download);
        download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(PDFTools.isDownloadManagerAvailable(view.getContext()) ){
                    PDFTools.showPDFUrl(view.getContext(),title, conceptPdfUrl);
                }
                else{
                    Toast.makeText(view.getContext(),"Phone is not supported",Toast.LENGTH_LONG).show();
                }
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
                            SharedPreferences pref = view.getContext().getSharedPreferences("course", Context.MODE_PRIVATE);
                            final String book = pref.getString("book","book");
                            final String chapter = pref.getString("chapter","chapter");
                            final String activityName = pref.getString("activity","activity");

                            DatabaseReference favourites;
                            favourites= FirebaseDatabase.getInstance().getReference()
                                    .child("chapters").child(book).child(chapter).child(activityName).child("newdata");

                            Query applesQuery = favourites.orderByChild("title").equalTo(title);
                            applesQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    for (DataSnapshot appleSnapshot: dataSnapshot.getChildren()) {
                                        appleSnapshot.getRef().removeValue();
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                }
                            });


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
        SharedPreferences pref = view.getContext().getSharedPreferences("course", Context.MODE_PRIVATE);
        final String book = pref.getString("book","book");
        final String chapter = pref.getString("chapter","chapter");
        final String activityName = pref.getString("activity","activity");

        Intent intent = new Intent(view.getContext(), PostShortNotes.class);
        intent.putExtra("book",book);
        intent.putExtra("chapter", chapter);
        intent.putExtra("tags", title);
        intent.putExtra("text", conceptPdfUrl);
        intent.putExtra("tab", activityName);
//        intent.putExtra("documentId", documentId);
        intent.putExtra("time", time);
        intent.putExtra("topicNum", topic);
        view.getContext().startActivity(intent);

    }



    private static int getDefaultCheckBoxButtonDrawableResourceId(Context context) {
        // pre-Honeycomb has a different way of setting the CheckBox button drawable
        if (Build.VERSION.SDK_INT <= 10) return Resources.getSystem().getIdentifier("checkbox_empty", "drawable", "android");
        // starting with Honeycomb, retrieve the theme-based indicator as CheckBox button drawable
        TypedValue value = new TypedValue();
        context.getTheme().resolveAttribute(android.R.attr.listChoiceIndicatorMultiple, value, true);
        return value.resourceId;
    }

    public void setTopic(int topic) {
        this.topic = topic;
        TextView topicnum = view.findViewById(R.id.topicNum);
        String txt = Integer.toString(topic);
        topicnum.setText(txt);
    }

    public void setTitle(String title) {
        this.title = title;
        TextView titleconcept = view.findViewById(R.id.conceptTitle);
        titleconcept.setText(title);
        final SharedPreferences prefs=view.getContext().getSharedPreferences(NAME, Context.MODE_PRIVATE);
        boolean check = prefs.getBoolean(title,false);
        final ImageButton favourite = itemView.findViewById(R.id.add_favorite);
        if(check){
            draw = view.getResources().getDrawable(R.drawable.favourite_fill);
            favourite.setImageDrawable(draw);
        }else {
            draw = view.getResources().getDrawable(R.drawable.favourite_blank);
            favourite.setImageDrawable(draw);
        }
    }

    public void setConceptPdfUrl(String conceptPdfUrl) {
        this.conceptPdfUrl = conceptPdfUrl;
        CheckBox checkBox = itemView.findViewById(R.id.checkBox);
        SharedPreferences prefAdmin = view.getContext().getSharedPreferences("ActivityPREF", Context.MODE_PRIVATE);
        boolean check=prefAdmin.getBoolean(conceptPdfUrl+"read",false);

        checkBox.setButtonDrawable(check ? R.drawable.checkbox_tick : getDefaultCheckBoxButtonDrawableResourceId(view.getContext()));
        CompoundButtonCompat.setButtonTintList(checkBox, check ? ColorStateList.valueOf(view.getResources().getColor(R.color.blue)) :ColorStateList.valueOf(view.getResources().getColor(R.color.black)));

    }

    public void setTime(String time) {
        this.time = time;
        TextView hoursval = view.findViewById(R.id.hoursval);
        hoursval.setText(time);
    }
}
