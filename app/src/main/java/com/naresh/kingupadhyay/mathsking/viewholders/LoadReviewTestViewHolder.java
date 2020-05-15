package com.naresh.kingupadhyay.mathsking.viewholders;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.media.Image;
import android.net.Uri;
import android.os.Build;
import android.os.CountDownTimer;
import android.util.TypedValue;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatRatingBar;
import androidx.appcompat.widget.PopupMenu;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.core.widget.CompoundButtonCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.apradanas.simplelinkabletext.Link;
import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Source;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.naresh.kingupadhyay.mathsking.Database.CountryData;
import com.naresh.kingupadhyay.mathsking.Database.PDFTools;
import com.naresh.kingupadhyay.mathsking.R;
import com.naresh.kingupadhyay.mathsking.activities.PostExample;
import com.naresh.kingupadhyay.mathsking.activities.PostTestData;
import com.naresh.kingupadhyay.mathsking.activities.TestSeriesViewCardDetails;
import com.naresh.kingupadhyay.mathsking.activities.ViewCardDetails;
import com.naresh.kingupadhyay.mathsking.activities.ViewCardTestSeriesDataUpload;
import com.naresh.kingupadhyay.mathsking.network.LoadEditorial;
import com.wang.avi.AVLoadingIndicatorView;

import java.util.List;


public class LoadReviewTestViewHolder extends RecyclerView.ViewHolder {
    private View view;

    private String userId;
    private String  solvepercent;//    private int level;// Hardness of question (o->very easy,1->easy,2->medium,3->Hard)
    private Timestamp uploadingTime;
    private int markspos;
    private int marksneg;
    private int chapterCode;

    private String tags;
    private String text;
    private String textImageUrl;
    private boolean option;//true-> objective , false -> subjective questions
    private int numericval;
    private boolean optionA;
    private boolean optionB;
    private boolean optionC;
    private boolean optionD;
    private AppCompatRatingBar ratingBar;
    private int tabNumber;//1->comment,2->editorial,3->submission
    private String documentId;
    private String currentUserId;
    private String questionImageUrl="";
    private String answerImageUrl="";
    private String answertext="";
    private FirebaseFirestore db;
    private int cardnumber=1;
    private Spinner spinner;
    private boolean acheckval=false;
    private boolean bcheckval=false;
    private boolean ccheckval=false;
    private boolean dcheckval=false;

    public LoadReviewTestViewHolder(View itemView) {
        super(itemView);
        view = itemView;
        defaultCardSettings(view);
        db= FirebaseFirestore.getInstance();

        SharedPreferences prefUseId = view.getContext().getSharedPreferences("ActivityPREF", Context.MODE_PRIVATE);
        currentUserId=prefUseId.getString("uid","userkhk");

        spinner=(Spinner) view.findViewById(R.id.spinnerNumbers);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(view.getContext(),
                android.R.layout.simple_spinner_item, CountryData.numberNames);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setVisibility(View.GONE);

        LinearLayout commentsLayout = view.findViewById(R.id.comments);
        commentsLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newActivityOpen(v,1);
            }
        });
        LinearLayout editorialLayout = view.findViewById(R.id.editorial);
        editorialLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newActivityOpen(v,2);

            }
        });
        LinearLayout submissionLayout = view.findViewById(R.id.submission);
        submissionLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newActivityOpen(v,3);
            }
        });

        RelativeLayout part3Rl = view.findViewById(R.id.part3);
        part3Rl.setVisibility(View.VISIBLE);
        SharedPreferences prefAdmin = view.getContext().getSharedPreferences("ActivityPREF", Context.MODE_PRIVATE);
        final String id=prefAdmin.getString("id","");
        ImageView reportedit = view.findViewById(R.id.report_bar);
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
        TextView Quest1 = view.findViewById(R.id.Quest);
        Quest1.setVisibility(View.VISIBLE);
        TextView cardNUm = view.findViewById(R.id.cardNum);
        cardNUm.setVisibility(View.VISIBLE);
        String txt = Integer.toString(cardnumberin);
        Quest1.setText(R.string.quest);
        cardNUm.setText(txt);
    }

    public void setMarkspos(int markspos) {
        this.markspos = markspos;
        String txt = Integer.toString(markspos);
        TextView markpostext = view.findViewById(R.id.marksobtainpositive);
        markpostext.setText(txt);
    }

    public void setMarksneg(int marksneg) {
        this.marksneg = marksneg;
        String txt = Integer.toString(marksneg);
        TextView marknegtext = view.findViewById(R.id.marksobtainNegative);
        marknegtext.setText(txt);
    }

    public void setNumericval(int numericval) {
        this.numericval = numericval;
        SharedPreferences prefs=view.getContext().getSharedPreferences("score", Context.MODE_PRIVATE);
        TextView select = view.findViewById(R.id.select);
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
                            SharedPreferences prefLive = view.getContext().getSharedPreferences("live", Context.MODE_PRIVATE);
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
        SharedPreferences prefLive = view.getContext().getSharedPreferences("live", Context.MODE_PRIVATE);
        String liveId = prefLive.getString("liveId","live");

        intent.putExtra("liveId", liveId);
        intent.putExtra("markspos",""+ markspos);
        intent.putExtra("marksneg", ""+marksneg);
        intent.putExtra("chapterCode", chapterCode);
        intent.putExtra("tags", tags);
        intent.putExtra("text", text);
        intent.putExtra("textImageUrl", textImageUrl);
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
        Intent intent = new Intent(context, TestSeriesViewCardDetails.class);
        SharedPreferences prefLive = view.getContext().getSharedPreferences("live", Context.MODE_PRIVATE);
        String liveId = prefLive.getString("liveId","live");
        intent.putExtra("liveId", liveId);
        intent.putExtra("markspos", markspos);
        intent.putExtra("marksneg", marksneg);
        intent.putExtra("chapterCode", chapterCode);
        intent.putExtra("tags", tags);
        intent.putExtra("text", text);
        intent.putExtra("textImageUrl", textImageUrl);
        intent.putExtra("option", option);
        intent.putExtra("numericval", numericval);
        intent.putExtra("optionA", optionA);
        intent.putExtra("optionB", optionB);
        intent.putExtra("optionC", optionC);
        intent.putExtra("optionD", optionD);
        intent.putExtra("tabNumber",tabNumberVal);
        intent.putExtra("documentId",documentId);
        intent.putExtra("cardnumber",cardnumber);
        context.startActivity(intent);
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
        SharedPreferences prefs=view.getContext().getSharedPreferences("score", Context.MODE_PRIVATE);
        TextView solve = view.findViewById(R.id.solvepercent);
        int marks = prefs.getInt(documentId,0);
        String totalscore = "Score :" +marks;
        solve.setText(totalscore);
    }

    public void setSolvepercent(int solvepercentintput) {
        this.solvepercent = Integer.toString(solvepercentintput);
        TextView solvePercent = view.findViewById(R.id.solvepercentval);
        solvePercent.setText(solvepercent);
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
        TextView tagsval = view.findViewById(R.id.tagsValues);
        tagsval.setText(tags);
    }

    public void setText(String text) {
        this.text = text;
        TextView textView = view.findViewById(R.id.conceptTitle);
        textView.setText(text);
    }

    public void setTextImageUrl(String textImageUrl) {
        this.textImageUrl = textImageUrl;
        final ImageView questionImg=view.findViewById(R.id.questionImage);
        AVLoadingIndicatorView progressBar=itemView.findViewById(R.id.progress_bar_questionImage);
        progressBar.show();
        PDFTools.picassoLoadImageAvl(questionImg , textImageUrl,progressBar);
        questionImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPopupImage(questionImg);
            }
        });
    }

    public void setOption(boolean option) {
        this.option = option;
        RelativeLayout relativeLayout = view.findViewById(R.id.partspinner);
        LinearLayout linearLayoutOptions = view.findViewById(R.id.part2Linear);
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
        SharedPreferences prefs=view.getContext().getSharedPreferences("score", Context.MODE_PRIVATE);

        CheckBox checkBoxA = view.findViewById(R.id.checkBoxA);
        CheckBox checkBoxB = view.findViewById(R.id.checkBoxB);
        CheckBox checkBoxC = view.findViewById(R.id.checkBoxC);
        CheckBox checkBoxD = view.findViewById(R.id.checkBoxD);

        checkBoxA.setButtonDrawable(optionA ? R.drawable.checkbox_tick : getDefaultCheckBoxButtonDrawableResourceId(view.getContext()));
        CompoundButtonCompat.setButtonTintList(checkBoxA, optionA ? ColorStateList.valueOf(view.getResources().getColor(R.color.green)) :ColorStateList.valueOf(view.getResources().getColor(R.color.black)));

        checkBoxB.setButtonDrawable(optionB ? R.drawable.checkbox_tick : getDefaultCheckBoxButtonDrawableResourceId(view.getContext()));
        CompoundButtonCompat.setButtonTintList(checkBoxB, optionB ? ColorStateList.valueOf(view.getResources().getColor(R.color.green)) :ColorStateList.valueOf(view.getResources().getColor(R.color.black)));

        checkBoxC.setButtonDrawable(optionC ? R.drawable.checkbox_tick : getDefaultCheckBoxButtonDrawableResourceId(view.getContext()));
        CompoundButtonCompat.setButtonTintList(checkBoxC, optionC ? ColorStateList.valueOf(view.getResources().getColor(R.color.green)) :ColorStateList.valueOf(view.getResources().getColor(R.color.black)));

        checkBoxD.setButtonDrawable(optionD ? R.drawable.checkbox_tick : getDefaultCheckBoxButtonDrawableResourceId(view.getContext()));
        CompoundButtonCompat.setButtonTintList(checkBoxD, optionD ? ColorStateList.valueOf(view.getResources().getColor(R.color.green)) :ColorStateList.valueOf(view.getResources().getColor(R.color.black)));

        boolean checka = prefs.getBoolean(documentId+"a",false);
        boolean checkb = prefs.getBoolean(documentId+"b",false);
        boolean checkc = prefs.getBoolean(documentId+"c",false);
        boolean checkd = prefs.getBoolean(documentId+"d",false);
        if(checka && checka != optionA){
            checkBoxA.setButtonDrawable(checka ? R.drawable.checkbox_cross : getDefaultCheckBoxButtonDrawableResourceId(view.getContext()));
            CompoundButtonCompat.setButtonTintList(checkBoxA, checka ? ColorStateList.valueOf(view.getResources().getColor(R.color.red)) :ColorStateList.valueOf(view.getResources().getColor(R.color.black)));
        }
        if(checkb && checkb != optionB){
            checkBoxB.setButtonDrawable(checkb ? R.drawable.checkbox_cross : getDefaultCheckBoxButtonDrawableResourceId(view.getContext()));
            CompoundButtonCompat.setButtonTintList(checkBoxB, checkb ? ColorStateList.valueOf(view.getResources().getColor(R.color.red)) :ColorStateList.valueOf(view.getResources().getColor(R.color.black)));
        }

        if(checkc && checkc != optionC){
            checkBoxC.setButtonDrawable(checkc ? R.drawable.checkbox_cross : getDefaultCheckBoxButtonDrawableResourceId(view.getContext()));
            CompoundButtonCompat.setButtonTintList(checkBoxC, checkc ? ColorStateList.valueOf(view.getResources().getColor(R.color.red)) :ColorStateList.valueOf(view.getResources().getColor(R.color.black)));
        }

        if(checkd && checkd != optionD){
            checkBoxD.setButtonDrawable(checkd ? R.drawable.checkbox_cross : getDefaultCheckBoxButtonDrawableResourceId(view.getContext()));
            CompoundButtonCompat.setButtonTintList(checkBoxD, checkd ? ColorStateList.valueOf(view.getResources().getColor(R.color.red)) :ColorStateList.valueOf(view.getResources().getColor(R.color.black)));
        }


    }

    public void defaultCardSettings(final View view){
        TextView marks = view.findViewById(R.id.marks);
        TextView signpos = view.findViewById(R.id.signpositive);
        TextView markpos = view.findViewById(R.id.marksobtainpositive);
        TextView slash = view.findViewById(R.id.marksobtainspace);
        TextView signneg = view.findViewById(R.id.signNegative);
        TextView markneg = view.findViewById(R.id.marksobtainNegative);

//        TextView solve = view.findViewById(R.id.solvepercent);
        TextView percentsign = view.findViewById(R.id.percent);
        TextView tagname = view.findViewById(R.id.tagsName);
        TextView a = view.findViewById(R.id.optionTextA);
        TextView b = view.findViewById(R.id.optionTextB);
        TextView c = view.findViewById(R.id.optionTextC);
        TextView d = view.findViewById(R.id.optionTextD);
        TextView comment = view.findViewById(R.id.comment);
        TextView editorial = view.findViewById(R.id.editorialText);
        TextView submission = view.findViewById(R.id.submissionText);
        TextView select = view.findViewById(R.id.select);
        RelativeLayout tagslyout = view.findViewById(R.id.tagslayout);

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
        SharedPreferences prefAdmin = view.getContext().getSharedPreferences("ActivityPREF", Context.MODE_PRIVATE);
        String id=prefAdmin.getString("id","");
        if(id.equals("vinayupadhyay02001@gmail.com") || id.equals("naresh03961999@gmail.com") || id.equals("rakshitaupadhyay1497@gmail.com") ) {
            tagslyout.setVisibility(View.VISIBLE);
        }else{
            tagslyout.setVisibility(View.GONE);
        }
    }

    public void showPopupImage(ImageView imageViewTemp){
        final Dialog myDialog = new Dialog(view.getContext());
        myDialog.setContentView(R.layout.show_image_popup);
        myDialog.setCancelable(false);
        TextView instructon = (TextView) myDialog.findViewById(R.id.instruction);
        ImageButton cancel = (ImageButton) myDialog.findViewById(R.id.cancel);
        SubsamplingScaleImageView imageView=(SubsamplingScaleImageView)myDialog.findViewById(R.id.showImage);
        instructon.setText(R.string.zoom);
        imageViewTemp.buildDrawingCache();
        Bitmap bitmap = imageViewTemp.getDrawingCache();
        imageView.setImage(ImageSource.bitmap(bitmap));
//        imageView.setImageBitmap(ssbitmap);
        myDialog.show();
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myDialog.dismiss();
            }
        });

    }

}

