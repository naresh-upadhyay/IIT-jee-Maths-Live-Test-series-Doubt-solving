package com.naresh.kingupadhyay.mathsking.viewholders;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.net.Uri;
import android.os.Build;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatRatingBar;
import androidx.appcompat.widget.PopupMenu;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.apradanas.simplelinkabletext.Link;
import com.apradanas.simplelinkabletext.LinkableTextView;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.naresh.kingupadhyay.mathsking.Database.PDFTools;
import com.naresh.kingupadhyay.mathsking.R;
import com.naresh.kingupadhyay.mathsking.UiMaterial.LoginOption;
import com.naresh.kingupadhyay.mathsking.activities.Basic_activity;
import com.naresh.kingupadhyay.mathsking.activities.PostDoubt;
import com.naresh.kingupadhyay.mathsking.activities.ViewCardDoubts;
import com.wang.avi.AVLoadingIndicatorView;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import static android.content.Context.CLIPBOARD_SERVICE;

public class LoadSuggestionsViewHolder extends RecyclerView.ViewHolder {
    private View view;

    private String book;
    private String chapter;
    private Timestamp uploadingTime;
    private String userId;
    private String userName;
    private int userRating;
    private String userImage;

    private String tags;
    private String text;
    private String textImageUrl;
    private AppCompatRatingBar ratingBar;
    private String documentId;
    private String currentUserId;
    private String questionImageUrl="";
    private FirebaseFirestore db;
    private LinearLayout upvoteLayout;
    private LinearLayout downvoteLayout;
    private LinearLayout replyLayout;
    private boolean skip;
    private ImageView upvoteImage;
    private ImageView downvoteImage;
    private boolean flagUpVote = false;
    private boolean flagDownVote = false;
    private TextView upvote;
    private TextView downvote;
    private TextView voteText;
    private String votes;
    private String tab;
    private List<Link> links;

    public LoadSuggestionsViewHolder(View itemView) {
        super(itemView);
        view = itemView;
        defaultCardSettings(view);
        db= FirebaseFirestore.getInstance();

        SharedPreferences prefUseId = view.getContext().getSharedPreferences("ActivityPREF", Context.MODE_PRIVATE);
        currentUserId=prefUseId.getString("uid","userkhk");


        SharedPreferences pref = view.getContext().getSharedPreferences("course", Context.MODE_PRIVATE);
        book = pref.getString("book","book");
        chapter = pref.getString("chapter","chapter");
        tab = pref.getString("tab","suggestions");

        SharedPreferences prefskip = view.getContext().getSharedPreferences("skip", Context.MODE_PRIVATE);
        skip=prefskip.getBoolean("skip",false);


        final FirebaseFirestore db= FirebaseFirestore.getInstance();
        final CollectionReference votesRef = db
                .collection("chapters").document(book)
                .collection(chapter).document("branches")
                .collection(tab);//userId = user

        LinearLayout replyLayoutsub = view.findViewById(R.id.replyLayoutsub);
        replyLayoutsub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newActivityOpen(v);
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

        upvoteImage = view.findViewById(R.id.upvoteImage);
        downvoteImage = view.findViewById(R.id.downImage);
        upvoteLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!skip){
                    SharedPreferences prefliked = view.getContext().getSharedPreferences("ActivityPREF", Context.MODE_PRIVATE);
                    flagUpVote = prefliked.getBoolean(documentId+"upvote",false);
                    flagDownVote = prefliked.getBoolean(documentId+"downvote",false);

                    downvoteImage.setColorFilter(view.getContext().getResources().getColor(R.color.white));
                    downvote.setTextColor(view.getResources().getColor(R.color.white));
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

                        upvoteImage.setColorFilter(view.getContext().getResources().getColor(R.color.white));
                        upvote.setTextColor(view.getResources().getColor(R.color.white));
                        flagUpVote = false;
                    }
                    flagDownVote = false;
                    SharedPreferences.Editor likedit = prefliked.edit();
                    likedit.putBoolean(documentId+"upvote",flagUpVote);
                    likedit.putBoolean(documentId+"downvote",flagDownVote);
                    likedit.apply();

                    try {
                        votesRef.document(documentId).update("votes",Integer.parseInt(votes));
                    }catch (Exception e){
                    }
                    if(flagUpVote){
                        upvoteImage.setColorFilter(view.getContext().getResources().getColor(R.color.blue));
                        upvote.setTextColor(view.getResources().getColor(R.color.blue));
                    }else{
                        upvoteImage.setColorFilter(view.getContext().getResources().getColor(R.color.white));
                        upvote.setTextColor(view.getResources().getColor(R.color.white));
                    }
                    if(flagDownVote){
                        downvote.setTextColor(view.getResources().getColor(R.color.blue));
                        downvoteImage.setColorFilter(view.getContext().getResources().getColor(R.color.blue));
                    }else{
                        downvote.setTextColor(view.getResources().getColor(R.color.white));
                        downvoteImage.setColorFilter(view.getContext().getResources().getColor(R.color.white));
                    }



                }else{
                    Toast.makeText(view.getContext(), "First authenticate yourself", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(view.getContext(), LoginOption.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    view.getContext().startActivity(intent);

                }
            }
        });
        downvoteLayout.setVisibility(View.INVISIBLE);
        downvoteLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!skip){
                    SharedPreferences prefliked = view.getContext().getSharedPreferences("ActivityPREF", Context.MODE_PRIVATE);
                    flagUpVote = prefliked.getBoolean(documentId+"upvote",false);
                    flagDownVote = prefliked.getBoolean(documentId+"downvote",false);

                    upvoteImage.setColorFilter(view.getContext().getResources().getColor(R.color.white));
                    upvote.setTextColor(view.getResources().getColor(R.color.white));
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

                        downvote.setTextColor(view.getResources().getColor(R.color.white));
                        downvoteImage.setColorFilter(view.getContext().getResources().getColor(R.color.white));
                        flagDownVote = false;
                    }
                    flagUpVote = false;
                    SharedPreferences.Editor likedit = prefliked.edit();
                    likedit.putBoolean(documentId+"upvote",flagUpVote);
                    likedit.putBoolean(documentId+"downvote",flagDownVote);
                    likedit.apply();

                    try {
                        votesRef.document(documentId).update("votes",Integer.parseInt(votes));
                    }catch (Exception e){
                    }

                    if(flagUpVote){
                        upvoteImage.setColorFilter(view.getContext().getResources().getColor(R.color.blue));
                        upvote.setTextColor(view.getResources().getColor(R.color.blue));
                    }else{
                        upvoteImage.setColorFilter(view.getContext().getResources().getColor(R.color.white));
                        upvote.setTextColor(view.getResources().getColor(R.color.white));
                    }
                    if(flagDownVote){
                        downvote.setTextColor(view.getResources().getColor(R.color.blue));
                        downvoteImage.setColorFilter(view.getContext().getResources().getColor(R.color.blue));
                    }else{
                        downvote.setTextColor(view.getResources().getColor(R.color.white));
                        downvoteImage.setColorFilter(view.getContext().getResources().getColor(R.color.white));
                    }


                }else{
                    Toast.makeText(view.getContext(), "First authenticate yourself", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(view.getContext(), LoginOption.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    view.getContext().startActivity(intent);
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
    public void newActivityOpen(View v){
        Context context = v.getContext();
        Intent intent = new Intent(context, ViewCardDoubts.class);
        intent.putExtra("book",book);
        intent.putExtra("chapter", chapter);
        intent.putExtra("userId", userId);
        intent.putExtra("userName", userName);
        intent.putExtra("userRating", userRating);
        intent.putExtra("userImage", userImage);
        intent.putExtra("tags", tags);
        intent.putExtra("text", text);
        intent.putExtra("textImageUrl", textImageUrl);
        intent.putExtra("votes", votes);
        intent.putExtra("tab", tab);
        intent.putExtra("documentId",documentId);
        context.startActivity(intent);

    }

    public void setVotes(int votesInput) {
        votes = Integer.toString(votesInput);
        voteText.setText(votes);
    }

    public void defaultCardSettings(View view){
        TextView tagname = view.findViewById(R.id.tagsName);
        upvote = view.findViewById(R.id.upvoteText);
        downvote = view.findViewById(R.id.downText);
        TextView reply = view.findViewById(R.id.replyTextsub);
        upvoteLayout = view.findViewById(R.id.upvoteLayout);
        downvoteLayout = view.findViewById(R.id.downvoteLayout);
        replyLayout = view.findViewById(R.id.replyLayoutsub);
        voteText= view.findViewById(R.id.voteText);

        tagname.setText(R.string.tagsName);
        upvote.setText(R.string.upvote);
        downvote.setText(R.string.downvote);
        reply.setText(R.string.reply);
    }


    public void setDocumentId(String documentId) {
        SharedPreferences prefliked = view.getContext().getSharedPreferences("ActivityPREF", Context.MODE_PRIVATE);
        flagUpVote = prefliked.getBoolean(documentId+"upvote",false);
        flagDownVote = prefliked.getBoolean(documentId+"downvote",false);
        upvoteImage.setColorFilter(view.getContext().getResources().getColor(R.color.white));
        upvote.setTextColor(view.getResources().getColor(R.color.white));
        downvoteImage.setColorFilter(view.getContext().getResources().getColor(R.color.white));
        downvote.setTextColor(view.getResources().getColor(R.color.white));

        if(flagUpVote){
            upvoteImage.setColorFilter(view.getContext().getResources().getColor(R.color.blue));
            upvote.setTextColor(view.getResources().getColor(R.color.blue));
        }else{
            upvoteImage.setColorFilter(view.getContext().getResources().getColor(R.color.white));
            upvote.setTextColor(view.getResources().getColor(R.color.white));
        }
        if(flagDownVote){
            downvote.setTextColor(view.getResources().getColor(R.color.blue));
            downvoteImage.setColorFilter(view.getContext().getResources().getColor(R.color.blue));
        }else{
            downvote.setTextColor(view.getResources().getColor(R.color.white));
            downvoteImage.setColorFilter(view.getContext().getResources().getColor(R.color.white));
        }

        this.documentId = documentId;
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
        final LinkableTextView textView = (LinkableTextView) view.findViewById(R.id.text_Comment);
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


//        final TextView textView = view.findViewById(R.id.conceptTitle);
//        textView.setText(text);
//        textView.setOnLongClickListener(new View.OnLongClickListener() {
//            @Override
//            public boolean onLongClick(View view) {
//                ClipboardManager clipboard = (ClipboardManager) view.getContext().getSystemService(CLIPBOARD_SERVICE);
//                ClipData cData = ClipData.newPlainText("text", textView.getText());
//                clipboard.setPrimaryClip(cData);
//                Toast.makeText(view.getContext(), "Copied", Toast.LENGTH_SHORT).show();
//                return true;
//            }
//        });

    }

    public void setTextImageUrl(String textImageUrl) {
        this.textImageUrl = textImageUrl;
        final ImageView questionImg=view.findViewById(R.id.questionImage);
        AVLoadingIndicatorView progressBar=itemView.findViewById(R.id.progress_bar_user_image);
//        progressBar.show();
        PDFTools.picassoLoadImageAvl(questionImg , textImageUrl,progressBar);
        questionImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PDFTools.showPopupImage(questionImg,view);
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
                        }catch (Exception e){
                        }
                        final DocumentReference userRef = db
                                .collection("chapters").document(book)
                                .collection(chapter).document("branches")
                                .collection(tab).document(documentId);//userId = user
                        userRef.delete();

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
        Intent intent = new Intent(context, PostDoubt.class);
        intent.putExtra("book",book);
        intent.putExtra("chapter", chapter);
        intent.putExtra("tags", tags);
        intent.putExtra("text", text);
        intent.putExtra("textImageUrl", questionImageUrl);
        intent.putExtra("votes", Integer.parseInt(votes));
        intent.putExtra("tab",tab);
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



}

