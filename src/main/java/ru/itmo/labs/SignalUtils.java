package ru.itmo.labs;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    public static double[] readNpySignal(String filePath) throws IOException {
        try (RandomAccessFile raf = new RandomAccessFile(filePath, "r")) {
            byte[] magic = new byte[6];
            raf.readFully(magic);
            byte[] expectedMagic = {(byte) 0x93, 'N', 'U', 'M', 'P', 'Y'};
            if (!Arrays.equals(magic, expectedMagic)) {
                throw new IOException("Not a NUMPY file.");
            }

            raf.readByte();
            raf.readByte();

            int headerLen = raf.read() | (raf.read() << 8);
            byte[] headerBytes = new byte[headerLen];
            raf.readFully(headerBytes);
            String header = new String(headerBytes, StandardCharsets.UTF_8).trim();

            Matcher matcher = Pattern.compile("'shape': \\((\\d+),?\\)").matcher(header);
            if (!matcher.find()) {
                throw new IOException("Could not find shape in header.");
            }
            int numElements = Integer.parseInt(matcher.group(1));

            double[] signal = new double[numElements];
            ByteBuffer dataBuffer = ByteBuffer.allocate(numElements * 8);
            dataBuffer.order(ByteOrder.LITTLE_ENDIAN);
            raf.getChannel().read(dataBuffer);
            dataBuffer.flip();

            for (int i = 0; i < numElements; i++) {
                signal[i] = dataBuffer.getDouble();
            }
            return signal;
        }
    }
    public static double[] cropToPowerOfTwo(double[] input) {
        int powerOfTwo = 1;
        while (powerOfTwo * 2 < input.length) {
            powerOfTwo *= 2;
        }
        return Arrays.copyOf(input, powerOfTwo);
    }

}
