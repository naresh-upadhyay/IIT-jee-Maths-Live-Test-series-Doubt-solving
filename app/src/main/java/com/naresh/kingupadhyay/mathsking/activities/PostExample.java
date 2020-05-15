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
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.apradanas.simplelinkabletext.Link;
import com.apradanas.simplelinkabletext.LinkableEditText;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class PostExample extends AppCompatActivity {

    private String book;
    private String chapter;
    private AdView mAdView;

    private FirebaseFirestore db;
    private RelativeLayout uploadImageLayout_post;
    private List<Link> links;
    private LinkableEditText editTextPostTags;
    private LinkableEditText editTextPostQuestion;
    private LinkableEditText editTextPostAnswer;
    private String currentUserId;
    private ImageView attachImageQuestion;
    private ImageView attachImageAnswer;
    private boolean optional;
    private boolean imageset;
    private int attachNumber=0;
    //example posting
    private int level=0;// Hardness of question (o->very easy,1->easy,2->medium,3->Hard)
    private int solvepercent=0;
    private Timestamp uploadingTime;
    private String questionImageUrl="";
    private String answerImageUrl="";
    private String documentId;
    private String textAns;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_example);
        defaultCardSettings();

        SharedPreferences prefUseId = getSharedPreferences("ActivityPREF", Context.MODE_PRIVATE);
        currentUserId=prefUseId.getString("uid","user");

        Bundle bundle = getIntent().getExtras();
        book = bundle.getString("book");
        chapter = bundle.getString("chapter","chapter");
        String tags=bundle.getString("tags","");
        final String text=bundle.getString("text","");
        questionImageUrl=bundle.getString("textImageUrl","");
        textAns=bundle.getString("textAns","");
        answerImageUrl=bundle.getString("textImageUrlAns","");
        boolean option=bundle.getBoolean("option",false);
        boolean optionA=bundle.getBoolean("optionA", false);
        boolean optionB=bundle.getBoolean("optionB", false);
        boolean optionC=bundle.getBoolean("optionC", false);
        boolean optionD=bundle.getBoolean("optionD", false);
        documentId=bundle.getString("documentId","");
        db= FirebaseFirestore.getInstance();
        editTextPostTags = findViewById(R.id.tagsvalLinkableEdit);
        editTextPostQuestion = findViewById(R.id.questionvalLinkableEdit);
        editTextPostAnswer = findViewById(R.id.answervalLinkableEdit);
        attachImageQuestion = findViewById(R.id.attachImage_post_Question);
        attachImageAnswer = findViewById(R.id.attachImage_post_Answer);

        Link linkUsername = new Link(Pattern.compile("(@\\w+)"))
                .setUnderlined(false)
                .setTextColor(R.color.blue)
                .setTextStyle(Link.TextStyle.BOLD)
                .setClickListener(new Link.OnClickListener() {
                    @Override
                    public void onClick(String text) {
                        // do something
                        Toast.makeText(PostExample.this, "Clicked username: " +text, Toast.LENGTH_SHORT).show();

                    }
                });

        links = new ArrayList<>();
        links.add(linkUsername);
        editTextPostTags.addLinks(links);
        editTextPostTags.setText(tags,TextView.BufferType.EDITABLE);
        editTextPostQuestion.addLinks(links);
        editTextPostQuestion.setText(text,TextView.BufferType.EDITABLE);
        editTextPostAnswer.addLinks(links);


        try{
            final DocumentReference userRef = db
                    .collection("chapters").document(book)
                    .collection(chapter).document("branches")
                    .collection("examples").document(documentId);//userId = user
            DocumentReference editorialRef = userRef.collection("editorial").document(currentUserId);
            editorialRef.get(Source.CACHE).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    LoadEditorial loadEditorial = documentSnapshot.toObject(LoadEditorial.class);
                    answerImageUrl = loadEditorial.getAnswerImage();
                    textAns = loadEditorial.getText();

                    editTextPostAnswer.setText(textAns,TextView.BufferType.EDITABLE);
                    if(!answerImageUrl.isEmpty() && answerImageUrl != null){
                        attachImageAnswer.setColorFilter(getResources().getColor(R.color.blue));
                    }
//                    userRef.delete();
                }
            });

        }catch (Exception e){
        }



        //tags
        editTextPostTags.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(editTextPostTags.getText().toString().isEmpty() ||editTextPostTags.getText().toString()==null)
                    editTextPostTags.setText(" ", TextView.BufferType.EDITABLE);
                if(b){
                    editTextPostTags.setPressed(true);
                    editTextPostTags.setSelection(editTextPostTags.getText().length());
                }else{
                    closeKeyBoard(view);
                }
            }
        });

        //question
        editTextPostQuestion.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(editTextPostQuestion.getText().toString().isEmpty() ||editTextPostQuestion.getText().toString()==null)
                    editTextPostQuestion.setText(" ", TextView.BufferType.EDITABLE);
                if(b){
                    editTextPostQuestion.setPressed(true);
                    editTextPostQuestion.setSelection(editTextPostQuestion.getText().length());
                }else{
                    closeKeyBoard(view);
                }
            }
        });

        //answers
        editTextPostAnswer.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(editTextPostAnswer.getText().toString().isEmpty() ||editTextPostAnswer.getText().toString()==null)
                    editTextPostAnswer.setText(" ", TextView.BufferType.EDITABLE);
                if(b){
                    editTextPostAnswer.setPressed(true);
                    editTextPostAnswer.setSelection(editTextPostAnswer.getText().length());
                }else{
                    closeKeyBoard(view);
                }
            }
        });

        attachImageQuestion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attachNumber = 1;
                Intent intent = new Intent(PostExample.this, ImageCroper.class);
                startActivity(intent);

            }
        });
        attachImageAnswer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attachNumber = 2;
                Intent intent = new Intent(PostExample.this, ImageCroper.class);
                startActivity(intent);

            }
        });
        if(!questionImageUrl.isEmpty() && questionImageUrl != null){
            attachImageQuestion.setColorFilter(getResources().getColor(R.color.blue));
        }
        if(!answerImageUrl.isEmpty() && answerImageUrl != null){
            attachImageAnswer.setColorFilter(getResources().getColor(R.color.blue));
        }

        final RelativeLayout optionSubj = findViewById(R.id.options);
        optionSubj.setVisibility(View.GONE);
        final CheckBox acheck = findViewById(R.id.checkBoxA);
        final CheckBox bcheck = findViewById(R.id.checkBoxB);
        final CheckBox ccheck = findViewById(R.id.checkBoxC);
        final CheckBox dcheck = findViewById(R.id.checkBoxD);

        final Switch switchoption = findViewById(R.id.optionSwitch);
        switchoption.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    optionSubj.setVisibility(View.VISIBLE);
                    optional = true;
                }else{
                    optionSubj.setVisibility(View.GONE);
                }
            }
        });
        switchoption.setChecked(option);
        acheck.setChecked(optionA);
        bcheck.setChecked(optionB);
        ccheck.setChecked(optionC);
        dcheck.setChecked(optionD);

        Button postButton = findViewById(R.id.postButton_post_example);
        postButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String tagstxt = editTextPostTags.getText().toString().trim();
                String questiontxt = editTextPostQuestion.getText().toString().trim();
                String answertxt = editTextPostAnswer.getText().toString().trim();
                if(tagstxt.isEmpty() || questiontxt.isEmpty() || answertxt.isEmpty()){
                    if (tagstxt.isEmpty()) {
                        editTextPostTags.setError("Write something!");
                    }
                    if (questiontxt.isEmpty()) {
                        editTextPostQuestion.setError("Write something!");
                    }
                    if (answertxt.isEmpty()) {
                        editTextPostAnswer.setError("Write something!");
                    }

                    if (tagstxt.isEmpty()) {
                        editTextPostTags.requestFocus();
                    }else if (questiontxt.isEmpty()) {
                        editTextPostQuestion.requestFocus();
                    }else{
                        editTextPostAnswer.requestFocus();
                    }
                    return;
                }
                if(!acheck.isChecked() && !bcheck.isChecked() && !ccheck.isChecked() && !dcheck.isChecked()){
                    switchoption.setChecked(false);
                    optionSubj.setVisibility(View.GONE);
                    optional=false;
                }
                if(questionImageUrl.isEmpty() || questionImageUrl == null){
                    attachImageQuestion.setColorFilter(getResources().getColor(R.color.red));
                    return;
                }
                if(answerImageUrl.isEmpty() || answerImageUrl == null){
                    attachImageAnswer.setColorFilter(getResources().getColor(R.color.red));
                    return;
                }


                try{
                    FirebaseFirestore db= FirebaseFirestore.getInstance();
                    DocumentReference exampleRef;
                    if(!documentId.isEmpty() && documentId != null){
                        exampleRef= db
                                .collection("chapters").document(book)
                                .collection(chapter).document("branches")
                                .collection("examples").document(documentId);//userId = user
                    }else{
                        exampleRef= db
                                .collection("chapters").document(book)
                                .collection(chapter).document("branches")
                                .collection("examples").document();//userId = user
                    }

                    //todo upload data
                    DocumentReference editorialRef = exampleRef.collection("editorial").document(currentUserId);

                    Map<String,Object> uploadMap = new HashMap<>();
                    uploadMap.put("solvepercent",solvepercent);
                    uploadMap.put("level",level);
                    uploadMap.put("uploadingTime",uploadingTime);
                    uploadMap.put("userId",currentUserId);
                    uploadMap.put("tags",tagstxt);
                    uploadMap.put("text",questiontxt);
                    uploadMap.put("textImage",questionImageUrl);
                    uploadMap.put("option",optional);
                    uploadMap.put("optionA",acheck.isChecked());
                    uploadMap.put("optionB",bcheck.isChecked());
                    uploadMap.put("optionC",ccheck.isChecked());
                    uploadMap.put("optionD",dcheck.isChecked());
                    exampleRef.set(uploadMap, SetOptions.merge());

                    Map<String,Object> uploadEditorial = new HashMap<>();
                    uploadEditorial.put("userId",currentUserId);
                    uploadEditorial.put("text",answertxt);
                    uploadEditorial.put("answerImage",answerImageUrl);
                    editorialRef.set(uploadEditorial, SetOptions.merge());



                    SharedPreferences prefEdit1 = getSharedPreferences("edit", Context.MODE_PRIVATE);
                    SharedPreferences.Editor edt11 = prefEdit1.edit();
                    edt11.putBoolean("image",false);
                    edt11.putString("imageUrl","");
                    edt11.apply();

                    onBackPressed();

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

            if(attachNumber == 1 && imageset){
                try {
                    Uri uri =  Uri.parse(questionImageUrl);
                    String path = uri.getPath();
                    int cut = path.lastIndexOf('/');
                    String imageFilename="";
                    if (cut != -1) {
                        imageFilename = path.substring(cut + 1);
                    }
                    deleteStoredImage("images/examples/submissions",imageFilename);

                }catch (Exception e){
                }

                questionImageUrl = prefEdit2.getString("imageUrl","");
                attachImageQuestion.setColorFilter(getResources().getColor(R.color.blue));
                SharedPreferences prefEdit1 = getSharedPreferences("edit", Context.MODE_PRIVATE);
                SharedPreferences.Editor edt11 = prefEdit1.edit();
                edt11.putBoolean("image",false);
                edt11.putString("imageUrl","");
                edt11.apply();

            }
            if(attachNumber == 2 && imageset){
                try {
                    Uri uri =  Uri.parse(answerImageUrl);
                    String path = uri.getPath();
                    int cut = path.lastIndexOf('/');
                    String imageFilename="";
                    if (cut != -1) {
                        imageFilename = path.substring(cut + 1);
                    }
                    deleteStoredImage("images/examples/submissions",imageFilename);

                }catch (Exception e){
                }

                answerImageUrl = prefEdit2.getString("imageUrl","");
                attachImageAnswer.setColorFilter(getResources().getColor(R.color.blue));
                SharedPreferences prefEdit1 = getSharedPreferences("edit", Context.MODE_PRIVATE);
                SharedPreferences.Editor edt11 = prefEdit1.edit();
                edt11.putBoolean("image",false);
                edt11.putString("imageUrl","");
                edt11.apply();

            }
            if(questionImageUrl.isEmpty() || questionImageUrl == null){
                attachImageQuestion.setColorFilter(getResources().getColor(R.color.black));
            }
            if(answerImageUrl.isEmpty() || answerImageUrl == null){
                attachImageAnswer.setColorFilter(getResources().getColor(R.color.black));
            }
        }catch (Exception e){

        }
    }


    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
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



    private void closeKeyBoard(View view){
        if (view != null){
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }


    public void defaultCardSettings(){
//        TextView tagname = findViewById(R.id.tagsName);
        TextView a = findViewById(R.id.optionTextA);
        TextView b = findViewById(R.id.optionTextB);
        TextView c = findViewById(R.id.optionTextC);
        TextView d = findViewById(R.id.optionTextD);
//        tagname.setText(R.string.tagsName);
        a.setText("A");
        b.setText("B");
        c.setText("C");
        d.setText("D");
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
