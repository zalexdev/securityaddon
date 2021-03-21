package com.huntmix.secbutton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.github.isabsent.filepicker.SimpleFilePickerDialog;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.datepicker.MaterialTextInputPicker;
import com.google.android.material.textfield.TextInputEditText;
import com.nordan.dialog.Animation;
import com.nordan.dialog.NordanAlertDialog;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import co.mobiwise.materialintro.animation.MaterialIntroListener;
import co.mobiwise.materialintro.shape.Focus;
import co.mobiwise.materialintro.shape.FocusGravity;
import co.mobiwise.materialintro.view.MaterialIntroView;

import static com.github.isabsent.filepicker.SimpleFilePickerDialog.CompositeMode.FILE_AND_FOLDER_MULTI_CHOICE;
import static com.github.isabsent.filepicker.SimpleFilePickerDialog.CompositeMode.FILE_ONLY_MULTI_CHOICE;
import static com.github.isabsent.filepicker.SimpleFilePickerDialog.CompositeMode.FOLDER_ONLY_MULTI_CHOICE;

public class Cryptonic extends AppCompatActivity implements SimpleFilePickerDialog.InteractionListenerString{
public TinyDB tinydb;
private static final String PICK_DIALOG = "PICK_DIALOG";
public Integer selection;
public String pass;
public MaterialCardView crypt;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.crypton);
        tinydb = new TinyDB(this);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        counter();
        crypt = (MaterialCardView) findViewById(R.id.materialCardView);
        tutor();
        final String rootPath = Environment.getExternalStorageDirectory().getAbsolutePath();
        findViewById(R.id.files).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showListItemDialog("Select files", rootPath, FILE_ONLY_MULTI_CHOICE, PICK_DIALOG);
                selection = 0;
            }
        });
        findViewById(R.id.defiles).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                    showListItemDialog("Select files", rootPath, FILE_ONLY_MULTI_CHOICE, PICK_DIALOG);
                    selection=1;}


        });
    }
public void tutor(){
    new MaterialIntroView.Builder(Cryptonic.this)
            .enableDotAnimation(false)
            .enableIcon(false)
            .setFocusGravity(FocusGravity.CENTER)
            .setFocusType(Focus.MINIMUM)
            .setDelayMillis(500)
            .enableFadeAnimation(true)
            .performClick(false)
            .setInfoText(getResources().getString(R.string.tutorial_pass))
            .setTarget(crypt)
            .setUsageId("pass")
            .setIdempotent(true)
            .setListener(new MaterialIntroListener() {
                @Override
                public void onUserClicked(String materialIntroViewId) {

                }
            })
            .show();
}











    @Override
    public void onResume(){
        super.onResume();
        counter();
        checkpermission();
    }
    public void crypt(View view){
        TextInputEditText mEdit   = (TextInputEditText) findViewById(R.id.gg);
        pass = mEdit.getText().toString();
        int len = pass.length();
        Log.i("pass", String.valueOf(len));
        if (len == 16){
        cryptwarn();}
        else{
            passerror();
        }
    }
    public void decrypt(View view){
        TextInputEditText mEdit   = (TextInputEditText) findViewById(R.id.gg);
        pass = mEdit.getText().toString();
        int len = pass.length();
        Log.i("pass", String.valueOf(len));
        if (len == 16){
            decryptwarn();}
        else{
            passerror();
        }
    }
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
            int choice = selection;
            if (choice == 0){
                tinydb.putListString("listcrypt", (ArrayList<String>) selectedPaths);
                counter();
            }
            if (choice == 1){
                tinydb.putListString("listdecrypt", (ArrayList<String>) selectedPaths);
                counter();
            }

        }
    }
    public void counter(){
        final MaterialButton app = findViewById(R.id.files);
        int count1 = tinydb.getListString("listcrypt").size();
        app.setText(getResources().getString(R.string.select)+" ("+String.valueOf(count1)+")");
        final MaterialButton folder = findViewById(R.id.defiles);
        int count2 = tinydb.getListString("listdecrypt").size();
        folder.setText(getResources().getString(R.string.select)+" ("+String.valueOf(count2)+")");
    }
    public void checkpermission(){
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){
            if(checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
                if(shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)){
                    ActivityCompat.requestPermissions(
                            Cryptonic.this,
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            123
                    );

                }else {
                    // Request permission
                    ActivityCompat.requestPermissions(
                            Cryptonic.this,
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            123
                    );
                }
            }else {
                // Permission already granted
            }
        }
    }
    public void decryptwarn() {
        new NordanAlertDialog.Builder(this)
                .setAnimation(Animation.POP)
                .isCancellable(true)
                .setTitle(getResources().getString(R.string.warn1))
                .setMessage(getResources().getString(R.string.ddcryptwarn))
                .setPositiveBtnText("OK")
                .setNegativeBtnText(getResources().getString(R.string.cancel))
                .setIcon(R.drawable.error,false)
                .onPositiveClicked(() -> {Intent intent = new Intent(this, Decrypt.class);
                    intent.putExtra("pass",pass);
                    startService(intent);})
                .onNegativeClicked(() -> {/* Do something here */})
                .build().show();


    }
    public void cryptwarn() {
        new NordanAlertDialog.Builder(this)
                .setAnimation(Animation.POP)
                .isCancellable(true)
                .setTitle(getResources().getString(R.string.warn1))
                .setMessage(getResources().getString(R.string.ddcryptwarn))
                .setPositiveBtnText("OK")
                .setNegativeBtnText(getResources().getString(R.string.cancel))
                .setIcon(R.drawable.error,false)
                .onPositiveClicked(() -> {Intent intent = new Intent(this, Crypter.class);
                    intent.putExtra("pass",pass);
                    startService(intent);})
                .onNegativeClicked(() -> {/* Do something here */})
                .build().show();


    }
    public void passerror() {
        new NordanAlertDialog.Builder(this)
                .setAnimation(Animation.POP)
                .isCancellable(true)
                .setTitle(getResources().getString(R.string.warn1))
                .setMessage(getResources().getString(R.string.lowpass))
                .setPositiveBtnText("OK")
                .setIcon(R.drawable.error,false)
                .onPositiveClicked(() -> {})
                .build().show();


    }
}