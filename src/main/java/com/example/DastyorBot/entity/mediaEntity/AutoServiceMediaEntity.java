package com.example.DastyorBot.entity.mediaEntity;

import com.example.DastyorBot.enums.MediaType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(name = "auto_service_media")
public class AutoServiceMediaEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name = "f_id")
    private String fId;
    @Column(name = "auto_service_id")
    private Integer autoServiceId;
    @Column(name = "media_type")
    private MediaType mediaType;
}
