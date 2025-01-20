package com.tutoras.tutoras.entity;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "events")
public class EventEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id_1", nullable = false)
    private UserEntity user;

    private LocalDateTime date;

    private String description;

    private String name;

    private LocalDateTime date_created;

    private LocalDateTime update_at;

    @ManyToOne
    @JoinColumn(name = "user_id_2", nullable = false)
    private UserEntity folowed_user;

    private LocalDateTime duration;

    @SuppressWarnings("unused")
    private EventEntity () {}

    public EventEntity(Long id, LocalDateTime date, LocalDateTime date_created, String name, UserEntity user, String description, UserEntity folowed_user, LocalDateTime duration) {
        this.id = id;
        this.date = date;
        this.date_created = date_created;
        this.name = name;
        this.user = user;
        this.update_at = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);;
        this.description = description;
        this.folowed_user = folowed_user;
        this.duration = duration;
    }

    public EventEntity(LocalDateTime date, LocalDateTime date_created, String name, UserEntity user, String description, UserEntity folowed_user, LocalDateTime duration) {
        this.date = date;
        this.date_created = date_created;
        this.name = name;
        this.user = user;
        this.update_at = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);;
        this.description = description;
        this.folowed_user = folowed_user;
        this.duration = duration;
    }
}
