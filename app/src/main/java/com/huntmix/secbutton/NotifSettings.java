package com.huntmix.secbutton;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.abdeveloper.library.MultiSelectDialog;
import com.abdeveloper.library.MultiSelectModel;

import java.util.ArrayList;
import java.util.List;

import ir.androidexception.andexalertdialog.AndExAlertDialog;
import ir.androidexception.andexalertdialog.AndExAlertDialogListener;
import ir.androidexception.andexalertdialog.InputType;

public class NotifSettings extends AppCompatActivity {
public TinyDB tinydb;
    public List<Integer> preselect;


    public  MultiSelectDialog multiSelectDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notif_settings);
        tinydb = new TinyDB(this);
        if(!tinydb.getBoolean("notif1")){grantdialog();tinydb.putBoolean("notif1",true);}
    }
    public void grantperm(){
        startActivity(new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS"));
    }
    public void grantbutton(View view){
        grantperm();
    }
    public void grantdialog(){
        new AndExAlertDialog.Builder(this)
                .setTitle(getResources().getString(R.string.settings))
                .setMessage(getResources().getString(R.string.key))
                .setPositiveBtnText("OK")
                .setImage(R.drawable.notification,20)
                .setCancelableOnTouchOutside(true)
                .OnPositiveClicked(new AndExAlertDialogListener() {
                    @Override
                    public void OnClick(String input) {
                        grantperm();
                    }
                })
                .build();
    }
    public void setupkey(View view){
        new AndExAlertDialog.Builder(this)
                .setTitle(getResources().getString(R.string.settings))
                .setMessage(getResources().getString(R.string.key))
                .setPositiveBtnText("Set")
                .setImage(R.drawable.notification,20)
                .setEditText(true,false, "Keyword", InputType.TEXT_SINGLE_LINE)
                .setCancelableOnTouchOutside(true)
                .OnPositiveClicked(new AndExAlertDialogListener() {
                    @Override
                    public void OnClick(String input) {
                        Toast.makeText(NotifSettings.this, "Setted: " + input, Toast.LENGTH_SHORT).show();
                        tinydb.putString("keynotif",input);
                    }
                })
                .build();
    }
    public void selectnotifapps(View view){
        Intent intent = new Intent(NotifSettings.this, SelectNotif.class);
        startActivity(intent);
    }
    public void setactionsnotif(View view){
        preselect = tinydb.getListInt("notifpre");
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
                        tinydb.putListString("notifactions",selectedNames);
                        tinydb.putListInt("notifpre",selectedIds);
                    }
                    @Override
                    public void onCancel() {
                    }
                });
        multiSelectDialog.show(getSupportFragmentManager(), "multiSelectDialog");
    }
}