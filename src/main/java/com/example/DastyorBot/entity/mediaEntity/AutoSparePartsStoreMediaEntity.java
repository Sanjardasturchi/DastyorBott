package com.example.DastyorBot.entity.mediaEntity;

import com.example.DastyorBot.enums.MediaType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(name = "auto_spare_parts_store_media")
public class AutoSparePartsStoreMediaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name = "f_id")
    private String fId;
    @Column(name = "auto_spare_parts_store_id")
    private Integer autoSparePartsStoreId;
    @Column(name = "media_type")
    private MediaType mediaType;
}
