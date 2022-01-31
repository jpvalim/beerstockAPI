package com.jpv.beerStock.service;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.empty;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;
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
import com.jpv.beerStock.exceptions.BeerNotFoundException;
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
	
	//validação da criação da cerveja no beerService
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
	
	//Testando lançamento de exception
	@Test
	void whenAlreadyRegistredBeerInformedThenExceptionShouldReturn() {
		//Given (Dada as os objetos cerveja)
		BeerDTO expectedBeerDTO = BeerDTOBuilder.builder().build().toBeerDTO();
		Beer duplicateddBeer = beerMapper.toModel(expectedBeerDTO);
		
		//when (criar o mock do findByName)
		Mockito.when(beerRepository.findByName(expectedBeerDTO.getName())).thenReturn(Optional.of(duplicateddBeer));
	
		//then validação da exception
		assertThrows(BeerAlreadyRegisteredException.class, () -> beerService.createBeer(expectedBeerDTO));
		
	}
	
	@Test
	void whenValidBeerNameIsGivenThenReturnBeer() throws BeerNotFoundException {
		//Given (Dada as os objetos cerveja)
		BeerDTO expectedFoundBeerDTO = BeerDTOBuilder.builder().build().toBeerDTO();
		Beer expectedFoundBeer = beerMapper.toModel(expectedFoundBeerDTO);
		
		//when (criar o mock do findByName)
		Mockito.when(beerRepository.findByName(expectedFoundBeerDTO.getName())).thenReturn(Optional.of(expectedFoundBeer));
		
		//then
		BeerDTO beerDTO = beerService.findByName(expectedFoundBeerDTO.getName());
		
		assertThat(expectedFoundBeerDTO, is(equalTo(beerDTO)));
		
	}
	
	@Test
	void whenNotFoundBeerThenReturnException() {
		//Given (Dada as os objetos cerveja)
		BeerDTO expectedBeerDTO = BeerDTOBuilder.builder().build().toBeerDTO();
				
		//when (criar o mock do findByName)
		Mockito.when(beerRepository.findByName(expectedBeerDTO.getName())).thenReturn(Optional.empty());
	
		//then validação da exception
		assertThrows(BeerNotFoundException.class, () -> beerService.findByName(expectedBeerDTO.getName()));
		
	}
	
	@Test
	void whenListBeerReturnsListOfBeers() {
		//Given
		BeerDTO expectedFoundBeerDTO = BeerDTOBuilder.builder().build().toBeerDTO();
		Beer expectedFoundBeer = beerMapper.toModel(expectedFoundBeerDTO);
				
		//when
		when(beerRepository.findAll()).thenReturn(Collections.singletonList(expectedFoundBeer));
		
		//then
		List<BeerDTO> foundedListBeerDTO = beerService.listAll();
		
		assertThat(foundedListBeerDTO, is(not(empty())));
		assertThat(foundedListBeerDTO.get(0), is(equalTo(expectedFoundBeerDTO)));
				
	}
	
	@Test
	void whenListBeerReturnsEmptyList() {
		
		//when
		when(beerRepository.findAll()).thenReturn(Collections.emptyList());
		
		//then
		List<BeerDTO> foundedListBeerDTO = beerService.listAll();
		
		assertThat(foundedListBeerDTO, is(empty()));
		
	}
	
	@Test
	void whenDeleteBeerWithValidIdThenBeerShouldBeDeleted() throws BeerNotFoundException {
		//Given
		BeerDTO expectedDeletedBeerDTO = BeerDTOBuilder.builder().build().toBeerDTO();
		Beer expectedDeletedBeer = beerMapper.toModel(expectedDeletedBeerDTO);	
		//when
		when(beerRepository.findById(expectedDeletedBeerDTO.getId())).thenReturn(Optional.of(expectedDeletedBeer));
		doNothing().when(beerRepository).deleteById(expectedDeletedBeerDTO.getId());
		
		//then
		beerService.deleteById(expectedDeletedBeerDTO.getId());
		
		//Método verify do mockito, isso acontece porque o método deleteById retorna vazio.
		verify(beerRepository, times(1)).findById(expectedDeletedBeerDTO.getId());
		verify(beerRepository, times(1)).deleteById(expectedDeletedBeerDTO.getId());
		
	}
	
}
