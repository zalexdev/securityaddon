package com.huntmix.secbutton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.pm.ShortcutInfoCompat;
import androidx.core.content.pm.ShortcutManagerCompat;
import androidx.core.graphics.drawable.IconCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


import com.github.isabsent.filepicker.SimpleFilePickerDialog;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textview.MaterialTextView;
import com.nordan.dialog.Animation;
import com.nordan.dialog.NordanAlertDialog;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.github.isabsent.filepicker.SimpleFilePickerDialog.CompositeMode.FILE_AND_FOLDER_MULTI_CHOICE;
import static com.github.isabsent.filepicker.SimpleFilePickerDialog.CompositeMode.FOLDER_ONLY_MULTI_CHOICE;
import static java.lang.String.*;

public class MainActivity extends AppCompatActivity implements SimpleFilePickerDialog.InteractionListenerString{
public TinyDB tinydb;
public String nameoftag;
public String iconpath;
public Boolean crypted;
public Boolean firstrun;
private static final String PICK_DIALOG = "PICK_DIALOG";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tinydb = new TinyDB(this);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        checkpermission();
        checkPermission2();
        firstrun = tinydb.getBoolean("first");
        if (firstrun == Boolean.FALSE){
            dialogsms();
            tinydb.putBoolean("first",true);
        }
        crypted = tinydb.getBoolean("cryptedf");
        if (crypted ==Boolean.TRUE){
            Intent intent = new Intent(MainActivity.this, Decrypter.class);
            startActivity(intent);
        }

// ======[Loading all...]======
        counter();

        final String rootPath = Environment.getExternalStorageDirectory().getAbsolutePath();
        findViewById(R.id.ff).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showListItemDialog("Select folders or files", rootPath, FILE_AND_FOLDER_MULTI_CHOICE, PICK_DIALOG);
                tinydb.putInt("selection",0);
            }
        });
        findViewById(R.id.crypt).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (tinydb.getString("pass") != "16symbolspassss" && tinydb.getString("pass") != "" && tinydb.getString("pass") != null){
                    generator();
                    showListItemDialog("Select folder(s)", rootPath, FOLDER_ONLY_MULTI_CHOICE, PICK_DIALOG);
                    tinydb.putInt("selection",1);}
                else{
                    passwarn();
                }
            }
        });
    }

    @Override
    public void onResume(){
        super.onResume();
        counter();
        checkPermission2();
        checkpermission();
        Boolean root = tinydb.getBoolean("rootmode");
        if (root == Boolean.TRUE){
            try {
                Runtime.getRuntime().exec("su");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }


    // ======[Loading status. Setting img and text on click]======


    // ======[Count selected apps, folders and files]======
    @SuppressLint("SetTextI18n")
    public void counter(){
        final MaterialButton app = findViewById(R.id.apps);
        int count1 = tinydb.getListString("list").size();
        app.setText(getResources().getString(R.string.select)+" ("+String.valueOf(count1)+")");
        final MaterialButton folder = findViewById(R.id.ff);
        int count2 = tinydb.getListString("list2").size();
        folder.setText(getResources().getString(R.string.select)+" ("+String.valueOf(count2)+")");
        final MaterialButton crypt = findViewById(R.id.crypt);
        int count3 = tinydb.getListString("list3").size();
        crypt.setText(getResources().getString(R.string.select)+" ("+String.valueOf(count3)+")");
    }
    // ======[Launch Select apps activity]======
    public void selectapps(View view){
        MaterialButton Button = (MaterialButton) findViewById(R.id.apps);
        Button.setText(getResources().getString(R.string.load));
        Intent intent = new Intent(MainActivity.this, SelectApps.class);
        startActivity(intent);
    }
    // ======[Open pick folders && Files dialog for delete]======
    @Override
    public void showListItemDialog(String title, String folderPath, SimpleFilePickerDialog.CompositeMode mode, String dialogTag){
        SimpleFilePickerDialog.build(folderPath, mode)
                .title(title)
                .neut("Back")
                .neg("Open")
                .pos("OK")
                .choiceMin(1)
                .show(this, dialogTag);
    }
    @Override
    public boolean onResult(@NonNull String dialogTag, int which, @NonNull Bundle extras) {
        List<String> selectedPaths = extras.getStringArrayList(SimpleFilePickerDialog.SELECTED_PATHS);
        WritePathes(selectedPaths);
        return false;
    }
    private void WritePathes(List<String> selectedPaths){
        if (selectedPaths != null && !selectedPaths.isEmpty()){
            int choice = tinydb.getInt("selection");
            if (choice == 0){
                tinydb.putListString("list2", (ArrayList<String>) selectedPaths);
                counter();
            }
            if (choice == 1){
                tinydb.putListString("list3", (ArrayList<String>) selectedPaths);
                counter();
            }

        }
    }
    public void passwarn(){

        new NordanAlertDialog.Builder(this)
                .setAnimation(Animation.POP)
                .isCancellable(false)
                .setTitle(getResources().getString(R.string.pass))
                .setMessage(getResources().getString(R.string.passwarn))
                .setPositiveBtnText("OK")
                .setNegativeBtnText(getResources().getString(R.string.cancel))
                .setIcon(R.drawable.error,false)
                .onPositiveClicked(() -> {Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                    startActivity(intent);})
                .onNegativeClicked(() -> {/* Do something here */})
                .build().show();
    }
    public void generator(){
        String result16 = tinydb.getString("pass");
        if (result16.length() < 16){
            while (result16.length() == 16){
                result16=result16+"1";
            }
            tinydb.putString("pass",result16);


        }
    }
    // ======[Permissions]======
    public void checkpermission(){
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){
            if(checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
                if(shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)){
                    ActivityCompat.requestPermissions(
                            MainActivity.this,
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            123
                    );

                }else {
                    // Request permission
                    ActivityCompat.requestPermissions(
                            MainActivity.this,
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            123
                    );
                }
            }else {
                // Permission already granted
            }
        }
    }
    public void checkPermission2(){
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){
            if(checkSelfPermission(Manifest.permission.RECEIVE_SMS)!= PackageManager.PERMISSION_GRANTED){
                if(shouldShowRequestPermissionRationale(Manifest.permission.RECEIVE_SMS)){
                    ActivityCompat.requestPermissions(
                            MainActivity.this,
                            new String[]{Manifest.permission.RECEIVE_SMS},
                            123
                    );

                }else {
                    // Request permission
                    ActivityCompat.requestPermissions(
                            MainActivity.this,
                            new String[]{Manifest.permission.RECEIVE_SMS},
                            123
                    );
                }
            }else {
                // Permission already granted
            }
        }

    }
    // ======[Add shortcut]======
    public void addshrt (View view){
        nameoftag = tinydb.getString("nameoftag");
        iconpath = tinydb.getString("iconpath");
        if (nameoftag.length()<3 && iconpath.length()<3){
            if (ShortcutManagerCompat.isRequestPinShortcutSupported(MainActivity.this))
            {
                ShortcutInfoCompat shortcutInfo = new ShortcutInfoCompat.Builder(MainActivity.this, "#1")
                        .setIntent(new Intent(MainActivity.this, Deleter.class).setAction(Intent.ACTION_MAIN))
                        .setShortLabel("Delete")
                        .setIcon(IconCompat.createWithResource(MainActivity.this, R.drawable.del2))
                        .build();
                ShortcutManagerCompat.requestPinShortcut(MainActivity.this, shortcutInfo, null);
            }
        }else {
            addcustomshrt();
        }
    }
    public void addcustomshrt(){
        File check = new File(iconpath);
        if (check.exists() && !check.isDirectory()){

            Bitmap bitmap = BitmapFactory.decodeFile(iconpath);
            if (ShortcutManagerCompat.isRequestPinShortcutSupported(MainActivity.this))
            {
                ShortcutInfoCompat shortcutInfo = new ShortcutInfoCompat.Builder(MainActivity.this, "#1")
                        .setIntent(new Intent(MainActivity.this, Deleter.class).setAction(Intent.ACTION_MAIN))
                        .setShortLabel(nameoftag)
                        .setIcon(IconCompat.createWithBitmap(bitmap))
                        .build();
                ShortcutManagerCompat.requestPinShortcut(MainActivity.this, shortcutInfo, null);
            }

        }else{

        new NordanAlertDialog.Builder(this)
                .setAnimation(Animation.POP)
                .isCancellable(false)
                .setTitle(getResources().getString(R.string.error))
                .setMessage(getResources().getString(R.string.short1))
                .setPositiveBtnText("OK")
                .setNegativeBtnText(getResources().getString(R.string.cancel))
                .setIcon(R.drawable.error,false)
                .onPositiveClicked(() -> {Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                    startActivity(intent);})
                .onNegativeClicked(() -> {/* Do something here */})
                .build().show();
    }}
    // ======[Other]======
    public void dialogsms(){
        new NordanAlertDialog.Builder(this)
                .setAnimation(Animation.POP)
                .isCancellable(false)
                .setTitle(getResources().getString(R.string.sms))
                .setMessage(getResources().getString(R.string.smswarn))
                .setPositiveBtnText("OK")
                .setNegativeBtnText(getResources().getString(R.string.cancel))
                .setIcon(R.drawable.warn,false)
                .onPositiveClicked(() -> {Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    Uri uri = Uri.fromParts("package", getPackageName(), null);
                    intent.setData(uri);
                    startActivity(intent);
                    Toast toast2 = Toast.makeText(getApplicationContext(),
                            "Enable autorun!", Toast.LENGTH_SHORT);
                    toast2.show();})
                .onNegativeClicked(() -> {/* Do something here */})
                .build().show();
    }
    public void openabout(View view){
        Intent intent = new Intent(MainActivity.this, About.class);
        startActivity(intent);

    }
    public void opensettings(View view) {
        Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
        startActivity(intent);


    }
    public void startdelete(View view) {
        new NordanAlertDialog.Builder(this)
                .setAnimation(Animation.POP)
                .isCancellable(true)
                .setTitle(getResources().getString(R.string.warn1))
                .setMessage(getResources().getString(R.string.delete))
                .setPositiveBtnText("OK")
                .setNegativeBtnText(getResources().getString(R.string.cancel))
                .setIcon(R.drawable.error,false)
                .onPositiveClicked(() -> {Intent intent = new Intent(MainActivity.this, Backgroundstarter.class);
                    startService(intent);
                    finish();})
                .onNegativeClicked(() -> {/* Do something here */})
                .build().show();


    }
}