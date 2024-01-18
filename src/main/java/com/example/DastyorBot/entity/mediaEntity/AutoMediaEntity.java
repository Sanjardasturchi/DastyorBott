package com.example.DastyorBot.entity.mediaEntity;


import com.example.DastyorBot.enums.MediaType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(name = "auto_media")
public class AutoMediaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name = "f_id")
    private String fId;
    @Column(name = "car_id")
    private Integer carId;
    @Column(name = "media_type")
    private MediaType mediaType;
}
