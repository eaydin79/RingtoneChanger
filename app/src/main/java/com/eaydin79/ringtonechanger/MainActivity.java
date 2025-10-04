package com.eaydin79.ringtonechanger;

import android.app.Activity;
import android.content.Intent;
import android.media.RingtoneManager;
import android.os.Bundle;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.layout_ringtone).setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, FileListActivity.class);
            intent.putExtra("fileType", RingtoneManager.TYPE_RINGTONE);
            startActivity(intent);
        });
        findViewById(R.id.layout_notification).setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, FileListActivity.class);
            intent.putExtra("fileType", RingtoneManager.TYPE_NOTIFICATION);
            startActivity(intent);
        });
        findViewById(R.id.layout_alarm).setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, FileListActivity.class);
            intent.putExtra("fileType", RingtoneManager.TYPE_ALARM);
            startActivity(intent);
        });
    }

}