package pdf_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pdf_service.entity.MaterialEntity;

import java.util.List;

public interface MaterialRepository extends JpaRepository<MaterialEntity, Long> {
}
