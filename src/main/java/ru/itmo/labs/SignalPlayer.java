package ru.itmo.labs;

import javax.sound.sampled.*;

public class SignalPlayer {
    private static final int SAMPLE_RATE = 44100;
    private static final int BITS_PER_SAMPLE = 16;
    private static final int CHANNELS = 1;
    private static final boolean SIGNED = true;
    private static final boolean BIG_ENDIAN = false;

    public static void playSignal(double[] signal) throws LineUnavailableException {
        double maxAmplitude = findMaxAbsoluteValue(signal);
        if (maxAmplitude == 0) return;
        double normalizationFactor = 0.8 * (Short.MAX_VALUE / maxAmplitude);

        AudioFormat format = new AudioFormat(
                SAMPLE_RATE,
                BITS_PER_SAMPLE,
                CHANNELS,
                SIGNED,
                BIG_ENDIAN
        );
        SourceDataLine line = AudioSystem.getSourceDataLine(format);
        line.open(format);
        line.start();

        byte[] buffer = new byte[2];
        for (double sample : signal) {
            short amplitude = (short) (sample * normalizationFactor);
            buffer[0] = (byte) (amplitude & 0xFF);
            buffer[1] = (byte) ((amplitude >> 8) & 0xFF);
            line.write(buffer, 0, 2);
        }

        line.drain();
        line.close();
    }

    private static double findMaxAbsoluteValue(double[] signal) {
        double max = 0;
        for (double v : signal) {
            if (Math.abs(v) > max) {
                max = Math.abs(v);
            }
        }
        return max;
    }
}