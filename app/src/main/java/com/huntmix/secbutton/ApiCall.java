package com.huntmix.secbutton;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;

public class ApiCall extends Activity {
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = new Intent(this, Api.class);
        List<String>  list= getIntent().getStringArrayListExtra("path");
        String pass = getIntent().getStringExtra("pass");
        String mode = getIntent().getStringExtra("mode");
        intent.putExtra("pass",pass);
        intent.putExtra("mode",mode);
        intent.putStringArrayListExtra("path", (ArrayList<String>) list);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(intent);
        }else{
            startService(intent);

        }
        finish();
    }
}
