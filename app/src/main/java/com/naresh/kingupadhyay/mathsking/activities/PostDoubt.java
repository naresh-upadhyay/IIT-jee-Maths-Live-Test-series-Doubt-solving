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
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class PostDoubt extends AppCompatActivity {

    private String book;
    private String chapter;
    private AdView mAdView;

    private FirebaseFirestore db;
    private List<Link> links;
    private LinkableEditText editTextPostTags;
    private LinkableEditText editTextPostQuestion;
    private String currentUserId;
    private ImageView attachImageQuestion;
    private boolean imageset;
    private int attachNumber=0;
    //example posting
    private int votes=0;
    private Timestamp uploadingTime=  new Timestamp(new Date());
    private String questionImageUrl="";
    private String documentId;
    private String tab;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_doubt);

        SharedPreferences prefUseId = getSharedPreferences("ActivityPREF", Context.MODE_PRIVATE);
        currentUserId=prefUseId.getString("uid","user");

        Bundle bundle = getIntent().getExtras();
        book = bundle.getString("book");
        chapter = bundle.getString("chapter","chapter");
        String tags=bundle.getString("tags","");
        final String text=bundle.getString("text","");
        questionImageUrl=bundle.getString("textImageUrl","");
        tab = bundle.getString("tab","doubts");
        documentId=bundle.getString("documentId","");
        votes = bundle.getInt("votes",0);
        db= FirebaseFirestore.getInstance();
        editTextPostTags = findViewById(R.id.tagsvalLinkableEdit);
        editTextPostQuestion = findViewById(R.id.questionvalLinkableEdit);
        attachImageQuestion = findViewById(R.id.attachImage_post_Question);

        Link linkUsername = new Link(Pattern.compile("(@\\w+)"))
                .setUnderlined(false)
                .setTextColor(R.color.blue)
                .setTextStyle(Link.TextStyle.BOLD)
                .setClickListener(new Link.OnClickListener() {
                    @Override
                    public void onClick(String text) {
                        // do something
                        Toast.makeText(PostDoubt.this, "Clicked username: " +text, Toast.LENGTH_SHORT).show();

                    }
                });

        links = new ArrayList<>();
        links.add(linkUsername);
        editTextPostTags.addLinks(links);
        editTextPostTags.setText(tags,TextView.BufferType.EDITABLE);
        editTextPostQuestion.addLinks(links);
        editTextPostQuestion.setText(text,TextView.BufferType.EDITABLE);


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


        attachImageQuestion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attachNumber = 1;
                Intent intent = new Intent(PostDoubt.this, ImageCroper.class);
                startActivity(intent);

            }
        });
        if(!questionImageUrl.isEmpty() && questionImageUrl != null){
            attachImageQuestion.setColorFilter(getResources().getColor(R.color.blue));
        }

        Button postButton = findViewById(R.id.postButton_post_example);
        postButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String tagstxt = editTextPostTags.getText().toString().trim();
                String questiontxt = editTextPostQuestion.getText().toString().trim();
                if(tagstxt.isEmpty() || questiontxt.isEmpty()){
                    if (tagstxt.isEmpty()) {
                        editTextPostTags.setError("Write something!");
                    }
                    if (questiontxt.isEmpty()) {
                        editTextPostQuestion.setError("Write something!");
                    }

                    if (tagstxt.isEmpty()) {
                        editTextPostTags.requestFocus();
                    }else{
                        editTextPostQuestion.requestFocus();
                    }
                    return;
                }

                try{
                    FirebaseFirestore db= FirebaseFirestore.getInstance();
                    DocumentReference exampleRef;
                    if(!documentId.isEmpty() && documentId != null){
                        exampleRef= db
                                .collection("chapters").document(book)
                                .collection(chapter).document("branches")
                                .collection(tab).document(documentId);//userId = user
                    }else{
                        exampleRef= db
                                .collection("chapters").document(book)
                                .collection(chapter).document("branches")
                                .collection(tab).document();//userId = user
                    }

                    //todo upload data

                    Map<String,Object> uploadMap = new HashMap<>();
                    uploadMap.put("votes",votes);
                    uploadMap.put("uploadingTime",uploadingTime);
                    uploadMap.put("userId",currentUserId);
                    uploadMap.put("tags",tagstxt);
                    uploadMap.put("text",questiontxt);
                    if(!questionImageUrl.isEmpty() && questionImageUrl != null){
                        uploadMap.put("textImage",questionImageUrl);
                    }

                    exampleRef.set(uploadMap, SetOptions.merge());

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
            if(questionImageUrl.isEmpty() || questionImageUrl == null){
                attachImageQuestion.setColorFilter(getResources().getColor(R.color.black));
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
