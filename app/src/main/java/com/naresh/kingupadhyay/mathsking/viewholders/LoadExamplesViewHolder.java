package com.naresh.kingupadhyay.mathsking.viewholders;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.net.Uri;
import android.os.Build;
import android.util.TypedValue;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Source;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.naresh.kingupadhyay.mathsking.Database.PDFTools;
import com.naresh.kingupadhyay.mathsking.R;
import com.naresh.kingupadhyay.mathsking.activities.GaveTest;
import com.naresh.kingupadhyay.mathsking.activities.PostExample;
import com.naresh.kingupadhyay.mathsking.activities.TestResult;
import com.naresh.kingupadhyay.mathsking.activities.ViewCardDetails;
import com.naresh.kingupadhyay.mathsking.network.LoadEditorial;
import com.wang.avi.AVLoadingIndicatorView;

import java.util.List;


public class LoadExamplesViewHolder extends RecyclerView.ViewHolder {
    private View view;

    private String book;
    private String chapter;
    private String  solvepercent;//    private int level;// Hardness of question (o->very easy,1->easy,2->medium,3->Hard)
    private Timestamp uploadingTime;
    private String userId;
    private String userName;
    private int userRating;
    private String userImage;

    private String tags;
    private String text;
    private String textImageUrl;
    private boolean option;//true-> objective , false -> subjective questions
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
    private boolean acheckval=false;
    private boolean bcheckval=false;
    private boolean ccheckval=false;
    private boolean dcheckval=false;

    private CheckBox checkBoxA ;
    private CheckBox checkBoxB;
    private CheckBox checkBoxC ;
    private CheckBox checkBoxD;

    public LoadExamplesViewHolder(View itemView) {
        super(itemView);
        view = itemView;
        defaultCardSettings(view);
        db= FirebaseFirestore.getInstance();

        SharedPreferences prefUseId = view.getContext().getSharedPreferences("ActivityPREF", Context.MODE_PRIVATE);
        currentUserId=prefUseId.getString("uid","userkhk");


        SharedPreferences pref = view.getContext().getSharedPreferences("course", Context.MODE_PRIVATE);
        book = pref.getString("book","book");
        chapter = pref.getString("chapter","chapter");
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

        SharedPreferences prefAdmin = view.getContext().getSharedPreferences("ActivityPREF", Context.MODE_PRIVATE);
        final String id=prefAdmin.getString("id","");

        ImageView reportedit = view.findViewById(R.id.report_bar);
        reportedit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(userId.equals(currentUserId) || id.equals("vinayupadhyay02001@gmail.com") || id.equals("naresh03961999@gmail.com") || id.equals("rakshitaupadhyay1497@gmail.com") ) {
                    popupMenuEditDelete(view);
                }else{
                    popupMenuReport(view);
                }

            }
        });

        checkBoxA = view.findViewById(R.id.checkBoxA);
        checkBoxB = view.findViewById(R.id.checkBoxB);
        checkBoxC = view.findViewById(R.id.checkBoxC);
        checkBoxD = view.findViewById(R.id.checkBoxD);
        SharedPreferences prefs=view.getContext().getSharedPreferences("score", Context.MODE_PRIVATE);
        final SharedPreferences.Editor edt = prefs.edit();


        checkBoxA.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                acheckval=b;
                edt.putBoolean(documentId+"a",b);
                edt.apply();
                checkBoxA.setButtonDrawable(b ? R.drawable.checkbox_tick : getDefaultCheckBoxButtonDrawableResourceId(view.getContext()));
                CompoundButtonCompat.setButtonTintList(checkBoxA, b ? ColorStateList.valueOf(view.getResources().getColor(R.color.blue)) :ColorStateList.valueOf(view.getResources().getColor(R.color.black)));
//                marksValueAll[cardnumber-1] = marksCalculation(acheckval,bcheckval,ccheckval,dcheckval);
//                edt.putInt(documentId,marksValueAll[cardnumber-1]);
//                edt.apply();

            }
        });

        checkBoxB.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                bcheckval=b;
                edt.putBoolean(documentId+"b",b);
                edt.apply();
                checkBoxB.setButtonDrawable(b ? R.drawable.checkbox_tick : getDefaultCheckBoxButtonDrawableResourceId(view.getContext()));
                CompoundButtonCompat.setButtonTintList(checkBoxB, b ? ColorStateList.valueOf(view.getResources().getColor(R.color.blue)) :ColorStateList.valueOf(view.getResources().getColor(R.color.black)));
//                marksValueAll[cardnumber-1] = marksCalculation(acheckval,bcheckval,ccheckval,dcheckval);
//                edt.putInt(documentId,marksValueAll[cardnumber-1]);
//                edt.apply();
            }
        });
        checkBoxC.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                ccheckval=b;
                edt.putBoolean(documentId+"c",b);
                edt.apply();
                checkBoxC.setButtonDrawable(b ? R.drawable.checkbox_tick : getDefaultCheckBoxButtonDrawableResourceId(view.getContext()));
                CompoundButtonCompat.setButtonTintList(checkBoxC, b ? ColorStateList.valueOf(view.getResources().getColor(R.color.blue)) :ColorStateList.valueOf(view.getResources().getColor(R.color.black)));
//                marksValueAll[cardnumber-1] = marksCalculation(acheckval,bcheckval,ccheckval,dcheckval);
//                edt.putInt(documentId,marksValueAll[cardnumber-1]);
//                edt.apply();
            }
        });
        checkBoxD.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                dcheckval=b;
                edt.putBoolean(documentId+"d",b);
                edt.apply();
                checkBoxD.setButtonDrawable(b ? R.drawable.checkbox_tick : getDefaultCheckBoxButtonDrawableResourceId(view.getContext()));
                CompoundButtonCompat.setButtonTintList(checkBoxD, b ? ColorStateList.valueOf(view.getResources().getColor(R.color.blue)) :ColorStateList.valueOf(view.getResources().getColor(R.color.black)));
//                marksValueAll[cardnumber-1] = marksCalculation(acheckval,bcheckval,ccheckval,dcheckval);
//                edt.putInt(documentId,marksValueAll[cardnumber-1]);
//                edt.apply();
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

                            final DocumentReference userRef = db
                                    .collection("chapters").document(book)
                                    .collection(chapter).document("branches")
                                    .collection("examples").document(documentId);//userId = user

                            //todo editorial data
                            DocumentReference editorialRef = userRef.collection("editorial").document(userId);
                            editorialRef.get(Source.CACHE).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
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
        Intent intent = new Intent(context, PostExample.class);
        intent.putExtra("book",book);
        intent.putExtra("chapter", chapter);
        intent.putExtra("tags", tags);
        intent.putExtra("text", text);
        intent.putExtra("textImageUrl", questionImageUrl);
        intent.putExtra("textAns", answertext);
        intent.putExtra("textImageUrlAns", answerImageUrl);
        intent.putExtra("option", option);
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
        if(option && !(acheckval||bcheckval||ccheckval||dcheckval)){
            showPopupFinishTest();
            return;
        }
        Context context = v.getContext();
        Intent intent = new Intent(context, ViewCardDetails.class);
        intent.putExtra("book",book);
        intent.putExtra("chapter", chapter);
        intent.putExtra("solvepercent", solvepercent);
        intent.putExtra("userId", userId);
        intent.putExtra("userName", userName);
        intent.putExtra("userRating", userRating);
        intent.putExtra("userImage", userImage);
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
        context.startActivity(intent);

    }

    public void showPopupFinishTest(){
        final Dialog myDialog = new Dialog(view.getContext());
        myDialog.setContentView(R.layout.show_finish_popup);
        myDialog.setCancelable(false);
//        TextView instructon = (TextView) myDialog.findViewById(R.id.instruction);
        Button cancel = (Button) myDialog.findViewById(R.id.cancel_choice);
        cancel.setVisibility(View.GONE);
        TextView actualmsgtxt = myDialog.findViewById(R.id.instruction_finish);
        TextView msgtxt = myDialog.findViewById(R.id.instruction_finish2);
        actualmsgtxt.setVisibility(View.INVISIBLE);
        msgtxt.setVisibility(View.VISIBLE);
        msgtxt.setText("Try to solve the question first, then open it");
        Button finish = (Button) myDialog.findViewById(R.id.finish_test);
        finish.setText("OK");
        myDialog.show();
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myDialog.dismiss();
            }
        });
        finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myDialog.dismiss();
            }
        });

    }


    public void setDocumentId(String documentId) {
        this.documentId = documentId;

        SharedPreferences prefs=view.getContext().getSharedPreferences("score", Context.MODE_PRIVATE);
        acheckval = prefs.getBoolean(documentId+"a",false);
        bcheckval = prefs.getBoolean(documentId+"b",false);
        ccheckval = prefs.getBoolean(documentId+"c",false);
        dcheckval = prefs.getBoolean(documentId+"d",false);
        checkBoxA.setButtonDrawable(acheckval ? R.drawable.checkbox_tick : getDefaultCheckBoxButtonDrawableResourceId(view.getContext()));
        CompoundButtonCompat.setButtonTintList(checkBoxA, acheckval ? ColorStateList.valueOf(view.getResources().getColor(R.color.blue)) :ColorStateList.valueOf(view.getResources().getColor(R.color.black)));

        checkBoxB.setButtonDrawable(bcheckval ? R.drawable.checkbox_tick : getDefaultCheckBoxButtonDrawableResourceId(view.getContext()));
        CompoundButtonCompat.setButtonTintList(checkBoxB, bcheckval ? ColorStateList.valueOf(view.getResources().getColor(R.color.blue)) :ColorStateList.valueOf(view.getResources().getColor(R.color.black)));

        checkBoxC.setButtonDrawable(ccheckval ? R.drawable.checkbox_tick : getDefaultCheckBoxButtonDrawableResourceId(view.getContext()));
        CompoundButtonCompat.setButtonTintList(checkBoxC, ccheckval ? ColorStateList.valueOf(view.getResources().getColor(R.color.blue)) :ColorStateList.valueOf(view.getResources().getColor(R.color.black)));

        checkBoxD.setButtonDrawable(dcheckval ? R.drawable.checkbox_tick : getDefaultCheckBoxButtonDrawableResourceId(view.getContext()));
        CompoundButtonCompat.setButtonTintList(checkBoxD, dcheckval ? ColorStateList.valueOf(view.getResources().getColor(R.color.blue)) :ColorStateList.valueOf(view.getResources().getColor(R.color.black)));

    }

    public void setUserName(String userName) {
        TextView textViewUserName = view.findViewById(R.id.user_name);
        textViewUserName.setText(userName);
        this.userName = userName;
    }

    public void setUserRating(int userRating) {
        ratingBar= view.findViewById(R.id.ratingbar);
        ratingBar.setNumStars(userRating);
        ratingBar.setRating(userRating);
        setCurrentRating(userRating);
        this.userRating = userRating;
    }

    private void setCurrentRating(float rating) {
        LayerDrawable drawable = (LayerDrawable)ratingBar.getProgressDrawable();
        switch (Math.round(rating)) {
            case 1:
                setRatingStarColor(drawable.getDrawable(2), ContextCompat.getColor(view.getContext(), R.color.star1));
                break;
            case 2:
                setRatingStarColor(drawable.getDrawable(2), ContextCompat.getColor(view.getContext(), R.color.star2));
                break;
            case 3:
                setRatingStarColor(drawable.getDrawable(2), ContextCompat.getColor(view.getContext(), R.color.star3));
                break;
            case 4:
                setRatingStarColor(drawable.getDrawable(2), ContextCompat.getColor(view.getContext(), R.color.star4));
                break;
            case 5:
                setRatingStarColor(drawable.getDrawable(2), ContextCompat.getColor(view.getContext(), R.color.star5));
                break;
            case 6:
                setRatingStarColor(drawable.getDrawable(2), ContextCompat.getColor(view.getContext(), R.color.star6));
                break;
            case 7:
                setRatingStarColor(drawable.getDrawable(2), ContextCompat.getColor(view.getContext(), R.color.star7));
                break;
            case 8:
                setRatingStarColor(drawable.getDrawable(2), ContextCompat.getColor(view.getContext(), R.color.star8));
                break;
            case 9:
                setRatingStarColor(drawable.getDrawable(2), ContextCompat.getColor(view.getContext(), R.color.star9));
                break;
            case 10:
                setRatingStarColor(drawable.getDrawable(2), ContextCompat.getColor(view.getContext(), R.color.star10));
                break;
        }
        setRatingStarColor(drawable.getDrawable(1), ContextCompat.getColor(view.getContext(), R.color.line_background));
        setRatingStarColor(drawable.getDrawable(0), ContextCompat.getColor(view.getContext(), R.color.line_background));
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
        ImageView dp = view.findViewById(R.id.userImage);
        AVLoadingIndicatorView progressBar=view.findViewById(R.id.progress_bar_user_image);
        progressBar.show();
        PDFTools.picassoLoadImageAvl(dp , userImage,progressBar);

        this.userImage = userImage;
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
                PDFTools.showPopupImage(questionImg,view);
            }
        });
    }

    public void setOption(boolean option) {
        this.option = option;
        LinearLayout linearLayoutOptions = view.findViewById(R.id.part2Linear);
        if(option)
            linearLayoutOptions.setVisibility(View.VISIBLE);
        else
            linearLayoutOptions.setVisibility(View.GONE);
    }

    public void setOptions(boolean optionA,boolean optionB,boolean optionC,boolean optionD) {
        this.optionA = optionA;
        this.optionB = optionB;
        this.optionC = optionC;
        this.optionD = optionD;
    }

    public void defaultCardSettings(final View view){
        TextView solve = view.findViewById(R.id.solvepercent);
        TextView percentsign = view.findViewById(R.id.percent);
        TextView tagname = view.findViewById(R.id.tagsName);
        TextView a = view.findViewById(R.id.optionTextA);
        TextView b = view.findViewById(R.id.optionTextB);
        TextView c = view.findViewById(R.id.optionTextC);
        TextView d = view.findViewById(R.id.optionTextD);
        TextView comment = view.findViewById(R.id.comment);
        TextView editorial = view.findViewById(R.id.editorialText);
        TextView submission = view.findViewById(R.id.submissionText);
        tagname.setText(R.string.tagsName);
        solve.setText(R.string.solve);
        percentsign.setText(R.string.percentsign);
        a.setText("A");
        b.setText("B");
        c.setText("C");
        d.setText("D");
        comment.setText(R.string.comment);
        editorial.setText(R.string.editorial);
        submission.setText(R.string.submission);

    }
}

