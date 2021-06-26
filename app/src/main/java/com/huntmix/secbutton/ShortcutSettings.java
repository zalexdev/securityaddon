package com.huntmix.secbutton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.pm.ShortcutInfoCompat;
import androidx.core.content.pm.ShortcutManagerCompat;
import androidx.core.graphics.drawable.IconCompat;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.view.View;
import android.widget.Toast;

import com.abdeveloper.library.MultiSelectDialog;
import com.abdeveloper.library.MultiSelectModel;
import com.github.angads25.toggle.interfaces.OnToggledListener;
import com.github.angads25.toggle.model.ToggleableView;
import com.github.angads25.toggle.widget.LabeledSwitch;
import com.github.isabsent.filepicker.SimpleFilePickerDialog;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import ir.androidexception.andexalertdialog.AndExAlertDialog;
import ir.androidexception.andexalertdialog.AndExAlertDialogListener;
import ir.androidexception.andexalertdialog.InputType;

import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.os.Build.VERSION.SDK_INT;
import static com.github.isabsent.filepicker.SimpleFilePickerDialog.CompositeMode.FILE_ONLY_DIRECT_CHOICE_IMMEDIATE;

public class ShortcutSettings extends AppCompatActivity implements SimpleFilePickerDialog.InteractionListenerString{
    private static final String PICK_DIALOG = "PICK_DIALOG";
    public TinyDB tinydb;
    public List<Integer> preselect;
    public  MultiSelectDialog multiSelectDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shortcut_settings);
        tinydb = new TinyDB(this);


        LabeledSwitch apppkg = findViewById(R.id.apppkg);
        if (tinydb.getString("apppkg").length() >3){apppkg.setOn(true);}
        if(checkSelfPermission(WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){tinydb.putBoolean("perm1",true);}
        permnotif1();
        apppkg.setOnToggledListener(new OnToggledListener() {
            @Override
            public void onSwitched(ToggleableView toggleableView, boolean isOn) {
                if (isOn){
                setapppkg();}

            }});
        final String rootPath = Environment.getExternalStorageDirectory().getAbsolutePath();
        findViewById(R.id.seticon).setOnClickListener(view -> {
            showListItemDialog("Select png, jpg, ico file", rootPath, FILE_ONLY_DIRECT_CHOICE_IMMEDIATE, PICK_DIALOG);
        });
    }
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
    private void WritePathes(List<String> selectedPaths){
        if (selectedPaths != null && !selectedPaths.isEmpty()){
            tinydb.putListString("icon", (ArrayList<String>) selectedPaths);
        }

    }
    public void checkpermission(){
        if(SDK_INT>= Build.VERSION_CODES.M){

            if(checkSelfPermission(WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
                if(shouldShowRequestPermissionRationale(WRITE_EXTERNAL_STORAGE)){
                    ActivityCompat.requestPermissions(
                            ShortcutSettings.this,
                            new String[]{WRITE_EXTERNAL_STORAGE},
                            123
                    );

                }else {
                    // Request permission
                    ActivityCompat.requestPermissions(
                            ShortcutSettings.this,
                            new String[]{WRITE_EXTERNAL_STORAGE},
                            123
                    );
                }
            }else{
                tinydb.putBoolean("perm1",true);
            }
        }

        if (SDK_INT >= Build.VERSION_CODES.R) {
            try {
                Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                intent.addCategory("android.intent.category.DEFAULT");
                intent.setData(Uri.parse(String.format("package:%s",getApplicationContext().getPackageName())));
                startActivityForResult(intent, 2296);
            } catch (Exception e) {
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                startActivityForResult(intent, 2296);
            }
        } else {
            //below android 11
            ActivityCompat.requestPermissions(ShortcutSettings.this, new String[]{WRITE_EXTERNAL_STORAGE}, 123);
        }

    }
    public void permnotif1(){
        if (!tinydb.getBoolean("perm1")){
            new AndExAlertDialog.Builder(this)
                    .setTitle(getResources().getString(R.string.perm))
                    .setImage(R.drawable.files,20)
                    .setMessage(getResources().getString(R.string.permask1))
                    .setPositiveBtnText(getResources().getString(R.string.grant))
                    .setCancelableOnTouchOutside(false)
                    .OnPositiveClicked(new AndExAlertDialogListener() {
                        @Override
                        public void OnClick(String input) {
                            checkpermission();
                        }
                    })
                    .build();}
    }
    public void setlabel(View view){
        new AndExAlertDialog.Builder(this)
                .setTitle(getResources().getString(R.string.warn1))
                .setMessage("Set label")
                .setPositiveBtnText("Set")
                .setImage(R.drawable.smsmain,20)
                .setEditText(true,false, "Label", InputType.TEXT_SINGLE_LINE)
                .setCancelableOnTouchOutside(true)
                .OnPositiveClicked(new AndExAlertDialogListener() {
                    @Override
                    public void OnClick(String input) {
                        Toast.makeText(ShortcutSettings.this, "Setted: " + input, Toast.LENGTH_SHORT).show();
                        tinydb.putString("label",input);
                        int pkg = tinydb.getString("apppkg").length();
                        LabeledSwitch apppkg = findViewById(R.id.apppkg);
                        if (input.length() >3){apppkg.setOn(true);}else{apppkg.setOn(false);}
                    }
                })
                .build();
    }
    public void setapppkg(){
        new AndExAlertDialog.Builder(this)
                .setTitle(getResources().getString(R.string.warn1))
                .setMessage("Set app pkg")
                .setPositiveBtnText("Set")
                .setImage(R.drawable.warning,20)
                .setEditText(true,false, "App package", InputType.TEXT_SINGLE_LINE)
                .setCancelableOnTouchOutside(true)
                .OnPositiveClicked(new AndExAlertDialogListener() {
                    @Override
                    public void OnClick(String input) {
                        Toast.makeText(ShortcutSettings.this, "Setted: " + input, Toast.LENGTH_SHORT).show();
                        tinydb.putString("apppkg",input);
                    }
                })
                .build();
    }
    public void setactionsshortcut(View view){
        preselect = tinydb.getListInt("shortpre");
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
                        tinydb.putListString("shortactions",selectedNames);
                        tinydb.putListInt("shortcutpre",selectedIds);
                    }
                    @Override
                    public void onCancel() {
                    }
                });
        multiSelectDialog.show(getSupportFragmentManager(), "multiSelectDialog");
    }
    public void addcustomshrt(View view){
        String iconpath = tinydb.getString("icon");
        String nameoftag = tinydb.getString("label");
        File check = new File(iconpath);
        if (check.exists() && !check.isDirectory()){

            Bitmap bitmap = BitmapFactory.decodeFile(iconpath);
            if (ShortcutManagerCompat.isRequestPinShortcutSupported(ShortcutSettings.this))
            {
                ShortcutInfoCompat shortcutInfo = new ShortcutInfoCompat.Builder(ShortcutSettings.this, "#1")
                        .setIntent(new Intent(getApplicationContext(), ShortcutLaunch.class).setAction(Intent.ACTION_MAIN))
                        .setShortLabel(nameoftag)
                        .setIcon(IconCompat.createWithBitmap(bitmap))
                        .build();
                ShortcutManagerCompat.requestPinShortcut(ShortcutSettings.this, shortcutInfo, null);
            }
            tinydb.putBoolean("short",true);
        }else{
            new AndExAlertDialog.Builder(this)
                    .setTitle(getResources().getString(R.string.error))
                    .setMessage(getResources().getString(R.string.short1))
                    .setPositiveBtnText("Ok")
                    .setCancelableOnTouchOutside(false)
                    .OnPositiveClicked(new AndExAlertDialogListener() {
                        @Override
                        public void OnClick(String input) {
                            final String rootPath = Environment.getExternalStorageDirectory().getAbsolutePath();
                            showListItemDialog("Select png, jpg, ico file", rootPath, FILE_ONLY_DIRECT_CHOICE_IMMEDIATE, PICK_DIALOG);
                        }
                    })
                    .build();

        }}

}