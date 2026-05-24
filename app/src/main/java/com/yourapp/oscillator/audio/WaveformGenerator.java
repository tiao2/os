package com.yourapp.oscillator.audio;

public class WaveformGenerator {
    private static final int SAMPLE_RATE = 44100;
    private double phase = 0.0;
    private double phaseIncrement;
    private int waveformType = 0;
    private double[] harmonicAmps = new double[20];

    public WaveformGenerator() {
        harmonicAmps[0] = 1.0;
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
            if (waveformType == 0) {
                sample = Math.sin(phase);
            } else if (waveformType == 1) {
                sample = Math.sin(phase) >= 0 ? 1.0 : -1.0;
            } else if (waveformType == 2) {
                sample = 2.0 * Math.abs(phase / Math.PI - 2.0 * Math.floor(phase / Math.PI + 0.5)) - 1.0;
            } else if (waveformType == 3) {
                sample = (phase / Math.PI) - 1.0;
            } else if (waveformType == 4) {
                for (int n = 0; n < harmonicAmps.length; n++) {
                    if (harmonicAmps[n] != 0.0) {
                        sample += harmonicAmps[n] * Math.sin((n + 1) * phase);
                    }
                }
                sample = Math.tanh(sample);
            }
            short shortSample = (short) (sample * amplitude * 32767);
            buffer[i] = shortSample;
            phase += phaseIncrement;
            if (phase >= 2.0 * Math.PI) phase -= 2.0 * Math.PI;
        }
        return buffer;
    }
}
