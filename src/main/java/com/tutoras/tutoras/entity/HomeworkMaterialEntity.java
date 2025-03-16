package com.tutoras.tutoras.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "homeworks_materials")
public class HomeworkMaterialEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "homework_id")
    private HomeworkEntity homework;
    
    @ManyToOne
    @JoinColumn(name = "material_id")
    private MaterialEntity material;
    
    @SuppressWarnings("unused")
    private HomeworkMaterialEntity() {}
    
    public HomeworkMaterialEntity(HomeworkEntity homework, MaterialEntity material) {
        this.homework = homework;
        this.material = material;
    }
} 