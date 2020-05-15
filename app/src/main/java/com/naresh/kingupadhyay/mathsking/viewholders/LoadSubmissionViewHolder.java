package com.naresh.kingupadhyay.mathsking.viewholders;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Handler;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.RecyclerView;

import com.apradanas.simplelinkabletext.Link;
import com.apradanas.simplelinkabletext.LinkableEditText;
import com.apradanas.simplelinkabletext.LinkableTextView;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.naresh.kingupadhyay.mathsking.Database.PDFTools;
import com.naresh.kingupadhyay.mathsking.R;
import com.naresh.kingupadhyay.mathsking.UiMaterial.LoginOption;
import com.naresh.kingupadhyay.mathsking.activities.Basic_activity;
import com.naresh.kingupadhyay.mathsking.activities.CommentView;
import com.wang.avi.AVLoadingIndicatorView;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import static android.content.Context.CLIPBOARD_SERVICE;

public class LoadSubmissionViewHolder extends RecyclerView.ViewHolder {
    private View view;
    private String userId;
    private String userName;
    private String userImage;
    private String text;
    private String subImage;
    private String votes;
    private boolean flagUpVote = false;
    private boolean flagDownVote = false;
    private TextView userNameView;
    private TextView textView;
    private TextView voteText;
    private TextView upvote;
    private TextView downvote;
    private TextView reply;
    private TextView edit;
    private LinearLayout upvoteLayout;
    private LinearLayout downvoteLayout;
    private LinearLayout replyLayout;
    private String currentUserId;
    private ImageView replyImage;
    private String thisDocumentId;
    private String book;
    private String chapter;
    private String documentId;
    private String tab;
    private int tabnumber;
    private String middleCollection;
    private boolean skip;
    private ImageView upvoteImage;
    private ImageView downvoteImage;
    private List<Link> links;
    private String liveId;
    private boolean test;


    //    private EditText editText1;
    public LoadSubmissionViewHolder(View itemView, final boolean same , final LinkableEditText linkableEditText, final ImageView attachImage) {
        super(itemView);
        view = itemView;
//        editText1=editText;
        defaultSetting(view);


        SharedPreferences pref = view.getContext().getSharedPreferences("edit", Context.MODE_PRIVATE);
        SharedPreferences.Editor edt = pref.edit();
        edt.putString("txt","");
        edt.apply();

        SharedPreferences prefskip = view.getContext().getSharedPreferences("skip", Context.MODE_PRIVATE);
        skip=prefskip.getBoolean("skip",false);


        SharedPreferences prefUseId = view.getContext().getSharedPreferences("ActivityPREF", Context.MODE_PRIVATE);
        currentUserId=prefUseId.getString("uid","userghgj");

        SharedPreferences intentPref = view.getContext().getSharedPreferences("intent", Context.MODE_PRIVATE);
        book = intentPref.getString("book","book");
        chapter = intentPref.getString("chapter","chapter");
        documentId = intentPref.getString("documentId","documentId");
        liveId = intentPref.getString("liveId","liveId");
        test = intentPref.getBoolean("test",false);
        tab = intentPref.getString("tab","tab");
        tabnumber = intentPref.getInt("tabNumber",1);
        if(tabnumber==1){
            middleCollection = "comments";
        }else if(tabnumber==2){
            middleCollection = "editorialcomment";
        }else if(tabnumber == 3){
            middleCollection = "submission";
        }

        final FirebaseFirestore db= FirebaseFirestore.getInstance();

        final CollectionReference chaptersRef;
        if(test){
            chaptersRef = db
                    .collection("test_series").document("branches")
                    .collection(liveId).document(documentId)
                    .collection(middleCollection);
        }else{
            chaptersRef = db.collection("chapters").document(book)
                    .collection(chapter).document("branches")
                    .collection(tab).document(documentId)//userId = user
                    .collection(middleCollection);

        }

        upvoteImage = view.findViewById(R.id.upvoteImage_submission);
        downvoteImage = view.findViewById(R.id.downImage_submission);
        replyImage = view.findViewById(R.id.replyImagesub_submission);
        upvoteLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!skip){
                    SharedPreferences prefliked = view.getContext().getSharedPreferences("ActivityPREF", Context.MODE_PRIVATE);
                    flagUpVote = prefliked.getBoolean(thisDocumentId+"upvote",false);
                    flagDownVote = prefliked.getBoolean(thisDocumentId+"downvote",false);

                    downvoteImage.setColorFilter(view.getContext().getResources().getColor(R.color.black));
                    downvote.setTextColor(view.getResources().getColor(R.color.black));
                    if(!flagUpVote){
                        if(flagDownVote){
                            votes = Integer.toString(Integer.parseInt(votes)+1);
                        }
                        votes = Integer.toString(Integer.parseInt(votes)+1);
                        voteText.setText(votes);

                        upvoteImage.setColorFilter(view.getContext().getResources().getColor(R.color.blue));
                        upvote.setTextColor(view.getResources().getColor(R.color.blue));
                        flagUpVote = true;
                    }else{
                        if(flagDownVote){
                            votes = Integer.toString(Integer.parseInt(votes)+1);
                        }
                        votes = Integer.toString(Integer.parseInt(votes)-1);
                        voteText.setText(votes);

                        upvoteImage.setColorFilter(view.getContext().getResources().getColor(R.color.black));
                        upvote.setTextColor(view.getResources().getColor(R.color.black));
                        flagUpVote = false;
                    }
                    flagDownVote = false;
                    SharedPreferences.Editor likedit = prefliked.edit();
                    likedit.putBoolean(thisDocumentId+"upvote",flagUpVote);
                    likedit.putBoolean(thisDocumentId+"downvote",flagDownVote);
                    likedit.apply();

                    try {
                        chaptersRef.document(thisDocumentId).update("votes",Integer.parseInt(votes));
                    }catch (Exception e){
                    }
                    if(flagUpVote){
                        upvoteImage.setColorFilter(view.getContext().getResources().getColor(R.color.blue));
                        upvote.setTextColor(view.getResources().getColor(R.color.blue));
                    }else{
                        upvoteImage.setColorFilter(view.getContext().getResources().getColor(R.color.black));
                        upvote.setTextColor(view.getResources().getColor(R.color.black));
                    }
                    if(flagDownVote){
                        downvote.setTextColor(view.getResources().getColor(R.color.blue));
                        downvoteImage.setColorFilter(view.getContext().getResources().getColor(R.color.blue));
                    }else{
                        downvote.setTextColor(view.getResources().getColor(R.color.black));
                        downvoteImage.setColorFilter(view.getContext().getResources().getColor(R.color.black));
                    }



                }else{
                    Toast.makeText(view.getContext(), "First authenticate yourself", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(view.getContext(), LoginOption.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    view.getContext().startActivity(intent);

                }
            }
        });
        downvoteLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!skip){
                    SharedPreferences prefliked = view.getContext().getSharedPreferences("ActivityPREF", Context.MODE_PRIVATE);
                    flagUpVote = prefliked.getBoolean(thisDocumentId+"upvote",false);
                    flagDownVote = prefliked.getBoolean(thisDocumentId+"downvote",false);

                    upvoteImage.setColorFilter(view.getContext().getResources().getColor(R.color.black));
                    upvote.setTextColor(view.getResources().getColor(R.color.black));
                    if(!flagDownVote){
                        if(flagUpVote){
                            votes = Integer.toString(Integer.parseInt(votes)-1);
                        }
                        votes = Integer.toString(Integer.parseInt(votes)-1);
                        voteText.setText(votes);

                        downvote.setTextColor(view.getResources().getColor(R.color.blue));
                        downvoteImage.setColorFilter(view.getContext().getResources().getColor(R.color.blue));
                        flagDownVote = true;
                    }else{
                        if(flagUpVote){
                            votes = Integer.toString(Integer.parseInt(votes)-1);
                        }
                        votes = Integer.toString(Integer.parseInt(votes)+1);
                        voteText.setText(votes);

                        downvote.setTextColor(view.getResources().getColor(R.color.black));
                        downvoteImage.setColorFilter(view.getContext().getResources().getColor(R.color.black));
                        flagDownVote = false;
                    }
                    flagUpVote = false;
                    SharedPreferences.Editor likedit = prefliked.edit();
                    likedit.putBoolean(thisDocumentId+"upvote",flagUpVote);
                    likedit.putBoolean(thisDocumentId+"downvote",flagDownVote);
                    likedit.apply();

                    try {
                        chaptersRef.document(thisDocumentId).update("votes",Integer.parseInt(votes));
                    }catch (Exception e){
                    }

                    if(flagUpVote){
                        upvoteImage.setColorFilter(view.getContext().getResources().getColor(R.color.blue));
                        upvote.setTextColor(view.getResources().getColor(R.color.blue));
                    }else{
                        upvoteImage.setColorFilter(view.getContext().getResources().getColor(R.color.black));
                        upvote.setTextColor(view.getResources().getColor(R.color.black));
                    }
                    if(flagDownVote){
                        downvote.setTextColor(view.getResources().getColor(R.color.blue));
                        downvoteImage.setColorFilter(view.getContext().getResources().getColor(R.color.blue));
                    }else{
                        downvote.setTextColor(view.getResources().getColor(R.color.black));
                        downvoteImage.setColorFilter(view.getContext().getResources().getColor(R.color.black));
                    }


                }else{
                    Toast.makeText(view.getContext(), "First authenticate yourself", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(view.getContext(), LoginOption.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    view.getContext().startActivity(intent);
                }
            }
        });

        replyLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

//                //todo take (@userId username) and then show into comment box
                reply.setTextColor(view.getResources().getColor(R.color.blue));
                edit.setTextColor(view.getResources().getColor(R.color.blue));
                replyImage.setColorFilter(view.getContext().getResources().getColor(R.color.blue));

                if(same){
                    if(userId.equals(currentUserId)) {//todo this is edit
                        linkableEditText.setText(text, TextView.BufferType.EDITABLE);
                        linkableEditText.setPressed(true);
                        linkableEditText.setSelection(linkableEditText.getText().length());
                        //todo then delete previous entry
                        try {
                            Uri uri =  Uri.parse(subImage);
                            String path = uri.getPath();
                            int cut = path.lastIndexOf('/');
                            String imageFilename="";
                            if (cut != -1) {
                                imageFilename = path.substring(cut + 1);
                            }
                            SharedPreferences prefEditu = view.getContext().getSharedPreferences("edit", Context.MODE_PRIVATE);
                            SharedPreferences.Editor edtu = prefEditu.edit();
                            if (!subImage.isEmpty() && subImage != null){
                                edtu.putBoolean("image",true);
                                attachImage.setColorFilter(view.getResources().getColor(R.color.blue));
                            }
                            else
                                edtu.putBoolean("image",false);
                            edtu.putString("imageUrl",subImage);
                            edtu.apply();

//                            Toast.makeText(view.getContext(), imageFilename, Toast.LENGTH_LONG).show();

                        }catch (Exception e){
                        }
                        try {
//                            deleteStoredImage("images/examples/submissions",imageFilename);
                            chaptersRef.document(thisDocumentId).delete();

                        }catch (Exception e){
                        }

                    }else{
                        String tempText = linkableEditText.getText().toString();
                        linkableEditText.setText(tempText+" @"+userId+" ", TextView.BufferType.EDITABLE);
                        linkableEditText.setPressed(true);
                        linkableEditText.setSelection(linkableEditText.getText().length());
                    }

                }else{
                    if(userId.equals(currentUserId)){//todo this is edit
                        SharedPreferences pref = view.getContext().getSharedPreferences("edit", Context.MODE_PRIVATE);
                        SharedPreferences.Editor edt = pref.edit();
                        edt.putString("txt",text);
                        edt.apply();
                        try {
                            Uri uri =  Uri.parse(subImage);
                            String path = uri.getPath();
                            int cut = path.lastIndexOf('/');
                            String imageFilename="";
                            if (cut != -1) {
                                imageFilename = path.substring(cut + 1);
                            }
                            SharedPreferences prefEditu = view.getContext().getSharedPreferences("edit", Context.MODE_PRIVATE);
                            SharedPreferences.Editor edtu = prefEditu.edit();
                            if (!subImage.isEmpty() && subImage != null)
                                edtu.putBoolean("image",true);
                            else
                                edtu.putBoolean("image",false);
                            edtu.putString("imageUrl",subImage);
                            edtu.apply();

//                            Toast.makeText(view.getContext(), imageFilename, Toast.LENGTH_LONG).show();

                        }catch (Exception e){
                        }
                        try {
//                            deleteStoredImage("images/examples/submissions",imageFilename);
                            chaptersRef.document(thisDocumentId).delete();

                        }catch (Exception e){
                        }

                    }else{
                        SharedPreferences pref = view.getContext().getSharedPreferences("edit", Context.MODE_PRIVATE);
                        SharedPreferences.Editor edt = pref.edit();
                        edt.putString("txt"," @"+userId+" ");
                        edt.apply();
                    }
                    if (!skip)
                        newActivityOpen(view);
                    else{
                        Toast.makeText(view.getContext(), "First authenticate yourself", Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(view.getContext(), LoginOption.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        view.getContext().startActivity(intent);
                    }
                }
            }
        });

        SharedPreferences prefAdmin = view.getContext().getSharedPreferences("ActivityPREF", Context.MODE_PRIVATE);
        final String id=prefAdmin.getString("id","");

        ImageView reportedit = view.findViewById(R.id.report_bar);
        reportedit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(userId.equals(currentUserId) || id.equals("vinayupadhyay02001@gmail.com") || id.equals("naresh03961999@gmail.com") || id.equals("rakshitaupadhyay1497@gmail.com") ) {
                    popupMenuDelete(view);
                }else{
                    popupMenuReport(view);
                }

            }
        });

        Link linkUsername = new Link(Pattern.compile("(\\s+@\\w+)"))
                .setUnderlined(false)
                .setTextColor(R.color.greenBlue)
                .setTextStyle(Link.TextStyle.BOLD)
                .setClickListener(new Link.OnClickListener() {
                    @Override
                    public void onClick(String text) {
                        // do something
                        Toast.makeText(view.getContext(), "Clicked username: " +text, Toast.LENGTH_SHORT).show();

                    }
                });


        Link linkNormal = new Link(Pattern.compile("([A-Za-z0-9.-~`!@#$%^&*()_+=-|}{';:/.,<>?])"))
                .setUnderlined(false)
                .setTextStyle(Link.TextStyle.NORMAL);

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


        links = new ArrayList<>();
        links.add(linkUrl);
        links.add(linkUsername);
        links.add(linkNormal);


    }

    private void popupMenuDelete(final View v) {
        //Creating the instance of PopupMenu
        PopupMenu popup = new PopupMenu(v.getContext(),v );
        // to inflate the menu resource (defined in XML) into the PopupMenu
        popup.getMenuInflater().inflate(R.menu.option_delete, popup.getMenu());
        //popup with OnMenuItemClickListener
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()){
                    case R.id.delete_popup :
                        try {
                            Uri uri =  Uri.parse(subImage);
                            String path = uri.getPath();
                            int cut = path.lastIndexOf('/');
                            String imageFilename="";
                            if (cut != -1) {
                                imageFilename = path.substring(cut + 1);
                            }
                            deleteStoredImage("images/examples/submissions",imageFilename);
//                            Toast.makeText(view.getContext(), imageFilename, Toast.LENGTH_LONG).show();

                        }catch (Exception e){
                        }
                        try {
//                            deleteStoredImage("images/examples/submissions",imageFilename);
                            FirebaseFirestore db= FirebaseFirestore.getInstance();
                            CollectionReference chaptersRef = db
                                    .collection("chapters").document(book)
                                    .collection(chapter).document("branches")
                                    .collection(tab).document(documentId)//userId = user
                                    .collection(middleCollection);
                            chaptersRef.document(thisDocumentId).delete();
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

    public void setThisDocumentId(String thisDocumentId) {
        SharedPreferences prefliked = view.getContext().getSharedPreferences("ActivityPREF", Context.MODE_PRIVATE);
        flagUpVote = prefliked.getBoolean(thisDocumentId+"upvote",false);
        flagDownVote = prefliked.getBoolean(thisDocumentId+"downvote",false);
        upvoteImage.setColorFilter(view.getContext().getResources().getColor(R.color.black));
        upvote.setTextColor(view.getResources().getColor(R.color.black));
        downvoteImage.setColorFilter(view.getContext().getResources().getColor(R.color.black));
        downvote.setTextColor(view.getResources().getColor(R.color.black));

        if(flagUpVote){
            upvoteImage.setColorFilter(view.getContext().getResources().getColor(R.color.blue));
            upvote.setTextColor(view.getResources().getColor(R.color.blue));
        }else{
            upvoteImage.setColorFilter(view.getContext().getResources().getColor(R.color.black));
            upvote.setTextColor(view.getResources().getColor(R.color.black));
        }
        if(flagDownVote){
            downvote.setTextColor(view.getResources().getColor(R.color.blue));
            downvoteImage.setColorFilter(view.getContext().getResources().getColor(R.color.blue));
        }else{
            downvote.setTextColor(view.getResources().getColor(R.color.black));
            downvoteImage.setColorFilter(view.getContext().getResources().getColor(R.color.black));
        }
        this.thisDocumentId = thisDocumentId;
    }

    public void newActivityOpen(View v){
        SharedPreferences intentPref = view.getContext().getSharedPreferences("intent", Context.MODE_PRIVATE);
        Context context = v.getContext();
        Intent intent = new Intent(context, CommentView.class);
        intent.putExtra("book",intentPref.getString("book","book"));
        intent.putExtra("chapter", intentPref.getString("chapter","chapter"));
        intent.putExtra("documentId", intentPref.getString("documentId","documentId"));
        intent.putExtra("tab", intentPref.getString("tab","tab"));
        intent.putExtra("tabNumber",intentPref.getInt("tabNumber",3));
        context.startActivity(intent);

    }

    public void setUserName(String userName) {
        userNameView.setText(userName);
        this.userName = userName;
    }

    public void setUserImage(String userImage) {
        ImageView imageViewUser = view.findViewById(R.id.userImage_submission);
        AVLoadingIndicatorView progressbarUser = view.findViewById(R.id.progress_bar_user_image_submission);
        progressbarUser.show();
        PDFTools.picassoLoadImageAvl(imageViewUser,userImage,progressbarUser);
        this.userImage = userImage;
    }

    public void setUserId(String userId) {
        this.userId = userId;
        if(userId.equals(currentUserId)){//todo this is to be edited
            reply.setVisibility(View.GONE);
            edit.setVisibility(View.VISIBLE);
            replyImage.setImageResource(R.drawable.ic_action_edit);
        }else{
            reply.setVisibility(View.VISIBLE);
            edit.setVisibility(View.GONE);
            replyImage.setImageResource(R.drawable.ic_action_comments);
        }

    }

    public void setText(String text) {
        this.text = text;
        final LinkableTextView textView = (LinkableTextView) view.findViewById(R.id.text_submission);
        textView.setText(text).addLinks(links).build();
        textView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                ClipboardManager clipboard = (ClipboardManager) view.getContext().getSystemService(CLIPBOARD_SERVICE);
                ClipData cData = ClipData.newPlainText("text", textView.getText());
                clipboard.setPrimaryClip(cData);
                Toast.makeText(view.getContext(), "Copied", Toast.LENGTH_SHORT).show();
                return true;
            }
        });
    }

    public void setSubImage(String subImage) {
        this.subImage = subImage;
        final ImageView imageViewAnswer = view.findViewById(R.id.solutonImage_submission);
        AVLoadingIndicatorView progressbarUser = view.findViewById(R.id.progress_bar_user_image_submission);
        progressbarUser.show();
        PDFTools.picassoLoadImageAvl(imageViewAnswer,subImage,progressbarUser);
        imageViewAnswer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PDFTools.showPopupImage(imageViewAnswer,view);
            }
        });

    }

    public void setVotes(int votesInput) {
        votes = Integer.toString(votesInput);
        voteText.setText(votes);
    }

    private void defaultSetting(View view){
        upvote = view.findViewById(R.id.upvoteText_submission);
        downvote= view.findViewById(R.id.downText_submission);
        reply= view.findViewById(R.id.replyTextsub_submission);
        edit = view.findViewById(R.id.editTextsub_submission);

        userNameView = view.findViewById(R.id.user_name_submission);
        textView = view.findViewById(R.id.text_submission);
        voteText= view.findViewById(R.id.voteText_submission);

        upvoteLayout = view.findViewById(R.id.upvoteLayout_submission);
        downvoteLayout = view.findViewById(R.id.downvoteLayout_submission);
        replyLayout = view.findViewById(R.id.replyLayoutsub_submission);

        upvote.setText(R. string.upvote);
        downvote.setText(R. string.downvote);
        reply.setText(R. string.reply);
        edit.setText(R.string.edit);
    }
}
