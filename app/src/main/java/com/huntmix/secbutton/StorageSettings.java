package com.huntmix.secbutton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.widget.Toast;

import com.github.angads25.toggle.interfaces.OnToggledListener;
import com.github.angads25.toggle.model.ToggleableView;
import com.github.angads25.toggle.widget.LabeledSwitch;
import com.github.isabsent.filepicker.SimpleFilePickerDialog;
import com.topjohnwu.superuser.Shell;

import java.util.ArrayList;
import java.util.List;

import ir.androidexception.andexalertdialog.AndExAlertDialog;
import ir.androidexception.andexalertdialog.AndExAlertDialogListener;

import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.os.Build.VERSION.SDK_INT;
import static com.github.isabsent.filepicker.SimpleFilePickerDialog.CompositeMode.FILE_AND_FOLDER_MULTI_CHOICE;

public class StorageSettings extends AppCompatActivity implements SimpleFilePickerDialog.InteractionListenerString{
    public Shell.Result check;
    public TinyDB tinydb;
    private static final String PICK_DIALOG = "PICK_DIALOG";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_storage_settings);
        tinydb = new TinyDB(this);
        if(checkSelfPermission(WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){tinydb.putBoolean("perm1",true);}
        permnotif1();


        final String rootPath = Environment.getExternalStorageDirectory().getAbsolutePath();
        findViewById(R.id.selectfolders).setOnClickListener(view -> {
            showListItemDialog("Select folders or files", rootPath, FILE_AND_FOLDER_MULTI_CHOICE, PICK_DIALOG);
        });
    }

    public void checkpermission(){
        if(SDK_INT>=Build.VERSION_CODES.M){

            if(checkSelfPermission(WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
                if(shouldShowRequestPermissionRationale(WRITE_EXTERNAL_STORAGE)){
                    ActivityCompat.requestPermissions(
                            StorageSettings.this,
                            new String[]{WRITE_EXTERNAL_STORAGE},
                            123
                    );

                }else {
                    // Request permission
                    ActivityCompat.requestPermissions(
                            StorageSettings.this,
                            new String[]{WRITE_EXTERNAL_STORAGE},
                            123
                    );
                }
            }else{
                tinydb.putBoolean("perm1",true);
            }
        }

            if (SDK_INT >= Build.VERSION_CODES.R) {
                new AndExAlertDialog.Builder(this)
                        .setTitle(getResources().getString(R.string.perm))
                        .setImage(R.drawable.android,20)
                        .setMessage(getResources().getString(R.string.rfiles))
                        .setPositiveBtnText(getResources().getString(R.string.yes))
                        .setNegativeBtnText(getResources().getString(R.string.no))
                        .OnNegativeClicked(new AndExAlertDialogListener() {
                            @Override
                            public void OnClick(String input) {

                            }
                        })
                        .setCancelableOnTouchOutside(false)
                        .OnPositiveClicked(new AndExAlertDialogListener() {
                            @Override
                            public void OnClick(String input) {
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
                            }
                        })
                        .build();
            } else {
                //below android 11
                ActivityCompat.requestPermissions(StorageSettings.this, new String[]{WRITE_EXTERNAL_STORAGE}, 123);
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

    @Override
    protected void onResume() {
        super.onResume();
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
                tinydb.putListString("list2", (ArrayList<String>) selectedPaths);
        }
    }
}