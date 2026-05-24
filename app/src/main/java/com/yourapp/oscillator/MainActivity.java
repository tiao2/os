package com.yourapp.oscillator;

import android.os.Bundle;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.yourapp.oscillator.audio.AudioEngine;

public class MainActivity extends AppCompatActivity {
    private AudioEngine audioEngine;
    private TextView tvFrequency;
    private Button btnPlayStop;
    private boolean isPlaying = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        audioEngine = new AudioEngine();

        tvFrequency = findViewById(R.id.tv_frequency_value);
        SeekBar sbFrequency = findViewById(R.id.seekbar_frequency);
        SeekBar sbVolume = findViewById(R.id.seekbar_volume);
        btnPlayStop = findViewById(R.id.btn_play_stop);

        sbFrequency.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) { tvFrequency.setText(progress + " Hz"); audioEngine.setFrequency(progress); }
            }
            @Override public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override public void onStopTrackingTouch(SeekBar seekBar) {}
        });
        sbVolume.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) audioEngine.setAmplitude(progress / 100.0);
            }
            @Override public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override public void onStopTrackingTouch(SeekBar seekBar) {}
        });
        findViewById(R.id.btn_sine).setOnClickListener(v -> { audioEngine.setWaveformType(0); Toast.makeText(this, "正弦波", Toast.LENGTH_SHORT).show(); });
        findViewById(R.id.btn_square).setOnClickListener(v -> { audioEngine.setWaveformType(1); Toast.makeText(this, "方波", Toast.LENGTH_SHORT).show(); });
        findViewById(R.id.btn_triangle).setOnClickListener(v -> { audioEngine.setWaveformType(2); Toast.makeText(this, "三角波", Toast.LENGTH_SHORT).show(); });
        findViewById(R.id.btn_sawtooth).setOnClickListener(v -> { audioEngine.setWaveformType(3); Toast.makeText(this, "锯齿波", Toast.LENGTH_SHORT).show(); });
        findViewById(R.id.btn_custom_waveform).setOnClickListener(v -> Toast.makeText(this, "自定义波形待实现", Toast.LENGTH_SHORT).show());
        btnPlayStop.setOnClickListener(v -> {
            if (isPlaying) { audioEngine.stop(); btnPlayStop.setText(R.string.start); isPlaying = false; }
            else { audioEngine.start(); btnPlayStop.setText(R.string.stop); isPlaying = true; }
        });
    }
    @Override protected void onDestroy() { if (isPlaying) audioEngine.stop(); super.onDestroy(); }
}
