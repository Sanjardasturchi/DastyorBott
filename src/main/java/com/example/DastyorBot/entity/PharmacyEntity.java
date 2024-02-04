package com.example.DastyorBot.entity;

import com.example.DastyorBot.constant.PharmacyConstants;
import com.example.DastyorBot.enums.AcctiveStatus;
import com.example.DastyorBot.enums.PharmacyType;
import jakarta.persistence.*;
import jakarta.ws.rs.DefaultValue;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.LocalTime;

@Setter
@Getter
@Entity
@Table(name = "pharmacy")
public class PharmacyEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Enumerated(EnumType.STRING)
    @Column(name = "acctive_status")
    private AcctiveStatus acctiveStatus;
    private String phone;
    private String username;
    private String description;
    @Column(name = "created_date_time")
    private LocalDateTime createdDateTime;
    private Double latitude;
    private Double longitude;
    @Column(name = "pharmacy_name")
    private String pharmacyName;
    @Column(name = "start_time")
    private LocalTime startTime;
    @Column(name = "end_time")
    private LocalTime endTime;
    @Column(name = "pharmacy_type")
    @DefaultValue(value = PharmacyConstants.PHARMACY_FOR_PERSON)
    private String pharmacyType;
}
