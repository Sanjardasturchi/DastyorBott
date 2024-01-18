package com.example.DastyorBot.dto;

import com.example.DastyorBot.enums.AcctiveStatus;
import com.example.DastyorBot.enums.CarType;
import com.example.DastyorBot.enums.SelectedPurchaseType;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.LocalTime;
@Setter
@Getter
public class AutomobileDTO {
    private Integer id;
    private AcctiveStatus acctiveStatus;
    private String phone;
    private String username;
    private String description;
    private LocalDateTime createdDateTime;
    private Double latitude;
    private Double longitude;
    private SelectedPurchaseType type; // Rent or Sale
    private String city;  //shahar
    private String district; //tuman
    private String brandName;
    private String model;
    private CarType carType;
    private Double price;
    private LocalTime startTime;
    private LocalTime endTime;
}
