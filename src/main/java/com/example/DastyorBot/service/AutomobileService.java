package com.example.DastyorBot.service;

import com.example.DastyorBot.constant.AutoConstant;
import com.example.DastyorBot.dto.AutomobileDTO;
import com.example.DastyorBot.entity.AutomobileEntity;
import com.example.DastyorBot.entity.mediaEntity.AutoMediaEntity;
import com.example.DastyorBot.enums.AcctiveStatus;
import com.example.DastyorBot.enums.CarType;
import com.example.DastyorBot.enums.SelectedPurchaseType;
import com.example.DastyorBot.repository.AutomobileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

@Service
public class AutomobileService {
    @Autowired
    private AutomobileRepository avtoRepository;
    @Autowired
    private ProfileService profileService;

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

    private List<AutomobileDTO> toDTOListFromList(List<AutomobileEntity> entities) {
        List<AutomobileDTO> dtos = new LinkedList<>();
        for (AutomobileEntity entity : entities) {
            dtos.add(toDTO(entity));
        }
        return dtos;
    }

    private List<AutomobileDTO> toDTOListFromIterable(Iterable<AutomobileEntity> entities) {
        List<AutomobileDTO> dtos = new LinkedList<>();
        for (AutomobileEntity entity : entities) {
            dtos.add(toDTO(entity));
        }
        return dtos;
    }

    private AutomobileDTO toDTO(AutomobileEntity entity) {
        AutomobileDTO dto = new AutomobileDTO();
        dto.setId(entity.getId());
        dto.setAcctiveStatus(entity.getAcctiveStatus());
        dto.setPhone(entity.getPhone());
        dto.setUsername(entity.getUsername());
        dto.setDescription(entity.getDescription());
        dto.setCreatedDateTime(entity.getCreatedDateTime());
        dto.setLatitude(entity.getLatitude());
        dto.setLongitude(entity.getLongitude());
        dto.setSalaryType(entity.getSalaryType());
        dto.setCity(entity.getCity());
        dto.setDistrict(entity.getDistrict());
        dto.setBrandName(entity.getBrandName());
        dto.setModel(entity.getModel());
        dto.setCarType(entity.getCarType());
        dto.setPrice(entity.getPrice());
        dto.setStartTime(entity.getStartTime());
        dto.setEndTime(entity.getEndTime());
        dto.setAdCreatorChatId(entity.getAdCreatorChatId());
        return dto;
    }

    public AutomobileDTO createAuto(String profileChatId, AutomobileDTO dto) {
        AutomobileEntity entity = new AutomobileEntity();
        entity.setAdCreatorChatId(profileChatId);
        entity.setAcctiveStatus(AcctiveStatus.BLOCK);
        entity.setCreatedDateTime(LocalDateTime.now());
        // todo (entity=dto)
        avtoRepository.save(entity);
        dto.setId(entity.getId());
        dto.setAdCreatorChatId(profileChatId);
        dto.setCreatedDateTime(entity.getCreatedDateTime());
        profileService.changingElementId(entity.getId(), profileChatId);
        return dto;
    }

    public void delete(Integer id) {
        avtoRepository.deleteById(id);
    }

    //Updating

    public void setCity(Integer changingElementId, String city) {
        Optional<AutomobileEntity> entity = avtoRepository.findById(changingElementId);
        if (entity.isEmpty()) {
            System.out.println("Nomalum Id kiritildi Manzil(AutoService (SetCity))");
            return;
        }
        AutomobileEntity entity1 = entity.get();
        entity1.setCity(city);
        avtoRepository.save(entity1);
    }

    public void setBrand(Integer changingElementId, String brandName) {
        Optional<AutomobileEntity> entity = avtoRepository.findById(changingElementId);
        if (entity.isEmpty()) {
            System.out.println("Nomalum Id kiritildi Manzil(AutoService (setBrand))");
            return;
        }
        AutomobileEntity entity1 = entity.get();
        entity1.setBrandName(brandName);
        avtoRepository.save(entity1);
    }

    public void setModel(Integer changingElementId, String model) {
        Optional<AutomobileEntity> entity = avtoRepository.findById(changingElementId);
        if (entity.isEmpty()) {
            System.out.println("Nomalum Id kiritildi Manzil(AutoService (setModel))");
            return;
        }
        AutomobileEntity entity1 = entity.get();
        entity1.setModel(model);
        avtoRepository.save(entity1);
    }

    public Boolean setPrice(Integer changingElementId, String text) {
        try {
            Double price = Double.valueOf(GeneralService.checkMoneyFromTheString(text));
            Optional<AutomobileEntity> entity = avtoRepository.findById(changingElementId);
            if (entity.isEmpty()) {
                System.out.println("Nomalum Id kiritildi Manzil(AutoService (setPrice))");
                return false;
            }
            AutomobileEntity entity1 = entity.get();
            entity1.setPrice(price);
            avtoRepository.save(entity1);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public void setPhone(Integer changingElementId, String phone) {
        Optional<AutomobileEntity> entity = avtoRepository.findById(changingElementId);
        if (entity.isEmpty()) {
            System.out.println("Nomalum Id kiritildi Manzil(AutoService (setPhone))");
            return;
        }
        AutomobileEntity entity1 = entity.get();
        entity1.setPhone(phone);
        avtoRepository.save(entity1);
    }

    public void setUsername(Integer changingElementId, String username) {
        Optional<AutomobileEntity> entity = avtoRepository.findById(changingElementId);
        if (entity.isEmpty()) {
            System.out.println("Nomalum Id kiritildi Manzil(AutoService (setUsername))");
            return;
        }
        AutomobileEntity entity1 = entity.get();
        entity1.setUsername(username);
        avtoRepository.save(entity1);
    }

    public void setDescription(Integer changingElementId, String description) {
        Optional<AutomobileEntity> entity = avtoRepository.findById(changingElementId);
        if (entity.isEmpty()) {
            System.out.println("Nomalum Id kiritildi Manzil(AutoService (setDescription))");
            return;
        }
        AutomobileEntity entity1 = entity.get();
        entity1.setDescription(description);
        avtoRepository.save(entity1);
    }

    public void setCarType(Integer changingElementId, String carType) {
        Optional<AutomobileEntity> entity = avtoRepository.findById(changingElementId);
        if (entity.isEmpty()) {
            System.out.println("Nomalum Id kiritildi Manzil(AutoService (setCarType))");
            return;
        }
        AutomobileEntity entity1 = entity.get();
        if (carType.equals(AutoConstant.CARS)) {
            entity1.setCarType(CarType.CAR);
        } else {
            entity1.setCarType(CarType.TRUCK);
        }
        avtoRepository.save(entity1);
    }

    public void setStartTime(Integer changingElementId, String time) {
        Optional<AutomobileEntity> entity = avtoRepository.findById(changingElementId);
        if (entity.isEmpty()) {
            System.out.println("Nomalum Id kiritildi Manzil(AutoService (setStartTime))");
            return;
        }
        AutomobileEntity entity1 = entity.get();
        entity1.setStartTime(LocalTime.parse(time));
        avtoRepository.save(entity1);
    }

    public void setEndTime(Integer changingElementId, String time) {
        Optional<AutomobileEntity> entity = avtoRepository.findById(changingElementId);
        if (entity.isEmpty()) {
            System.out.println("Nomalum Id kiritildi Manzil(AutoService (setEndTime))");
            return;
        }
        AutomobileEntity entity1 = entity.get();
        entity1.setEndTime(LocalTime.parse(time));
        avtoRepository.save(entity1);
    }

    public void setDistrict(Integer changingElementId, String district) {
        Optional<AutomobileEntity> entity = avtoRepository.findById(changingElementId);
        if (entity.isEmpty()) {
            System.out.println("Nomalum Id kiritildi Manzil(AutoService (setDistrict))");
            return;
        }
        AutomobileEntity entity1 = entity.get();
        entity1.setDistrict(district);
        avtoRepository.save(entity1);
    }

    public void setSalaryType(Integer id, String salaryType) {
        Optional<AutomobileEntity> entity = avtoRepository.findById(id);
        if (entity.isEmpty()) {
            System.out.println("Nomalum Id kiritildi Manzil(AutoService (setSalaryType))");
            return;
        }
        AutomobileEntity entity1 = entity.get();
        entity1.setSalaryType(SelectedPurchaseType.valueOf(salaryType));
        avtoRepository.save(entity1);
    }

    public String changeAcctiveStatus(String id, AcctiveStatus acctiveStatus) {
        try {
            String iD = GeneralService.checkMoneyFromTheString(id);
            Integer autoId=Integer.valueOf(iD);
            Optional<AutomobileEntity> auto = avtoRepository.findById(autoId);
            if (auto.isEmpty()) {
                return "Avtomobile id si noto'g'ri kiritildi!";
            }
            AutomobileEntity entity = auto.get();
            entity.setAcctiveStatus(acctiveStatus);
            avtoRepository.save(entity);
            return "Ma'lumot muvaffaqiyatli saqlandi!";
        }catch (Exception e){
            return "Malumot kiritishda xatolik, iltimos e'tiborli bo'ing";
        }
    }

    public AutomobileDTO getById(Integer autoId) {
        Optional<AutomobileEntity> entity = avtoRepository.findById(autoId);
        return entity.map(this::toDTO).orElse(null);
    }
}
