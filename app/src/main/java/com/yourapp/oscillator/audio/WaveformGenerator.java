package com.yourapp.oscillator.audio;

public class WaveformGenerator {
    private static final int SAMPLE_RATE = 44100;
    private double phase = 0.0;
    private double phaseIncrement;
    private int waveformType = 0; // 0:正弦,1:方波,2:三角波,3:锯齿波,4:自定义
    private double[] harmonicAmps = new double[20]; // 自定义音色用

    public WaveformGenerator() {
        harmonicAmps[0] = 1.0;
        for (int i = 1; i < 20; i++) harmonicAmps[i] = 0.0;
    }

    public void setFrequency(double freq) {
        this.phaseIncrement = 2.0 * Math.PI * freq / SAMPLE_RATE;
    }

    public void setWaveformType(int type) {
        this.waveformType = type;
    }

    public void setCustomHarmonics(double[] amps) {
        System.arraycopy(amps, 0, harmonicAmps, 0, Math.min(amps.length, 20));
    }

    public short[] generateBuffer(int bufferSize, double amplitude) {
        short[] buffer = new short[bufferSize];
        for (int i = 0; i < bufferSize; i++) {
            double sample = 0.0;
            switch (waveformType) {
                case 0: // 正弦
                    sample = Math.sin(phase);
                    break;
                case 1: // 方波
                    sample = Math.sin(phase) >= 0 ? 1.0 : -1.0;
                    break;
                case 2: // 三角波
                    sample = 2.0 * Math.abs(phase / Math.PI - 2.0 * Math.floor(phase / Math.PI + 0.5)) - 1.0;
                    break;
                case 3: // 锯齿波
                    sample = (phase / Math.PI) - 1.0;
                    break;
                case 4: // 自定义（加法合成）
                    for (int n = 0; n < harmonicAmps.length; n++) {
                        if (harmonicAmps[n] != 0.0) {
                            sample += harmonicAmps[n] * Math.sin((n + 1) * phase);
                        }
                    }
                    sample = Math.tanh(sample); // 软限幅
                    break;
                default:
                    sample = 0.0;
            }
            short shortSample = (short) (sample * amplitude * 32767);
            buffer[i] = shortSample;
            phase += phaseIncrement;
            if (phase >= 2.0 * Math.PI) phase -= 2.0 * Math.PI;
        }
        return buffer;
    }
}
