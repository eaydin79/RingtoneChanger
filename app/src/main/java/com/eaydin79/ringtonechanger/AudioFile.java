package com.eaydin79.ringtonechanger;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.view.View;
import android.widget.RadioButton;

public class AudioFile {

    private final RadioButton radioButton;
    private final Uri uri;
    private AudioFileClickListener audioFileClickListener;

    public AudioFile(Context context, String name, Uri uri) {
        this.uri = uri;
        radioButton = new RadioButton(context);
        radioButton.setOnClickListener(this::radioButtonClick);
        radioButton.setText(name);
        radioButton.setTextSize(20);
        radioButton.setTextColor(Color.DKGRAY);
    }

    public void radioButtonClick(View view) {
        if (audioFileClickListener != null) audioFileClickListener.onClick(uri);
    }

    public RadioButton getRadioButton() {
        return radioButton;
    }

    public void setAudioFileClickListener(AudioFileClickListener audioFileClickListener) {
        this.audioFileClickListener = audioFileClickListener;
    }
}
