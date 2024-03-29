package com.example.DastyorBot.entity;

import com.example.DastyorBot.enums.AcctiveStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.LocalTime;
@Setter
@Getter
@Entity
@Table(name = "hospital")
public class HospitalEntity {
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
    @Column(name = "hospital_name")
    private String hospitalName;
    @Column(name = "start_time")
    private LocalTime startTime;
    @Column(name = "end_time")
    private LocalTime endTime;
    @Column(name = "types_of_available_treatment")
    private String typesOfAvailableTreatment;  // (mavjud davolash turlari)
}