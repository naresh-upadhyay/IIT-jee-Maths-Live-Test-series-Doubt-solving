package com.naresh.kingupadhyay.mathsking.activities;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.apradanas.simplelinkabletext.Link;
import com.apradanas.simplelinkabletext.LinkableEditText;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.MobileAds;
import com.google.android.material.tabs.TabLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdView;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.naresh.kingupadhyay.mathsking.R;
import com.naresh.kingupadhyay.mathsking.fragments.LeaderBoard;
import com.naresh.kingupadhyay.mathsking.fragments.Performance;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class TestResult extends AppCompatActivity {

    private Toolbar toolbar;
    protected String NAME="name";
    private AdView mAdView;
    private static boolean skip;
    private String title;
    private boolean liveCheck;
    private String liveIdcurrent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test__series);
//        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        MobileAds.initialize(this, "ca-app-pub-6924423095909700~8475665982");

        mAdView = findViewById(R.id.adVmain);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);


        SharedPreferences pref = getSharedPreferences("skip", Context.MODE_PRIVATE);
        skip=pref.getBoolean("skip",false);

        Bundle bundle = getIntent().getExtras();
        title = bundle.getString("title","title");

        ImageButton back=findViewById(R.id.backConcept);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        // Adding Toolbar to Main screen
        toolbar = (Toolbar) findViewById(R.id.conceptToolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        // Setting ViewPager for each Tabs
        ViewPager viewPager = (ViewPager) findViewById(R.id.conceptViewpager);
        setupViewPager(viewPager);
        // Set Tabs inside Toolbar
        TabLayout tabs = (TabLayout) findViewById(R.id.conceptTabs);
        tabs.setupWithViewPager(viewPager);
        TextView titleb = findViewById(R.id.titleb);
        titleb.setText(title);

        Button invite = findViewById(R.id.inviteFriend);
        invite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Context context=getApplicationContext();
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_SUBJECT,"Rock in mathematics  (free)");
                intent.putExtra(Intent.EXTRA_TEXT,"Click here to get FREE all mathematics formulas,shortcuts,concepts, NCERT , " +
                        "with examples, questions and weekly live test series."+"\nhttp://play.google.com/store/apps/details?id=" + context.getPackageName());

                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // this will make such that when user returns to your app, your app is displayed, instead of the email app.
                startActivity(Intent.createChooser(intent,"Share app"));

            }
        });


        SharedPreferences prefLive = getSharedPreferences("skip", Context.MODE_PRIVATE);
        liveCheck = prefLive.getBoolean("live",false);
        liveIdcurrent = prefLive.getString("liveId","liveId");
        if(liveCheck){
            showPopupFeedback();
        }


    }

    @Override
    protected void onResume() {
        super.onResume();
        try{
            SharedPreferences prefEdit1 = getSharedPreferences("edit", Context.MODE_PRIVATE);
            SharedPreferences.Editor edt11 = prefEdit1.edit();
            edt11.putBoolean("image",false);
            edt11.putString("imageUrl","");
            edt11.apply();
        }catch (Exception e){
        }
    }


    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
    }

    // Add Fragments to Tabs
    private void setupViewPager(ViewPager viewPager) {
        Adapter adapter = new Adapter(getSupportFragmentManager());
        adapter.addFragment(new Performance(), "Performance");
        adapter.addFragment(new LeaderBoard(), "Leader Board");
        viewPager.setAdapter(adapter);
    }

    static class Adapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public Adapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

    public void showPopupFeedback(){
        final Dialog myDialog = new Dialog(TestResult.this);
        myDialog.setContentView(R.layout.popup_feedback);
        myDialog.setCancelable(false);
        Link linkUrl = new Link(Pattern.compile("(([A-Za-z]{3,9}:(?://)?)(?:[-;:&=+$,\\w]+@)?[A-Za-z0-9.-]+|(?:www\\.|[-;:&=+$,\\w]+@)[A-Za-z0-9.-]+)((?:/[+~%/.\\w-]*)?\\??(?:[-+=&;%@.\\w]*)#?(?:[.!/\\\\\\w]*))?"))
                .setUnderlined(true)
                .setTextColor(R.color.star10)
                .setTextStyle(Link.TextStyle.BOLD)
                .setClickListener(new Link.OnClickListener() {
                    @Override
                    public void onClick(String text) {
                        Intent intent = new Intent(TestResult.this, Basic_activity.class);
                        intent.putExtra("pdfUrl",text);
                        intent.putExtra("titleNoti","Clicked Link");
                        startActivity(intent);

                    }
                });


        List<Link> links = new ArrayList<>();
        links.add(linkUrl);
        final LinkableEditText editText = (LinkableEditText)myDialog.findViewById(R.id.tagsvalLinkableEdit);
        editText.addLinks(links);

        editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(editText.getText().toString().isEmpty() ||editText.getText().toString()==null)
                    editText.setText(" ", TextView.BufferType.EDITABLE);
                if(b){
                    editText.setPressed(true);
                    editText.setSelection(editText.getText().length());
                }else{
                    closeKeyBoard(view);
                }
            }
        });


        Button postButton = (Button) myDialog.findViewById(R.id.postButton);
        final RadioGroup rgroupFeedback = (RadioGroup) myDialog.findViewById(R.id.rgroupFeedback);
        RadioGroup rgroupRate = (RadioGroup) myDialog.findViewById(R.id.rgroupRate);
        final RadioButton radioButtoneasy = myDialog.findViewById(R.id.easy);
        final RadioButton radioButtonmedium = myDialog.findViewById(R.id.medium);
        final RadioButton radioButtonhard = myDialog.findViewById(R.id.hard);

        rgroupFeedback.setVisibility(View.VISIBLE);
        rgroupRate.setVisibility(View.INVISIBLE);
        myDialog.show();
        postButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(radioButtoneasy.isChecked()||radioButtonmedium.isChecked()||radioButtonhard.isChecked()){
                    uploadFeedbackTest(rgroupFeedback,editText,myDialog);
                }else{
                    Toast.makeText(TestResult.this, "Select One" , Toast.LENGTH_SHORT).show();

                }
            }
        });

    }

    public void uploadFeedbackTest(RadioGroup radioGroup,LinkableEditText linkableEditText,Dialog dialog){

        String txt;
        if (radioGroup.getCheckedRadioButtonId() == R.id.easy) {
            txt = "Easy";
        }else if(radioGroup.getCheckedRadioButtonId() == R.id.medium){
            txt = "Medium";
        }else{
            txt = "Hard";
        }

        String tagstxt = linkableEditText.getText().toString().trim();
        if (tagstxt.isEmpty()) {
            linkableEditText.setError("Write something!");
            linkableEditText.requestFocus();
            return;
        }

        try{
            FirebaseFirestore db= FirebaseFirestore.getInstance();
            DocumentReference exampleRef = db
                        .collection("feedback").document("live")
                        .collection("app").document();

            //todo upload data
            SharedPreferences prefUseId = getSharedPreferences("ActivityPREF", Context.MODE_PRIVATE);
            String currentUserId=prefUseId.getString("uid","userghgj");

            Map<String,Object> uploadMap = new HashMap<>();
            uploadMap.put("userId", currentUserId);
            uploadMap.put("testlevel", txt);
            uploadMap.put("textmsg", tagstxt);
            uploadMap.put("uploadingTime",new Timestamp(new Date()));
            exampleRef.set(uploadMap, SetOptions.merge());
            dialog.dismiss();

        }catch (Exception e){
        }
    }



    private void closeKeyBoard(View view){
        if (view != null){
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
