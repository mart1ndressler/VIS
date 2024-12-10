package org.dre0065.Event;

import org.springframework.context.ApplicationEvent;

public class EntityAddedEvent extends ApplicationEvent
{
    private final Object entity;

    public EntityAddedEvent(Object source, Object entity) {
        super(source);
        this.entity = entity;
    }

    public Object getEntity() {
        return entity;
    }
}
