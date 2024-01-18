package com.example.DastyorBot.service;


import com.example.DastyorBot.constant.CommonConstants;
import com.example.DastyorBot.dto.ProfileDTO;
import com.example.DastyorBot.entity.ProfileEntity;
import com.example.DastyorBot.enums.AcctiveStatus;
import com.example.DastyorBot.enums.ProfileRole;
import com.example.DastyorBot.enums.SelectedPurchaseType;
import com.example.DastyorBot.repository.ProfileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class ProfileService {
    @Autowired
    private ProfileRepository profileRepository;
    public ProfileDTO getByChatId(String chatId) {
        return toDTO(profileRepository.findByChatId(chatId));
    }

    public ProfileDTO save(ProfileDTO dto) {
        ProfileEntity entity = new ProfileEntity();
        entity.setName(dto.getName());
        entity.setSurname(dto.getSurname());
        entity.setPhone(dto.getPhone());
        entity.setAcctiveStatus(AcctiveStatus.ACCTIVE);
        entity.setCurrentStep(CommonConstants.MENU);
        entity.setRole(ProfileRole.USER);
        entity.setCreatedDateTime(dto.getCreatedDateTime());
        entity.setUsername(dto.getUsername());
        entity.setChatId(dto.getChatId());

        profileRepository.save(entity);
        dto.setId(entity.getId());
        dto.setCreatedDateTime(entity.getCreatedDateTime());
        return dto;
    }

    public void changeStep(String chatId, String step) {
        profileRepository.changeStep(step,chatId);
    }
    public void changingElementId(Integer id, String chatId) {
        profileRepository.changingElementId(id,chatId);
    }
    private ProfileDTO toDTO(ProfileEntity entity){
        if (entity==null){
            return null;
        }
        ProfileDTO dto=new ProfileDTO();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setSurname(entity.getSurname());
        dto.setUsername(entity.getUsername());
        dto.setPhone(entity.getPhone());
        dto.setRole(entity.getRole());
        dto.setAcctiveStatus(entity.getAcctiveStatus());
        dto.setChangingElementId(entity.getChangingElementId());
        dto.setChatId(entity.getChatId());
        dto.setCreatedDateTime(entity.getCreatedDateTime());
        dto.setCurrentStep(entity.getCurrentStep());
        dto.setLatitude(entity.getLatitude());
        dto.setLongitude(entity.getLongitude());
        dto.setSelectedPurchaseType(entity.getSelectedPurchaseType());
        return dto;
    }

    public void changePurchaseType(SelectedPurchaseType selectedPurchaseType, String chatId) {
        profileRepository.changePurchaseType(selectedPurchaseType,chatId);
    }
}
