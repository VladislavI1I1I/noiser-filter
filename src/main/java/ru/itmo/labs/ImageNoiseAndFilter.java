package ru.itmo.labs;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Random;
import javax.imageio.ImageIO;

public class ImageNoiseAndFilter {
    public static void addNoise(BufferedImage image, double probability) {
        int width = image.getWidth();
        int height = image.getHeight();
        Random rand = new Random();

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (rand.nextDouble() < probability) {
                    int rgb = image.getRGB(x, y);
                    int r = (rgb >> 16) & 0xFF;
                    int g = (rgb >> 8) & 0xFF;
                    int b = rgb & 0xFF;

                    int noiseIntensity = rand.nextInt(256);
                    if (rand.nextBoolean()) {
                        r = Math.min(255, r + noiseIntensity);
                        g = Math.min(255, g + noiseIntensity);
                        b = Math.min(255, b + noiseIntensity);
                    } else {
                        r = Math.max(0, r - noiseIntensity);
                        g = Math.max(0, g - noiseIntensity);
                        b = Math.max(0, b - noiseIntensity);
                    }

                    image.setRGB(x, y, (r << 16) | (g << 8) | b);
                }
            }
        }
    }
    public static BufferedImage applyMedianFilter(BufferedImage image, int radius) {
        int width = image.getWidth();
        int height = image.getHeight();
        BufferedImage result = new BufferedImage(width, height, image.getType());
        int size = (2 * radius + 1) * (2 * radius + 1);
        int[] r = new int[size];
        int[] g = new int[size];
        int[] b = new int[size];

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int idx = 0;
                for (int dy = -radius; dy <= radius; dy++) {
                    for (int dx = -radius; dx <= radius; dx++) {
                        int nx = Math.min(Math.max(x + dx, 0), width - 1);
                        int ny = Math.min(Math.max(y + dy, 0), height - 1);
                        int rgb = image.getRGB(nx, ny);
                        r[idx] = (rgb >> 16) & 0xFF;
                        g[idx] = (rgb >> 8) & 0xFF;
                        b[idx] = rgb & 0xFF;
                        idx++;
                    }
                }
                Arrays.sort(r, 0, idx);
                Arrays.sort(g, 0, idx);
                Arrays.sort(b, 0, idx);
                int median = (r[idx / 2] << 16) | (g[idx / 2] << 8) | b[idx / 2];
                result.setRGB(x, y, median);
            }
        }
        return result;
    }

    public static void main() {
        String filename = "input.jpg";
        try (InputStream inputStream = ImageNoiseAndFilter.class
                .getClassLoader()
                .getResourceAsStream(filename)) {

            if (inputStream == null) {
                throw new IOException("Файл не найден в ресурсах: " + filename);
            }

            BufferedImage img = ImageIO.read(inputStream);
            System.out.println("Изображение загружено успешно: " + img.getWidth() + "x" + img.getHeight());

            addNoise(img, 0.3);
            ImageIO.write(img, "jpg", new File("noisy.jpg"));
            BufferedImage filtered = applyMedianFilter(img, 1);
            ImageIO.write(filtered, "jpg", new File("filtered.jpg"));
        } catch (IOException e) {
            System.err.println("Ошибка при чтении ресурса: " + e.getMessage());
            e.printStackTrace();
        }
    }
}