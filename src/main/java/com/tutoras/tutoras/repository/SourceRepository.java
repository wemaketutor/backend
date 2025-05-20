package com.tutoras.tutoras.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.tutoras.tutoras.entity.SourceEntity;

import java.util.List;


public interface SourceRepository extends JpaRepository<SourceEntity, Long> {
    @Query("SELECT s FROM SourceEntity s WHERE s.id IN :ids")
    List<SourceEntity> getAllByIds(@Param("ids") List<Long> ids);
}