package com.naresh.kingupadhyay.mathsking.UiMaterial;


import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;

import com.apradanas.simplelinkabletext.Link;
import com.apradanas.simplelinkabletext.LinkableEditText;
import com.apradanas.simplelinkabletext.LinkableTextView;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;

import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.core.view.GravityCompat;
import androidx.viewpager.widget.ViewPager;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.firestore.Source;
import com.naresh.kingupadhyay.mathsking.BuildConfig;
import com.naresh.kingupadhyay.mathsking.activities.Basic_activity;
import com.naresh.kingupadhyay.mathsking.activities.CourseDetails;
import com.naresh.kingupadhyay.mathsking.activities.NcertChapterWise;
import com.naresh.kingupadhyay.mathsking.activities.PostFeedback;
import com.naresh.kingupadhyay.mathsking.activities.ReadInstructions;
import com.naresh.kingupadhyay.mathsking.activities.TestResult;
import com.naresh.kingupadhyay.mathsking.activities.Test_Series;
import com.naresh.kingupadhyay.mathsking.fragments.Course;
import com.naresh.kingupadhyay.mathsking.Database.DownloadedList;
import com.naresh.kingupadhyay.mathsking.activities.Favourites;
import com.naresh.kingupadhyay.mathsking.fragments.Ncert_data;
import com.naresh.kingupadhyay.mathsking.fragments.Questions;
import com.naresh.kingupadhyay.mathsking.R;
import com.naresh.kingupadhyay.mathsking.fragments.ShortCut;
import com.naresh.kingupadhyay.mathsking.network.UserDetails;
import com.naresh.kingupadhyay.mathsking.viewholders.LoadShotNotesViewHolder;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public String name;
    public String id;
    private DatabaseReference update;
    protected String NAME="name";
    FirebaseUser firebaseUser;
    private String userImage = "Empty";
    private int rating=0;
    private int follow=0;
    private int following=0;
    private boolean publicStatus=true;// public/private status of user(private user nobody knows you)
    private int solutionMade=0;
    private AdView mAdView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MobileAds.initialize(this, "ca-app-pub-6924423095909700~8475665982");

        mAdView = findViewById(R.id.adVmain);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);


        SharedPreferences pref = getSharedPreferences("ActivityPREF", Context.MODE_PRIVATE);
        name=pref.getString("name","Unknown Name");
        id=pref.getString("id","Unknown Id");

        try{
            Calendar c = Calendar.getInstance();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss a");
            String strDate = sdf.format(c.getTime());
            firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
            update=FirebaseDatabase.getInstance().getReference().child("users").child(firebaseUser.getUid()).child("Last_online");
            update.onDisconnect().setValue(strDate);

        }catch (Exception e){
        }
        //todo save current user details and update
        SharedPreferences prefUpdate = getSharedPreferences("skip", Context.MODE_PRIVATE);
        boolean skip=prefUpdate.getBoolean("skip",false);


        if(!skip){
            try{
                final FirebaseFirestore db= FirebaseFirestore.getInstance();
                firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
                final DocumentReference docRef = db.collection("users").document(firebaseUser.getUid());
                docRef.get(Source.SERVER).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        UserDetails userDetails = documentSnapshot.toObject(UserDetails.class);
                        name = userDetails.getName();
                        userImage = userDetails.getUserImage();
                        rating = userDetails.getRating();
                        follow = userDetails.getFollow();
                        following = userDetails.getFollowing();
                        publicStatus = userDetails.isPpStatus();
                        solutionMade = userDetails.getSolved();
                        Map<String,Object> uploadMap = new HashMap<>();
                        uploadMap.put("rating",ratingCalculation(solutionMade));
                        docRef.set(uploadMap, SetOptions.merge());

                        SharedPreferences prefUseId = getSharedPreferences("ActivityPREF", Context.MODE_PRIVATE);
                        SharedPreferences.Editor edt = prefUseId.edit();
                        edt.putString("uid",firebaseUser.getUid());
                        edt.putString("userImage",userImage);
                        edt.putInt("rating",ratingCalculation(solutionMade));
                        edt.putInt("follow",follow);
                        edt.putInt("following",following);
                        edt.putBoolean("publicStatus",publicStatus);
                        edt.putInt("solutionMade",solutionMade);
                        edt.apply();

                    }
                });


            }catch (Exception e){
            }
        }


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Setting ViewPager for each Tabs
        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);
        // Set Tabs inside Toolbar
        TabLayout tabs = (TabLayout) findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);
        // Create Navigation drawer and inlfate layout
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        //int versionCode = BuildConfig.VERSION_CODE;
        String versionName = BuildConfig.VERSION_NAME;
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
//        Menu menu = navigationView.getMenu();

//        // find MenuItem you want to change
//        MenuItem nav_update = menu.findItem(R.id.update);
//
//        // set new title to the MenuItem
//        String updateName="Update "+" V "+versionName;
//        nav_update.setTitle(updateName);


        navigationView.setNavigationItemSelectedListener(this);
        View headerView = navigationView.getHeaderView(0);
        ImageView profilePic=headerView.findViewById(R.id.profilePic);

        try{

            Picasso.get().load(firebaseUser.getPhotoUrl()).placeholder(R.mipmap.ic_launcher)
                    .into(profilePic, new Callback() {
                        @Override
                        public void onSuccess() {
                        }
                        @Override
                        public void onError(Exception e) {
                        }
                    });
        }catch (Exception e){
        }

        TextView userName=(TextView)headerView.findViewById(R.id.id_name);
        userName.setText(name);
        TextView phoneNumber = (TextView) headerView.findViewById(R.id.id_phone);
        phoneNumber.setText(id);
        isStoragePermissionGranted();
    }

    public int ratingCalculation(int solution){
        if(solution>=0 && solution <50)
            return 1;
        else if(solution>=50 && solution <150){
            return 2;
        }else if(solution>=150 && solution <300){
            return 3;
        }else if(solution>=300 && solution <500){
            return 4;
        }else if(solution>=500 && solution <800){
            return 5;
        }else if(solution>=800 && solution <1200){
            return 6;
        }else if(solution>=1200 && solution <1700){
            return 7;
        }else if(solution>=1700 && solution <2300){
            return 8;
        }else if(solution>=2300 && solution <3000){
            return 9;
        }else {
            return 10;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }


    // Add Fragments to Tabs
    private void setupViewPager(ViewPager viewPager) {
        Adapter adapter = new Adapter(getSupportFragmentManager());
        adapter.addFragment(new Course(), "Course");
        adapter.addFragment(new ShortCut(), "Formulas");
        adapter.addFragment(new Questions(), "Previous Years");
        adapter.addFragment(new Ncert_data(), "NCERT");
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
    public  int backpress=0;
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            backpress = (backpress + 1);
            if(backpress<=1){
                Toast.makeText(getApplicationContext(), " Press Back again to Exit ", Toast.LENGTH_SHORT).show();
            }else{
                this.finish();
            }
        }

    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.home) {

        //} else if (id == R.id.r_books) {

        //} else if (id == R.id.r_sites) {

        } else if (id == R.id.favourite) {
            SharedPreferences pref = getSharedPreferences("skip", Context.MODE_PRIVATE);
            boolean skip=pref.getBoolean("skip",false);

            if(!skip){
                Intent intent = new Intent(MainActivity.this, Favourites.class);
                startActivity(intent);
            }else{
                Toast.makeText(getApplicationContext(), "Favourites is empty", Toast.LENGTH_SHORT).show();
            }

        } else if (id == R.id.downloaded) {
            Intent intent = new Intent(MainActivity.this, DownloadedList.class);
            startActivity(intent);

        } else if (id == R.id.test_series) {
            SharedPreferences prefUpdate = getSharedPreferences("skip", Context.MODE_PRIVATE);
            boolean skip=prefUpdate.getBoolean("skip",false);
            if(!skip){
                Intent intent = new Intent(MainActivity.this, Test_Series.class);
                startActivity(intent);
            }else{
                Toast.makeText(MainActivity.this, "First authenticate yourself", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(MainActivity.this, LoginOption.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        } else if (id == R.id.adminInstructions) {
            SharedPreferences prefAdmin = getSharedPreferences("ActivityPREF", Context.MODE_PRIVATE);
            String idVal=prefAdmin.getString("id","");
            if(idVal.equals("vinayupadhyay02001@gmail.com") || idVal.equals("naresh03961999@gmail.com") || idVal.equals("rakshitaupadhyay1497@gmail.com") ) {
                showPopupWriteInstruction();
            }else{
                Intent intent = new Intent(MainActivity.this, ReadInstructions.class);
                intent.putExtra("flag",true);
                startActivity(intent);
            }

        } else if (id == R.id.share) {
            Context context=getApplicationContext();
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/plain");
            intent.putExtra(Intent.EXTRA_SUBJECT,"Rock in mathematics  (free)");
            intent.putExtra(Intent.EXTRA_TEXT,"Click here to get FREE all mathematics formulas,shortcuts,concepts, NCERT , " +
                    "with examples, questions and weekly live test series."+"\nhttp://play.google.com/store/apps/details?id=" + context.getPackageName());

            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // this will make such that when user returns to your app, your app is displayed, instead of the email app.
            startActivity(Intent.createChooser(intent,"Share app"));
        //} else if (id == R.id.settings) {
        } else if (id == R.id.r_us) {
            rateAndShare();

        } else if (id == R.id.instagram) {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.instagram.com/iit_jee_maths_king/"));
            startActivity(browserIntent);
        } else if (id == R.id.facebook) {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://m.facebook.com/iitjeemathsking"));
            startActivity(browserIntent);
        } else if (id == R.id.feedback) {
            SharedPreferences prefAdmin = getSharedPreferences("ActivityPREF", Context.MODE_PRIVATE);
            String idVal=prefAdmin.getString("id","");
            if(idVal.equals("vinayupadhyay02001@gmail.com") || idVal.equals("naresh03961999@gmail.com") || idVal.equals("rakshitaupadhyay1497@gmail.com") ) {
                Intent intent = new Intent(MainActivity.this, PostFeedback.class);
                startActivity(intent);
            }else{
                SharedPreferences prefUpdate = getSharedPreferences("skip", Context.MODE_PRIVATE);
                boolean skip=prefUpdate.getBoolean("skip",false);
                if(!skip){
                    showPopupFeedback();
                }else{
                    Toast.makeText(MainActivity.this, "First authenticate yourself", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(MainActivity.this, LoginOption.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }
            }

        } else if (id == R.id.help) {
            Intent intent = new Intent(Intent.ACTION_SENDTO); // it's not ACTION_SEND
            intent.setType("message/rfc822");
            intent.putExtra(Intent.EXTRA_SUBJECT, "Feedback and Help related query");
            intent.putExtra(Intent.EXTRA_TEXT, "[Edit here...]");
            intent.setData(Uri.parse("mailto:maths.developers@gmail.com")); // or just "mailto:" for blank
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // this will make such that when user returns to your app, your app is displayed, instead of the email app.
            try {
                startActivity(Intent.createChooser(intent, "Send email using"));
            } catch (android.content.ActivityNotFoundException ex) {
                Toast.makeText(MainActivity.this, "No email client installed.", Toast.LENGTH_SHORT).show();
            }

        }else if (id ==R.id.logout){
            FirebaseAuth.getInstance().signOut();
            SharedPreferences pref = getSharedPreferences("skip", Context.MODE_PRIVATE);
            SharedPreferences.Editor edt = pref.edit();
            edt.putBoolean("skip",false);
            edt.apply();
            Intent intent = new Intent(MainActivity.this, SplashActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            this.finish();
        }


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void rateAndShare(){
        Context context=getApplicationContext();
        Uri uri = Uri.parse("market://details?id=" + context.getPackageName());
        Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
        // To count with Play market backstack, After pressing back button,
        // to taken back to our application, we need to add following flags to intent.
        goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                Intent.FLAG_ACTIVITY_NEW_DOCUMENT |
                Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
        try {
            startActivity(goToMarket);
        } catch (ActivityNotFoundException e) {
            startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("http://play.google.com/store/apps/details?id=" + context.getPackageName())));
        }
    }

    String TAG = "king";
    public  void isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            isReadStoragePermissionGranted();
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED ) {
                Log.v(TAG,"Permission is granted");
                //return true;
            } else {

                Log.v(TAG,"Permission is revoked");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                // return false;
            }
        }
        else { //permission is automatically granted on sdk<23 upon installation
            Log.v(TAG,"Permission is granted");
            // return true;
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            Log.v(TAG,"Permission: "+permissions[0]+ "was "+grantResults[0]);
            //resume tasks needing this permission
        }
    }

    public void isReadStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.v(TAG,"Permission is granted1");
                //return true;
            } else {

                Log.v(TAG,"Permission is revoked1");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 3);
                //return false;
            }
        }
        else { //permission is automatically granted on sdk<23 upon installation
            Log.v(TAG,"Permission is granted1");
            //return true;
        }
    }


    public void showPopupFeedback(){
        final Dialog myDialog = new Dialog(MainActivity.this);
        myDialog.setContentView(R.layout.popup_feedback);
        myDialog.setCancelable(true);
        Link linkUrl = new Link(Pattern.compile("(([A-Za-z]{3,9}:(?://)?)(?:[-;:&=+$,\\w]+@)?[A-Za-z0-9.-]+|(?:www\\.|[-;:&=+$,\\w]+@)[A-Za-z0-9.-]+)((?:/[+~%/.\\w-]*)?\\??(?:[-+=&;%@.\\w]*)#?(?:[.!/\\\\\\w]*))?"))
                .setUnderlined(true)
                .setTextColor(R.color.star10)
                .setTextStyle(Link.TextStyle.BOLD)
                .setClickListener(new Link.OnClickListener() {
                    @Override
                    public void onClick(String text) {
                        Intent intent = new Intent(MainActivity.this, Basic_activity.class);
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
        final RadioGroup rgroupRate = (RadioGroup) myDialog.findViewById(R.id.rgroupRate);
        final RadioButton radioButtoneasy = myDialog.findViewById(R.id.nsatisfied);
        final RadioButton radioButtonmedium = myDialog.findViewById(R.id.satisfied);
        final RadioButton radioButtonhard = myDialog.findViewById(R.id.excellent);

        rgroupFeedback.setVisibility(View.INVISIBLE);
        rgroupRate.setVisibility(View.VISIBLE);
        myDialog.show();
        postButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(radioButtoneasy.isChecked()||radioButtonmedium.isChecked()||radioButtonhard.isChecked()){
                    uploadFeedbackTest(rgroupRate,editText,myDialog);
                }else{
                    Toast.makeText(MainActivity.this, "Select One" , Toast.LENGTH_SHORT).show();

                }
            }
        });

    }

    public void uploadFeedbackTest(RadioGroup radioGroup,LinkableEditText linkableEditText,Dialog dialog){
        String txt;
        if (radioGroup.getCheckedRadioButtonId() == R.id.nsatisfied) {
            txt = "Not Satisfied";
        }else if(radioGroup.getCheckedRadioButtonId() == R.id.satisfied){
            txt = "Satisfied";
        }else{
            txt = "Excellent";
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
//            String iduser=prefUseId.getString("id","Unknown Id");
//            iduser = iduser +"\n"+txt+"\n";
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


    public void showPopupWriteInstruction(){
        final Dialog myDialog = new Dialog(MainActivity.this);
        myDialog.setContentView(R.layout.popup_instrucitons_test_series);
        myDialog.setCancelable(true);
        Link linkUrl = new Link(Pattern.compile("(([A-Za-z]{3,9}:(?://)?)(?:[-;:&=+$,\\w]+@)?[A-Za-z0-9.-]+|(?:www\\.|[-;:&=+$,\\w]+@)[A-Za-z0-9.-]+)((?:/[+~%/.\\w-]*)?\\??(?:[-+=&;%@.\\w]*)#?(?:[.!/\\\\\\w]*))?"))
                .setUnderlined(true)
                .setTextColor(R.color.star10)
                .setTextStyle(Link.TextStyle.BOLD)
                .setClickListener(new Link.OnClickListener() {
                    @Override
                    public void onClick(String text) {
                        // do something
                        Context context = MainActivity.this;
//                        Toast.makeText(context, "Clicked link: " +text, Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(context, Basic_activity.class);
                        intent.putExtra("pdfUrl",text);
                        intent.putExtra("titleNoti","Clicked Link");
                        context.startActivity(intent);

                    }
                });


        List<Link> links = new ArrayList<>();
        links.add(linkUrl);
        final CardView editTextCard = (CardView) myDialog.findViewById(R.id.editTextInstruciton);
        final LinkableTextView textView = myDialog.findViewById(R.id.textLinkableTextViewInstruciton);
        final LinkableEditText editText = (LinkableEditText)myDialog.findViewById(R.id.editTextLinkableEditInstruciton);
        textView.setVisibility(View.GONE);
        editTextCard.setVisibility(View.VISIBLE);
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
        postButton.setText("Send");

        myDialog.show();
        postButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadInstruction(editText,myDialog);
            }
        });

    }

    public void uploadInstruction(LinkableEditText linkableEditText,Dialog dialog){

        String tagstxt = linkableEditText.getText().toString().trim();
        if (tagstxt.isEmpty()) {
            linkableEditText.setError("Write something!");
            linkableEditText.requestFocus();
            return;
        }

        try{
            DatabaseReference favourites= FirebaseDatabase.getInstance().getReference().child("admininstructions");
            favourites.child("live").child("textmsg").setValue(tagstxt);
            dialog.dismiss();

        }catch (Exception e){
        }
    }




}
