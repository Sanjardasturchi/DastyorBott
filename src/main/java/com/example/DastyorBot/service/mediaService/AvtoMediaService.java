package com.example.DastyorBot.service.mediaService;

import com.example.DastyorBot.entity.mediaEntity.AutoMediaEntity;
import com.example.DastyorBot.repository.mediaRepository.AutoMediaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AvtoMediaService {
    @Autowired
    private AutoMediaRepository autoMediaRepository;
    public Boolean save(AutoMediaEntity avtoFoto) {
        autoMediaRepository.save(avtoFoto);
        return true;
    }

    public List<AutoMediaEntity> getById(Integer id) {
        return autoMediaRepository.findByCarId(id);
    }
}
