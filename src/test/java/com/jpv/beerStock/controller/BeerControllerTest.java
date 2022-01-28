package com.jpv.beerStock.controller;

import static com.jpv.beerStock.utils.JsonConvertionUtils.asJsonString;
import static org.hamcrest.CoreMatchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

import com.jpv.beerStock.builder.BeerDTOBuilder;
import com.jpv.beerStock.dto.BeerDTO;
import com.jpv.beerStock.services.BeerService;



@ExtendWith(MockitoExtension.class)
public class BeerControllerTest {
	private static final String BEER_API_URL_PATH = "/api/v1/beers";
	private static final long VALID_BEER_ID = 1L;
	private static final long INVALID_BEER_ID = 1L;
	private static final String BEER_API_SUBPATH_INCREMENT_URL = "/increment";
	private static final String BEER_API_SUBPATH_DECREMENT_URL = "/decrement";
	
	private MockMvc mockMvc;
	
	@Mock
	private BeerService beerService;
	
	@InjectMocks
	private BeerController beerController;
	
	@BeforeEach
	void setUp() {
		mockMvc = MockMvcBuilders.standaloneSetup(beerController)
				.setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
				.setViewResolvers((s, locale) -> new MappingJackson2JsonView())
				.build();
				
	}
	
	@Test
	void whenPostCalledBeerCreated() throws Exception {
		//given
		BeerDTO beerDTO = BeerDTOBuilder.builder().build().toBeerDTO();
		
		//when
		Mockito.when(beerService.createBeer(beerDTO)).thenReturn(beerDTO);
		
		// then
        mockMvc.perform(post(BEER_API_URL_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(beerDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name", is(beerDTO.getName())))
                .andExpect(jsonPath("$.brand", is(beerDTO.getBrand())))
                .andExpect(jsonPath("$.type", is(beerDTO.getType().toString())));
   
	}

	@Test
	void whenPostCalledWithoutRequiredFielReturnError() throws Exception {
		//given
		BeerDTO beerDTO = BeerDTOBuilder.builder().build().toBeerDTO();
		beerDTO.setBrand(null);
		
	
		// then
        mockMvc.perform(post(BEER_API_URL_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(beerDTO)))
                .andExpect(status().isBadRequest());
  
	}



				
				

}
