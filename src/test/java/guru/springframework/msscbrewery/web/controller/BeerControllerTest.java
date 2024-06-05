package guru.springframework.msscbrewery.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import guru.springframework.msscbrewery.services.BeerService;
import guru.springframework.msscbrewery.web.model.BeerDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.hamcrest.core.Is.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BeerController.class)
class BeerControllerTest {

    @MockBean
    BeerService beerService;

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    BeerDto validBeer;

    @BeforeEach
    void setUp() {
        validBeer = BeerDto.builder()
                .id(UUID.randomUUID())
                .beerName("Beer1")
                .beerStyle("PALE_ALE")
                .upc(123456789012L)
                .build();
    }

    @Test
    void getBeer() throws Exception {
        given(beerService.getBeerById(any(UUID.class))).willReturn(validBeer);

        mockMvc.perform(get("/api/v1/beer/" + validBeer.getId().toString()).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(validBeer.getId().toString())))
                .andExpect(jsonPath("$.beerName", is("Beer1")));

        then(beerService).should().getBeerById(eq(validBeer.getId()));
    }

    @Test
    void handlePost() throws Exception {
        BeerDto beerDto = validBeer;
        beerDto.setId(null);
        BeerDto savedDto = BeerDto.builder()
                .id(UUID.randomUUID())
                .beerName("New Beer")
                .build();
        String beerDtoJSon = objectMapper.writeValueAsString(beerDto);

        given(beerService.saveNewBeer(any())).willReturn(savedDto);

        mockMvc.perform(post("/api/v1/beer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(beerDtoJSon))
                .andExpect(status().isCreated());

        then(beerService).should().saveNewBeer(eq(beerDto));
    }

    @Test
    void handleUpdate() throws Exception {
        BeerDto beerDto = validBeer;
        String beerDtoJSon = objectMapper.writeValueAsString(beerDto);

        mockMvc.perform(put("/api/v1/beer/" + validBeer.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(beerDtoJSon))
                .andExpect(status().isNoContent());

        then(beerService).should().updateBeer(eq(validBeer.getId()), eq(beerDto));
    }

    @Test
    void deleteBeer() throws Exception {
        mockMvc.perform(delete("/api/v1/beer/" + validBeer.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        then(beerService).should().deleteById(eq(validBeer.getId()));
    }
}