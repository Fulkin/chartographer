package ru.kontur.chartographer.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.kontur.chartographer.exception.NoExistChartaException;
import ru.kontur.chartographer.exception.PositionException;
import ru.kontur.chartographer.service.ChartaService;

import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolationException;
import javax.validation.constraints.Max;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping("/chartas")
@Validated
public class ChartographerController {

    @Autowired
    ChartaService chartasService;

    @PostMapping("/")
    public ResponseEntity<UUID> createChartas(
            @RequestParam @Positive @Max(20000) int width,
            @RequestParam @Positive @Max(50000) int height) throws IOException {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(chartasService.createChartas(width, height));
    }

    @PostMapping("/{id}/")
    public void setFragmentChartas(
            @PathVariable UUID id,
            @RequestParam @PositiveOrZero int x,
            @RequestParam @PositiveOrZero int y,
            @RequestParam @Positive int width,
            @RequestParam @Positive int height,
            @RequestBody byte[] image) throws IOException, NoExistChartaException, PositionException {
        chartasService.setFragmentChartas(id, x, y, width, height, image);
    }

    @GetMapping(value = "/{id}/", produces = "image/bmp")
    public byte[] getFragmentChartas(
            @PathVariable UUID id,
            @RequestParam @PositiveOrZero int x,
            @RequestParam @PositiveOrZero int y,
            @RequestParam @Positive @Max(5000) int width,
            @RequestParam @Positive @Max(5000) int height) throws IOException, NoExistChartaException, PositionException {
        return chartasService.getFragmentChartas(id, x, y, width, height);
    }

    @DeleteMapping("/{id}")
    public void deleteChartas(@PathVariable UUID id) throws NoExistChartaException {
        chartasService.deleteChartas(id);
    }

    @ExceptionHandler({ConstraintViolationException.class, PositionException.class})
    public void badRequestParam(HttpServletResponse response) {
        try {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Bad request coordinates or dimensions!");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @ExceptionHandler({NoExistChartaException.class, IOException.class})
    public void notExistCharta(HttpServletResponse response) {
        try {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Not exist file or error for reading/writing!");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
