package ru.itmo.labs;

import org.knowm.xchart.BitmapEncoder;
import org.knowm.xchart.XYChart;
import org.knowm.xchart.XYChartBuilder;
import org.knowm.xchart.style.Styler;

import java.io.IOException;
import java.util.Arrays;
import java.util.stream.IntStream;

public class SignalVisualizer {

    public static void saveSignalChart(double[] signal, String title, String filePath) throws IOException {
        int displayPoints = 2000;
        double[] displaySignal = Arrays.copyOf(signal, Math.min(signal.length, displayPoints));

        double[] xData = IntStream.range(0, displaySignal.length).mapToDouble(i -> i).toArray();

        XYChart chart = new XYChartBuilder()
                .width(800)
                .height(600)
                .title(title)
                .xAxisTitle("Отсчеты")
                .yAxisTitle("Амплитуда")
                .build();

        chart.getStyler().setLegendPosition(Styler.LegendPosition.InsideNW);
        chart.getStyler().setPlotGridLinesVisible(true);

        chart.addSeries("cигнал", xData, displaySignal);

        BitmapEncoder.saveBitmap(chart, filePath, BitmapEncoder.BitmapFormat.PNG);
        System.out.println("График сохранен в " + filePath + ".png");
    }
} 