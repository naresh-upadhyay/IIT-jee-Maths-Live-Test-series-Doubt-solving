package com.naresh.kingupadhyay.mathsking.captue;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.app.Activity;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnPausedListener;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.isseiaoki.simplecropview.CropImageView;
import com.isseiaoki.simplecropview.callback.CropCallback;
import com.isseiaoki.simplecropview.callback.LoadCallback;
import com.isseiaoki.simplecropview.callback.SaveCallback;
import com.isseiaoki.simplecropview.util.Logger;
import com.naresh.kingupadhyay.mathsking.R;
import com.naresh.kingupadhyay.mathsking.activities.CourseDetails;
import com.wang.avi.AVLoadingIndicatorView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;



public class ImageCroper extends AppCompatActivity {

    private CropImageView mCropView;
    private Bitmap.CompressFormat mCompressFormat = Bitmap.CompressFormat.JPEG;
    private RectF mFrameRect = null;
    private Uri mSourceUri = null;
    private AVLoadingIndicatorView avLoadingIndicatorView;
    private static final int REQUEST_PICK_IMAGE = 8889;
    private static final String KEY_FRAME_RECT = "FrameRect";
    private static final String KEY_SOURCE_URI = "SourceUri";
    private static final int CAMERA_REQUEST = 1888;
    private static final int MY_CAMERA_PERMISSION_CODE = 100;
    private static final int READ_STORAGE_PERMISSION_CHECK = 150;
    private static final int WRITE_STORAGE_PERMISSION_CHECK = 200;
    private ImageView outputImage;
    private String pathOutputImage;
    private String filenameOutputImage;
    private RelativeLayout tab_barLayout;
    private LinearLayout linearLayoutMain;
    private LinearLayout linearLayoutNewImage;
    private TextView hinttext;
    private Bitmap outputBitmap;
    private String currentPhotoPath;
    static final int REQUEST_TAKE_PHOTO = 12;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_croper);
        defaultData();
        isStoragePermissionGranted();
        ImageButton back=findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        avLoadingIndicatorView = findViewById(R.id.progress_bar_backdrop);
        hinttext = findViewById(R.id.hintTxt);
        hinttext.setText(R.string.select);
        final TextView toolbartext = findViewById(R.id.titleb);
        toolbartext.setText(R.string.picncrop);


        linearLayoutMain = findViewById(R.id.actual_image_view_layout);
        linearLayoutNewImage= findViewById(R.id.new_image_view_layout);
        tab_barLayout = findViewById(R.id.tab_bar);
        tab_barLayout.setVisibility(View.GONE);
        linearLayoutNewImage.setVisibility(View.GONE);
        linearLayoutMain.setVisibility(View.VISIBLE);

        avLoadingIndicatorView.hide();
        mCropView = (CropImageView) findViewById(R.id.cropImageView);
        if (savedInstanceState != null) {
            // restore data
            hinttext.setVisibility(View.GONE);
            mFrameRect = savedInstanceState.getParcelable(KEY_FRAME_RECT);
            mSourceUri = savedInstanceState.getParcelable(KEY_SOURCE_URI);
        }

        RelativeLayout done = findViewById(R.id.done_layout);
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(mSourceUri!=null){
                    try{
                        avLoadingIndicatorView.show();
                        mCropView.crop(mSourceUri).execute(mCropCallback);
                        avLoadingIndicatorView.hide();
                        linearLayoutMain.setVisibility(View.GONE);
                        linearLayoutNewImage.setVisibility(View.VISIBLE);
                        toolbartext.setText(R.string.croped);
                    }catch (Exception e){
                    }
                }else{
                    Toast.makeText(ImageCroper.this, "Select Image", Toast.LENGTH_LONG).show();
                }


            }
        });

        RelativeLayout rotateLeft = findViewById(R.id.leftrotate_layout);
        rotateLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try{
                    mCropView.rotateImage(CropImageView.RotateDegrees.ROTATE_M90D); // rotate counter-clockwise by 90 degrees
                }catch (Exception e){

                }

            }
        });

        RelativeLayout rotateRight = findViewById(R.id.rightrotate_layout);
        rotateRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try{
                    mCropView.rotateImage(CropImageView.RotateDegrees.ROTATE_90D); // rotate clockwise by 90 degrees
                }catch (Exception e){
                }
            }
        });
        RelativeLayout gallery = findViewById(R.id.gallery_layout);
        gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hinttext.setVisibility(View.GONE);
                if(isStoragePermissionGranted())
                    pickImage();
            }
        });
        RelativeLayout cameraAcess = findViewById(R.id.camera_layout);
        cameraAcess.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hinttext.setVisibility(View.GONE);
                if(isStoragePermissionGranted())
                    dispatchTakePictureIntent();
            }
        });
        //todo show image
        outputImage = findViewById(R.id.image_view);
        RelativeLayout retakeLayout = findViewById(R.id.retake_layout);
        retakeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                linearLayoutMain.setVisibility(View.VISIBLE);
                linearLayoutNewImage.setVisibility(View.GONE);
                toolbartext.setText(R.string.picncrop);
            }
        });
        SharedPreferences prefEdit = getSharedPreferences("edit", Context.MODE_PRIVATE);
        final SharedPreferences.Editor edt = prefEdit.edit();
        edt.putBoolean("image",false);
        edt.apply();
        RelativeLayout uploadLayout = findViewById(R.id.upload_layout);
        uploadLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    SharedPreferences prefEdit2 = getSharedPreferences("edit", Context.MODE_PRIVATE);
                    String imageUrl=prefEdit2.getString("imageUrl","");

                    Uri uri =  Uri.parse(imageUrl);
                    String path = uri.getPath();
                    int cut = path.lastIndexOf('/');
                    String imageFilename="";
                    if (cut != -1) {
                        imageFilename = path.substring(cut + 1);
                    }
                    deleteStoredImage("images/examples/submissions",imageFilename);

                }catch (Exception e){
                }


                saveImageFromBitmap(outputBitmap);
                //todo upload image
                edt.putBoolean("image",true);
                edt.apply();
                //todo go back when successful upload
                uploadTOFirebase(outputImage,"images/examples/submissions",filenameOutputImage);
            }
        });

    }


    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }


    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.naresh.kingupadhyay.mathsking.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }
    private void galleryAddPic() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(currentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
    }

    private void setPic(ImageView imageView) {
        // Get the dimensions of the View
        int targetW = imageView.getWidth();
        int targetH = imageView.getHeight();

        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;

        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Determine how much to scale down the image
        int scaleFactor = Math.min(photoW/targetW, photoH/targetH);

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

        Bitmap bitmap = BitmapFactory.decodeFile(currentPhotoPath, bmOptions);
        imageView.setImageBitmap(bitmap);
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


    public void uploadTOFirebase(ImageView imageView,String path,String filename){
        FirebaseStorage storage = FirebaseStorage.getInstance();
        // Create a storage reference from our app
        StorageReference storageRef = storage.getReference();
        final StorageReference conceptImagesRef = storageRef.child(path+"/"+filename);

        // Get the data from an ImageView as bytes
        imageView.setDrawingCacheEnabled(true);
        imageView.buildDrawingCache();
        Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        final UploadTask uploadTask = conceptImagesRef.putBytes(data);
        // Observe state change events such as progress, pause, and resume
        final ProgressDialog progDailog = new ProgressDialog(ImageCroper.this);
        progDailog.setTitle("Data Uploading....");
        progDailog.setCancelable(false);
        progDailog.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                uploadTask.cancel();
                dialog.dismiss();
            }
        });
        progDailog.show();

        uploadTask.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
//                System.out.println("Upload is " + progress + "% done");
                progDailog.setMessage(progress + "% done");

            }
        }).addOnPausedListener(new OnPausedListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onPaused(UploadTask.TaskSnapshot taskSnapshot) {
//                System.out.println("Upload is paused");
            }
        });


        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Toast.makeText(ImageCroper.this, "Uploading Failed", Toast.LENGTH_LONG).show();
                // Handle unsuccessful uploads
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(ImageCroper.this, "Successfully Attached", Toast.LENGTH_LONG).show();
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                // ...
            }
        });
        Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }

                // Continue with the task to get the download URL
                return conceptImagesRef.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    Uri downloadUri = task.getResult();
                    SharedPreferences prefEditu = getSharedPreferences("edit", Context.MODE_PRIVATE);
                    SharedPreferences.Editor edtu = prefEditu.edit();
                    edtu.putString("imageUrl",downloadUri.toString());
                    edtu.apply();
                    onBackPressed();

//                                  onBackPressed();  Toast.makeText(ImageCroper.this, downloadUri.getPath(), Toast.LENGTH_LONG).show();
                } else {
                    // Handle failures
                    // ...
                }
            }
        });
    }

    public void defaultData(){
        TextView lefttxt = findViewById(R.id.leftrotateTextview);
        TextView righttxt = findViewById(R.id.rightrotateTextview);
        TextView gallerytxt = findViewById(R.id.galleryTextview);
        TextView cameratxt = findViewById(R.id.cameraText);
        TextView donetxt = findViewById(R.id.done_text);
        TextView retack = findViewById(R.id.retakeText);
        TextView upload = findViewById(R.id.upload_text);
        lefttxt.setText(R.string.leftrotate);
        righttxt.setText(R.string.rightrotate);
        gallerytxt.setText(R.string.gallery);
        cameratxt.setText(R.string.camera);
        donetxt.setText(R.string.capture);
        retack.setText(R.string.retake);
        upload.setText(R.string.done);
    }

    public void defaultCroper(Uri mSourceUri1,RectF rectF){
        tab_barLayout.setVisibility(View.VISIBLE);
        mCropView.load(mSourceUri1)
                .initialFrameRect(rectF)
                .useThumbnail(true)
                .execute(mLoadCallback);

        mCropView.setCropMode(CropImageView.CropMode.FREE);
        mCropView.setInitialFrameScale(0.75f);
        mCropView.setCompressQuality(85);
        mCropView.setOutputMaxSize(120,70);
        mCropView.setMinFrameSizeInDp(15);
//        mCropView.setOutputWidth(100);
        mCropView.setOutputHeight(350);

    }
    @Override public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // save data
        outState.putParcelable(KEY_FRAME_RECT, mCropView.getActualCropRect());
        outState.putParcelable(KEY_SOURCE_URI, mCropView.getSourceUri());
    }


//    private void dispatchTakePictureIntent() {
//        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
//            startActivityForResult(takePictureIntent, CAMERA_REQUEST);
//        }
//    }



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_CAMERA_PERMISSION_CODE)
        {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                Toast.makeText(this, "camera permission granted", Toast.LENGTH_LONG).show();
                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAMERA_REQUEST);
            }
            else
            {
                Toast.makeText(this, "camera permission denied", Toast.LENGTH_LONG).show();
            }
        }
        else if (requestCode == READ_STORAGE_PERMISSION_CHECK)
        {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                Toast.makeText(this, "read permission granted", Toast.LENGTH_LONG).show();
            }
            else
            {
                Toast.makeText(this, "read permission denied", Toast.LENGTH_LONG).show();
            }
        }
        else if (requestCode == WRITE_STORAGE_PERMISSION_CHECK)
        {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                Toast.makeText(this, "write permission granted", Toast.LENGTH_LONG).show();
            }
            else
            {
                Toast.makeText(this, "write permission denied", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_PICK_IMAGE && resultCode == Activity.RESULT_OK) {
            if (data == null) {
                //Display an error
                return;
            }
            mFrameRect = null;
            mSourceUri = data.getData();
            defaultCroper(mSourceUri,mFrameRect);
        }else if (requestCode == REQUEST_TAKE_PHOTO && resultCode == Activity.RESULT_OK) {
//            Bitmap photo = (Bitmap) data.getExtras().get("data");
            File file = new File(currentPhotoPath);
            Bitmap bitmap;
            try{
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), Uri.fromFile(file));
                if (bitmap != null) {
                    mFrameRect = null;
                    mSourceUri = getImageUri(ImageCroper.this,bitmap);
                    defaultCroper(mSourceUri,mFrameRect);
                }
            }catch (IOException e){
            }

        }else if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK) {
            Bitmap photo = (Bitmap) data.getExtras().get("data");
            mFrameRect = null;
            mSourceUri = getImageUri(ImageCroper.this,photo);
            defaultCroper(mSourceUri,mFrameRect);
        }
    }


    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }



    public Uri createSaveUri() {
        return createNewUri(ImageCroper.this, mCompressFormat);
    }

    public static String getDirPath() {
        String dirPath = "";
        File imageDir = null;
        File extStorageDir = Environment.getExternalStorageDirectory();
        if (extStorageDir.canWrite()) {
            imageDir = new File(extStorageDir.getPath() + "/MathsKing");
        }
        if (imageDir != null) {
            if (!imageDir.exists()) {
                imageDir.mkdirs();
            }
            if (imageDir.canWrite()) {
                dirPath = imageDir.getPath();
            }
        }
        return dirPath;
    }

    public static Uri getUriFromDrawableResId(Context context, int drawableResId) {
        StringBuilder builder = new StringBuilder().append(ContentResolver.SCHEME_ANDROID_RESOURCE)
                .append("://")
                .append(context.getResources().getResourcePackageName(drawableResId))
                .append("/")
                .append(context.getResources().getResourceTypeName(drawableResId))
                .append("/")
                .append(context.getResources().getResourceEntryName(drawableResId));
        return Uri.parse(builder.toString());
    }

    public static Uri createNewUri(Context context, Bitmap.CompressFormat format) {
        long currentTimeMillis = System.currentTimeMillis();
        Date today = new Date(currentTimeMillis);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");
        String title = dateFormat.format(today);
        String dirPath = getDirPath();
        String fileName = "maths" + title + "." + getMimeType(format);
        String path = dirPath + "/" + fileName;
        File file = new File(path);
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, title);
        values.put(MediaStore.Images.Media.DISPLAY_NAME, fileName);
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/" + getMimeType(format));
        values.put(MediaStore.Images.Media.DATA, path);
        long time = currentTimeMillis / 1000;
        values.put(MediaStore.MediaColumns.DATE_ADDED, time);
        values.put(MediaStore.MediaColumns.DATE_MODIFIED, time);
        if (file.exists()) {
            values.put(MediaStore.Images.Media.SIZE, file.length());
        }

        ContentResolver resolver = context.getContentResolver();
        Uri uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        Logger.i("SaveUri = " + uri);
        return uri;
    }

    public static String getMimeType(Bitmap.CompressFormat format) {
        Logger.i("getMimeType CompressFormat = " + format);
        switch (format) {
            case JPEG:
                return "jpeg";
            case PNG:
                return "png";
        }
        return "png";
    }

    public static Uri createTempUri(Context context) {
        return Uri.fromFile(new File(context.getCacheDir(), "cropped"));
    }

    //todo verify callbacks
    private final LoadCallback mLoadCallback = new LoadCallback() {
        @Override public void onSuccess() {
        }

        @Override public void onError(Throwable e) {
        }
    };

    private final CropCallback mCropCallback = new CropCallback() {
        @Override public void onSuccess(Bitmap cropped) {
            mCropView.save(cropped)
                    .compressFormat(mCompressFormat)
                    .execute(createTempUri(ImageCroper.this), mSaveCallback);
        }

        @Override public void onError(Throwable e) {
        }
    };

    private final SaveCallback mSaveCallback = new SaveCallback() {
        @Override public void onSuccess(Uri outputUri) {
            avLoadingIndicatorView.hide();
            try
            {
                outputBitmap = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver() , outputUri);
                outputImage.setImageBitmap(outputBitmap);

            }
            catch (Exception e)
            {
                //handle exception
            }
//            Toast.makeText(ImageCroper.this,outputUri.toString(), Toast.LENGTH_SHORT).show();
//            dismissProgress();
//            ((BasicActivity) getActivity()).startResultActivity(outputUri);
        }

        @Override public void onError(Throwable e) {
//            dismissProgress();
        }
    };

    public void cameraImage(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
            {
                ActivityCompat.requestPermissions(ImageCroper.this, new String[]{Manifest.permission.CAMERA}, MY_CAMERA_PERMISSION_CODE);
            }
            else
            {
                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAMERA_REQUEST);
            }

        }

    }
    public void pickImage() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            startActivityForResult(new Intent(Intent.ACTION_GET_CONTENT).setType("image/*"),
                    REQUEST_PICK_IMAGE);
        } else {
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("image/*");
            startActivityForResult(intent, REQUEST_PICK_IMAGE);
        }
    }

    public  boolean isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            isReadStoragePermissionGranted();
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED ) {
                return true;
            } else {

                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, WRITE_STORAGE_PERMISSION_CHECK);
                Toast.makeText(this, "Try again", Toast.LENGTH_LONG).show();
                return false;
            }
        }
        else { //permission is automatically granted on sdk<23 upon installation
            return true;
        }
    }

    public void isReadStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                //return true;
            } else {

                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, READ_STORAGE_PERMISSION_CHECK);
                //return false;
            }
        }
        else { //permission is automatically granted on sdk<23 upon installation
            //return true;
        }
    }

    public void saveImageFromBitmap(Bitmap bitmap){
        long currentTimeMillis = System.currentTimeMillis();
        Date today = new Date(currentTimeMillis);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");
        String title = dateFormat.format(today);
        String dirPath = getDirPath();
        FirebaseUser firebaseUser= FirebaseAuth.getInstance().getCurrentUser();

        String fileName = "maths" +firebaseUser.getUid()+ title + "." + getMimeType(mCompressFormat);
        filenameOutputImage = fileName;
        String path = dirPath + "/" + fileName;
        File file = new File(path);
        try {
            FileOutputStream fos = new FileOutputStream(file);
            bitmap.compress(mCompressFormat, 100, fos);
            fos.flush();
            fos.close();
        } catch (FileNotFoundException e) {
//            Log.d(TAG, "File not found: " + e.getMessage());
        } catch (IOException e) {
//            Log.d(TAG, "Error accessing file: " + e.getMessage());
        }

    }

    @Override
    public void onBackPressed(){
        FragmentManager fm = getFragmentManager();
        if (fm.getBackStackEntryCount() > 0) {
            fm.popBackStack();
        } else {
            super.onBackPressed();
        }
    }


}
