package pdf_service.entity;

import com.tutoras.tutoras.entity.TeacherEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;

import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.JoinTable;

import lombok.Getter;
import lombok.Setter;


import jakarta.persistence.*;
import lombok.*;


@Entity
@Table(name = "materials")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MaterialEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String subject;

    private String description;

    @Column(name = "file_url", nullable = false)
    private String fileUrl;

    @Column(name = "is_public", nullable = false)
    private Boolean isPublic;

    @Column(name = "teacher_id", nullable = false)
    private Long teacherId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "teacher_id", nullable = false, insertable = false, updatable = false)
    private TeacherEntity teacher;

//    @OneToMany(mappedBy = "material", cascade = CascadeType.ALL, orphanRemoval = true)
//    private Set<MaterialVisibleByUserEntity> visibleToUsers;

    public MaterialEntity(SourceEntity source, Long teacherId, String fileUrl) {
        this.title = source.getTitle();
        this.description = source.getDescription();
        this.fileUrl = fileUrl;
        this.isPublic = false;
        this.teacherId = teacherId;
    }

}
