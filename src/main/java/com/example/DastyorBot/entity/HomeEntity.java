package com.example.DastyorBot.entity;


import com.example.DastyorBot.enums.AcctiveStatus;
import com.example.DastyorBot.enums.SelectedPurchaseType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.LocalTime;

@Setter
@Getter
@Entity
@Table(name = "home")
public class HomeEntity {
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
    @Enumerated(EnumType.STRING)
    @Column(name = "select_pursaches_type")
    private SelectedPurchaseType selectedPurchaseType; // Rent or Sale
    private String city;  //shahar
    private String district; //tuman
    private Double price;
    @Column(name = "start_time")
    private LocalTime startTime;
    @Column(name = "end_time")
    private LocalTime endTime;
}
