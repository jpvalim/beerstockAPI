package com.jpv.beerStock.service;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import com.jpv.beerStock.builder.BeerDTOBuilder;
import com.jpv.beerStock.dto.BeerDTO;
import com.jpv.beerStock.entity.Beer;
import com.jpv.beerStock.exceptions.BeerAlreadyRegisteredException;
import com.jpv.beerStock.mappers.BeerMapper;
import com.jpv.beerStock.repositories.BeerRepository;
import com.jpv.beerStock.services.BeerService;

//informa que estou utilizando o Mockito para criar as classes dublês
@ExtendWith (MockitoExtension.class)
public class BeerServiceTest {
	
	//Não será a classe que iremos testar, mas beeRepository precisa para o teste, por isso anota co @Mock
	@Mock
	private BeerRepository beerRepository;
	
	private BeerMapper beerMapper = BeerMapper.INSTANCE;
	
	@InjectMocks
	private BeerService beerService;
	
	@Test
	void whenBeerInformedThenItShowBeerCreated() throws BeerAlreadyRegisteredException {
		//Given (Dada as os objetos cerveja)
		BeerDTO expectedBeerDTO = BeerDTOBuilder.builder().build().toBeerDTO();
		Beer expectedSavedBeer = beerMapper.toModel(expectedBeerDTO);
		
		//When (Como o beerRepository é um mock eu preciso instruir o que ele vai retornar quando for chamado)
		Mockito.when(beerRepository.findByName(expectedBeerDTO.getName())).thenReturn(Optional.empty());
		Mockito.when(beerRepository.save(expectedSavedBeer)).thenReturn(expectedSavedBeer);
		
		//Then (Chamar o método beerService para testar)
		BeerDTO createdBeerDTO = beerService.createBeer(expectedBeerDTO);
		
		//Usando os métodos do HamCrast
		assertThat(createdBeerDTO.getId(), is(equalTo(expectedBeerDTO.getId())));
		assertThat(createdBeerDTO.getName(), is(equalTo(expectedBeerDTO.getName())));
		assertThat(createdBeerDTO.getQuantity(), is(equalTo(expectedBeerDTO.getQuantity())));
		assertThat(createdBeerDTO.getQuantity(), is (greaterThan(2)));
	}
}
