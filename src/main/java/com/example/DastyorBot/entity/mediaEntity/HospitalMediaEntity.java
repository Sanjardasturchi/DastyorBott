package com.example.DastyorBot.entity.mediaEntity;

import com.example.DastyorBot.enums.MediaType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(name = "hospital_media")
public class HospitalMediaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name = "f_id")
    private String fId;
    @Column(name = "hospital_id")
    private Integer hospitalId;
    @Column(name = "media_type")
    private MediaType mediaType;
}
