package ru.kontur.chartographer.exception;

public class PositionException extends Exception {
    public PositionException() {
        super("Fragment is out of bounds of the picture");
    }
}
