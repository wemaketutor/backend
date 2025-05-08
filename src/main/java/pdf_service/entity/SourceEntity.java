package pdf_service.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

import jakarta.persistence.Table;

import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "Sources")
public class SourceEntity {

    @Id
    private Long id;

    private String title;
    private String description;

    private String body;

    public SourceEntity(Long id, String title, String description, String body) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.body = body;
    }
}
