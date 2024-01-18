package com.example.DastyorBot.dto;

import com.example.DastyorBot.enums.AcctiveStatus;
import com.example.DastyorBot.enums.ProfileRole;
import com.example.DastyorBot.enums.SelectedPurchaseType;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
@Setter
@Getter
public class ProfileDTO {
    private Integer id;
    private AcctiveStatus acctiveStatus;
    private String phone;
    private String username;
    private LocalDateTime createdDateTime;
    private Double latitude;
    private Double longitude;
    private String name;
    private String surname;
    private String chatId;
    private ProfileRole role;
    private String currentStep;
    private SelectedPurchaseType selectedPurchaseType;
    private Integer changingElementId;
}
