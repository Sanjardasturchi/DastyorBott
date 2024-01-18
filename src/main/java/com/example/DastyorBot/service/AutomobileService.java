package com.example.DastyorBot.service;

import com.example.DastyorBot.dto.AutomobileDTO;
import com.example.DastyorBot.entity.AutomobileEntity;
import com.example.DastyorBot.repository.AutomobileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;

@Service
public class AutomobileService {
    @Autowired
    private AutomobileRepository avtoRepository;
    public List<AutomobileEntity> getAllAutoFrom0ToSum(Double summa) {
        Iterable<AutomobileEntity> allAvto = avtoRepository.findAll();
            List<AutomobileEntity> res = new LinkedList<>();
            for (AutomobileEntity avtomobile : allAvto) {
                if (avtomobile.getPrice() <= summa) {
                    res.add(avtomobile);
                }
            }
            return res;

    }

    public List<AutomobileDTO> getAll() {
        return toDTOListFromIterable(avtoRepository.findAll());
    }
    private List<AutomobileDTO> toDTOListFromList(List<AutomobileEntity> entities){
        List<AutomobileDTO> dtos=new LinkedList<>();
        for (AutomobileEntity entity : entities) {
            dtos.add(toDTO(entity));
        }
        return dtos;
    }
    private List<AutomobileDTO> toDTOListFromIterable(Iterable<AutomobileEntity> entities){
        List<AutomobileDTO> dtos=new LinkedList<>();
        for (AutomobileEntity entity : entities) {
            dtos.add(toDTO(entity));
        }
        return dtos;
    }
    private AutomobileDTO toDTO(AutomobileEntity entity){
        AutomobileDTO dto=new AutomobileDTO();
        dto.setId(entity.getId());
        dto.setAcctiveStatus(entity.getAcctiveStatus());
        dto.setPhone(entity.getPhone());
        dto.setUsername(entity.getUsername());
        dto.setDescription(entity.getDescription());
        dto.setCreatedDateTime(entity.getCreatedDateTime());
        dto.setLatitude(entity.getLatitude());
        dto.setLongitude(entity.getLongitude());
        dto.setType(entity.getType());
        dto.setCity(entity.getCity());
        dto.setDistrict(entity.getDistrict());
        dto.setBrandName(entity.getBrandName());
        dto.setModel(entity.getModel());
        dto.setCarType(entity.getCarType());
        dto.setPrice(entity.getPrice());
        dto.setStartTime(entity.getStartTime());
        dto.setEndTime(entity.getEndTime());
        return dto;
    }
}
