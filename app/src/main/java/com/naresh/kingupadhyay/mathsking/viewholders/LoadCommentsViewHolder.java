package com.naresh.kingupadhyay.mathsking.viewholders;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.apradanas.simplelinkabletext.Link;
import com.apradanas.simplelinkabletext.LinkableEditText;
import com.apradanas.simplelinkabletext.LinkableTextView;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
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

public class LoadCommentsViewHolder extends RecyclerView.ViewHolder {
    private View view;
    private String userId;
    private String userName;
    private String userImage;
    private String text;
    private String likes;
    private boolean flagLike = false;
    private TextView likeNum;
    private TextView like;
    private Point p;
    private RelativeLayout replyreportLayout;
    private RelativeLayout editdeleteLayout;
    private TextView edittextview;
    private TextView deletetextview;
    private String currentUserId;
    private String thisDocumentId;
    private String book;
    private String chapter;
    private String documentId;
    private int tabnumber;
    private String middleCollection;
    private boolean skip;
    private List<Link> links;
    private String liveId;
    private boolean test;

    public LoadCommentsViewHolder(View itemView, final boolean same , final LinkableEditText linkableEditText) {
        super(itemView);
        view = itemView;
        SharedPreferences pref = view.getContext().getSharedPreferences("edit", Context.MODE_PRIVATE);
        SharedPreferences.Editor edt = pref.edit();
        edt.putString("txt","");
        edt.apply();

        SharedPreferences prefskip = view.getContext().getSharedPreferences("skip", Context.MODE_PRIVATE);
        skip=prefskip.getBoolean("skip",false);


        SharedPreferences prefUseId = view.getContext().getSharedPreferences("ActivityPREF", Context.MODE_PRIVATE);
        currentUserId=prefUseId.getString("uid","userkhk");
        SharedPreferences intentPref = view.getContext().getSharedPreferences("intent", Context.MODE_PRIVATE);
        book = intentPref.getString("book","book");
        chapter = intentPref.getString("chapter","chapter");
        documentId = intentPref.getString("documentId","documentId");
        tabnumber = intentPref.getInt("tabNumber",1);
        liveId = intentPref.getString("liveId","liveId");
        test = intentPref.getBoolean("test",false);

        if(tabnumber==1){
            middleCollection = "comments";
        }else if(tabnumber==2){
            middleCollection = "editorialcomment";
        }else if(tabnumber == 3){
            middleCollection = "submission";
        }
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
                    .collection("examples").document(documentId)//userId = user
                    .collection(middleCollection);
        }

        likeNum = view.findViewById(R.id.totalLike_Comment);
        like= view.findViewById(R.id.like_Comment);//todo fix it so that user can like/unlike only one time
        like.setText(R.string.like);
        final TextView replyTextView = view.findViewById(R.id.reply_Comment);
        final TextView reportTextView = view.findViewById(R.id.report_Comment);
        edittextview = view.findViewById(R.id.edit_Comment);
        deletetextview = view.findViewById(R.id.delete_Comment);
        replyTextView.setText(R.string.reply);
        reportTextView.setText(R.string.report);
        edittextview.setText(R.string.edit);
        deletetextview.setText(R.string.delete);
        replyreportLayout = itemView.findViewById(R.id.replyreportLayout_Comment);
        editdeleteLayout = itemView.findViewById(R.id.editdeleteLayout_Comment);

        edittextview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edittextview.setTextColor(view.getResources().getColor(R.color.blue));
                if(same){
                    linkableEditText.setText(text, TextView.BufferType.EDITABLE);
                    linkableEditText.setPressed(true);
                    linkableEditText.setSelection(linkableEditText.getText().length());
                    try {
                        chaptersRef.document(thisDocumentId).delete();
                    }catch (Exception e){
                    }
                }else{
                    SharedPreferences pref = view.getContext().getSharedPreferences("edit", Context.MODE_PRIVATE);
                    SharedPreferences.Editor edt = pref.edit();
                    edt.putString("txt",text);
                    edt.apply();
                    try {
                        chaptersRef.document(thisDocumentId).delete();
                    }catch (Exception e){
                    }

                    //todo delete it
                    newActivityOpen(view);

                }
            }
        });
        deletetextview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    chaptersRef.document(thisDocumentId).delete();
                }catch (Exception e){
                }
            }
        });


        RelativeLayout likeLayout = itemView.findViewById(R.id.likeLayout_Comment);
        likeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!skip){
                    SharedPreferences prefliked = view.getContext().getSharedPreferences("ActivityPREF", Context.MODE_PRIVATE);
                    flagLike = prefliked.getBoolean(thisDocumentId,false);

                    if(!flagLike){
                        likes = Integer.toString(Integer.parseInt(likes)+1);
                        likeNum.setText(likes);
                        like.setTextColor(v.getResources().getColor(R.color.blue));
                        flagLike = true;
                    }
                    else{
                        likes = Integer.toString(Integer.parseInt(likes)-1);
                        likeNum.setText(likes);
                        like.setTextColor(v.getResources().getColor(R.color.black));
                        flagLike = false;
                    }
//                    SharedPreferences prefliked = view.getContext().getSharedPreferences("ActivityPREF", Context.MODE_PRIVATE);
                    SharedPreferences.Editor likedit = prefliked.edit();
                    likedit.putBoolean(thisDocumentId,flagLike);
                    likedit.apply();

                    try {
                        chaptersRef.document(thisDocumentId).update("votes",Integer.parseInt(likes));
                    }catch (Exception e){
                    }

                    if(flagLike){
                        like.setTextColor(view.getResources().getColor(R.color.blue));
                    }
                    else{
                        like.setTextColor(view.getResources().getColor(R.color.black));
                    }


                }else{
                    Toast.makeText(view.getContext(), "First authenticate yourself", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(view.getContext(), LoginOption.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    view.getContext().startActivity(intent);
                }
            }
        });
        replyTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                replyTextView.setTextColor(view.getResources().getColor(R.color.blue));
                if(same){
                    String tempText = linkableEditText.getText().toString();
                    linkableEditText.setText(tempText+" @"+userId+" ", TextView.BufferType.EDITABLE);
                    linkableEditText.setPressed(true);
                    linkableEditText.setSelection(linkableEditText.getText().length());
                }else {
                    SharedPreferences pref = view.getContext().getSharedPreferences("edit", Context.MODE_PRIVATE);
                    SharedPreferences.Editor edt = pref.edit();
                    edt.putString("txt", " @" + userId+" ");
                    edt.apply();
//                    newActivityOpen(view);
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
        reportTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                reportTextView.setTextColor(view.getResources().getColor(R.color.blue));

            }
        });

    }

    public void setThisDocumentId(String thisDocumentId) {
        SharedPreferences prefliked = view.getContext().getSharedPreferences("ActivityPREF", Context.MODE_PRIVATE);
        flagLike = prefliked.getBoolean(thisDocumentId,false);
        if(flagLike){
            like.setTextColor(view.getResources().getColor(R.color.blue));
        }
        else{
            like.setTextColor(view.getResources().getColor(R.color.black));
        }

        this.thisDocumentId = thisDocumentId;
    }

    public void setUserName(String userName) {
        TextView nameview = view.findViewById(R.id.user_name_Comment);
        nameview.setText(userName);
        this.userName = userName;
    }

    public void setUserImage(String userImage) {
        ImageView dp = view.findViewById(R.id.userImage_Comment);
        AVLoadingIndicatorView progressBar=itemView.findViewById(R.id.progress_bar_user_image_Comment);
        progressBar.show();
        PDFTools.picassoLoadImageAvl(dp , userImage,progressBar);
        this.userImage = userImage;
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

    }

    public void setUserId(String userId) {
        this.userId = userId;
        SharedPreferences prefAdmin = view.getContext().getSharedPreferences("ActivityPREF", Context.MODE_PRIVATE);
        final String id=prefAdmin.getString("id","");
        if(userId.equals(currentUserId) || id.equals("vinayupadhyay02001@gmail.com") || id.equals("naresh03961999@gmail.com") || id.equals("rakshitaupadhyay1497@gmail.com") ) {
            replyreportLayout.setVisibility(View.GONE);
            editdeleteLayout.setVisibility(View.VISIBLE);
        }else{
            replyreportLayout.setVisibility(View.VISIBLE);
            editdeleteLayout.setVisibility(View.GONE);
        }

    }

    public void setLikes(int likesInput) {
//        likes = Integer.toString(Integer.parseInt(likes));
        likes = Integer.toString(likesInput);
        likeNum.setText(likes);
    }

    public void newActivityOpen(View v){
        SharedPreferences intentPref = view.getContext().getSharedPreferences("intent", Context.MODE_PRIVATE);
        Context context = v.getContext();
        Intent intent = new Intent(context, CommentView.class);
        intent.putExtra("book",intentPref.getString("book","book"));
        intent.putExtra("chapter", intentPref.getString("chapter","chapter"));
        intent.putExtra("documentId", intentPref.getString("documentId","documentId"));
        intent.putExtra("tab", intentPref.getString("tab","tab"));
        intent.putExtra("tabNumber",intentPref.getInt("tabNumber",1));
        context.startActivity(intent);

    }

}
