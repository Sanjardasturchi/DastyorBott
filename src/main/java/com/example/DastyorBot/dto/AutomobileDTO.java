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
    private String city;  //shahar
    private CarType carType;
    private SelectedPurchaseType salaryType; // Rent or Sale
    private String brandName;
    private String model;
    private Double price;
    private String phone;
    private String username;
    private String description;
    private String district; //tuman (50/50)
    private LocalTime startTime;
    private LocalTime endTime;
    private Double latitude;
    private Double longitude;
    private LocalDateTime createdDateTime;
    private AcctiveStatus acctiveStatus;
    private String adCreatorChatId;
}
