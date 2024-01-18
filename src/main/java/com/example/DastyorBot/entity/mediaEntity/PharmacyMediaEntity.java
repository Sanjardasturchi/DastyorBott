package com.example.DastyorBot.entity.mediaEntity;

import com.example.DastyorBot.enums.MediaType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(name = "pharmacy_media")
public class PharmacyMediaEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name = "f_id")
    private String fId;
    @Column(name = "pharmacy_id")
    private Integer pharmacyId;
    @Column(name = "media_type")
    private MediaType mediaType;
}
