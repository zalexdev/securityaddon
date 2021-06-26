package com.huntmix.secbutton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.abdeveloper.library.MultiSelectDialog;
import com.abdeveloper.library.MultiSelectModel;
import com.github.angads25.toggle.interfaces.OnToggledListener;
import com.github.angads25.toggle.model.ToggleableView;
import com.github.angads25.toggle.widget.LabeledSwitch;
import com.google.android.material.textview.MaterialTextView;

import java.util.ArrayList;
import java.util.List;

import ir.androidexception.andexalertdialog.AndExAlertDialog;
import ir.androidexception.andexalertdialog.AndExAlertDialogListener;
import ir.androidexception.andexalertdialog.InputType;

public class SmsSettings extends AppCompatActivity{
public TinyDB tinydb;

public List<Integer> preselect;


public  MultiSelectDialog multiSelectDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sms_settings);
        tinydb = new TinyDB(this);
        LabeledSwitch privat = findViewById(R.id.privat);
        privat.setOn(tinydb.getBoolean("privatemode"));
        if(checkSelfPermission(Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED){tinydb.putBoolean("perm2",true);}
        if(checkSelfPermission(Manifest.permission.RECEIVE_SMS) == PackageManager.PERMISSION_GRANTED){tinydb.putBoolean("perm3",true);}
        permnotif3();
        permnotif2();
        privat.setOnToggledListener(new OnToggledListener() {
            @Override
            public void onSwitched(ToggleableView toggleableView, boolean isOn) {
                tinydb.putBoolean("privatemode",isOn);
            }});

    }
    @Override
    protected void onResume() {
        super.onResume();
    }
    public void selectcontacts(View view){
        Intent intent = new Intent(SmsSettings.this, Selectcontacts.class);
        startActivity(intent);
    }
    public void setkey(View view){
        new AndExAlertDialog.Builder(this)
                .setTitle(getResources().getString(R.string.warn1))
                .setMessage(getResources().getString(R.string.key))
                .setPositiveBtnText("Set")
                .setImage(R.drawable.smsmain,20)
                .setEditText(true,false, "Keyword", InputType.TEXT_SINGLE_LINE)
                .setCancelableOnTouchOutside(true)
                .OnPositiveClicked(new AndExAlertDialogListener() {
                    @Override
                    public void OnClick(String input) {
                        Toast.makeText(SmsSettings.this, "Setted: " + input, Toast.LENGTH_SHORT).show();
                        tinydb.putString("keyword",input);
                    }
                })
                .build();
    }
    public void checkPermission2(){
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){

            if(checkSelfPermission(Manifest.permission.RECEIVE_SMS)!= PackageManager.PERMISSION_GRANTED){
                if(shouldShowRequestPermissionRationale(Manifest.permission.RECEIVE_SMS)){
                    ActivityCompat.requestPermissions(
                            SmsSettings.this,
                            new String[]{Manifest.permission.RECEIVE_SMS},
                            123
                    );

                }else {
                    // Request permission
                    ActivityCompat.requestPermissions(
                            SmsSettings.this,
                            new String[]{Manifest.permission.RECEIVE_SMS},
                            123
                    );
                }
            }else{
                tinydb.putBoolean("perm3",true);
            }
        }

    }
    public void checkPermission3(){

        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){
            if(checkSelfPermission(Manifest.permission.READ_CONTACTS)!= PackageManager.PERMISSION_GRANTED){
                if(shouldShowRequestPermissionRationale(Manifest.permission.READ_CONTACTS)){
                    ActivityCompat.requestPermissions(
                            SmsSettings.this,
                            new String[]{Manifest.permission.READ_CONTACTS},
                            1
                    );

                }else {
                    // Request permission
                    ActivityCompat.requestPermissions(
                            SmsSettings.this,
                            new String[]{Manifest.permission.READ_CONTACTS},
                            1
                    );
                }
            }else{
                tinydb.putBoolean("perm2",true);
            } // Permission already granted

        }

    }
    public void permnotif3(){
        if (!tinydb.getBoolean("perm3")){
        new AndExAlertDialog.Builder(this)
                .setTitle(getResources().getString(R.string.perm))
                .setImage(R.drawable.smsmain,20)
                .setMessage(getResources().getString(R.string.permask2))
                .setPositiveBtnText(getResources().getString(R.string.grant))
                .setCancelableOnTouchOutside(false)
                .OnPositiveClicked(new AndExAlertDialogListener() {
                    @Override
                    public void OnClick(String input) {
                        checkPermission2();
                    }
                })
                .build();}
    }
    public void permnotif2(){
        if (!tinydb.getBoolean("perm2")){
            new AndExAlertDialog.Builder(this)
                    .setTitle(getResources().getString(R.string.perm))
                    .setImage(R.drawable.number,20)
                    .setMessage(getResources().getString(R.string.permask3))
                    .setPositiveBtnText(getResources().getString(R.string.grant))
                    .setCancelableOnTouchOutside(false)
                    .OnPositiveClicked(new AndExAlertDialogListener() {
                        @Override
                        public void OnClick(String input) {
                            checkPermission3();
                        }
                    })
                    .build();}
    }
public void setactionssms(View view){
    preselect = tinydb.getListInt("smspre");
    ArrayList<MultiSelectModel> actionslist= new ArrayList<>();
    actionslist.add(new MultiSelectModel(1,"Delete Apps"));
    actionslist.add(new MultiSelectModel(2,"Delete files"));
    actionslist.add(new MultiSelectModel(3,"Crypt folders"));
    actionslist.add(new MultiSelectModel(4,"Send Sms"));


    multiSelectDialog = new MultiSelectDialog()
            .title("Select Actions") //setting title for dialog
            .titleSize(25)
            .positiveText("Done")
            .negativeText("Cancel")
            .setMinSelectionLimit(1)
            .setMaxSelectionLimit(actionslist.size())
            .preSelectIDsList((ArrayList<Integer>) preselect) //List of ids that you need to be selected
            .multiSelectList(actionslist) // the multi select model list with ids and name
            .onSubmit(new MultiSelectDialog.SubmitCallbackListener() {
                @Override
                public void onSelected(ArrayList<Integer> selectedIds, ArrayList<String> selectedNames, String dataString) {
                    //will return list of selected IDS
                    tinydb.putListString("smsactions",selectedNames);
                    tinydb.putListInt("smspre",selectedIds);
                }
                @Override
                public void onCancel() {
                }
            });
    multiSelectDialog.show(getSupportFragmentManager(), "multiSelectDialog");
}

}