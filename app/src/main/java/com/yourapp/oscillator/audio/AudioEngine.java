package com.yourapp.oscillator.audio;

import android.media.AudioAttributes;
import android.media.AudioFormat;
import android.media.AudioTrack;

public class AudioEngine {
    private static final int SAMPLE_RATE = 44100;
    private AudioTrack audioTrack;
    private WaveformGenerator generator;
    private PlayThread playThread;
    private boolean isPlaying = false;
    private double frequency = 440.0;
    private double amplitude = 0.8;
    private int waveformType = 0;

    public AudioEngine() {
        generator = new WaveformGenerator();
        generator.setFrequency(frequency);
        generator.setWaveformType(waveformType);
        initAudioTrack();
    }

    private void initAudioTrack() {
        int bufferSize = AudioTrack.getMinBufferSize(SAMPLE_RATE, AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT);
        bufferSize *= 2;
        audioTrack = new AudioTrack.Builder()
                .setAudioAttributes(new AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .build())
                .setAudioFormat(new AudioFormat.Builder()
                        .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                        .setSampleRate(SAMPLE_RATE)
                        .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                        .build())
                .setBufferSizeInBytes(bufferSize)
                .setTransferMode(AudioTrack.MODE_STREAM)
                .build();
    }

    public void start() {
        if (isPlaying) return;
        isPlaying = true;
        audioTrack.play();
        playThread = new PlayThread();
        playThread.start();
    }

    public void stop() {
        isPlaying = false;
        if (playThread != null) {
            try { playThread.join(); } catch (InterruptedException e) { e.printStackTrace(); }
        }
        if (audioTrack != null) {
            audioTrack.stop();
            audioTrack.flush();
        }
    }

    public void setFrequency(double freq) {
        this.frequency = freq;
        generator.setFrequency(freq);
    }

    public void setAmplitude(double amp) {
        this.amplitude = amp;
    }

    public void setWaveformType(int type) {
        this.waveformType = type;
        generator.setWaveformType(type);
    }

    public void setCustomHarmonics(double[] amps) {
        generator.setCustomHarmonics(amps);
        generator.setWaveformType(4); // 切换到自定义模式
        this.waveformType = 4;
    }

    private class PlayThread extends Thread {
        @Override
        public void run() {
            android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_URGENT_AUDIO);
            int bufferSize = AudioTrack.getMinBufferSize(SAMPLE_RATE, AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT) * 2;
            while (isPlaying) {
                short[] samples = generator.generateBuffer(bufferSize / 2, amplitude);
                byte[] buffer = new byte[bufferSize];
                for (int i = 0; i < samples.length; i++) {
                    buffer[i*2] = (byte) (samples[i] & 0xff);
                    buffer[i*2+1] = (byte) ((samples[i] >> 8) & 0xff);
                }
                audioTrack.write(buffer, 0, buffer.length);
            }
        }
    }
}
