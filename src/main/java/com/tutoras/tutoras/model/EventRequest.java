package com.tutoras.tutoras.model;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Getter;
import lombok.extern.jackson.Jacksonized;

@Getter
@Builder
@Jacksonized
public class EventRequest {
    private LocalDateTime date;
    private LocalDateTime date_created;
    private String name;
    private String description;
    private Long event_id;
    private Long getingPersonId;
}
