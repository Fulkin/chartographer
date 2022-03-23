package ru.kontur.chartographer.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
class ChartographerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private UUID id;
    private static final int WIDTH_CHECKED_CHARTA = 1000;
    private static final int HEIGHT_CHECKED_CHARTA = 1000;

    @BeforeEach
    public void initCharta() throws Exception {
        ResultActions resultActions = this.mockMvc.perform(post("/chartas/")
                .param("width", String.valueOf(WIDTH_CHECKED_CHARTA))
                .param("height", String.valueOf(HEIGHT_CHECKED_CHARTA)));
        MvcResult result = resultActions.andReturn();
        String contentAsString = result.getResponse().getContentAsString();
        id = objectMapper.readValue(contentAsString, UUID.class);
    }

    @AfterEach
    public void deleteCharta() throws Exception {
        this.mockMvc.perform(delete("/chartas/" + id + "/"));
    }


    @Test
    void createChartas() throws Exception {
        ResultActions resultActions = this.mockMvc.perform(post("/chartas/")
                .param("width", String.valueOf(WIDTH_CHECKED_CHARTA))
                .param("height", String.valueOf(HEIGHT_CHECKED_CHARTA)))
                .andDo(print())
                .andExpect(status().isCreated());
        MvcResult result = resultActions.andReturn();
        String contentAsString = result.getResponse().getContentAsString();
        UUID otherId = objectMapper.readValue(contentAsString, UUID.class);
        this.mockMvc.perform(delete("/chartas/" + otherId));
    }

    @Test
    void createNegativeCoordinatesCharta() throws Exception {
        this.mockMvc.perform(post("/chartas/")
                .param("width", "-1")
                .param("height", "10"))
                .andDo(print())
                .andExpect(status().isBadRequest());
        this.mockMvc.perform(post("/chartas/")
                .param("width", "10")
                .param("height", "-1"))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    void createOutOfLimitValuesCoordinatesCharta() throws Exception {
        this.mockMvc.perform(post("/chartas/")
                .param("width", "20001")
                .param("height", String.valueOf(HEIGHT_CHECKED_CHARTA / 2)))
                .andDo(print())
                .andExpect(status().isBadRequest());
        this.mockMvc.perform(post("/chartas/")
                .param("width", String.valueOf(WIDTH_CHECKED_CHARTA / 2))
                .param("height", "50001"))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    void setFragmentChartas() throws Exception {
        byte[] byteArray = getImageAsByteArray();
        this.mockMvc.perform(post("/chartas/" + id + "/")
                .param("x", "0")
                .param("y", "0")
                .param("width", "100")
                .param("height", "100")
                .content(byteArray)
        )
                .andExpect(status().isOk());
    }

    @Test
    void setFragmentNotExistChartas() throws Exception {
        byte[] byteArray = getImageAsByteArray();
        this.mockMvc.perform(post("/chartas/" + UUID.randomUUID() + "/")
                .param("x", "0")
                .param("y", "0")
                .param("width", "100")
                .param("height", "100")
                .content(byteArray)
        )
                .andExpect(status().isNotFound());
    }

    @Test
    void setFragmentOutsideCharta() throws Exception {
        byte[] byteArray = getImageAsByteArray();
        this.mockMvc.perform(post("/chartas/" + id + "/")
                .param("x", String.valueOf(WIDTH_CHECKED_CHARTA * 3 / 4))
                .param("y", String.valueOf(HEIGHT_CHECKED_CHARTA * 3 / 4))
                .param("width", "500")
                .param("height", "500")
                .content(byteArray)
        )
                .andExpect(status().isOk());
    }

    @Test
    void setFragmentCoordinatesOutsideCharta() throws Exception {
        byte[] byteArray = getImageAsByteArray();
        this.mockMvc.perform(post("/chartas/" + id + "/")
                .param("x", String.valueOf(WIDTH_CHECKED_CHARTA + 1))
                .param("y", String.valueOf(0))
                .param("width", "500")
                .param("height", "500")
                .content(byteArray)
        )
                .andExpect(status().isBadRequest());
        this.mockMvc.perform(post("/chartas/" + id + "/")
                .param("x", String.valueOf(0))
                .param("y", String.valueOf(HEIGHT_CHECKED_CHARTA + 1))
                .param("width", "500")
                .param("height", "500")
                .content(byteArray)
        )
                .andExpect(status().isBadRequest());
    }

    @Test
    void getFragmentChartas() throws Exception {
        byte[] byteArray = getImageAsByteArray();
        this.mockMvc.perform(get("/chartas/" + id + "/")
                .param("x", "0")
                .param("y", "0")
                .param("width", "100")
                .param("height", "100")
                .content(byteArray)
        )
                .andExpect(status().isOk());
    }

    @Test
    void getFragmentNotExistChartas() throws Exception {
        byte[] byteArray = getImageAsByteArray();
        this.mockMvc.perform(get("/chartas/" + UUID.randomUUID() + "/")
                .param("x", "0")
                .param("y", "0")
                .param("width", "100")
                .param("height", "100")
                .content(byteArray)
        )
                .andExpect(status().isNotFound());
    }

    @Test
    void getOutOfCoordinatesFragments() throws Exception {
        byte[] byteArray = getImageAsByteArray();
        this.mockMvc.perform(get("/chartas/" + id + "/")
                .param("x", String.valueOf(WIDTH_CHECKED_CHARTA + 1))
                .param("y", String.valueOf(HEIGHT_CHECKED_CHARTA + 1))
                .param("width", "100")
                .param("height", "100")
                .content(byteArray)
        )
                .andExpect(status().isBadRequest());

    }

    @Test
    void getOutOfLimitValuesWidthAndHeight() throws Exception {
        byte[] byteArray = getImageAsByteArray();
        this.mockMvc.perform(get("/chartas/" + id + "/")
                .param("x", "0")
                .param("y", "0")
                .param("width", "5001")
                .param("height", "5001")
                .content(byteArray)
        )
                .andExpect(status().isBadRequest());
    }

    @Test
    void deleteChartas() throws Exception {
        this.mockMvc.perform(delete("/chartas/" + id + "/"))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    void deleteNotExistChartas() throws Exception {
        this.mockMvc.perform(delete("/chartas/" + UUID.randomUUID() + "/"))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    private byte[] getImageAsByteArray() throws Exception {
        File file = new File("src/test/resources/1234.bmp");
        BufferedImage imageFromFile = ImageIO.read(file);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ImageIO.write(imageFromFile, "BMP", outputStream);
        return outputStream.toByteArray();
    }
}