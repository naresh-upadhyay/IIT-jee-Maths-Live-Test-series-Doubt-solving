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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
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
import com.naresh.kingupadhyay.mathsking.Database.CountryData;
import com.naresh.kingupadhyay.mathsking.R;
import com.naresh.kingupadhyay.mathsking.captue.ImageCroper;
import com.naresh.kingupadhyay.mathsking.network.LoadEditorial;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class PostTestData extends AppCompatActivity {

    private AdView mAdView;

    private FirebaseFirestore db;
    private RelativeLayout uploadImageLayout_post;
    private List<Link> links;
//    private LinkableEditText editTextPostTags;
    private LinkableEditText editTextPostQuestion;
    private LinkableEditText editTextPostmarkspos;
    private LinkableEditText editTextPostmarksneg;
    private LinkableEditText editTextPostAnswer;
    private String currentUserId;
    private ImageView attachImageQuestion;
    private ImageView attachImageAnswer;
    private boolean optional=false;
    private Spinner spinnerQuestion;
    private Spinner spinnerNumber;
    private boolean imageset;
    private int attachNumber=0;
    private int solvepercent=0;
    private Timestamp uploadingTime = new Timestamp(new Date());
    private String questionImageUrl="";
    private String answerImageUrl="";
    private String documentId;
    private String textAns;
    private String liveId;
    private String markspos;
    private String marksneg;
    private int chapterCode;
    private int numericval;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_test_data);
        defaultCardSettings();

        SharedPreferences prefUseId = getSharedPreferences("ActivityPREF", Context.MODE_PRIVATE);
        currentUserId=prefUseId.getString("uid","user");

        Bundle bundle = getIntent().getExtras();
        liveId = bundle.getString("liveId","liveId");
        markspos = bundle.getString("markspos","");
        marksneg = bundle.getString("marksneg","");
        chapterCode = bundle.getInt("chapterCode",0);
        String tags=bundle.getString("tags","");
        final String text=bundle.getString("text","");
        questionImageUrl=bundle.getString("textImageUrl","");
        textAns=bundle.getString("textAns","");
        answerImageUrl=bundle.getString("textImageUrlAns","");
        optional=bundle.getBoolean("option",false);
        numericval=bundle.getInt("numericval",0);
        boolean optionA=bundle.getBoolean("optionA", false);
        boolean optionB=bundle.getBoolean("optionB", false);
        boolean optionC=bundle.getBoolean("optionC", false);
        boolean optionD=bundle.getBoolean("optionD", false);
        documentId=bundle.getString("documentId","");
        db= FirebaseFirestore.getInstance();
//        editTextPostTags = findViewById(R.id.tagsvalLinkableEdit);
        editTextPostQuestion = findViewById(R.id.questionvalLinkableEdit);
        editTextPostmarkspos = findViewById(R.id.marksposLinkableEdit);
        editTextPostmarksneg = findViewById(R.id.marksnegLinkableEdit);
        editTextPostAnswer = findViewById(R.id.answervalLinkableEdit);
        attachImageQuestion = findViewById(R.id.attachImage_post_Question);
        attachImageAnswer = findViewById(R.id.attachImage_post_Answer);
        spinnerQuestion = findViewById(R.id.spinnerQuestion);
        spinnerNumber = findViewById(R.id.spinnerNumbers);

        Link linkUsername = new Link(Pattern.compile("(@\\w+)"))
                .setUnderlined(false)
                .setTextColor(R.color.blue)
                .setTextStyle(Link.TextStyle.BOLD)
                .setClickListener(new Link.OnClickListener() {
                    @Override
                    public void onClick(String text) {
                        // do something
                        Toast.makeText(PostTestData.this, "Clicked username: " +text, Toast.LENGTH_SHORT).show();

                    }
                });

        links = new ArrayList<>();
        links.add(linkUsername);
//        editTextPostTags.addLinks(links);
//        editTextPostTags.setText(tags,TextView.BufferType.EDITABLE);
        editTextPostQuestion.addLinks(links);
        editTextPostQuestion.setText(text,TextView.BufferType.EDITABLE);
        editTextPostAnswer.addLinks(links);
        editTextPostmarkspos.setText(markspos,TextView.BufferType.EDITABLE);
        editTextPostmarksneg.setText(marksneg,TextView.BufferType.EDITABLE);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(PostTestData.this,
                android.R.layout.simple_spinner_item, CountryData.chaptersName);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerQuestion.setAdapter(adapter);
        spinnerQuestion.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                int spinNumber = CountryData.chaptersNumber[i];
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
        ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(PostTestData.this,
                android.R.layout.simple_spinner_item, CountryData.numberNames);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerNumber.setAdapter(adapter2);
        spinnerNumber.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                int spinNumber = CountryData.numberCodes[i];
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        spinnerQuestion.setSelection(chapterCode);
        spinnerNumber.setSelection(numericval);

        try{
            final DocumentReference userRef = db
                    .collection("test_series").document("branches")
                    .collection(liveId).document(documentId);//userId = user
            DocumentReference editorialRef = userRef.collection("editorial").document("live");
            editorialRef.get(Source.SERVER).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
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



//        //tags
//        editTextPostTags.setOnFocusChangeListener(new View.OnFocusChangeListener() {
//            @Override
//            public void onFocusChange(View view, boolean b) {
//                if(editTextPostTags.getText().toString().isEmpty() ||editTextPostTags.getText().toString()==null)
//                    editTextPostTags.setText(" ", TextView.BufferType.EDITABLE);
//                if(b){
//                    editTextPostTags.setPressed(true);
//                    editTextPostTags.setSelection(editTextPostTags.getText().length());
//                }else{
//                    closeKeyBoard(view);
//                }
//            }
//        });

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
        //pos marks
        editTextPostmarkspos.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(editTextPostmarkspos.getText().toString().isEmpty() ||editTextPostmarkspos.getText().toString()==null)
                    editTextPostmarkspos.setText(" ", TextView.BufferType.EDITABLE);
                if(b){
                    editTextPostmarkspos.setPressed(true);
                    editTextPostmarkspos.setSelection(editTextPostmarkspos.getText().length());
                }else{
                    closeKeyBoard(view);
                }
            }
        });
        //neg marks
        editTextPostmarksneg.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(editTextPostmarksneg.getText().toString().isEmpty() ||editTextPostmarksneg.getText().toString()==null)
                    editTextPostmarksneg.setText(" ", TextView.BufferType.EDITABLE);
                if(b){
                    editTextPostmarksneg.setPressed(true);
                    editTextPostmarksneg.setSelection(editTextPostmarksneg.getText().length());
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
                Intent intent = new Intent(PostTestData.this, ImageCroper.class);
                startActivity(intent);

            }
        });
        attachImageAnswer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attachNumber = 2;
                Intent intent = new Intent(PostTestData.this, ImageCroper.class);
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
        final RelativeLayout partspinner = findViewById(R.id.partspinner);
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
                    partspinner.setVisibility(View.GONE);
                    optionSubj.setVisibility(View.VISIBLE);
                    optional = true;
                }else{
                    partspinner.setVisibility(View.VISIBLE);
                    optionSubj.setVisibility(View.GONE);
                    optional = false;
                }
            }
        });
        switchoption.setChecked(optional);
//        spinnerNumber.setSelection(numericval);
        acheck.setChecked(optionA);
        bcheck.setChecked(optionB);
        ccheck.setChecked(optionC);
        dcheck.setChecked(optionD);

        Button postButton = findViewById(R.id.postButton_post_example);
        postButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                String tagstxt = editTextPostTags.getText().toString().trim();
                String questiontxt = editTextPostQuestion.getText().toString().trim();
                String marksposxt = editTextPostmarkspos.getText().toString().trim();
                String marksnegtxt = editTextPostmarksneg.getText().toString().trim();
                String answertxt = editTextPostAnswer.getText().toString().trim();
                if(questiontxt.isEmpty() || marksnegtxt.isEmpty() || marksposxt.isEmpty() || answertxt.isEmpty()){
                    if (questiontxt.isEmpty()) {
                        editTextPostQuestion.setError("Write something!");
                    }
                    if (marksnegtxt.isEmpty()) {
                        editTextPostmarksneg.setError("Write something!");
                    }
                    if (marksposxt.isEmpty()) {
                        editTextPostmarkspos.setError("Write something!");
                    }
                    if (answertxt.isEmpty()) {
                        editTextPostAnswer.setError("Write something!");
                    }

                    if (questiontxt.isEmpty()) {
                        editTextPostQuestion.requestFocus();
                    }else if (marksnegtxt.isEmpty()) {
                        editTextPostmarksneg.requestFocus();
                    }else if (marksposxt.isEmpty()) {
                        editTextPostmarkspos.requestFocus();
                    }else{
                        editTextPostAnswer.requestFocus();
                    }
                    return;
                }
                if (spinnerQuestion.getSelectedItemPosition()==0){
                    Toast.makeText(PostTestData.this, "Select chapter", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(!acheck.isChecked() && !bcheck.isChecked() && !ccheck.isChecked() && !dcheck.isChecked()){
                    switchoption.setChecked(false);
                    optionSubj.setVisibility(View.GONE);
                    optional=false;
                    partspinner.setVisibility(View.VISIBLE);
                    if (spinnerNumber.getSelectedItemPosition()==0 || spinnerQuestion.getSelectedItemPosition()==0){
                        Toast.makeText(PostTestData.this, "Select marks and chapter both", Toast.LENGTH_SHORT).show();
                        return;
                    }
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
                    DocumentReference liveRef;
                    if(!documentId.isEmpty() && documentId != null){
                        liveRef = db
                                .collection("test_series").document("branches")
                                .collection(liveId).document(documentId);
                    }else{
                        liveRef = db
                                .collection("test_series").document("branches")
                                .collection(liveId).document();
                    }

                    //todo upload data
                    DocumentReference editorialRef = liveRef.collection("editorial").document("live");
                    int chaptercodeVal = CountryData.chaptersNumber[spinnerQuestion.getSelectedItemPosition()];
                    int numberCodeVal = CountryData.numberCodes[spinnerNumber.getSelectedItemPosition()];
                    String tagstxt = CountryData.chaptersName[spinnerQuestion.getSelectedItemPosition()];
                    Map<String,Object> uploadMap = new HashMap<>();
                    uploadMap.put("markspos",Integer.parseInt(marksposxt));
                    uploadMap.put("marksneg",Integer.parseInt(marksnegtxt));
                    uploadMap.put("chapterCode",chaptercodeVal);
                    uploadMap.put("numericval",numberCodeVal);

                    uploadMap.put("solvepercent",solvepercent);
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
                    liveRef.set(uploadMap, SetOptions.merge());

                    Map<String,Object> uploadEditorial = new HashMap<>();
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
                    Toast.makeText(PostTestData.this, "remove space between - and num", Toast.LENGTH_SHORT).show();
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
