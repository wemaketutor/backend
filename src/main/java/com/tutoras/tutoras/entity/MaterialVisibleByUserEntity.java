package com.tutoras.tutoras.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "materials_visible_by_users")
public class MaterialVisibleByUserEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "material_id")
    private MaterialEntity material;
    
    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity user;
    
    @SuppressWarnings("unused")
    private MaterialVisibleByUserEntity() {}
    
    public MaterialVisibleByUserEntity(MaterialEntity material, UserEntity user) {
        this.material = material;
        this.user = user;
    }
}