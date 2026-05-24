package com.yourapp.oscillator;

import android.os.Bundle;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.yourapp.oscillator.audio.AudioEngine;
import com.yourapp.oscillator.ui.CustomWaveformDialog;

public class MainActivity extends AppCompatActivity {
    private AudioEngine audioEngine;
    private TextView tvFrequency;
    private SeekBar sbFrequency, sbVolume;
    private Button btnPlayStop;
    private boolean isPlaying = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        audioEngine = new AudioEngine();

        tvFrequency = findViewById(R.id.tv_frequency_value);
        sbFrequency = findViewById(R.id.seekbar_frequency);
        sbVolume = findViewById(R.id.seekbar_volume);
        btnPlayStop = findViewById(R.id.btn_play_stop);

        sbFrequency.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    tvFrequency.setText(progress + " Hz");
                    audioEngine.setFrequency(progress);
                }
            }
            @Override public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        sbVolume.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    audioEngine.setAmplitude(progress / 100.0);
                }
            }
            @Override public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        findViewById(R.id.btn_sine).setOnClickListener(v -> setWaveform(0));
        findViewById(R.id.btn_square).setOnClickListener(v -> setWaveform(1));
        findViewById(R.id.btn_triangle).setOnClickListener(v -> setWaveform(2));
        findViewById(R.id.btn_sawtooth).setOnClickListener(v -> setWaveform(3));

        findViewById(R.id.btn_custom_waveform).setOnClickListener(v -> showCustomWaveformDialog());

        btnPlayStop.setOnClickListener(v -> {
            if (isPlaying) {
                audioEngine.stop();
                btnPlayStop.setText(R.string.start);
                isPlaying = false;
            } else {
                audioEngine.start();
                btnPlayStop.setText(R.string.stop);
                isPlaying = true;
            }
        });
    }

    private void setWaveform(int type) {
        audioEngine.setWaveformType(type);
        String name = "";
        switch (type) {
            case 0: name = "正弦波"; break;
            case 1: name = "方波"; break;
            case 2: name = "三角波"; break;
            case 3: name = "锯齿波"; break;
        }
        Toast.makeText(this, name, Toast.LENGTH_SHORT).show();
    }

    private void showCustomWaveformDialog() {
        CustomWaveformDialog dialog = new CustomWaveformDialog();
        dialog.setAudioEngine(audioEngine);
        dialog.setOnWaveformSaveListener((name, fundamentalAmp, harmonicAmps) -> {
            audioEngine.setCustomHarmonics(harmonicAmps);
            Toast.makeText(this, "已应用自定义音色：" + name, Toast.LENGTH_SHORT).show();
        });
        dialog.show(getSupportFragmentManager(), "custom_waveform");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (isPlaying) {
            audioEngine.stop();
        }
    }
}
