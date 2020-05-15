package com.naresh.kingupadhyay.mathsking.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

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
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.gms.ads.AdView;
import com.naresh.kingupadhyay.mathsking.R;
import com.naresh.kingupadhyay.mathsking.fragments.AskSolveDoubts;
import com.naresh.kingupadhyay.mathsking.fragments.ConceptsFragment;
import com.naresh.kingupadhyay.mathsking.fragments.Examples;
import com.naresh.kingupadhyay.mathsking.fragments.Suggestions;

import java.util.ArrayList;
import java.util.List;

public class Concepts extends AppCompatActivity {

    private Toolbar toolbar;
    private ProgressDialog progDailog;
    protected String NAME="name";
    private String book;
    private String chapter;
    private String titleval;
    private AdView mAdView;
    private static boolean skip;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_concepts);
//        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        MobileAds.initialize(this, "ca-app-pub-6924423095909700~8475665982");

        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);


        SharedPreferences pref = getSharedPreferences("skip", Context.MODE_PRIVATE);
        skip=pref.getBoolean("skip",false);

        Bundle bundle = getIntent().getExtras();
        book = bundle.getString("book");
        chapter = bundle.getString("chapter");
        titleval = bundle.getString("title");

        SharedPreferences prefs=getSharedPreferences("course", Context.MODE_PRIVATE);
        SharedPreferences.Editor edt = prefs.edit();
        edt.putString("book",book);
        edt.putString("chapter",chapter);
        edt.apply();

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
        titleb.setText(titleval);

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
        adapter.addFragment(new ConceptsFragment(), "Concepts");
        adapter.addFragment(new Examples(), "Examples");
        adapter.addFragment(new AskSolveDoubts(), "Doubts Ask/Solve");
        adapter.addFragment(new Suggestions(), "Suggestions");
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


    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }


//    public void progressBar(){
//        progDailog = new ProgressDialog(Concepts.this);
//        progDailog.setTitle("Data Loading....");
//        progDailog.setMessage("Please Wait.....");
//        progDailog.setCancelable(false);
//        progDailog.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                dialog.dismiss();
//            }
//        });
//        progDailog.show();
//
//    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
