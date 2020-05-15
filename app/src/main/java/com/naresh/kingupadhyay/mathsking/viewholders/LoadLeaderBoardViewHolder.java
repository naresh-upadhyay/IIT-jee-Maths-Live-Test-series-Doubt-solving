package com.naresh.kingupadhyay.mathsking.viewholders;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.naresh.kingupadhyay.mathsking.Database.PDFTools;
import com.naresh.kingupadhyay.mathsking.R;
import com.wang.avi.AVLoadingIndicatorView;

public class LoadLeaderBoardViewHolder extends RecyclerView.ViewHolder {
    private View view;
    private String userId;
    private String userName;
    private String userImage;
    private String currentUserId;
    private int Rank;

    public LoadLeaderBoardViewHolder(View itemView) {
        super(itemView);
        view = itemView;
        SharedPreferences prefUseId = view.getContext().getSharedPreferences("ActivityPREF", Context.MODE_PRIVATE);
        currentUserId=prefUseId.getString("uid","userkhk");
    }

    public void setUserName(String userName) {
        this.userName = userName;
        TextView nameview = view.findViewById(R.id.participantName);
        nameview.setText(userName);
    }

    public void setUserImage(String userImage) {
        this.userImage = userImage;
        ImageView dp = view.findViewById(R.id.userImage_post_comment2);
        AVLoadingIndicatorView progressBar=itemView.findViewById(R.id.progress_bar_user_image_post_comment2);
        progressBar.show();
        PDFTools.picassoLoadImageAvl(dp , userImage,progressBar);
    }

    public void setUserId(String userId) {
        this.userId = userId;
        TextView nameview = view.findViewById(R.id.participantName);
        TextView participantRank = view.findViewById(R.id.participantRank);
        if(userId.equals(currentUserId)){
            nameview.setTextColor(view.getResources().getColor(R.color.blue));
            participantRank.setTextColor(view.getResources().getColor(R.color.blue));
        }else{
            nameview.setTextColor(view.getResources().getColor(R.color.black));
            participantRank.setTextColor(view.getResources().getColor(R.color.black));
        }

    }

    public void setRank(int rank) {
        Rank = rank;
        TextView participantRank = view.findViewById(R.id.participantRank);
        String txt = Integer.toString(rank);
        participantRank.setText(txt);
    }
}
