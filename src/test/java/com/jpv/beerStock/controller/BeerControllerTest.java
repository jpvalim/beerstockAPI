package com.jpv.beerStock.controller;

import static com.jpv.beerStock.utils.JsonConvertionUtils.asJsonString;
import static org.hamcrest.CoreMatchers.is;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Collections;

import javax.net.ssl.SSLEngineResult.Status;

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
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

import com.jpv.beerStock.builder.BeerDTOBuilder;
import com.jpv.beerStock.dto.BeerDTO;
import com.jpv.beerStock.exceptions.BeerNotFoundException;
import com.jpv.beerStock.repositories.BeerRepository;
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

	@Test
	void whenGETCalledValidNameReturnOKStatus() throws Exception {
		//given
		BeerDTO beerDTO = BeerDTOBuilder.builder().build().toBeerDTO();
		
		when(beerService.findByName(beerDTO.getName())).thenReturn(beerDTO);
		
		ResultActions resultPerform = mockMvc.perform(MockMvcRequestBuilders.get(BEER_API_URL_PATH + "/" + beerDTO.getName()).contentType(MediaType.APPLICATION_JSON));
		resultPerform.andExpect(status().isOk());
		resultPerform.andExpect(jsonPath("$.name", is(beerDTO.getName())));
	
	}

	@Test
	void whenGETCalledInvalidNameReturnNotFoundException() throws Exception{
		//given
		BeerDTO beerDTO = BeerDTOBuilder.builder().build().toBeerDTO();
		
		//when
		when(beerService.findByName(beerDTO.getName())).thenThrow(BeerNotFoundException.class);
		
		//then
		ResultActions resultPerform = mockMvc.perform(MockMvcRequestBuilders.get(BEER_API_URL_PATH + "/" + beerDTO.getName()).contentType(MediaType.APPLICATION_JSON));
		resultPerform.andExpect(status().isNotFound());
				
	}
	
	@Test
	void whenGETListCalledReturnOKStatus() throws Exception {
		//given
		BeerDTO beerDTO = BeerDTOBuilder.builder().build().toBeerDTO();
		
		when(beerService.listAll()).thenReturn(Collections.singletonList(beerDTO));
		
		ResultActions resultPerform = mockMvc.perform(MockMvcRequestBuilders.get(BEER_API_URL_PATH).contentType(MediaType.APPLICATION_JSON));
		resultPerform.andExpect(status().isOk());
		resultPerform.andExpect(jsonPath("$[0].name", is(beerDTO.getName())));
	
	}
	
	@Test
	void whenGETEmptyListCalledReturnOKStatus() throws Exception {
		//when		
		when(beerService.listAll()).thenReturn(Collections.emptyList());
		
		//then
		ResultActions resultPerform = mockMvc.perform(MockMvcRequestBuilders.get(BEER_API_URL_PATH).contentType(MediaType.APPLICATION_JSON));
		resultPerform.andExpect(status().isOk());

	}
	
	@Test
	void whenDELETECalledValidIDReturnNoContentStatus() throws Exception {
		//given
		BeerDTO beerDTO = BeerDTOBuilder.builder().build().toBeerDTO();
		
		//when
		doNothing().when(beerService).deleteById(beerDTO.getId());;
		
		ResultActions resultPerform = mockMvc.perform(MockMvcRequestBuilders.delete(BEER_API_URL_PATH + "/" + beerDTO.getId()).contentType(MediaType.APPLICATION_JSON));
		resultPerform.andExpect(status().isNoContent());
			
	}
	
	@Test
	void whenDELETECalledInvalidIDReturnNotFoundStatus() throws Exception {
				
		//when
		doThrow(BeerNotFoundException.class).when(beerService).deleteById(INVALID_BEER_ID);;
		
		ResultActions resultPerform = mockMvc.perform(MockMvcRequestBuilders.delete(BEER_API_URL_PATH + "/" + INVALID_BEER_ID).contentType(MediaType.APPLICATION_JSON));
		resultPerform.andExpect(status().isNotFound());
			
	}
	
	//Aula 13
	
	

}
