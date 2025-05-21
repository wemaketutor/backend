package com.tutoras.tutoras.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Table;
import jakarta.persistence.Column;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "Sources")
public class SourceEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String description;

    @Column(columnDefinition = "TEXT")
    private String body;

    public SourceEntity(Long id, String title, String description, String body) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.body = body;
    }
    
    public SourceEntity(String title, String description, String body) {
        this.title = title;
        this.description = description;
        this.body = body;
    }
}
