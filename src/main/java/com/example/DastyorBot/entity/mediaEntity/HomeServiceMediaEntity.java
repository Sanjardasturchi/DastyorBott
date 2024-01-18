package com.example.DastyorBot.entity.mediaEntity;

import com.example.DastyorBot.enums.MediaType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(name = "home_service_media")
public class HomeServiceMediaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name = "f_id")
    private String fId;
    @Column(name = "home_service_id")
    private Integer homeId;
    @Column(name = "media_type")
    private MediaType mediaType;
}
