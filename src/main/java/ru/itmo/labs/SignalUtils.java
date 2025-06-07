package ru.itmo.labs;

import java.io.*;
import java.util.Random;

public class SignalUtils {
    public static double[] generateCleanSignal(int n, double dt, double[] amplitudes, double[] frequencies) {
        double[] signal = new double[n];
        for (int i = 0; i < n; i++) {
            double t = i * dt;
            double value = 0;
            for (int j = 0; j < amplitudes.length; j++) {
                value += amplitudes[j] * Math.sin(2 * Math.PI * frequencies[j] * t);
            }
            signal[i] = value;
        }
        return signal;
    }

    public static double[] addGaussianNoise(double[] signal, double mean, double stdDev) {
        double[] noisy = new double[signal.length];
        Random rand = new Random();
        for (int i = 0; i < signal.length; i++) {
            noisy[i] = signal[i] + mean + stdDev * rand.nextGaussian();
        }
        return noisy;
    }

    public static double[] addPeriodicNoise(double[] signal, double amplitude, double frequency, double dt) {
        double[] noisy = new double[signal.length];

        for (int i = 0; i < signal.length; i++) {
            double t = i * dt;
            double noise = amplitude * Math.sin(2 * Math.PI * frequency * t);
            noise += amplitude * 0.5 * Math.sin(2 * Math.PI * frequency * 2 * t);
            noise += amplitude * 0.25 * Math.sin(2 * Math.PI * frequency * 3 * t);

            noisy[i] = signal[i] + noise;
        }

        return noisy;
    }
}
