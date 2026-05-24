package com.yourapp.oscillator.ui;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.yourapp.oscillator.R;
import com.yourapp.oscillator.audio.AudioEngine;
import java.util.ArrayList;
import java.util.List;

public class CustomWaveformDialog extends BottomSheetDialogFragment {
    private static final int MAX_HARMONIC = 20;
    private EditText etName;
    private SeekBar sbFundamental;
    private TextView tvFundamentalValue;
    private LinearLayout harmonicsContainer;
    private List<HarmonicView> harmonicViews = new ArrayList<>();
    private double[] harmonicAmps = new double[MAX_HARMONIC];
    private AudioEngine audioEngine;
    private OnWaveformSaveListener listener;

    public interface OnWaveformSaveListener {
        void onSave(String name, double fundamentalAmp, double[] harmonicAmps);
    }

    public void setAudioEngine(AudioEngine engine) {
        this.audioEngine = engine;
    }

    public void setOnWaveformSaveListener(OnWaveformSaveListener listener) {
        this.listener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_custom_waveform, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        etName = view.findViewById(R.id.et_waveform_name);
        sbFundamental = view.findViewById(R.id.sb_fundamental_amp);
        tvFundamentalValue = view.findViewById(R.id.tv_fundamental_value);
        harmonicsContainer = view.findViewById(R.id.ll_harmonics_container);
        Button btnAdd = view.findViewById(R.id.btn_add_harmonic);
        Button btnCancel = view.findViewById(R.id.btn_cancel);
        Button btnSave = view.findViewById(R.id.btn_save);

        // 初始化数组（默认正弦波）
        for (int i = 0; i < MAX_HARMONIC; i++) harmonicAmps[i] = 0.0;
        harmonicAmps[0] = 1.0; // 基波

        // 添加基波谐波条目（1次谐波，不可删除）
        addHarmonicView(1, 100, false);
        sbFundamental.setProgress(80);
        tvFundamentalValue.setText("80%");
        sbFundamental.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                tvFundamentalValue.setText(progress + "%");
                if (audioEngine != null) {
                    double amp = progress / 100.0;
                    // 实时调整整体音量（基波振幅影响所有谐波比例）
                    // 这里简单演示：重设自定义音色时应用
                }
            }
            @Override public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        btnAdd.setOnClickListener(v -> addHarmonic());
        btnCancel.setOnClickListener(v -> dismiss());
        btnSave.setOnClickListener(v -> {
            String name = etName.getText().toString().trim();
            if (name.isEmpty()) name = "未命名";
            double fundamentalAmp = sbFundamental.getProgress() / 100.0;
            if (listener != null) {
                listener.onSave(name, fundamentalAmp, harmonicAmps);
            }
            dismiss();
        });
    }

    private void addHarmonicView(int order, int percent, boolean deletable) {
        View itemView = LayoutInflater.from(getContext()).inflate(R.layout.item_harmonic, harmonicsContainer, false);
        TextView tvOrder = itemView.findViewById(R.id.tv_harmonic_order);
        SeekBar sbAmp = itemView.findViewById(R.id.sb_harmonic_amp);
        TextView tvValue = itemView.findViewById(R.id.tv_harmonic_amp_value);
        ImageButton btnDelete = itemView.findViewById(R.id.btn_delete_harmonic);

        tvOrder.setText(order + "次谐波");
        sbAmp.setProgress(percent);
        tvValue.setText(percent + "%");
        if (!deletable) btnDelete.setVisibility(View.INVISIBLE);
        else btnDelete.setVisibility(View.VISIBLE);

        sbAmp.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                tvValue.setText(progress + "%");
                harmonicAmps[order-1] = progress / 100.0;
                if (audioEngine != null) {
                    audioEngine.setCustomHarmonics(harmonicAmps);
                }
            }
            @Override public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        btnDelete.setOnClickListener(v -> {
            if (order == 1) {
                Toast.makeText(getContext(), "基波不能删除", Toast.LENGTH_SHORT).show();
                return;
            }
            harmonicsContainer.removeView(itemView);
            harmonicAmps[order-1] = 0.0;
            harmonicViews.remove(itemView);
            if (audioEngine != null) {
                audioEngine.setCustomHarmonics(harmonicAmps);
            }
        });

        harmonicsContainer.addView(itemView);
        harmonicViews.add(itemView);
    }

    private void addHarmonic() {
        int maxOrder = 0;
        for (int i = 0; i < MAX_HARMONIC; i++) {
            if (harmonicAmps[i] > 0) maxOrder = i+1;
        }
        int newOrder = maxOrder + 1;
        if (newOrder > MAX_HARMONIC) {
            Toast.makeText(getContext(), "最多支持20次谐波", Toast.LENGTH_SHORT).show();
            return;
        }
        addHarmonicView(newOrder, 0, true);
    }
}
