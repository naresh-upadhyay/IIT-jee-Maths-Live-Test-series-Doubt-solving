package com.naresh.kingupadhyay.mathsking.viewholders;


import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.apradanas.simplelinkabletext.Link;
import com.apradanas.simplelinkabletext.LinkableTextView;
import com.naresh.kingupadhyay.mathsking.R;
import com.naresh.kingupadhyay.mathsking.activities.Basic_activity;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import static android.content.Context.CLIPBOARD_SERVICE;

public class LoadInstructionsViewHolder extends RecyclerView.ViewHolder {
    private View view;
    private String text;
//    private List<Link> links;

    public LoadInstructionsViewHolder(View itemView) {
        super(itemView);
        view = itemView;
//        Link linkUsername = new Link(Pattern.compile("(\\s+@\\w+)"))
//                .setUnderlined(false)
//                .setTextColor(R.color.greenBlue)
//                .setTextStyle(Link.TextStyle.BOLD)
//                .setClickListener(new Link.OnClickListener() {
//                    @Override
//                    public void onClick(String text) {
//                        // do something
//                        Toast.makeText(view.getContext(), "Clicked username: " +text, Toast.LENGTH_SHORT).show();
//
//                    }
//                });
//
//
//        Link linkNormal = new Link(Pattern.compile("([A-Za-z0-9.-~`!@#$%^&*()_+=-|}{';:/.,<>?])"))
//                .setUnderlined(false)
//                .setTextStyle(Link.TextStyle.NORMAL);
//
//        Link linkUrl = new Link(Pattern.compile("(([A-Za-z]{3,9}:(?://)?)(?:[-;:&=+$,\\w]+@)?[A-Za-z0-9.-]+|(?:www\\.|[-;:&=+$,\\w]+@)[A-Za-z0-9.-]+)((?:/[+~%/.\\w-]*)?\\??(?:[-+=&;%@.\\w]*)#?(?:[.!/\\\\\\w]*))?"))
//                .setUnderlined(true)
//                .setTextColor(R.color.star10)
//                .setTextStyle(Link.TextStyle.BOLD)
//                .setClickListener(new Link.OnClickListener() {
//                    @Override
//                    public void onClick(String text) {
//                        // do something
//                        Context context = view.getContext();
////                        Toast.makeText(context, "Clicked link: " +text, Toast.LENGTH_SHORT).show();
//                        Intent intent = new Intent(context, Basic_activity.class);
//                        intent.putExtra("pdfUrl",text);
//                        intent.putExtra("titleNoti","Clicked Link");
//                        context.startActivity(intent);
//
//                    }
//                });
//
//
//        links = new ArrayList<>();
//        links.add(linkUrl);
//        links.add(linkUsername);
//        links.add(linkNormal);
//        final LinkableTextView textView = view.findViewById(R.id.textLinkableTextViewInstruciton);
//        textView.setVisibility(View.VISIBLE);

    }

    public void setText(String text) {
        this.text = text;
        TextView textView = view.findViewById(R.id.conceptTitle);
        textView.setText(text);
//        textView.setText(text).addLinks(links).build();
//
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
}


