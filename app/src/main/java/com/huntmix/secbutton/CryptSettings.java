package com.huntmix.secbutton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;

import com.github.isabsent.filepicker.SimpleFilePickerDialog;
import com.google.android.material.textview.MaterialTextView;

import java.util.ArrayList;
import java.util.List;

import ir.androidexception.andexalertdialog.AndExAlertDialog;
import ir.androidexception.andexalertdialog.AndExAlertDialogListener;
import ir.androidexception.andexalertdialog.InputType;

import static com.github.isabsent.filepicker.SimpleFilePickerDialog.CompositeMode.FILE_AND_FOLDER_MULTI_CHOICE;

public class CryptSettings extends AppCompatActivity implements SimpleFilePickerDialog.InteractionListenerString{
    public TinyDB tinydb;
    private static final String PICK_DIALOG = "PICK_DIALOG";
    public String rootPath;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crypt_settings);
        tinydb = new TinyDB(this);
        if(checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){tinydb.putBoolean("perm1",true);}
        permnotif1();
        findViewById(R.id.selectcrypt).setOnClickListener(view -> {
            rootPath = Environment.getExternalStorageDirectory().getAbsolutePath();
            showListItemDialog("Select folders or files", rootPath, FILE_AND_FOLDER_MULTI_CHOICE, PICK_DIALOG);

        });
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
            tinydb.putListString("list3", (ArrayList<String>) selectedPaths);
            MaterialTextView tag = findViewById(R.id.selectc);
            tag.setText("Select ("+tinydb.getListString("list3").size()+")");
        }
    }

    public void checkpermission(){
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){

            if(checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
                if(shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)){
                    ActivityCompat.requestPermissions(
                            CryptSettings.this,
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            123
                    );

                }else {
                    // Request permission
                    ActivityCompat.requestPermissions(
                            CryptSettings.this,
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            123
                    );
                }
            }else{
                tinydb.putBoolean("perm1",true);
            }
        }
    }
    public void permnotif1(){
        if (!tinydb.getBoolean("perm1")){
            new AndExAlertDialog.Builder(this)
                    .setTitle(getResources().getString(R.string.perm))
                    .setImage(R.drawable.files,20)
                    .setMessage(getResources().getString(R.string.permask1))
                    .setPositiveBtnText(getResources().getString(R.string.grant))
                    .setCancelableOnTouchOutside(false)
                    .OnPositiveClicked(new AndExAlertDialogListener() {
                        @Override
                        public void OnClick(String input) {
                            checkpermission();
                        }
                    })
                    .build();}
    }
    public void passset(){

        new AndExAlertDialog.Builder(this)
                .setTitle(getResources().getString(R.string.password))
                .setMessage(getResources().getString(R.string.setpassword))
                .setPositiveBtnText("Set")
                .setNegativeBtnText("Cancel")
                .setImage(R.drawable.pass,20)
                .setEditText(true,false, getResources().getString(R.string.password), InputType.PASSWORD)
                .setCancelableOnTouchOutside(true)
                .OnPositiveClicked(new AndExAlertDialogListener() {
                    @Override
                    public void OnClick(String input) {
                        if (input.length() == 16){
                        tinydb.putString("pass",input);
                        MaterialTextView tag = findViewById(R.id.passtext);
                        tag.setText("Pass setted");}else{
                            passerror();
                        }
                    }
                })
                .OnNegativeClicked(new AndExAlertDialogListener() {
                    @Override
                    public void OnClick(String input) {

                    }
                })
                .build();
    }
    public void setpassword(View view){
        passset();
    }
    public void passerror() {
        new AndExAlertDialog.Builder(this)
                .setTitle(getResources().getString(R.string.warn1))
                .setMessage(getResources().getString(R.string.lowpass))
                .setPositiveBtnText("Ok")
                .setCancelableOnTouchOutside(false)

                .build();
    }
}