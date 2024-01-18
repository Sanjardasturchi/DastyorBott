package com.example.DastyorBot.entity;

import com.example.DastyorBot.enums.AcctiveStatus;
import com.example.DastyorBot.enums.ProfileRole;
import com.example.DastyorBot.enums.SelectedPurchaseType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
@Entity
@Table(name = "profile")
public class ProfileEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Enumerated(EnumType.STRING)
    @Column(name = "acctive_status")
    private AcctiveStatus acctiveStatus;
    private String phone;
    private String username;
    @Column(name = "created_date_time")
    private LocalDateTime createdDateTime;
    private Double latitude;
    private Double longitude;
    private String name;
    private String surname;
    @Column(name = "chat_id")
    private String chatId;
    @Enumerated(EnumType.STRING)
    private ProfileRole role;
    @Column(name = "current_step")
    private String currentStep;
    @Enumerated(EnumType.STRING)
    @Column(name = "selected_purchase_type")
    private SelectedPurchaseType selectedPurchaseType;
    @Column(name = "changing_element_id")
    private Integer changingElementId;
}
