package pdf_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pdf_service.entity.MaterialEntity;
import pdf_service.entity.SourcesEntity;

import java.util.ArrayList;
import java.util.List;


public interface CreateMaterialRepository extends JpaRepository<MaterialEntity, Long> {
    // Находит материалы по списку ID
    @Query("SELECT m FROM Sources m WHERE m.id IN :id")
    ArrayList<SourcesEntity> getAllByIds(@Param("id") List<Long> ids);
} //done