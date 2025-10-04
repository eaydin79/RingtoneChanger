package com.eaydin79.ringtonechanger;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.documentfile.provider.DocumentFile;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FileListActivity extends Activity {

    public static final int REQUEST_OPEN_DIRECTORY = 7979;
    private int fileType = RingtoneManager.TYPE_RINGTONE;
    private MediaPlayer mediaPlayer;
    private RingtonePreferences ringtonePreferences;
    private Uri directoryUri;
    private TextView txtPermission;
    private TextView txtPermissionInfo;
    private TextView txtDirectory;
    private RadioGroup radioGroup;
    private MenuItem menuItemApply;
    private Uri selectedAudioUri;

    private void setRingtone() {
        try {
            RingtoneManager.setActualDefaultRingtoneUri(this, fileType, selectedAudioUri);
            Toast.makeText(this, R.string.msg_success, Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Log.e("FileListActivity", "setRingtone: " + e);
            new AlertDialog.Builder(this)
                    .setTitle(R.string.exception_msg)
                    .setMessage(e.getMessage())
                    .setPositiveButton(R.string.btn_ok, null)
                    .create().show();
        }
    }

    private void playSound() {
        new Thread(() -> {
            if (mediaPlayer != null) {
                mediaPlayer.release();
            }
            mediaPlayer = MediaPlayer.create(getApplicationContext(), selectedAudioUri);
            mediaPlayer.setOnCompletionListener(mp -> {
                mp.reset();
                mp.release();
            });
            mediaPlayer.start();
        }).start();
    }

    private void openDirectoryPicker(View view) {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
        intent.addFlags(
                Intent.FLAG_GRANT_READ_URI_PERMISSION |
                        Intent.FLAG_GRANT_WRITE_URI_PERMISSION |
                        Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION
        );
        startActivityForResult(intent, REQUEST_OPEN_DIRECTORY);
    }

    private void radioButtonsClick(Uri uri) {
        selectedAudioUri = uri;
        playSound();
        if (hasPermission()) {
            menuItemApply.setEnabled(true);
            menuItemApply.setIcon(R.drawable.ic_action_apply_enabled);
        } else {
            menuItemApply.setEnabled(false);
            menuItemApply.setIcon(R.drawable.ic_action_apply_disabled);
        }
    }

    private void loadFiles() {
        radioGroup.removeAllViews();
        DocumentFile fileListDir = DocumentFile.fromTreeUri(this, directoryUri);
        if (fileListDir == null || !fileListDir.isDirectory()) {
            Log.i("FileListActivity", "loadFiles: " + "directory is null");
            return;
        }
        List<DocumentFile> documentFileList = new ArrayList<>(Arrays.asList(fileListDir.listFiles()));
        for (DocumentFile documentFile: documentFileList) {
            if (!documentFile.isFile() || documentFile.getType() == null || !documentFile.getType().startsWith("audio/")) continue;
            AudioFile audioFile = new AudioFile(this, documentFile.getName(), documentFile.getUri());
            audioFile.setAudioFileClickListener(this::radioButtonsClick);
            radioGroup.addView(audioFile.getRadioButton());
        }
    }

    private void loadList() {
        try {
            directoryUri = ringtonePreferences.getDirUri(fileType);
            loadFiles();
        } catch (NullPointerException e) {
            Log.i("FileListActivity", "loadList: " + "Directory not selected");
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK || requestCode != REQUEST_OPEN_DIRECTORY || data == null) return;
        directoryUri = data.getData();
        if (directoryUri == null) {
            Log.i("FileListActivity.onActivityResult", "uri is null");
            return;
        }
        if ((data.getFlags() & Intent.FLAG_GRANT_READ_URI_PERMISSION) != Intent.FLAG_GRANT_READ_URI_PERMISSION) {
            Log.i("FileListActivity.onActivityResult", "permission not granted = FLAG_GRANT_READ_URI_PERMISSION");
            return;
        }
        try {
            getContentResolver().releasePersistableUriPermission(ringtonePreferences.getDirUri(fileType), Intent.FLAG_GRANT_READ_URI_PERMISSION);
        } catch (Exception e) {
            Log.e("FileListActivity.onActivityResult", e.toString());
        }
        getContentResolver().takePersistableUriPermission(directoryUri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
        ringtonePreferences.setDirUri(fileType, directoryUri);
        setDirectoryLayout();
    }

    private void openPermissionSettings(View view) {
        Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
        intent.setData(Uri.parse("package:com.eaydin79.ringtonechanger"));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    private boolean hasPermission() {
        return Settings.System.canWrite(this);
    }

    private void setPermissionLayout() {
        if (hasPermission()) {
            txtPermission.setTextColor(getColor(R.color.green));
            txtPermission.setText(R.string.txt_permission_granted);
            txtPermissionInfo.setText(R.string.txt_permission_info_granted);
        } else {
            txtPermission.setTextColor(getColor(R.color.red));
            txtPermission.setText(R.string.txt_permission_required);
            txtPermissionInfo.setText(R.string.txt_permission_info_required);
        }
    }

    private void setDirectoryLayout() {
        try {
            directoryUri = ringtonePreferences.getDirUri(fileType);
        } catch (NullPointerException e) {
            txtDirectory.setText(R.string.txt_not_selected);
            return;
        }
        try {
            txtDirectory.setText(DocumentsContract.getTreeDocumentId(directoryUri).split(":")[1]);
        } catch (Exception e) {
            txtDirectory.setText(directoryUri.toString());
            Log.e("FileListActivity.setDirectoryLayout", e.toString());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        menuItemApply = menu.findItem(R.id.action_apply);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_apply) setRingtone();
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mediaPlayer != null) mediaPlayer.release();
        menuItemApply.setEnabled(false);
        menuItemApply.setIcon(R.drawable.ic_action_apply_disabled);
    }

    @Override
    protected void onResume() {
        super.onResume();
        setPermissionLayout();
        setDirectoryLayout();
        loadList(); //TO-DO loadFiles on background thread to avoid UI freeze
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_list);
        Intent intent = getIntent();
        fileType = intent.getIntExtra("fileType", 0);
        switch (fileType) {
            case RingtoneManager.TYPE_RINGTONE: setTitle(R.string.ringtones); break;
            case RingtoneManager.TYPE_NOTIFICATION: setTitle(R.string.notifications); break;
            case RingtoneManager.TYPE_ALARM: setTitle(R.string.alarms); break;
            default: Log.e("FileListActivity", "onCreate: " + "fileType is wrong"); finish(); return;
        }
        ringtonePreferences = new RingtonePreferences(this);
        txtDirectory = findViewById(R.id.txt_directory);
        txtPermission = findViewById(R.id.txt_permission);
        txtPermissionInfo = findViewById(R.id.txt_permission_info);
        radioGroup = findViewById(R.id.radio_group);
        findViewById(R.id.layout_directory).setOnClickListener(this::openDirectoryPicker);
        findViewById(R.id.layout_permission).setOnClickListener(this::openPermissionSettings);
    }
}