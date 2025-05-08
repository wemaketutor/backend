package pdf_service.entity;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import com.tutoras.tutoras.entity.StudentEntity;
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

@Entity
@Getter
@Setter
@Table(name = "materials")
public class MaterialEntity {

    @Id
    private Long id;

    private String title;
    private String description;
    private String fileUrl;
    private boolean isPublic;

    @ManyToOne
    @JoinColumn(name = "teacher_id", nullable = false)
    @JsonIgnore
    private TeacherEntity teacher;

    @ManyToMany
    @JoinTable(
            name = "MaterialsVisibleByUsers",
            joinColumns = @JoinColumn(name = "material_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    @JsonIgnore
    private List<StudentEntity> students = new ArrayList<>();

    public MaterialEntity(Long id, String title, String description, String fileUrl,
                          boolean isPublic, TeacherEntity teacher, List<StudentEntity> students) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.fileUrl = fileUrl;
        this.isPublic = isPublic;
        this.teacher = teacher;
        this.students = students;
    }
}
