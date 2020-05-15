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


import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import static android.content.Context.CLIPBOARD_SERVICE;


public class LoadFeedbackViewHolder extends RecyclerView.ViewHolder {
    private View view;

    private Timestamp uploadingTime;
    private String uid;
    private String userName;
    private String userImage;
    private String tags;
    private String text;
    private List<Link> links;
    private String documentId;

    public LoadFeedbackViewHolder(View itemView) {
        super(itemView);
        view = itemView;
        defaultCardSettings(view);
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

        ImageView reportedit = view.findViewById(R.id.report_bar);
        reportedit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    popupMenuDelete(view);
            }
        });



    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
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
                            FirebaseFirestore db= FirebaseFirestore.getInstance();
                            DocumentReference chaptersRef = db
                                    .collection("feedback").document("live")
                                    .collection("app").document(documentId);
                            chaptersRef.delete();
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


    public void defaultCardSettings(final View view){
        TextView tagname = view.findViewById(R.id.tagsName);
        tagname.setText(R.string.tagsName);

    }

    public void setUserName(String userName) {
        TextView textViewUserName = view.findViewById(R.id.usernamefeedback);
        textViewUserName.setText(userName);
        this.userName = userName;
    }

    public void setUserImage(String userImage) {
        ImageView dp = view.findViewById(R.id.userImage_feedback);
        AVLoadingIndicatorView progressBar=view.findViewById(R.id.progress_bar_user_feedback);
        progressBar.show();
        PDFTools.picassoLoadImageAvl(dp , userImage,progressBar);
        this.userImage = userImage;
    }

    public void setUploadingTime(Timestamp uploadingTime) {
        this.uploadingTime = uploadingTime;
    }

    public void setUid(String uid) {
        this.uid = uid;
        final TextView useridfeedback = view.findViewById(R.id.useridfeedback);
        useridfeedback.setText(uid);
        useridfeedback.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                ClipboardManager clipboard = (ClipboardManager) view.getContext().getSystemService(CLIPBOARD_SERVICE);
                ClipData cData = ClipData.newPlainText("text", useridfeedback.getText());
                clipboard.setPrimaryClip(cData);
                Toast.makeText(view.getContext(), "Copied", Toast.LENGTH_SHORT).show();
                return true;
            }
        });

    }

    public void setTags(String tags) {
        this.tags = tags;
        TextView tagsval = view.findViewById(R.id.tagsValues);
        tagsval.setText(tags);
    }

    public void setText(String text) {
        this.text = text;
        final LinkableTextView textView = (LinkableTextView) view.findViewById(R.id.text_feedback);
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


}


