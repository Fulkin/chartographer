package ru.kontur.chartographer.service;

import org.springframework.stereotype.Service;
import ru.kontur.chartographer.exception.NoExistChartaException;
import ru.kontur.chartographer.exception.PositionException;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.UUID;

@Service
public class ChartaServiceImpl implements ChartaService {

    @Override
    public UUID createChartas(int width, int height) throws IOException {
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);
        UUID id = UUID.randomUUID();
        saveImage(id, image);
        return id;
    }

    @Override
    public void setFragmentChartas(UUID id, int x, int y, int width, int height, byte[] image) throws IOException, PositionException, NoExistChartaException {
        BufferedImage imageFromFile = getImage(id);
        int widthCharta = imageFromFile.getWidth();
        int heightCharta = imageFromFile.getHeight();
        if (x > widthCharta || y > heightCharta) {
            throw new PositionException();
        }

        BufferedImage fragment = ImageIO.read(new ByteArrayInputStream(image));
        width = Math.min(fragment.getWidth(), Math.min(widthCharta - x, width));
        height = Math.min(fragment.getHeight(), Math.min(heightCharta - y, height));
        fragment = fragment.getSubimage(0, 0, width, height);
        imageFromFile.setRGB(x, y, width, height,
                fragment.getRGB(0, 0, width, height, null, 0, width),
                0, width);

        saveImage(id, imageFromFile);
    }

    @Override
    public byte[] getFragmentChartas(UUID id, int x, int y, int width, int height) throws PositionException, IOException, NoExistChartaException {
        BufferedImage imageFromFile = getImage(id);
        int widthCharta = imageFromFile.getWidth();
        int heightCharta = imageFromFile.getHeight();
        if (x > widthCharta || y > heightCharta) {
            throw new PositionException();
        }
        BufferedImage sendImage = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);
        width = Math.min(widthCharta - x, width);
        height = Math.min(heightCharta - y, height);
        sendImage.setRGB(0, 0, width, height,
                imageFromFile.getRGB(x, y, width, height, null, 0, width),
                0, width);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ImageIO.write(sendImage, "BMP", outputStream);

        return outputStream.toByteArray();
    }

    @Override
    public void deleteChartas(UUID id) throws NoExistChartaException {
        File file = new File(getFileName(id));
        if (!file.delete()) {
            throw new NoExistChartaException(id);
        }
    }

    private String getFileName(UUID id) {
        File file = new File("../chartas");
        if (!file.exists() || !file.isDirectory()) {
            file.mkdir();
        }
        return "../chartas/" + id + ".bmp";
    }

    private void saveImage(UUID id, BufferedImage image) throws IOException {
        String fileName = getFileName(id);
        File file = new File(fileName);
        file.createNewFile();
        ImageIO.write(image, "BMP", file);
    }

    private BufferedImage getImage(UUID id) throws NoExistChartaException {
        try {
            String createdFileName = getFileName(id);
            File file = new File(createdFileName);
            return ImageIO.read(file);
        } catch (IOException e) {
            throw new NoExistChartaException(id);
        }
    }
}
