package com.huntmix.secbutton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Toast;

import com.github.angads25.toggle.interfaces.OnToggledListener;
import com.github.angads25.toggle.model.ToggleableView;
import com.github.angads25.toggle.widget.LabeledSwitch;

import ir.androidexception.andexalertdialog.AndExAlertDialog;
import ir.androidexception.andexalertdialog.AndExAlertDialogListener;
import ir.androidexception.andexalertdialog.InputType;

import static android.os.Build.VERSION.SDK_INT;

public class AntitheftSettings extends AppCompatActivity {
public TinyDB tinydb;
    public DeviceAdminManager dam;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_antitheft_settings);
        tinydb = new TinyDB(this);
        LabeledSwitch enable = findViewById(R.id.enableat);
        enable.setOn(tinydb.getBoolean("enableat"));
        dam = new DeviceAdminManager(this);
        if (dam.isActiveAdmin()){tinydb.putBoolean("dam",true);}else {tinydb.putBoolean("dam",false);}
        checkgeoperm();
        permnotif3();
        permnotif4();
        getallgeo();
        enable.setOnToggledListener(new OnToggledListener() {
            @Override
            public void onSwitched(ToggleableView toggleableView, boolean isOn) {
                tinydb.putBoolean("enableat",isOn);
            }
        });
    }
    public void setlocatecommand(View view){setlocate();}
    public void setlocate(){

        String pregeo = tinydb.getString("geocommand");
        new AndExAlertDialog.Builder(this)
                .setTitle(getResources().getString(R.string.commandsetup))
                .setMessage(getResources().getString(R.string.commandgeo))
                .setPositiveBtnText("Set")
                .setCancelableOnTouchOutside(false)
                .setImage(R.drawable.location,20)
                .setEditText(true,false, pregeo, InputType.TEXT_SINGLE_LINE)
                .setCancelableOnTouchOutside(true)
                .OnPositiveClicked(new AndExAlertDialogListener() {
                    @Override
                    public void OnClick(String input) {
                        if(input.length()>3){
                            Toast.makeText(AntitheftSettings.this, "Setted: " + input, Toast.LENGTH_SHORT).show();
                            tinydb.putString("geocommand",input);}else{
                            Toast.makeText(AntitheftSettings.this, getResources().getString(R.string.toomin), Toast.LENGTH_SHORT).show();
                            setlocate();
                        }
                    }
                })
                .build();}
    public void setblockcommand(View view){setblock();}
    public void setblock(){
        if (!dam.isActiveAdmin()){

            new AndExAlertDialog.Builder(this)
                    .setTitle(getResources().getString(R.string.warn1))
                    .setMessage(getResources().getString(R.string.adminwarn))
                    .setPositiveBtnText("Ok")
                    .OnPositiveClicked(new AndExAlertDialogListener() {
                        @Override
                        public void OnClick(String input) {startActivityForResult(dam.getStartAdminEnableIntent(), 1);

                        }
                    })
                    .build();}else{
        String pregeo = tinydb.getString("blockcommand");
        new AndExAlertDialog.Builder(this)
                .setTitle(getResources().getString(R.string.commandsetup))
                .setMessage(getResources().getString(R.string.commandblock))
                .setPositiveBtnText("Set")
                .setCancelableOnTouchOutside(false)
                .setImage(R.drawable.locked,20)
                .setEditText(true,false, pregeo, InputType.TEXT_SINGLE_LINE)
                .setCancelableOnTouchOutside(true)
                .OnPositiveClicked(new AndExAlertDialogListener() {
                    @Override
                    public void OnClick(String input) {
                        if(input.length()>3){
                        Toast.makeText(AntitheftSettings.this, "Setted: " + input, Toast.LENGTH_SHORT).show();
                        tinydb.putString("blockcommand",input);}else{
                            Toast.makeText(AntitheftSettings.this, getResources().getString(R.string.toomin), Toast.LENGTH_SHORT).show();
                            setblock();
                        }
                    }
                })
                .build();}
    }
    public void setwipecommand(View view){setwipe();}
    public void setwipe(){
        if (!dam.isActiveAdmin()){

            new AndExAlertDialog.Builder(this)
                    .setTitle(getResources().getString(R.string.warn1))
                    .setMessage(getResources().getString(R.string.adminwarn))
                    .setPositiveBtnText("Ok")
                    .OnPositiveClicked(new AndExAlertDialogListener() {
                        @Override
                        public void OnClick(String input) {startActivityForResult(dam.getStartAdminEnableIntent(), 1);

                        }
                    })
                    .build();}else {
        String pregeo = tinydb.getString("wipecommand");
        new AndExAlertDialog.Builder(this)
                .setTitle(getResources().getString(R.string.commandsetup))
                .setMessage(getResources().getString(R.string.setwipecommand))
                .setPositiveBtnText("Set")
                .setCancelableOnTouchOutside(false)
                .setImage(R.drawable.wipe,20)
                .setEditText(true,false, pregeo, InputType.TEXT_SINGLE_LINE)
                .setCancelableOnTouchOutside(true)
                .OnPositiveClicked(new AndExAlertDialogListener() {
                    @Override
                    public void OnClick(String input) {
                        if(input.length()>3){
                            Toast.makeText(AntitheftSettings.this, "Setted: " + input, Toast.LENGTH_SHORT).show();
                            tinydb.putString("wipecommand",input);}else{
                            Toast.makeText(AntitheftSettings.this, getResources().getString(R.string.toomin), Toast.LENGTH_SHORT).show();
                            setblock();
                        }
                    }
                })
                .build();}
    }
    public void checkgeoperm(){
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){

            if(checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION)!= PackageManager.PERMISSION_GRANTED){
                if(shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_COARSE_LOCATION)){
                    ActivityCompat.requestPermissions(
                            AntitheftSettings.this,
                            new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                            123
                    );

                }else {
                    // Request permission
                    ActivityCompat.requestPermissions(
                            AntitheftSettings.this,
                            new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                            123
                    );
                }
            }else{
            }
        }
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){

            if(checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED){
                if(shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)){
                    ActivityCompat.requestPermissions(
                            AntitheftSettings.this,
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                            123
                    );

                }else {
                    // Request permission
                    ActivityCompat.requestPermissions(
                            AntitheftSettings.this,
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                            123
                    );
                }
            }else{
            }
        }
    }
    public void getallgeo(){
        if (!tinydb.getBoolean("geofirst")){
        new AndExAlertDialog.Builder(this)
                .setTitle(getResources().getString(R.string.perm))
                .setImage(R.drawable.location,20)
                .setMessage(getResources().getString(R.string.getgeo))
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
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uri = Uri.fromParts("package", getPackageName(), null);
                        intent.setData(uri);
                        startActivity(intent);
                    }
                })
                .build();
        tinydb.putBoolean("geofirst",true);
        }
    }
    public void checkPermission2(){
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){

            if(checkSelfPermission(Manifest.permission.RECEIVE_SMS)!= PackageManager.PERMISSION_GRANTED){
                if(shouldShowRequestPermissionRationale(Manifest.permission.RECEIVE_SMS)){
                    ActivityCompat.requestPermissions(
                            AntitheftSettings.this,
                            new String[]{Manifest.permission.RECEIVE_SMS},
                            123
                    );

                }else {
                    // Request permission
                    ActivityCompat.requestPermissions(
                            AntitheftSettings.this,
                            new String[]{Manifest.permission.RECEIVE_SMS},
                            123
                    );
                }
            }else{
                tinydb.putBoolean("perm3",true);
            }
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
    public void checkPermission4(){
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){

            if(checkSelfPermission(Manifest.permission.SEND_SMS)!= PackageManager.PERMISSION_GRANTED){
                if(shouldShowRequestPermissionRationale(Manifest.permission.SEND_SMS)){
                    ActivityCompat.requestPermissions(
                            AntitheftSettings.this,
                            new String[]{Manifest.permission.SEND_SMS},
                            123
                    );

                }else {
                    // Request permission
                    ActivityCompat.requestPermissions(
                            AntitheftSettings.this,
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
}