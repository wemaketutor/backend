package com.tutoras.tutoras.model;

import java.util.List;

import com.tutoras.tutoras.entity.EventEntity;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class EventResponse {
    private List<EventEntity> ownerEvents;
    private List<EventEntity> followerEvents;

    public EventResponse(List<EventEntity> ownerEvents, List<EventEntity> followerEvents) {
        this.ownerEvents = ownerEvents;
        this.followerEvents = followerEvents;
    }
}
