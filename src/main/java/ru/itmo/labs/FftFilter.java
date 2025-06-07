package ru.itmo.labs;

import org.apache.commons.math3.complex.Complex;
import org.apache.commons.math3.transform.*;

import java.util.Arrays;

public class FftFilter {
    public static double[] filter(double[] signal) {
        FastFourierTransformer fft = new FastFourierTransformer(DftNormalization.STANDARD);
        Complex[] spectrum = fft.transform(signal, TransformType.FORWARD);

        int n = spectrum.length;
        double[] absSpectrum = new double[n];
        for (int i = 0; i < n; i++) {
            absSpectrum[i] = spectrum[i].abs();
        }

        double[] sortedAmplitudes = absSpectrum.clone();
        Arrays.sort(sortedAmplitudes);
        double threshold = sortedAmplitudes[(int)(0.99 * n)] * 2;

        Complex[] filteredSpectrum = spectrum.clone();
        for (int i = 0; i < n/2; i++) {
            if (absSpectrum[i] < threshold) {
                int j = n - i - 1;
                filteredSpectrum[i] = Complex.ZERO;
                filteredSpectrum[j] = Complex.ZERO;
            }
        }
        if (n % 2 == 0) {
            int mid = n / 2;
            if (absSpectrum[mid] < threshold) {
                filteredSpectrum[mid] = Complex.ZERO;
            }
        }

        Complex[] complexResult = fft.transform(filteredSpectrum, TransformType.INVERSE);
        double[] result = new double[n];
        for (int i = 0; i < n; i++) {
            result[i] = complexResult[i].getReal();
        }
        return result;
    }
}