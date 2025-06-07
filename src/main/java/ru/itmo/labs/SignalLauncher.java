package ru.itmo.labs;

import javax.sound.sampled.LineUnavailableException;

public class SignalLauncher {
    public static void main(String[] args) {
        double dt = 0.0001;
        int n = 131072;
        double[] amplitudes = {1.0, 0.6, 0.3, 0.1};
        double[] frequencies = {82.41, 164.81, 247.23, 329.63};

        double[] clean = SignalUtils.generateCleanSignal(n, dt, amplitudes, frequencies);
        double[] noisyGaussian = SignalUtils.addGaussianNoise(clean, 0.1, 1);
        double[] noisyPeriodical = SignalUtils.addPeriodicNoise(clean, 1.0, 50.0, dt);

        double[] filtered = FftFilter.filter(noisyGaussian);
        try {
            System.out.println("Воспроизведение чистого сигнала...");
            SignalPlayer.playSignal(clean);

            System.out.println("Воспроизведение зашумленного гауссовым шумом сигнала...");
            SignalPlayer.playSignal(noisyGaussian);

            System.out.println("Воспроизведение зашумленного периодическим шумом сигнала...");
            SignalPlayer.playSignal(noisyPeriodical);

            System.out.println("Воспроизведение отфильтрованного сигнала...");
            SignalPlayer.playSignal(filtered);
        } catch (LineUnavailableException e) {
            System.err.println(e.getMessage());
        }
    }
}