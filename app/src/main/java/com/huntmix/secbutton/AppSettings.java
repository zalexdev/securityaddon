package com.huntmix.secbutton;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.github.angads25.toggle.interfaces.OnToggledListener;
import com.github.angads25.toggle.model.ToggleableView;
import com.github.angads25.toggle.widget.LabeledSwitch;
import com.google.android.material.textview.MaterialTextView;
import com.topjohnwu.superuser.Shell;

public class AppSettings extends AppCompatActivity {
public TinyDB tinydb;
public Shell.Result check;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_settings);
        tinydb = new TinyDB(this);
        boolean rootdb = tinydb.getBoolean("rootapps");
        boolean sysappdb = tinydb.getBoolean("sysapp");
        boolean cleardb = tinydb.getBoolean("clear");
        LabeledSwitch root = findViewById(R.id.root);
        LabeledSwitch sysapp = findViewById(R.id.sysapp);
        LabeledSwitch clear = findViewById(R.id.clear);
        if (rootdb){ root.setColorOff(getResources().getColor(R.color.green));
            root.setColorOn(getResources().getColor(R.color.white));}
        root.setOn(rootdb);
        sysapp.setOn(sysappdb);
        clear.setOn(cleardb);
        root.setOnToggledListener(new OnToggledListener() {
            @Override
            public void onSwitched(ToggleableView toggleableView, boolean isOn) {
                if (isOn){
                    if (checkroot()){
                        tinydb.putBoolean("rootapps",isOn);
                        root.setColorOff(getResources().getColor(R.color.green));
                        root.setColorOn(getResources().getColor(R.color.white));

                    }
                    if (!checkroot()){
                        root.performClick();
                        root.setColorOff(getResources().getColor(R.color.red));
                        root.setColorOn(getResources().getColor(R.color.white));
                        tinydb.putBoolean("rootapps",false);
                        toaster("You didn`t grant root!");
                    }

                }
                if (!isOn){
                    root.setOn(false);
                    root.setColorOff(getResources().getColor(R.color.red));
                    root.setColorOn(getResources().getColor(R.color.white));
                    tinydb.putBoolean("rootapps",false);

                }

            }});
        sysapp.setOnToggledListener(new OnToggledListener() {
            @Override
            public void onSwitched(ToggleableView toggleableView, boolean isOn) {
                if (isOn){
                    if (tinydb.getBoolean("rootapps")){
                    tinydb.putBoolean("sysapp",isOn);}else{
                       sysapp.setColorOff(getResources().getColor(R.color.red));
                       sysapp.setColorOn(getResources().getColor(R.color.white));
                        sysapp.performClick();
                        toaster("You need root for this option");
                    }
                }
                if (!isOn){
                    tinydb.putBoolean("sysapp",isOn);
                }


            }});
        clear.setOnToggledListener(new OnToggledListener() {
            @Override
            public void onSwitched(ToggleableView toggleableView, boolean isOn) {
                if (isOn){
                    if (tinydb.getBoolean("rootapps")){
                        Log.i("gg", String.valueOf(tinydb.getBoolean("rootapps")));
                        tinydb.putBoolean("clear",isOn);}else{
                        clear.setColorOff(getResources().getColor(R.color.red));
                        clear.setColorOn(getResources().getColor(R.color.white));
                        clear.performClick();
                        toaster("You need root for this option");
                    }
                }
                if (!isOn){
                    tinydb.putBoolean("clear",isOn);
                }

            }});
    }
    public boolean checkroot(){
        check = Shell.su("su").exec();
        return check.isSuccess();
    }
    public void toaster(String status){
        Toast stat = Toast.makeText(getApplicationContext(), status, Toast.LENGTH_SHORT);
        stat.show();

    }
    public void selectapps(View view){
        Intent intent = new Intent(AppSettings.this, SelectApps.class);
        startActivity(intent);
    }

}