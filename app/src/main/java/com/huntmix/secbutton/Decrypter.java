package com.huntmix.secbutton;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.nordan.dialog.Animation;
import com.nordan.dialog.NordanAlertDialog;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

public class Decrypter extends AppCompatActivity {
    public String pass;
    public Button mButton;


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.decrypter);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.white));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.white));
        }

        mButton = (Button) findViewById(R.id.button);
        EditText mEdit   = (EditText)findViewById(R.id.appCompatEditText);

        mButton.setOnClickListener(
                new View.OnClickListener()
                {
                    public void onClick(View view)
                    {
                     pass = mEdit.getText().toString();
                     Log.e("pass",pass);
                     if (pass.length() <1){
                         passnull();
                     }else{
                         passwarn();
                     }

                    }
                });
    }
    public void passwarn(){
        new NordanAlertDialog.Builder(this)
                .setAnimation(Animation.POP)
                .isCancellable(false)
                .setTitle(getResources().getString(R.string.warn1))
                .setMessage(getResources().getString(R.string.decrypter)+pass)
                .setPositiveBtnText("OK")
                .setNegativeBtnText(getResources().getString(R.string.cancel))
                .setIcon(R.drawable.warn,false)
                .onPositiveClicked(() -> {Intent intent = new Intent(Decrypter.this, Backgrounddecrypt.class);
                    intent.putExtra("pass",pass);
                    startService(intent);
                    finish();})
                .build().show();
    }
    public void passnull(){
        new NordanAlertDialog.Builder(this)
                .setAnimation(Animation.POP)
                .isCancellable(false)
                .setTitle(getResources().getString(R.string.warn1))
                .setMessage(getResources().getString(R.string.passnull))
                .setPositiveBtnText("OK")
                .setIcon(R.drawable.error,false)
                .onPositiveClicked(() -> {})

                .build().show();
    }

}