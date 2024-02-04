package com.example.DastyorBot.service;

import com.example.DastyorBot.dto.PharmacyDTO;
import com.example.DastyorBot.entity.PharmacyEntity;
import com.example.DastyorBot.entity.mediaEntity.PharmacyMediaEntity;
import com.example.DastyorBot.enums.AcctiveStatus;
import com.example.DastyorBot.enums.MediaType;
import com.example.DastyorBot.repository.PharmacyRepository;
import com.example.DastyorBot.repository.mediaRepository.PharmacyMediaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.LinkedList;
import java.util.List;

@Service
public class PharmacyService {
    @Autowired
    private PharmacyRepository pharmacyRepository;
    @Autowired
    private PharmacyMediaRepository pharmacyMediaRepository;
    public Integer creat() {
        PharmacyEntity entity=new PharmacyEntity();
        entity.setCreatedDateTime(LocalDateTime.now());
        entity.setAcctiveStatus(AcctiveStatus.BLOCK);
        pharmacyRepository.save(entity);
        return entity.getId();
    }

    public void setStartTime(String data, Integer changingElementId) {
        pharmacyRepository.setStartTime(LocalTime.parse(data),changingElementId);
    }

    public void setType(String data, Integer changingElementId) {
        pharmacyRepository.setType(data,changingElementId);
    }

    public void setEndTime(String data, Integer changingElementId) {
        pharmacyRepository.setEndTime(LocalTime.parse(data),changingElementId);
    }

    public void setUsername(Integer id, String username) {
        pharmacyRepository.setUsername(id,username);
    }

    public void setPhone(Integer id, String phone) {
        pharmacyRepository.setPhone(phone,id);
    }

    public void setName(Integer id, String name) {
        pharmacyRepository.setName(name,id);
    }

    public void setLocation(Integer id, Double latitude, Double longitude) {
        pharmacyRepository.setLocation(latitude,longitude,id);
    }

    public void setDescription(Integer id, String description) {
        pharmacyRepository.setDescription(description,id);
    }

    public PharmacyDTO findById(Integer id) {
        return toDTOFull(pharmacyRepository.findById(id).get());
    }
    private PharmacyDTO toDTOFull(PharmacyEntity entity){
        PharmacyDTO dto=new PharmacyDTO();
        dto.setId(entity.getId());
        dto.setPharmacyName(entity.getPharmacyName());
        dto.setPhone(entity.getPhone());
        dto.setUsername(entity.getUsername());
        dto.setLatitude(entity.getLatitude());
        dto.setLongitude(entity.getLongitude());
        dto.setPharmacyType(entity.getPharmacyType());
        dto.setDescription(entity.getDescription());
        dto.setAcctiveStatus(entity.getAcctiveStatus());
        dto.setStartTime(entity.getStartTime());
        dto.setEndTime(entity.getEndTime());
        dto.setCreatedDateTime(entity.getCreatedDateTime());
        return dto;
    }

    public void saveMedia(String fId, MediaType mediaType, Integer id) {
        PharmacyMediaEntity entity=new PharmacyMediaEntity();
        entity.setFId(fId);
        entity.setPharmacyId(id);
        entity.setMediaType(mediaType);
        pharmacyMediaRepository.save(entity);
    }

    public List<PharmacyDTO> getAll() {
        return toDTOList(pharmacyRepository.findAll());
    }

    private List<PharmacyDTO> toDTOList(Iterable<PharmacyEntity> all) {
        List<PharmacyDTO> dtos=new LinkedList<>();
        for (PharmacyEntity entity : all) {
            dtos.add(toDTOFull(entity));
        }
        return dtos;
    }

    public void changeStatus(Integer id, AcctiveStatus status) {
        pharmacyRepository.changeStatus(id,status);
    }
}
