package com.example.DastyorBot.entity;

import com.example.DastyorBot.enums.AcctiveStatus;
import com.example.DastyorBot.enums.CarType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.LocalTime;

@Setter
@Getter
@Entity
@Table(name = "auto_service")
public class AutoServiceEntity{
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
    @Column(name = "type_of_car_serviced")
    private CarType typeOfCarServiced;
    @Column(name = "service_branch_name")
    private String serviceBranchName;
    @Column(name = "start_time")
    private LocalTime startTime;
    @Column(name = "end_time")
    private LocalTime endTime;
}
