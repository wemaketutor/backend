package pdf_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pdf_service.entity.MaterialEntity;

import java.util.ArrayList;
import java.util.List;


public interface CreateMaterialRepository extends JpaRepository<MaterialEntity, Long> {
    // Находит материалы по списку ID
    @Query("SELECT m FROM MaterialEntity m WHERE m.id IN :ids")
    ArrayList<MaterialEntity> getAllByIds(@Param("ids") List<Long> ids);
} //done