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

import ir.androidexception.andexalertdialog.AndExAlertDialog;
import ir.androidexception.andexalertdialog.AndExAlertDialogListener;
import ir.androidexception.andexalertdialog.InputType;

public class SosSettings extends AppCompatActivity {
public  TinyDB tinydb;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sos_settings);
        tinydb = new TinyDB(this);
        if(checkSelfPermission(Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED){tinydb.putBoolean("perm2",true);}
        if(checkSelfPermission(Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED){tinydb.putBoolean("perm4",true);}
        permnotif2();
        permnotif4();

    }
    public void selectcontacts(View view){
        Intent intent = new Intent(SosSettings.this, Selectnumbers.class);
        startActivity(intent);
    }
    public void checkPermission3(){

        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){
            if(checkSelfPermission(Manifest.permission.READ_CONTACTS)!= PackageManager.PERMISSION_GRANTED){
                if(shouldShowRequestPermissionRationale(Manifest.permission.READ_CONTACTS)){
                    ActivityCompat.requestPermissions(
                            SosSettings.this,
                            new String[]{Manifest.permission.READ_CONTACTS},
                            1
                    );

                }else {
                    // Request permission
                    ActivityCompat.requestPermissions(
                            SosSettings.this,
                            new String[]{Manifest.permission.READ_CONTACTS},
                            1
                    );
                }
            }else{
                tinydb.putBoolean("perm2",true);
            } // Permission already granted

        }

    }
    public void checkPermission4(){
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){

            if(checkSelfPermission(Manifest.permission.SEND_SMS)!= PackageManager.PERMISSION_GRANTED){
                if(shouldShowRequestPermissionRationale(Manifest.permission.SEND_SMS)){
                    ActivityCompat.requestPermissions(
                            SosSettings.this,
                            new String[]{Manifest.permission.SEND_SMS},
                            123
                    );

                }else {
                    // Request permission
                    ActivityCompat.requestPermissions(
                            SosSettings.this,
                            new String[]{Manifest.permission.SEND_SMS},
                            123
                    );
                }
            }
            else{
                tinydb.putBoolean("perm4",true);
            }
        }

    }
    public void permnotif4(){
        if(!tinydb.getBoolean("perm4")){
        new AndExAlertDialog.Builder(this)
                .setTitle(getResources().getString(R.string.perm))
                .setImage(R.drawable.message,20)
                .setMessage(getResources().getString(R.string.permask4))
                .setPositiveBtnText(getResources().getString(R.string.grant))
                .setCancelableOnTouchOutside(false)
                .OnPositiveClicked(new AndExAlertDialogListener() {
                    @Override
                    public void OnClick(String input) {
                        checkPermission4();
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
    public void setsostext(View view){
        new AndExAlertDialog.Builder(this)
                .setTitle(getResources().getString(R.string.warn1))
                .setMessage(getResources().getString(R.string.sendsms2))
                .setPositiveBtnText("Set")
                .setImage(R.drawable.smsmain,20)
                .setEditText(true,false, "Your message", InputType.TEXT_MULTI_LINE)
                .setCancelableOnTouchOutside(true)
                .OnPositiveClicked(new AndExAlertDialogListener() {
                    @Override
                    public void OnClick(String input) {
                        Toast.makeText(SosSettings.this, "Setted: " + input, Toast.LENGTH_SHORT).show();
                        tinydb.putString("msg",input);
                    }
                })

                .build();
    }

}