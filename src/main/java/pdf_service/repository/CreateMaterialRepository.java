package pdf_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pdf_service.entity.MaterialEntity;
import pdf_service.entity.SourceEntity;

import java.util.ArrayList;
import java.util.List;


public interface CreateMaterialRepository extends JpaRepository<MaterialEntity, Long> {
    @Query("SELECT m FROM Sources m WHERE m.id IN :ids")
    List<SourceEntity> getAllByIds(@Param("ids") List<Long> ids);
}