package com.naresh.kingupadhyay.mathsking.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.gms.ads.AdView;
import com.naresh.kingupadhyay.mathsking.R;
import com.naresh.kingupadhyay.mathsking.fragments.LiveTestSeries;
import com.naresh.kingupadhyay.mathsking.fragments.PreviousTestSeries;
import com.naresh.kingupadhyay.mathsking.fragments.UpcomingTestSeries;

import java.util.ArrayList;
import java.util.List;

public class Test_Series extends AppCompatActivity {

    private Toolbar toolbar;
    protected String NAME="name";
    private AdView mAdView;
    private static boolean skip;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test__series);

        MobileAds.initialize(this, "ca-app-pub-6924423095909700~8475665982");

        mAdView = findViewById(R.id.adVmain);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);


        SharedPreferences pref = getSharedPreferences("skip", Context.MODE_PRIVATE);
        skip=pref.getBoolean("skip",false);

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
        titleb.setText(R.string.testseries);
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
        adapter.addFragment(new UpcomingTestSeries(), "Upcoming");
        adapter.addFragment(new LiveTestSeries(), "Live");
        adapter.addFragment(new PreviousTestSeries(), "Previous");
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


    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
