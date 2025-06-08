package ru.itmo.labs;

import javax.sound.sampled.LineUnavailableException;
import java.io.IOException;
import java.util.Scanner;

import static ru.itmo.labs.SignalUtils.*;

public class SignalLauncher {
    private final static double dt = 0.0001;
    private final static int n = 131072;
    private final static double[] amplitudes = {1.0, 0.6, 0.3, 0.1};
    private final static double[] frequencies = {82.41, 164.81, 247.23, 329.63};
    public static void main(String[] args) {
        try {
            System.out.println("Выберите способ работы программы: Генерация сигнала или считывание из signal_2.npy (введите 1 или 2)");
            Scanner scanner = new Scanner(System.in);
            String line = scanner.nextLine();
            double[] clean;
            if (line.equals("2")){
                System.out.println("Чтение сигнала из signal_2.npy...");
               clean  = cropToPowerOfTwo(readNpySignal("signal_2.npy"));
                System.out.println("Сигнал успешно загружен.");
            }  else if (line.equals("1")){
                System.out.println("Генерация сигнала...");
                clean = SignalUtils.generateCleanSignal(n, dt, amplitudes, frequencies);
            } else {
                throw new Exception("Некорректный ввод");
            }



            double[] noisyGaussian = SignalUtils.addGaussianNoise(clean, 0.1, 1);

            double[] filtered = FftFilter.filter(noisyGaussian);

            System.out.println("Сохранение графиков...");
            SignalVisualizer.saveSignalChart(clean, "Исходный сигнал", "clean_signal");
            SignalVisualizer.saveSignalChart(noisyGaussian, "Зашумленный сигнал", "noisy_signal");
            SignalVisualizer.saveSignalChart(filtered, "Отфильтрованный сигнал", "filtered_signal");
            System.out.println("Графики сохранены.");

            System.out.println("Воспроизведение чистого сигнала...");
            SignalPlayer.playSignal(clean);

            System.out.println("Воспроизведение зашумленного сигнала...");
            SignalPlayer.playSignal(noisyGaussian);

            System.out.println("Воспроизведение отфильтрованного сигнала...");
            SignalPlayer.playSignal(filtered);

        } catch (IOException e) {
            System.err.println("Ошибка чтения файла или сохранения графика: " + e.getMessage());
            e.printStackTrace();
        } catch (LineUnavailableException e) {
            System.err.println("Ошибка воспроизведения: " + e.getMessage());
        } catch (Exception e) {
            System.err.println( e.getMessage());
        }
    }
}