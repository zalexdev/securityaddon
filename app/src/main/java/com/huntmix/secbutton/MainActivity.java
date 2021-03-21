package com.huntmix.secbutton;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.pm.ShortcutInfoCompat;
import androidx.core.content.pm.ShortcutManagerCompat;
import androidx.core.graphics.drawable.IconCompat;

import android.Manifest;
import android.annotation.SuppressLint;

import android.content.Intent;
import android.content.pm.PackageManager;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.view.View;
import android.widget.Toast;


import com.github.isabsent.filepicker.SimpleFilePickerDialog;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.nordan.dialog.Animation;
import com.nordan.dialog.NordanAlertDialog;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


import com.wafflecopter.multicontactpicker.ContactResult;
import com.wafflecopter.multicontactpicker.LimitColumn;
import com.wafflecopter.multicontactpicker.MultiContactPicker;
import com.wafflecopter.multicontactpicker.RxContacts.PhoneNumber;

import co.mobiwise.materialintro.animation.MaterialIntroListener;
import co.mobiwise.materialintro.shape.Focus;
import co.mobiwise.materialintro.shape.FocusGravity;

import co.mobiwise.materialintro.shape.ShapeType;
import co.mobiwise.materialintro.view.MaterialIntroView;
import static com.github.isabsent.filepicker.SimpleFilePickerDialog.CompositeMode.FILE_AND_FOLDER_MULTI_CHOICE;
import static com.github.isabsent.filepicker.SimpleFilePickerDialog.CompositeMode.FOLDER_ONLY_MULTI_CHOICE;

public class MainActivity extends AppCompatActivity implements SimpleFilePickerDialog.InteractionListenerString{
public TinyDB tinydb;
public String nameoftag;
public String iconpath;
public Boolean crypted;
public Boolean firstrun;
public DeviceAdminManager dam;
public MaterialButton lock;
public MaterialButton settings;
public MaterialButton starter;
public MaterialButton shrt;
public MaterialButton crypto;
public MaterialButton sms;
public MaterialCardView appcard;
public ArrayList<String> nums = new ArrayList<>();
private static final String PICK_DIALOG = "PICK_DIALOG";
    private static final int CONTACT_PICKER_REQUEST = 991;

    private ArrayList<ContactResult> results = new ArrayList<>();
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tinydb = new TinyDB(this);
        dam = new DeviceAdminManager(this);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
         lock = (MaterialButton) findViewById(R.id.lock);
         settings = (MaterialButton) findViewById(R.id.settings);
         starter = (MaterialButton) findViewById(R.id.starter);
         crypto = (MaterialButton) findViewById(R.id.crypto);
         shrt = (MaterialButton) findViewById(R.id.shrt);
         sms = (MaterialButton) findViewById(R.id.sms);
         appcard = (MaterialCardView) findViewById(R.id.appcard);

        Intent pushIntent = new Intent(this, Background.class);
        this.startForegroundService(pushIntent);
        firstrun = tinydb.getBoolean("first");
        if (firstrun == Boolean.FALSE){
            permasker();
            tinydb.putBoolean("first",true);
        }if(firstrun==Boolean.TRUE){

            checkPermission4();
            checkPermission3();
            checkPermission2();
            checkpermission();

        }

        crypted = tinydb.getBoolean("cryptedf");
        if (crypted ==Boolean.TRUE){
            Intent intent = new Intent(MainActivity.this, Decrypter.class);
            startActivity(intent);
        }

// ======[Loading all...]======
        counter();
        final String rootPath = Environment.getExternalStorageDirectory().getAbsolutePath();
        findViewById(R.id.ff).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showListItemDialog("Select folders or files", rootPath, FILE_AND_FOLDER_MULTI_CHOICE, PICK_DIALOG);
                tinydb.putInt("selection",0);

            }
        });
        findViewById(R.id.crypt).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (tinydb.getString("pass").length()>1 && !tinydb.getString("pass").equals("") && tinydb.getString("pass") != null){
                    generator();
                    showListItemDialog("Select folder(s)", rootPath, FOLDER_ONLY_MULTI_CHOICE, PICK_DIALOG);
                    tinydb.putInt("selection",1);}
                else{
                    passwarn();
                }
            }
        });
    }
    @Override
    public void onResume(){
        super.onResume();
        counter();

        boolean root = tinydb.getBoolean("rootmode");
        if (root == Boolean.TRUE){
            try {
                Runtime.getRuntime().exec("su");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
    // ======[Count selected apps, folders and files]======
    @SuppressLint("SetTextI18n")
    public void counter(){
        final MaterialButton app = findViewById(R.id.apps);
        int count1 = tinydb.getListString("list").size();
        app.setText(getResources().getString(R.string.select)+" ("+String.valueOf(count1)+")");
        final MaterialButton folder = findViewById(R.id.ff);
        int count2 = tinydb.getListString("list2").size();
        folder.setText(getResources().getString(R.string.select)+" ("+String.valueOf(count2)+")");
        final MaterialButton crypt = findViewById(R.id.crypt);
        int count3 = tinydb.getListString("list3").size();
        crypt.setText(getResources().getString(R.string.select)+" ("+String.valueOf(count3)+")");
    }
    // ======[Launch Select apps activity]======
    public void selectapps(View view){
        MaterialButton Button = (MaterialButton) findViewById(R.id.apps);
        Button.setText(getResources().getString(R.string.load));
        Intent intent = new Intent(MainActivity.this, SelectApps.class);
        startActivity(intent);
    }
    // ======[Open pick folders && Files dialog for delete]======
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
    //saving numbers to preferences
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == CONTACT_PICKER_REQUEST){
            if(resultCode == RESULT_OK) {
                results.addAll(MultiContactPicker.obtainResult(data));;
                if(results.size() > 0) {
                    for (int i = 0; i<results.size();i++){
                        List<PhoneNumber> num = results.get(i).getPhoneNumbers();
                        for (int i2 = 0;i2<num.size();i2++){
                            nums.add(num.get(i2).getNumber());
                        tinydb.putListString("nums",nums);
                    }}
                }
            } else if(resultCode == RESULT_CANCELED){
                toaster("No contacts selected!");
            }
        }
    }
    //write pathes todelete(1) and tocrypt(2) in preferences
    private void WritePathes(List<String> selectedPaths){
        if (selectedPaths != null && !selectedPaths.isEmpty()){
            int choice = tinydb.getInt("selection");
            if (choice == 0){
                tinydb.putListString("list2", (ArrayList<String>) selectedPaths);
                counter();
            }
            if (choice == 1){
                tinydb.putListString("list3", (ArrayList<String>) selectedPaths);
                counter();
            }

        }
    }
    // WARN to not seted password
    public void passwarn(){

        new NordanAlertDialog.Builder(this)
                .setAnimation(Animation.POP)
                .isCancellable(false)
                .setTitle(getResources().getString(R.string.pass))
                .setMessage(getResources().getString(R.string.passwarn))
                .setPositiveBtnText("OK")
                .setNegativeBtnText(getResources().getString(R.string.cancel))
                // .setIcon(R.drawable.error,false)
                .onPositiveClicked(() -> {Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                    startActivity(intent);})
                .onNegativeClicked(() -> {/* Do something here */})
                .build().show();
    }
    //fill password to 16 symbols
    public void generator(){
        String result16 = tinydb.getString("pass");
        if (result16.length() < 16){
            while (result16.length() == 16){
                result16=result16+"1";
            }
            tinydb.putString("pass",result16);


        }
    }
    // ======[Permissions]======
    public void checkpermission(){
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){

            if(checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
                if(shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)){
                    ActivityCompat.requestPermissions(
                            MainActivity.this,
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            123
                    );

                }else {
                    // Request permission
                    ActivityCompat.requestPermissions(
                            MainActivity.this,
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            123
                    );
                }
            }
        }
    }
    public void checkPermission2(){
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){

            if(checkSelfPermission(Manifest.permission.RECEIVE_SMS)!= PackageManager.PERMISSION_GRANTED){
                if(shouldShowRequestPermissionRationale(Manifest.permission.RECEIVE_SMS)){
                    ActivityCompat.requestPermissions(
                            MainActivity.this,
                            new String[]{Manifest.permission.RECEIVE_SMS},
                            123
                    );

                }else {
                    // Request permission
                    ActivityCompat.requestPermissions(
                            MainActivity.this,
                            new String[]{Manifest.permission.RECEIVE_SMS},
                            123
                    );
                }
            }
        }

    }
    public void checkPermission3(){

        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){
            if(checkSelfPermission(Manifest.permission.READ_CONTACTS)!= PackageManager.PERMISSION_GRANTED){
                if(shouldShowRequestPermissionRationale(Manifest.permission.READ_CONTACTS)){
                    ActivityCompat.requestPermissions(
                            MainActivity.this,
                            new String[]{Manifest.permission.READ_CONTACTS},
                            1
                    );

                }else {
                    // Request permission
                    ActivityCompat.requestPermissions(
                            MainActivity.this,
                            new String[]{Manifest.permission.READ_CONTACTS},
                            1
                    );
                }
            } // Permission already granted

        }

    }
    public void checkPermission4(){
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){

            if(checkSelfPermission(Manifest.permission.SEND_SMS)!= PackageManager.PERMISSION_GRANTED){
                if(shouldShowRequestPermissionRationale(Manifest.permission.SEND_SMS)){
                    ActivityCompat.requestPermissions(
                            MainActivity.this,
                            new String[]{Manifest.permission.SEND_SMS},
                            123
                    );

                }else {
                    // Request permission
                    ActivityCompat.requestPermissions(
                            MainActivity.this,
                            new String[]{Manifest.permission.SEND_SMS},
                            123
                    );
                }
            }
        }

    }

    // ======[Add shortcut]======
    //default shortcut
    public void addshrt (View view){
        nameoftag = tinydb.getString("nameoftag");
        iconpath = tinydb.getString("iconpath");
        if (nameoftag.length()<3 && iconpath.length()<3){
            if (ShortcutManagerCompat.isRequestPinShortcutSupported(MainActivity.this))
            {
                ShortcutInfoCompat shortcutInfo = new ShortcutInfoCompat.Builder(MainActivity.this, "#1")
                        .setIntent(new Intent(MainActivity.this, Shortcut.class).setAction(Intent.ACTION_MAIN))
                        .setShortLabel("Delete")
                        .setIcon(IconCompat.createWithResource(MainActivity.this, R.drawable.del2))
                        .build();
                ShortcutManagerCompat.requestPinShortcut(MainActivity.this, shortcutInfo, null);
            }
        }else {
            addcustomshrt();
        }
    }
    //user shortcut
    public void addcustomshrt(){
        File check = new File(iconpath);
        if (check.exists() && !check.isDirectory()){

            Bitmap bitmap = BitmapFactory.decodeFile(iconpath);
            if (ShortcutManagerCompat.isRequestPinShortcutSupported(MainActivity.this))
            {
                ShortcutInfoCompat shortcutInfo = new ShortcutInfoCompat.Builder(MainActivity.this, "#1")
                        .setIntent(new Intent(MainActivity.this, Shortcut.class).setAction(Intent.ACTION_MAIN))
                        .setShortLabel(nameoftag)
                      .setIcon(IconCompat.createWithBitmap(bitmap))
                        .build();
                ShortcutManagerCompat.requestPinShortcut(MainActivity.this, shortcutInfo, null);
            }

        }else{

        new NordanAlertDialog.Builder(this)
                .setAnimation(Animation.POP)
                .isCancellable(false)
                .setTitle(getResources().getString(R.string.error))
                .setMessage(getResources().getString(R.string.short1))
                .setPositiveBtnText("OK")
                .setNegativeBtnText(getResources().getString(R.string.cancel))
               // .setIcon(R.drawable.error,false)
                .onPositiveClicked(() -> {Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                    startActivity(intent);})
                .onNegativeClicked(() -> {/* Do something here */})
                .build().show();
    }}
    // ======[Other]======
    //sms settings
    public void dialogsms(){
        new NordanAlertDialog.Builder(this)
                .setAnimation(Animation.POP)
                .isCancellable(false)
                .setTitle(getResources().getString(R.string.sms))
                .setMessage(getResources().getString(R.string.smswarn))
                .setPositiveBtnText("OK")
                .setNegativeBtnText(getResources().getString(R.string.cancel))
              //  .setIcon(R.drawable.warn,false)
                .onPositiveClicked(() -> {Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    Uri uri = Uri.fromParts("package", getPackageName(), null);
                    intent.setData(uri);
                    startActivity(intent);
                    Toast toast2 = Toast.makeText(getApplicationContext(),
                            "Enable autorun!", Toast.LENGTH_SHORT);
                    toast2.show();
                    opennotif();
                })
                .onNegativeClicked(() -> {/* Do something here */})
                .build().show();
    }
    //open page about
    public void openabout(View view){
        Intent intent = new Intent(MainActivity.this, About.class);
        startActivity(intent);


    }
    //select contacts
    public void contact(View view){
        if (tinydb.getString("msg").length()<2){
            checkPermission3();
            checkPermission4();
        new NordanAlertDialog.Builder(this)
                .setAnimation(Animation.POP)
                .isCancellable(true)
                .setTitle(getResources().getString(R.string.warn1))
                .setMessage(getResources().getString(R.string.sendsms2))
                .setPositiveBtnText("OK")
                .setNegativeBtnText(getResources().getString(R.string.cancel))
                // .setIcon(R.drawable.error,false)
                .onPositiveClicked(() -> {Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                    startActivity(intent); })
                .onNegativeClicked(() -> {/* Do something here */})
                .build().show();}
        else{
            contacts();
        }
    }
    //open crypt decrypt page
    public void opencrypt(View view){ Intent intent = new Intent(MainActivity.this, Cryptonic.class);
        startActivity(intent);

    }
    //open notification settings
    public void opennotif(){
        new NordanAlertDialog.Builder(this)
                .setAnimation(Animation.POP)
                .isCancellable(false)
                .setTitle(getResources().getString(R.string.notifsettings))
                .setMessage(getResources().getString(R.string.notif1))
                .setPositiveBtnText("OK")
                .setNegativeBtnText(getResources().getString(R.string.cancel))
                //  .setIcon(R.drawable.warn,false)
                .onPositiveClicked(() -> {Intent settingsIntent = new Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        .putExtra(Settings.EXTRA_APP_PACKAGE, getPackageName());
                    startActivity(settingsIntent);
                    toaster("Disable notifications!");})
                .onNegativeClicked(() -> {})
                .build().show();

    }
    //open settings
    public void opensettings(View view) {
        Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
        startActivity(intent);


    }
    //starting actions
    public void startdelete(View view) {
        new NordanAlertDialog.Builder(this)
                .setAnimation(Animation.POP)
                .isCancellable(true)
                .setTitle(getResources().getString(R.string.warn1))
                .setMessage(getResources().getString(R.string.delete))
                .setPositiveBtnText("OK")
                .setNegativeBtnText(getResources().getString(R.string.cancel))
               // .setIcon(R.drawable.error,false)
                .onPositiveClicked(() -> {Intent intent = new Intent(MainActivity.this, Backgroundstarter.class);
                    startService(intent);
                    finish();})
                .onNegativeClicked(() -> {/* Do something here */})
                .build().show();


    }
    //enable SAA service
    public void enablelock(View view){
        int maxpin = tinydb.getInt("pinwrong");
        boolean pin = tinydb.getBoolean("pin");
        if (!dam.isActiveAdmin()){
        if (pin == Boolean.TRUE){
            if (maxpin>1&&maxpin<6){
                new NordanAlertDialog.Builder(this)
                        .setAnimation(Animation.POP)
                        .isCancellable(true)
                        .setTitle(getResources().getString(R.string.warn1))
                        .setMessage(getResources().getString(R.string.adminwarn))
                        .setPositiveBtnText("OK")
                        .setNegativeBtnText(getResources().getString(R.string.cancel))
                        // .setIcon(R.drawable.error,false)
                        .onPositiveClicked(() -> { startActivityForResult(dam.getStartAdminEnableIntent(), 1);})
                        .onNegativeClicked(() -> {/* Do something here */})
                        .build().show();
            }



            else{
                Toast toast2 = Toast.makeText(getApplicationContext(),
                        "2-5 You select:"+String.valueOf(maxpin), Toast.LENGTH_SHORT);
                toast2.show();
            }
        }





        else{
            new NordanAlertDialog.Builder(this)
                    .setAnimation(Animation.POP)
                    .isCancellable(true)
                    .setTitle(getResources().getString(R.string.warn1))
                    .setMessage(getResources().getString(R.string.enablepin))
                    .setPositiveBtnText("OK")
                    .setNegativeBtnText(getResources().getString(R.string.cancel))
                    // .setIcon(R.drawable.error,false)
                    .onPositiveClicked(() -> {Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                        startActivity(intent);})
                    .onNegativeClicked(() -> {/* Do something here */})
                    .build().show();
        }}else{
            Toast toast2 = Toast.makeText(getApplicationContext(),
                    "Enabled!", Toast.LENGTH_SHORT);
            toast2.show();

        }



    }
    //open contacts
    public void contacts(){
        new MultiContactPicker.Builder(MainActivity.this)
                .theme(R.style.MultiContactPicker_Azure)
                .hideScrollbar(false)
                .showTrack(true)
                .searchIconColor(Color.WHITE)
                .setChoiceMode(MultiContactPicker.CHOICE_MODE_MULTIPLE)
                .handleColor(ContextCompat.getColor(MainActivity.this, R.color.colorPrimary))
                .bubbleColor(ContextCompat.getColor(MainActivity.this, R.color.colorPrimary))
                .bubbleTextColor(Color.WHITE)
                .setTitleText("Select Contacts")
                .setLoadingType(MultiContactPicker.LOAD_SYNC)
                .limitToColumn(LimitColumn.NONE)
                .setActivityAnimations(android.R.anim.fade_in, android.R.anim.fade_out,
                        android.R.anim.fade_in,
                        android.R.anim.fade_out)
                .showPickerForResult(CONTACT_PICKER_REQUEST);
    }
    //making toast
    public void toaster(String status){
        Toast stat = Toast.makeText(getApplicationContext(), status, Toast.LENGTH_SHORT);
        stat.show();
    }
//tutorial

    public void starttuorial(){
        new MaterialIntroView.Builder(this)
                .enableDotAnimation(false)
                .enableIcon(false)
                .setFocusGravity(FocusGravity.CENTER)
                .setFocusType(Focus.NORMAL)
                .setDelayMillis(500)
                .enableFadeAnimation(true)
                .performClick(false)
                .setInfoText(getResources().getString(R.string.tutor_settings))
                .setTarget(settings)
                .setUsageId("settings")
                .setIdempotent(true)
                .setListener(new MaterialIntroListener() {
                    @Override
                    public void onUserClicked(String materialIntroViewId) {

                        new MaterialIntroView.Builder(MainActivity.this)
                                .enableDotAnimation(false)
                                .enableIcon(false)
                                .setFocusGravity(FocusGravity.CENTER)
                                .setFocusType(Focus.NORMAL)
                                .setDelayMillis(500)
                                .enableFadeAnimation(true)
                                .performClick(false)
                                .setInfoText(getResources().getString(R.string.tutorial_shrt))
                                .setTarget(shrt)
                                .setUsageId("shrt")
                                .setIdempotent(true)
                                .setListener(new MaterialIntroListener() {
                                    @Override
                                    public void onUserClicked(String materialIntroViewId) {

                                        new MaterialIntroView.Builder(MainActivity.this)
                                                .enableDotAnimation(false)
                                                .enableIcon(false)
                                                .setFocusGravity(FocusGravity.CENTER)
                                                .setFocusType(Focus.NORMAL)
                                                .setDelayMillis(500)
                                                .enableFadeAnimation(true)
                                                .performClick(false)
                                                .setInfoText(getResources().getString(R.string.tutorial_lock))
                                                .setTarget(lock)
                                                .setUsageId("lock")
                                                .setIdempotent(true)
                                                .setListener(new MaterialIntroListener() {
                                                    @Override
                                                    public void onUserClicked(String materialIntroViewId) {

                                                        new MaterialIntroView.Builder(MainActivity.this)
                                                                .enableDotAnimation(false)
                                                                .enableIcon(false)
                                                                .setFocusGravity(FocusGravity.CENTER)
                                                                .setFocusType(Focus.NORMAL)
                                                                .setDelayMillis(500)
                                                                .enableFadeAnimation(true)
                                                                .performClick(false)
                                                                .setInfoText(getResources().getString(R.string.tutorial_sms))
                                                                .setTarget(sms)
                                                                .setUsageId("sms")
                                                                .setIdempotent(true)
                                                                .setListener(new MaterialIntroListener() {
                                                                    @Override
                                                                    public void onUserClicked(String materialIntroViewId) {
                                                                        checkPermission4();
                                                                        checkPermission3();
                                                                        checkPermission2();

                                                                        new MaterialIntroView.Builder(MainActivity.this)
                                                                                .enableDotAnimation(false)
                                                                                .enableIcon(false)
                                                                                .setFocusGravity(FocusGravity.CENTER)
                                                                                .setFocusType(Focus.NORMAL)
                                                                                .setDelayMillis(500)
                                                                                .setShape(ShapeType.RECTANGLE)
                                                                                .enableFadeAnimation(true)
                                                                                .performClick(false)
                                                                                .setInfoText(getResources().getString(R.string.tutorial_apps))
                                                                                .setTarget(appcard)
                                                                                .setUsageId("appcard")
                                                                                .setIdempotent(true)
                                                                                .setListener(new MaterialIntroListener() {
                                                                                    @Override
                                                                                    public void onUserClicked(String materialIntroViewId) {
                                                                                        checkpermission();

                                                                                    }
                                                                                })
                                                                                .show();
dialogsms();
                                                                    }
                                                                })
                                                                .show();
                                                    }
                                                })
                                                .show();

                                    }
                                })
                                .show();
                    }
                })
                .show();


    }
//permission asker
public void permasker(){
    new NordanAlertDialog.Builder(this)
            .setAnimation(Animation.POP)
            .isCancellable(true)
            .setTitle(getResources().getString(R.string.perm))
            .setMessage(getResources().getString(R.string.perm2))
            .setPositiveBtnText("OK")
            .setNegativeBtnText(getResources().getString(R.string.cancel))
            // .setIcon(R.drawable.error,false)
            .onPositiveClicked(() -> {starttuorial();checkPermission2();})
            .onNegativeClicked(() -> {System.exit(1);})
            .build().show();
}

}