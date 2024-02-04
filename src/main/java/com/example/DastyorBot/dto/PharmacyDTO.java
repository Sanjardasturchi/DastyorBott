package com.example.DastyorBot.dto;

import com.example.DastyorBot.enums.AcctiveStatus;
import com.example.DastyorBot.enums.PharmacyType;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.LocalTime;
@Setter
@Getter
public class PharmacyDTO {
    private Integer id;
    private String pharmacyType;
    private LocalTime startTime;
    private LocalTime endTime;
    private String username;
    private String phone;
    private String pharmacyName;
    private String description;
    private Double latitude;
    private Double longitude;
    private AcctiveStatus acctiveStatus;
    private LocalDateTime createdDateTime;
}
