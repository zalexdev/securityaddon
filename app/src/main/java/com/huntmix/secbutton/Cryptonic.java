package com.huntmix.secbutton;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.github.isabsent.filepicker.SimpleFilePickerDialog;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;

import ir.androidexception.andexalertdialog.AndExAlertDialog;
import ir.androidexception.andexalertdialog.AndExAlertDialogListener;

import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.os.Build.VERSION.SDK_INT;
import static com.github.isabsent.filepicker.SimpleFilePickerDialog.CompositeMode.FILE_ONLY_MULTI_CHOICE;

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

        counter();
        if(checkSelfPermission(WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){tinydb.putBoolean("perm1",true);}
        permnotif1();
        final String rootPath = Environment.getExternalStorageDirectory().getAbsolutePath();
        findViewById(R.id.selectcr).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showListItemDialog("Select files", rootPath, FILE_ONLY_MULTI_CHOICE, PICK_DIALOG);
            }
        });


        }
    public void checkpermission(){
        if(SDK_INT>=Build.VERSION_CODES.M){

            if(checkSelfPermission(WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
                if(shouldShowRequestPermissionRationale(WRITE_EXTERNAL_STORAGE)){
                    ActivityCompat.requestPermissions(
                            Cryptonic.this,
                            new String[]{WRITE_EXTERNAL_STORAGE},
                            123
                    );

                }else {
                    // Request permission
                    ActivityCompat.requestPermissions(
                            Cryptonic.this,
                            new String[]{WRITE_EXTERNAL_STORAGE},
                            123
                    );
                }
            }else{
                tinydb.putBoolean("perm1",true);
            }
        }

        if (SDK_INT >= Build.VERSION_CODES.R) {
            try {
                Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                intent.addCategory("android.intent.category.DEFAULT");
                intent.setData(Uri.parse(String.format("package:%s",getApplicationContext().getPackageName())));
                startActivityForResult(intent, 2296);
            } catch (Exception e) {
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                startActivityForResult(intent, 2296);
            }
        } else {
            //below android 11
            ActivityCompat.requestPermissions(Cryptonic.this, new String[]{WRITE_EXTERNAL_STORAGE}, 123);
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
                    .build();}}












    @Override
    public void onResume(){
        super.onResume();
        counter();

    }
    public void crypt(View view){
        TextInputEditText mEdit   = (TextInputEditText) findViewById(R.id.passinput);
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
        TextInputEditText mEdit   = (TextInputEditText) findViewById(R.id.passinput);
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
                tinydb.putListString("listcryptonic", (ArrayList<String>) selectedPaths);
                counter();
        }
    }
    public void counter(){
        final MaterialButton app = findViewById(R.id.selectcr);
        int count1 = tinydb.getListString("listcryptonic").size();
        app.setText(getResources().getString(R.string.select)+" ("+String.valueOf(count1)+")");
    }
    public void decryptwarn() {
        new AndExAlertDialog.Builder(this)
                .setTitle(getResources().getString(R.string.warn1))
                .setMessage(getResources().getString(R.string.ddcryptwarn))
                .setPositiveBtnText("Ok")
                .setCancelableOnTouchOutside(false)
                .OnPositiveClicked(new AndExAlertDialogListener() {
                    @Override
                    public void OnClick(String input) {Intent intent = new Intent(Cryptonic.this, Api.class);
                        intent.putExtra("pass",pass);
                        intent.putExtra("mode","decrypt");
                        intent.putStringArrayListExtra("path", (ArrayList<String>) tinydb.getListString("listcryptonic"));
                        startService(intent);
                        tinydb.putString("listcryptonic","");
                        counter();

                    }
                })
                .build();


    }
    public void cryptwarn() {
        new AndExAlertDialog.Builder(this)
                .setTitle(getResources().getString(R.string.warn1))
                .setMessage(getResources().getString(R.string.ddcryptwarn))
                .setPositiveBtnText("Ok")
                .setCancelableOnTouchOutside(false)
                .OnPositiveClicked(new AndExAlertDialogListener() {
                    @Override
                    public void OnClick(String input) {Intent intent = new Intent(Cryptonic.this, Api.class);
                        intent.putExtra("pass",pass);
                        intent.putExtra("mode","crypt");
                        intent.putStringArrayListExtra("path", (ArrayList<String>) tinydb.getListString("listcryptonic"));
                        startService(intent);
                        tinydb.putString("listcryptonic","");
                        counter();
                    }
                })
                .build();



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