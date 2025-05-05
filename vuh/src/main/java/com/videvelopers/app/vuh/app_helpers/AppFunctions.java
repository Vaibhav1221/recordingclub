package com.videvelopers.app.vuh.app_helpers;

import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.videvelopers.app.vuh.app_components.ConnectivityReceiver;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import com.videvelopers.app.resources.R;


public class AppFunctions {

    private final Context context;
    private final AppCompatActivity activity;
    private final ProgressDialog progressDialog;
    private final int autoplay_value=0;
    private final String myBookMark="";

    public AppFunctions(Context context,AppCompatActivity activity){
        this.activity=activity;
        this.context=context;
        progressDialog=new ProgressDialog(activity);
        progressDialog.setCancelable(false);
    }

    public boolean isInternetConnected(){
        return ConnectivityReceiver.isConnected();
    }

    public void setCreateAlertDialogForNotConnectedToTheInternet(){
        MaterialAlertDialogBuilder builder=new MaterialAlertDialogBuilder(activity);
        builder.setCancelable(false);
        builder.setTitle(R.string.default_internet_alert_dialog_title);
        builder.setMessage(R.string.default_internet_alert_dialog_text);
        builder.setNegativeButton(R.string.btn_exit, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                activity.finish();
            }
        });
        builder.setPositiveButton(R.string.btn_retry, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                activity.finish();
                activity.startActivity(new Intent(context,activity.getClass()).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
            }
        });
        builder.show();
    }

    public void setDisplayAlertDialog(String title, String message, String positiveButtonText, DialogInterface.OnClickListener positiveButtonListener){
        MaterialAlertDialogBuilder builder=new MaterialAlertDialogBuilder(activity);
        builder.setCancelable(true);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton(positiveButtonText,positiveButtonListener);
        builder.show();
    }

    public void setDisplayAlertDialog(String title, String message, String negativeButtonText, String positiveButtonText, DialogInterface.OnClickListener negativeButtonListener, DialogInterface.OnClickListener positiveButtonListener){
        MaterialAlertDialogBuilder builder=new MaterialAlertDialogBuilder(activity);
        builder.setCancelable(false);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setNegativeButton(negativeButtonText,negativeButtonListener);
        builder.setPositiveButton(positiveButtonText,positiveButtonListener);
        builder.show();
    }

    public void setAlertDialogForNoInformations(){
        setDisplayAlertDialog(activity.getResources().getString(R.string.default_error_text), activity.getResources().getString(R.string.default_no_informations), activity.getResources().getString(R.string.btn_ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
    }

    public void setAlertDialogForResponseError(){
        setDisplayAlertDialog(activity.getResources().getString(R.string.default_error_text), activity.getResources().getString(R.string.default_error), activity.getResources().getString(R.string.btn_ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
    }

    public void setAlertDialogForServerError(){
        setDisplayAlertDialog(activity.getResources().getString(R.string.default_error_text), activity.getResources().getString(R.string.default_server_error), activity.getResources().getString(R.string.btn_ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
    }

    public void setAlertDialogForErrorFromServer(String message){
        setDisplayAlertDialog(activity.getResources().getString(R.string.default_error_text), message, activity.getResources().getString(R.string.btn_ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
    }

    public void setShowProgressDialogWithTitleAndMessage(String title,String message){
        if (!progressDialog.isShowing()){
            progressDialog.setTitle(title);
            progressDialog.setMessage(message);
            progressDialog.show();
        }
    }

    public void setShowProgressDialogWithTitle(String title){
        if (!progressDialog.isShowing()){
            progressDialog.setTitle(title);
            progressDialog.show();
        }
    }

    public void setShowProgressDialogWithTitleAndMessageAndProgress(String title,String message, int progress){
        if (!progressDialog.isShowing()){
            progressDialog.setTitle(title);
            progressDialog.setMessage(message);
            progressDialog.setProgress(progress);
            progressDialog.show();
        }
    }

    public void setHideProgressDialog(){
        if (progressDialog.isShowing()){
            progressDialog.hide();
        }
    }

    public int getSDKAPIVersion(){
        return Build.VERSION.SDK_INT;
    }

    public int getAppVersionCode(){
        try {
            PackageInfo packageInfo=activity.getPackageManager().getPackageInfo(activity.getPackageName(),0);
            return packageInfo.versionCode;
        }
        catch (Exception e){
            return 0;
        }
    }

    public String getAppVersionName(){
        try {
            PackageInfo packageInfo=activity.getPackageManager().getPackageInfo(activity.getPackageName(),0);
            return packageInfo.versionName;
        }
        catch (Exception e){
            return "";
        }
    }

    public void setCopyTextToClipboard(String text){
        ClipboardManager clipboardManager=(ClipboardManager) activity.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clipData=ClipData.newPlainText("Tech-Freedom",text);
        clipboardManager.setPrimaryClip(clipData);
        Toast.makeText(activity, activity.getResources().getString(R.string.default_clipboard_message), Toast.LENGTH_SHORT).show();
    }

    public void setShareText(String text){
        Intent intent=new Intent(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_TEXT,text);
        intent.setType("text/plain");
        activity.startActivity(Intent.createChooser(intent,"Share text via:"));
    }

    public void addOrUpdateUrl(String url) {
        try {
            SQLiteDatabase database = activity.openOrCreateDatabase("url_database", Context.MODE_PRIVATE, null);
            database.execSQL("CREATE TABLE IF NOT EXISTS urls (id INTEGER PRIMARY KEY AUTOINCREMENT, url_value TEXT UNIQUE);");
            database.execSQL("INSERT OR REPLACE INTO urls (url_value) VALUES ('" + url + "')");
            database.close();
        } catch (Exception e) {
            Toast.makeText(activity, "Error while initializing the database.", Toast.LENGTH_SHORT).show();
        }
    }

    public String getFirstUrlFromDatabase() {
        String firstUrl = "";
        try {
            SQLiteDatabase database = activity.openOrCreateDatabase("url_database", Context.MODE_PRIVATE, null);
            Cursor cursor = database.rawQuery("SELECT url_value FROM urls LIMIT 1", null);

            if (cursor != null && cursor.moveToFirst()) {
                firstUrl = cursor.getString(cursor.getColumnIndex("url_value"));
                cursor.close();
            }

            database.close();
        } catch (Exception e) {
            Toast.makeText(activity, "Error while accessing the database.", Toast.LENGTH_SHORT).show();
        }

        return firstUrl;
    }

    public String getFirstFileNameFromDatabase() {
        String firstName = "";
        try {
            SQLiteDatabase database = activity.openOrCreateDatabase("file_name_database", Context.MODE_PRIVATE, null);
            Cursor cursor = database.rawQuery("SELECT name_value FROM names LIMIT 1", null);

            if (cursor != null && cursor.moveToFirst()) {
                firstName = cursor.getString(cursor.getColumnIndex("name_value"));
                cursor.close();
            }
            database.close();
        } catch (Exception e) {}
        return firstName;
    }

    public void deleteTablesAndDatabases(Context context) {
        try {
            // Delete the urls table and database
            SQLiteDatabase urlDatabase = context.openOrCreateDatabase("url_database", Context.MODE_PRIVATE, null);
            urlDatabase.execSQL("DROP TABLE IF EXISTS urls");
            urlDatabase.close();

            context.deleteDatabase("url_database");

            // Delete the names table and database
            SQLiteDatabase nameDatabase = context.openOrCreateDatabase("file_name_database", Context.MODE_PRIVATE, null);
            nameDatabase.execSQL("DROP TABLE IF EXISTS names");
            nameDatabase.close();

            context.deleteDatabase("file_name_database");
        } catch (Exception e) {}
    }


    public String getPathFromUri(Uri uri) {
        try {
            Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);
            int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
            cursor.moveToFirst();
            String name = cursor.getString(nameIndex);
            File file = new File(context.getCacheDir() + "/tmp_paths/" + name);
            if (file.exists()) {
                file.delete();
            }
            InputStream inputStream = context.getContentResolver().openInputStream(uri);
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            int read = 0;
            int maxBufferSize = 1024 * 1024;
            int bytesAvailable = inputStream.available();
            int bufferSize = Math.min(bytesAvailable, maxBufferSize);
            final byte[] buffers = new byte[bufferSize];
            while ((read = inputStream.read(buffers)) != -1) {
                fileOutputStream.write(buffers, 0, read);
            }
            inputStream.close();
            fileOutputStream.close();
            return file.getPath();
        }
        catch (Exception e) {
            return "No path found.";
        }
    }

    public Uri getFileUriFromPath(Context context, String filePath) {
        Uri contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String[] projection = { MediaStore.Audio.Media._ID };
        String selection = MediaStore.Audio.Media.DATA + "=?";
        String[] selectionArgs = new String[]{ filePath };

        Cursor cursor = context.getContentResolver().query(contentUri, projection, selection, selectionArgs, null);
        if (cursor != null && cursor.moveToFirst()) {
            int id = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID));
            cursor.close();
            return ContentUris.withAppendedId(contentUri, id);
        }

        if (cursor != null)
            cursor.close();

        return null;
    }


}
