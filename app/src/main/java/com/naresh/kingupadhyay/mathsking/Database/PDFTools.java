package com.naresh.kingupadhyay.mathsking.Database;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;
import com.naresh.kingupadhyay.mathsking.activities.Basic_activity;
import com.naresh.kingupadhyay.mathsking.R;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.wang.avi.AVLoadingIndicatorView;

import java.io.File;
import static android.net.Uri.*;

public class PDFTools {

    //private static final String GOOGLE_DRIVE_PDF_READER_PREFIX = "http://drive.google.com/viewer?url=";
    private static final String PDF_MIME_TYPE = "application/pdf";
    private static final String HTML_MIME_TYPE = "text/html";
    private static Basic_activity basic_activity = new Basic_activity();
    private static long downloadId1;

    /**
     * If a PDF reader is installed, download the PDF file and open it in a reader.
     * Otherwise ask the user if he/she wants to view it in the Google Drive online PDF reader.<br />
     * <br />
     * <b>BEWARE:</b> This method
     * @param context
     * @param pdfUrl
     * @return
     */
    public static void showPDFUrl(final Context context,final String title, final String pdfUrl ) {
        if ( isPDFSupported( context ) ) {
            downloadAndOpenPDF(context, title, pdfUrl);
        } else {
            askToOpenPDFThroughGoogleDrive( context,title, pdfUrl );
        }
    }


    public static boolean isDownloadManagerAvailable(Context context) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
            return true;
        }
        return false;
    }
    /**
     * Downloads a PDF with the Android DownloadManager and opens it with an installed PDF reader app.
     * @param context
     * @param pdfUrl
     */
    @TargetApi(Build.VERSION_CODES.GINGERBREAD)
    public static void downloadAndOpenPDF(final Context context, final String title,final String pdfUrl) {
        try{
            // Get filename
            final String filename = title + ".pdf";
            // download link "https://drive.google.com/uc?export=download&id=1KkFEEsrfNDiG4sfDocvDIrjgLhGEWSvY"
            final String downloadLink = "https://drive.google.com/uc?export=download&id="+ pdfUrl.substring( pdfUrl.lastIndexOf( "=" ) + 1 );
            // The place where the downloaded PDF file will be put
            final File tempFile = new File( context.getExternalFilesDir( Environment.DIRECTORY_DOWNLOADS ), filename );

            if ( tempFile.isFile()) {
                // If we have downloaded the file before, just go ahead and show it.
                openPDF( context, fromFile( tempFile ) );
                return;
            }

          // Create the download request
            DownloadManager.Request request = new DownloadManager.Request(Uri.parse(downloadLink));
            request.setDescription("wait ...");
            request.setTitle(title);
            // in order for this if to run, you must use the android 3.2 to compile your app
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                request.allowScanningByMediaScanner();
                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
            }
            request.setDestinationInExternalFilesDir(context,Environment.DIRECTORY_DOWNLOADS, filename);

            // get download service and enqueue file
            final DownloadManager manager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);

            // Show progress dialog while downloading
            final ProgressDialog progDailog = new ProgressDialog(context);
            //Toast.makeText(context,"Download Manager",Toast.LENGTH_LONG).show();
            BroadcastReceiver onComplete = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    if ( !progDailog.isShowing() ) {
                        return;
                    }
                    context.unregisterReceiver( this );

                    progDailog.dismiss();
                    long downloadId = intent.getLongExtra( DownloadManager.EXTRA_DOWNLOAD_ID, -1 );
                    Cursor c = manager.query( new DownloadManager.Query().setFilterById( downloadId ) );
                    downloadId1=downloadId;
                    if ( c.moveToFirst() ) {
                        int status = c.getInt( c.getColumnIndex( DownloadManager.COLUMN_STATUS ) );
                        if ( status == DownloadManager.STATUS_SUCCESSFUL ) {
                            Toast.makeText(context, "Offline saved", Toast.LENGTH_SHORT).show();
                            openPDF( context, fromFile( tempFile) );
                        }
                    }
                    c.close();
                }
            };

            context.registerReceiver( onComplete, new IntentFilter( DownloadManager.ACTION_DOWNLOAD_COMPLETE ) );
            manager.enqueue(request);

            progDailog.setTitle("Data Downloading....");
            progDailog.setMessage("Please Wait.....");
            progDailog.setCancelable(false);
            progDailog.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    manager.remove(downloadId1);//todo progress bar cancelling progress
                    dialog.dismiss();

                }
            });
            progDailog.show();


        }catch (Exception ex){
        }finally {
        }
    }

    //download and share pdf

    @TargetApi(Build.VERSION_CODES.GINGERBREAD)
    public static void downloadAndSharePDF(final Context context, final String title,final String pdfUrl) {
        try{
            // Get filename
            final String filename = title + ".pdf";
            // download link "https://drive.google.com/uc?export=download&id=1KkFEEsrfNDiG4sfDocvDIrjgLhGEWSvY"
            final String downloadLink = "https://drive.google.com/uc?export=download&id="+ pdfUrl.substring( pdfUrl.lastIndexOf( "=" ) + 1 );
            // The place where the downloaded PDF file will be put
            final File tempFile = new File( context.getExternalFilesDir( Environment.DIRECTORY_DOWNLOADS ), filename );

            if ( tempFile.isFile()) {
                // If we have downloaded the file before, just go ahead and show it.
                sharePdf( context, tempFile);
                return;
            }

            // Create the download request
            DownloadManager.Request request = new DownloadManager.Request(Uri.parse(downloadLink));
            request.setDescription("wait ...");
            request.setTitle(title);
            // in order for this if to run, you must use the android 3.2 to compile your app
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                request.allowScanningByMediaScanner();
                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
            }
            request.setDestinationInExternalFilesDir(context,Environment.DIRECTORY_DOWNLOADS, filename);

            // get download service and enqueue file
            final DownloadManager manager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);

            // Show progress dialog while downloading
            final ProgressDialog progDailog = new ProgressDialog(context);
            //Toast.makeText(context,"Download Manager",Toast.LENGTH_LONG).show();
            BroadcastReceiver onComplete = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    if ( !progDailog.isShowing() ) {
                        return;
                    }
                    context.unregisterReceiver( this );

                    progDailog.dismiss();
                    long downloadId = intent.getLongExtra( DownloadManager.EXTRA_DOWNLOAD_ID, -1 );
                    Cursor c = manager.query( new DownloadManager.Query().setFilterById( downloadId ) );
                    downloadId1=downloadId;
                    if ( c.moveToFirst() ) {
                        int status = c.getInt( c.getColumnIndex( DownloadManager.COLUMN_STATUS ) );
                        if ( status == DownloadManager.STATUS_SUCCESSFUL ) {
                            Toast.makeText(context, "Offline saved", Toast.LENGTH_SHORT).show();
                            sharePdf( context,  tempFile );
                        }
                    }
                    c.close();
                }
            };

            context.registerReceiver( onComplete, new IntentFilter( DownloadManager.ACTION_DOWNLOAD_COMPLETE ) );
            manager.enqueue(request);

            progDailog.setTitle("Data Downloading....");
            progDailog.setMessage("Please Wait.....");
            progDailog.setCancelable(false);
            progDailog.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    manager.remove(downloadId1);//todo progress bar cancelling progress
                    dialog.dismiss();

                }
            });
            progDailog.show();


        }catch (Exception ex){
        }finally {
        }
    }


    /**
     * Show a dialog asking the user if he wants to open the PDF through Google Drive
     * @param context
     * @param pdfUrl
     */
    public static void askToOpenPDFThroughGoogleDrive( final Context context,final String title, final String pdfUrl ) {
        new AlertDialog.Builder( context )
                .setTitle( title )
                .setMessage( R.string.pdf_show_online_dialog_question )
                .setNegativeButton( R.string.pdf_show_online_dialog_button_no, null )
                .setPositiveButton( R.string.pdf_show_online_dialog_button_yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        openPDFThroughGoogleDrive(context, pdfUrl);
                    }
                })
                .show();
    }

    /**
     * Launches a browser to view the PDF through Google Drive
     * @param context
     * @param pdfUrl
     */
    public static void openPDFThroughGoogleDrive(final Context context, final String pdfUrl) {

        Intent i = new Intent( Intent.ACTION_VIEW );
        i.setDataAndType(parse(pdfUrl ), HTML_MIME_TYPE );
        context.startActivity( i );
    }
    /**
     * Open a local PDF file with an installed reader
     * @param context
     * @param localUri
     */
    public static final void openPDF(Context context, Uri localUri ) {
        Intent i = new Intent( Intent.ACTION_VIEW );
        i.setDataAndType( localUri, PDF_MIME_TYPE );
        context.startActivity( i );
    }
    /**
     * Checks if any apps are installed that supports reading of PDF files.
     * @param context
     * @return
     */
    public static boolean isPDFSupported( Context context ) {
        Intent i = new Intent( Intent.ACTION_VIEW );
        final File tempFile = new File( context.getExternalFilesDir( Environment.DIRECTORY_DOWNLOADS ), "test.pdf" );
        i.setDataAndType( fromFile( tempFile ), PDF_MIME_TYPE );
        return context.getPackageManager().queryIntentActivities( i, PackageManager.MATCH_DEFAULT_ONLY ).size() > 0;
    }
    public static void sharePdf(Context context,final File imageFile){
        Uri uri = Uri.fromFile(imageFile);
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent .setType("application/pdf");
        intent.putExtra(Intent.EXTRA_SUBJECT,"Downloaded from MathsKing app google play store");
        intent.putExtra(Intent.EXTRA_TEXT,"Click here to get FREE all mathematics formulas,shortcuts,concepts with examples,questions and live test series challenges.  \nhttp://play.google.com/store/apps/details?id=com.naresh.kingupadhyay.mathsking");
        intent .putExtra(Intent.EXTRA_STREAM, uri);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // this will make such that when user returns to your app, your app is displayed, instead of the email app.
        context.startActivity(Intent.createChooser(intent,"Share Pdf"));
    }
    public static File tempFile(Context context,String pdfUrl){

        // Get filename
        final String filename = pdfUrl.substring( pdfUrl.lastIndexOf( "=" ) + 1 ) + ".pdf";
        // The place where the downloaded PDF file will be put
        final File tempFile1 = new File( context.getExternalFilesDir( Environment.DIRECTORY_DOWNLOADS ), filename );
        return tempFile1;
    }

    //ImageLoad
    public static void picassoLoadImageAvl(final ImageView imageView,final String stringUrl, final AVLoadingIndicatorView avLoadingIndicatorView){
        Picasso.get().load(stringUrl)
                .into(imageView, new Callback() {
                    @Override
                    public void onSuccess() {
                        avLoadingIndicatorView.hide();
//                        avLoadingIndicatorView.setVisibility(View.GONE);
                    }
                    @Override
                    public void onError(Exception e) {
                        avLoadingIndicatorView.hide();
//                        avLoadingIndicatorView.setVisibility(View.GONE);
//                        imageView.setBackgroundResource(R.drawable.home);
//                        imageView.setBackgroundResource(R.mipmap.ic_launcher);
                    }
                });

    }

    public static int ratingCalculation(int solution){
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

    public static void showPopupImage(ImageView imageViewTemp,View view){
        final Dialog myDialog = new Dialog(view.getContext());
        myDialog.setContentView(R.layout.show_image_popup);
        myDialog.setCancelable(false);
        TextView instructon = (TextView) myDialog.findViewById(R.id.instruction);
        ImageButton cancel = (ImageButton) myDialog.findViewById(R.id.cancel);
        SubsamplingScaleImageView imageView=(SubsamplingScaleImageView)myDialog.findViewById(R.id.showImage);
        instructon.setText(R.string.zoom);
        imageViewTemp.buildDrawingCache();
        Bitmap bitmap = imageViewTemp.getDrawingCache();
        imageView.setImage(ImageSource.bitmap(bitmap));
//        imageView.setImageBitmap(ssbitmap);
        myDialog.show();
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myDialog.dismiss();
            }
        });

    }

    public boolean isNetworkAvailable(View view) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) view.getContext().getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

}

