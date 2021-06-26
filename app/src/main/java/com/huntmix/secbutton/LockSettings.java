package com.huntmix.secbutton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.abdeveloper.library.MultiSelectDialog;
import com.abdeveloper.library.MultiSelectModel;
import com.github.angads25.toggle.interfaces.OnToggledListener;
import com.github.angads25.toggle.model.ToggleableView;
import com.github.angads25.toggle.widget.LabeledSwitch;

import java.util.ArrayList;
import java.util.List;

import ir.androidexception.andexalertdialog.AndExAlertDialog;
import ir.androidexception.andexalertdialog.AndExAlertDialogListener;
import ir.androidexception.andexalertdialog.InputType;

import static android.Manifest.permission.CAMERA;
import static android.os.Build.VERSION.SDK_INT;

public class LockSettings extends AppCompatActivity {
    public TinyDB tinydb;
    public DeviceAdminManager dam;
    public List<Integer> preselect;
    public  MultiSelectDialog multiSelectDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lock_settings);
        tinydb = new TinyDB(this);
        dam = new DeviceAdminManager(this);
        if (dam.isActiveAdmin()){tinydb.putBoolean("dam",true);}else {tinydb.putBoolean("dam",false);}
        if (!dam.isActiveAdmin()){lock(); }
        LabeledSwitch camera = findViewById(R.id.camera);
        camera.setOn(tinydb.getBoolean("camera"));
        camera.setOnToggledListener(new OnToggledListener() {
            @Override
            public void onSwitched(ToggleableView toggleableView, boolean isOn) {
                if (isOn){
                    if(SDK_INT>= Build.VERSION_CODES.M){

                        if(checkSelfPermission(CAMERA)!= PackageManager.PERMISSION_GRANTED){
                            if(shouldShowRequestPermissionRationale(CAMERA)){
                                ActivityCompat.requestPermissions(
                                        LockSettings.this,
                                        new String[]{CAMERA},
                                        123
                                );

                            }else {
                                // Request permission
                                ActivityCompat.requestPermissions(
                                        LockSettings.this,
                                        new String[]{CAMERA},
                                        123
                                );
                            }
                        }else{
                            tinydb.putBoolean("camera",isOn);
                        }

                    }
                }else{
                    tinydb.putBoolean("camera",isOn);
                }


            }
        });
    }
    public void setactionslock(View view){
        preselect = tinydb.getListInt("lockpre");
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
                        tinydb.putListString("lockactions",selectedNames);
                        tinydb.putListInt("lockpre",selectedIds);
                    }
                    @Override
                    public void onCancel() {
                    }
                });
        multiSelectDialog.show(getSupportFragmentManager(), "multiSelectDialog");
    }
    public void lock(){
        int pin = tinydb.getInt("pinwrong");
        if (pin>1&&pin<6){
            if (!dam.isActiveAdmin()){

                new AndExAlertDialog.Builder(this)
                        .setTitle(getResources().getString(R.string.warn1))
                        .setMessage(getResources().getString(R.string.adminwarn))
                        .setPositiveBtnText("Ok")
                        .OnPositiveClicked(new AndExAlertDialogListener() {
                            @Override
                            public void OnClick(String input) {startActivityForResult(dam.getStartAdminEnableIntent(), 1);
                            maxpin();
                            }
                        })
                        .build();


            }else{
                new AndExAlertDialog.Builder(this)
                        .setTitle("SAA Service")
                        .setMessage("Already activated")
                        .setPositiveBtnText("Ok")
                        .setImage(R.drawable.icon,20)
                        .setCancelableOnTouchOutside(true)
                        .OnPositiveClicked(new AndExAlertDialogListener() {
                            @Override
                            public void OnClick(String input) {startActivityForResult(dam.getStartAdminEnableIntent(), 1);
                            }
                        })
                        .build();

            }}else{
            maxpin();
        }



    }
    public void setmaxpin(View view){
        maxpin();
    }
    public void maxpin(){

        new AndExAlertDialog.Builder(this)
                .setTitle(getResources().getString(R.string.settings))
                .setMessage(getResources().getString(R.string.enablepin))
                .setPositiveBtnText("Set")
                .setImage(R.drawable.pass,20)
                .setEditText(true,false, "2-5", InputType.NUMBER)
                .setCancelableOnTouchOutside(true)
                .OnPositiveClicked(new AndExAlertDialogListener() {
                    @Override
                    public void OnClick(String input) {
                        if (Integer.parseInt(input)<6 && Integer.parseInt(input)>1){
                            tinydb.putInt("pinwrong", Integer.parseInt(input));
                            lock();
                        }
                        else{


                        }
                    }
                })
                .build();}
    }
