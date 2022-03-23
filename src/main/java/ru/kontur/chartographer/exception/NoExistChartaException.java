package ru.kontur.chartographer.exception;

import java.util.UUID;

public class NoExistChartaException extends Exception{

    public NoExistChartaException(UUID id) {
        super("Charta with id: " + id + " doesn't exist!");
    }
}
