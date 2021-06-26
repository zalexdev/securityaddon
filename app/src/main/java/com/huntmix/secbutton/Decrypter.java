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

import ir.androidexception.andexalertdialog.AndExAlertDialog;
import ir.androidexception.andexalertdialog.AndExAlertDialogListener;

public class Decrypter extends AppCompatActivity {
    public String pass;
    public Button mButton;


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.decrypter);


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
                     if (pass.length() == 16){
                         passwarn();
                     }else{
                         passnull();
                     }

                    }
                });
    }
    public void passwarn(){
        new AndExAlertDialog.Builder(this)
                .setTitle(getResources().getString(R.string.warn1))
                .setMessage("Are you sure?"+pass)
                .setPositiveBtnText("Ok")
                .setImage(R.drawable.warning,20)
                .setCancelableOnTouchOutside(false)
                .OnPositiveClicked(new AndExAlertDialogListener() {
                    @Override
                    public void OnClick(String input) {Intent intent = new Intent(Decrypter.this, Backgrounddecrypt.class);
                        intent.putExtra("pass",pass);
                        startService(intent);
                        finish();

                    }
                })
                .build();

    }
    public void passnull(){
        new AndExAlertDialog.Builder(this)
                .setTitle(getResources().getString(R.string.warn1))
                .setMessage("Enter 16 symbol password!")
                .setPositiveBtnText("Ok")
                .setImage(R.drawable.close,20)
                .setCancelableOnTouchOutside(false)
                .build();

    }

}