package image;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;

public class Converter implements TextGraphicsConverter {
    private int maxWidth;
    private int maxHeight;
    private double maxRatio;
    private TextColorSchema schema;

    @Override
    public String convert(String url) throws IOException, BadImageSizeException {

        BufferedImage img = null;
        try {
            img = ImageIO.read(new URL(url));
        } catch (FileNotFoundException e) {
            System.out.println("Файл не найден!");
        }

        assert img != null;
        int newWidth = img.getWidth();
        int newHeight = img.getHeight();

        // Проверка на максимально допустимое соотношение сторон изображения
        if (maxHeight > 0 || maxWidth > 0) {
            if (maxHeight <= 0) maxHeight = img.getHeight();
            if (maxWidth <= 0) maxWidth = img.getWidth();
            if (maxRatio > 0) {
                if ((img.getHeight() >= maxRatio * img.getWidth()) || (img.getWidth() >= maxRatio * img.getHeight())) {
                    double imgRation;
                    if (img.getHeight() > img.getWidth()) {
                        imgRation = (double) img.getHeight() / img.getWidth();
                    } else {
                        imgRation = (double) img.getWidth() / img.getHeight();
                    }
                    throw new BadImageSizeException(imgRation, getMaxRatio());
                }
            }
            newWidth = resizeImage(img, getMaxWidth(), getMaxHeight()).getWidth();
            newHeight = resizeImage(img, getMaxWidth(), getMaxHeight()).getHeight();
        }

        // Ребрендинг изображения
        Image scaledImage = img.getScaledInstance(newWidth, newHeight, BufferedImage.SCALE_SMOOTH);

        // Создание чёрно-белой копии изображения.
        BufferedImage bwImg = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_BYTE_GRAY);
        Graphics2D graphics = bwImg.createGraphics();
        graphics.drawImage(scaledImage, 0, 0, null);

        // Проход по пикселям и создание строки символов
        WritableRaster bwRaster = bwImg.getRaster();
        StringBuilder builder = new StringBuilder((newWidth * 2) * newHeight);
        if (schema == null) schema = new TextColor();
        for (int h = 0; h < newHeight; h++) {
            if (builder.length() != 0) builder.append("\n");
            for (int w = 0; w < newWidth; w++) {
                int color = bwRaster.getPixel(w, h, new int[3])[0];
                char c = schema.convert(color);
                builder.append(c).append(c);
            }
        }
        return builder.toString(); // Возвращаем собранный текст.
    }

    public int getMaxWidth() {
        return maxWidth;
    }

    @Override
    public void setMaxWidth(int width) {
        this.maxWidth = width;
    }

    public int getMaxHeight() {
        return maxHeight;
    }

    @Override
    public void setMaxHeight(int height) {
        this.maxHeight = height;
    }

    public double getMaxRatio() {
        return maxRatio;
    }

    @Override
    public void setMaxRatio(double maxRatio) {
        this.maxRatio = maxRatio;
    }

    @Override
    public void setTextColorSchema(TextColorSchema schema) {
        this.schema = schema;
    }

    BufferedImage resizeImage(BufferedImage originalImage, int targetWidth, int targetHeight) {
        BufferedImage resizedImage = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics2D = resizedImage.createGraphics();
        graphics2D.drawImage(originalImage, 0, 0, targetWidth, targetHeight, null);
        graphics2D.dispose();
        return resizedImage;
    }
}
