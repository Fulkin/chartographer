package ru.kontur.chartographer.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.kontur.chartographer.exception.NoExistChartaException;
import ru.kontur.chartographer.exception.PositionException;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class ChartaServiceImplTest {

    @Autowired
    private ChartaService chartaService;

    private UUID idCheckedCharta;
    private static final int WIDTH_CHECKED_CHARTA = 1000;
    private static final int HEIGHT_CHECKED_CHARTA = 1000;

    @BeforeEach
    public void initCharta() {
        UUID id = null;
        try {
            id = chartaService.createChartas(WIDTH_CHECKED_CHARTA, HEIGHT_CHECKED_CHARTA);
        } catch (IOException e) {
            e.printStackTrace();
        }
        idCheckedCharta = id;
    }


    @Test
    void createChartas() {
        assertNotNull(idCheckedCharta);
        assertDoesNotThrow(() ->
                chartaService.deleteChartas(idCheckedCharta)
        );
    }

    @Test
    void badRequestCoordinates() {
        assertThrows(PositionException.class, () -> {
            File file = new File("src/test/resources/1234.bmp");
            BufferedImage imageFromFile = ImageIO.read(file);

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ImageIO.write(imageFromFile, "BMP", outputStream);
            byte[] byteArray = outputStream.toByteArray();
            chartaService.setFragmentChartas(idCheckedCharta, WIDTH_CHECKED_CHARTA + 1, 0, 100, 100, byteArray);
        });

        assertThrows(PositionException.class, () -> {
            File file = new File("src/test/resources/1234.bmp");
            BufferedImage imageFromFile = ImageIO.read(file);

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ImageIO.write(imageFromFile, "BMP", outputStream);
            byte[] byteArray = outputStream.toByteArray();
            chartaService.setFragmentChartas(idCheckedCharta, 0, HEIGHT_CHECKED_CHARTA + 1, 100, 100, byteArray);
        });

        assertThrows(PositionException.class, () ->
                chartaService.getFragmentChartas(idCheckedCharta, WIDTH_CHECKED_CHARTA + 1, 0, 100, 100)
        );

        assertThrows(PositionException.class, () ->
                chartaService.getFragmentChartas(idCheckedCharta, 0, HEIGHT_CHECKED_CHARTA + 1, 100, 100)
        );
        assertDoesNotThrow(() ->
                chartaService.deleteChartas(idCheckedCharta)
        );
    }

    @Test
    void setGetFragmentAndDeleteCharta() {
        assertDoesNotThrow(() -> {
            File file = new File("src/test/resources/1234.bmp");
            BufferedImage imageFromFile = ImageIO.read(file);

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ImageIO.write(imageFromFile, "BMP", outputStream);
            byte[] byteArray = outputStream.toByteArray();
            chartaService.setFragmentChartas(idCheckedCharta, 0, 0, 100, 100, byteArray);
        });
        assertDoesNotThrow(() ->
                chartaService.getFragmentChartas(idCheckedCharta, 0, 0, 100, 100)
        );
        assertDoesNotThrow(() ->
                chartaService.deleteChartas(idCheckedCharta)
        );
    }

    @Test
    void notExistFile() {
        assertThrows(NoExistChartaException.class, () -> {
            File file = new File("src/test/resources/1234.bmp");
            BufferedImage imageFromFile = ImageIO.read(file);

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ImageIO.write(imageFromFile, "BMP", outputStream);
            byte[] byteArray = outputStream.toByteArray();
            chartaService.setFragmentChartas(UUID.randomUUID(), 0, 0, 100, 100, byteArray);
        });
        assertThrows(NoExistChartaException.class, () ->
                chartaService.getFragmentChartas(UUID.randomUUID(), 0, 0, 100, 100)
        );
        assertThrows(NoExistChartaException.class, () ->
                chartaService.deleteChartas(UUID.randomUUID()));
        assertDoesNotThrow(() ->
                chartaService.deleteChartas(idCheckedCharta)
        );
    }

    @Test
    void deleteChartas() {
        assertDoesNotThrow(() ->
                chartaService.deleteChartas(idCheckedCharta)
        );
        assertThrows(NoExistChartaException.class, () ->
                chartaService.getFragmentChartas(idCheckedCharta, 0, 0, 100, 100)
        );
        assertThrows(NoExistChartaException.class, () -> {
            File file = new File("src/test/resources/1234.bmp");
            BufferedImage imageFromFile = ImageIO.read(file);

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ImageIO.write(imageFromFile, "BMP", outputStream);
            byte[] byteArray = outputStream.toByteArray();
            chartaService.setFragmentChartas(idCheckedCharta, 0, 0, 100, 100, byteArray);
        });
    }
}