package com.tutoras.tutoras.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tutoras.tutoras.entity.MaterialEntity;
import com.tutoras.tutoras.entity.MaterialVisibleByUserEntity;
import com.tutoras.tutoras.entity.UserEntity;

@Repository
public interface MaterialVisibleByUserRepository extends JpaRepository<MaterialVisibleByUserEntity, Long> {
    List<MaterialVisibleByUserEntity> findByMaterial(MaterialEntity material);
    List<MaterialVisibleByUserEntity> findByUser(UserEntity user);
    void deleteByMaterialAndUser(MaterialEntity material, UserEntity user);
} 