package com.example.DastyorBot.repository.mediaRepository;

import com.example.DastyorBot.entity.mediaEntity.AutoMediaEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface AutoMediaRepository extends CrudRepository<AutoMediaEntity,Integer> {
    List<AutoMediaEntity> findByCarId(Integer id);
}
