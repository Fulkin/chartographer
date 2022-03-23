package ru.kontur.chartographer.service;

import ru.kontur.chartographer.exception.NoExistChartaException;
import ru.kontur.chartographer.exception.PositionException;

import java.io.IOException;
import java.util.UUID;

public interface ChartaService {

    UUID createChartas(int width, int height) throws IOException;

    void setFragmentChartas(UUID id, int x, int y, int width, int height, byte[] image) throws IOException, PositionException, NoExistChartaException;

    byte[] getFragmentChartas(UUID id, int x, int y, int width, int height) throws IOException, PositionException, NoExistChartaException;

    void deleteChartas(UUID id) throws NoExistChartaException;

}
