package com.example.DastyorBot.entity;


import com.example.DastyorBot.enums.AcctiveStatus;
import com.example.DastyorBot.enums.CarType;
import com.example.DastyorBot.enums.SelectedPurchaseType;
import jakarta.persistence.*;
import jakarta.ws.rs.DefaultValue;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.LocalTime;

@Setter
@Getter
@Entity
@Table(name = "auto")
public class  AutomobileEntity{
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
    @DefaultValue("now")
    private LocalDateTime createdDateTime;
    private Double latitude;
    private Double longitude;
    @Enumerated(EnumType.STRING)
    private SelectedPurchaseType salaryType; // Rent or Sale
    private String city;  //shahar
    private String district; //tuman
    @Column(name = "brand_name")
    private String brandName;
    private String model;
    @Enumerated(EnumType.STRING)
    @Column(name = "car_type")
    private CarType carType;
    private Double price;
    @Column(name = "start_time")
    private LocalTime startTime;
    @Column(name = "end_time")
    private LocalTime endTime;
    @Column(name = "ad_creator_chat_id")
    private String adCreatorChatId;
}
