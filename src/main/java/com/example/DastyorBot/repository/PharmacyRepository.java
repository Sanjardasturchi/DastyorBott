package com.example.DastyorBot.repository;

import com.example.DastyorBot.entity.PharmacyEntity;
import com.example.DastyorBot.enums.AcctiveStatus;
import com.example.DastyorBot.enums.PharmacyType;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.time.LocalTime;

public interface PharmacyRepository extends CrudRepository<PharmacyEntity,Integer> {
    @Transactional
    @Modifying
    @Query("update PharmacyEntity set startTime=?1 where id=?2")
    void setStartTime(LocalTime data, Integer changingElementId);

    @Transactional
    @Modifying
    @Query("update PharmacyEntity set endTime=?1 where id=?2")
    void setEndTime(LocalTime data, Integer changingElementId);

    @Transactional
    @Modifying
    @Query("update PharmacyEntity set pharmacyType=?1 where id=?2")
    void setType(String data, Integer changingElementId);

    @Transactional
    @Modifying
    @Query("update PharmacyEntity set username=?2 where id=?1")
    void setUsername(Integer id, String username);

    @Transactional
    @Modifying
    @Query("update PharmacyEntity set phone=?1 where id=?2")
    void setPhone(String phone, Integer id);

    @Transactional
    @Modifying
    @Query("update PharmacyEntity set pharmacyName=?1 where id=?2")
    void setName(String name, Integer id);

    @Transactional
    @Modifying
    @Query("update PharmacyEntity set latitude=?1,longitude=?2 where id=?3")
    void setLocation(Double latitude, Double longitude, Integer id);

    @Transactional
    @Modifying
    @Query("update PharmacyEntity set description=?1 where id=?2")
    void setDescription(String description, Integer id);

    @Transactional
    @Modifying
    @Query("update PharmacyEntity set acctiveStatus=?2 where id=?1")
    void changeStatus(Integer id, AcctiveStatus status);
}
