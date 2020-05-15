package com.naresh.kingupadhyay.mathsking.Database;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.naresh.kingupadhyay.mathsking.R;
import com.naresh.kingupadhyay.mathsking.helpers.FileAdapter;
import com.naresh.kingupadhyay.mathsking.network.FileBeen;

import java.io.File;
import java.util.ArrayList;

import static android.net.Uri.fromFile;
import static com.naresh.kingupadhyay.mathsking.Database.PDFTools.openPDF;

public class DownloadedList extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener {


    private Toolbar toolbar;
    private AdView mAdView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_downloaded_list);


        MobileAds.initialize(this, "ca-app-pub-6924423095909700~8475665982");

        mAdView = findViewById(R.id.adViewbasic);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);


        setTitle("Pdf Reader");
        //Check if permission is granted(for Marshmallow and higher versions)
        if (Build.VERSION.SDK_INT >= 23)
            checkPermission();
        else
            initViews();

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);


        ImageButton back=findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();

            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }


    ListView listView;
    String path;
    ArrayList<FileBeen> list;
    FileAdapter adapter;
    File tempFile;
    void initViews(){
        //views initialization
        listView = (ListView)findViewById(R.id.listView);
        list = new ArrayList<>();

        //get the absolute path of phone storage
        //path = Environment.getExternalStorageDirectory().getAbsolutePath();
        path = this.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS).toString();
        //calling the initList that will initialize the list to be given to Adapter for binding data
        initList(path);

        adapter = new FileAdapter(this, R.layout.content_downloaded_list, list);

        //set the adapter on listView
        listView.setAdapter(adapter);

        //when user chooses a particular pdf file from list,
        //start another activity that will show the pdf
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //intent.putExtra("keyName", list.get(position).getFileName());
                //intent.putExtra("keyPath",list.get(position).getFilePath());
                tempFile = new File( DownloadedList.this.getExternalFilesDir( Environment.DIRECTORY_DOWNLOADS ), list.get(position).getFileName() );

                PopupMenu popup = new PopupMenu(DownloadedList.this, view);
                popup.setOnMenuItemClickListener(DownloadedList.this);
                popup.inflate(R.menu.menu_downloaded_list);
                popup.show();



            }
        });
    }


    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.show_item:
                openPDF( DownloadedList.this, fromFile( tempFile) );
                // do your code
                return true;
            case R.id.delete_item:
                tempFile.delete();
                initViews();
                Toast.makeText(this, "File deleted", Toast.LENGTH_SHORT).show();
                // do your code
                return true;
            default:
                return false;
        }
    }

    //initializing the ArrayList
    void initList(String path){
        try{
            File file = new File(path);
            File[] fileArr = file.listFiles();
            String fileName;
            for(File file1 : fileArr){
                if(file1.isDirectory()){
                    initList(file1.getAbsolutePath());
                }else{
                    fileName = file1.getName();
                    //choose only the pdf files
                    if(fileName.endsWith(".pdf")){
                        list.add(new FileBeen(fileName, file1.getAbsolutePath()));
                    }
                }

            }
        }catch(Exception e){
            Log.i("show","Something went wrong. "+e.toString());
        }
    }

    //Handling permissions for Android Marshmallow and above
    void checkPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            //if permission granted, initialize the views
            initViews();
        } else {
            //show the dialog requesting to grant permission
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    initViews();
                } else {
                    //permission is denied (this is the first time, when "never ask again" is not checked)
                    if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                        finish();
                    }
                    //permission is denied (and never ask again is  checked)
                    else {
                        //shows the dialog describing the importance of permission, so that user should grant
                        AlertDialog.Builder builder = new AlertDialog.Builder(this);
                        builder.setMessage("You have forcefully denied Read storage permission.\n\nThis is necessary for the working of app." + "\n\n" + "Click on 'Grant' to grant permission")
                                //This will open app information where user can manually grant requested permission
                                .setPositiveButton("Grant", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        finish();
                                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                                Uri.fromParts("package", getPackageName(), null));
                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(intent);
                                    }
                                })
                                //close the app
                                .setNegativeButton("Don't", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        finish();
                                    }
                                });
                        builder.setCancelable(false);
                        builder.create().show();
                    }
                }
        }
    }
}
