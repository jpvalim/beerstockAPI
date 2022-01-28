package com.jpv.beerStock.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jpv.beerStock.entity.Beer;

import java.util.Optional;

public interface BeerRepository extends JpaRepository<Beer, Long> {

    Optional<Beer> findByName(String name);
}
