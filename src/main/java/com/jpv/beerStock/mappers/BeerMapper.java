package com.jpv.beerStock.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import com.jpv.beerStock.dto.BeerDTO;
import com.jpv.beerStock.entity.Beer;

@Mapper
public interface BeerMapper {

    BeerMapper INSTANCE = Mappers.getMapper(BeerMapper.class);

    Beer toModel(BeerDTO beerDTO);

    BeerDTO toDTO(Beer beer);
}
