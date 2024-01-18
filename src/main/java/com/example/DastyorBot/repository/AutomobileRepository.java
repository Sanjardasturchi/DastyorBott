package com.example.DastyorBot.repository;

import com.example.DastyorBot.entity.AutomobileEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface AutomobileRepository extends CrudRepository<AutomobileEntity,Integer>, PagingAndSortingRepository<AutomobileEntity,Integer> {
}
