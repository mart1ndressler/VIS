package org.dre0065.Event;

import org.springframework.context.*;

public class EntityAddedEvent extends ApplicationEvent
{
    private final Object entity;
    private final EntityOperationType operationType;

    public EntityAddedEvent(Object source, Object entity, EntityOperationType operationType)
    {
        super(source);
        this.entity = entity;
        this.operationType = operationType;
    }

    public Object getEntity() {return entity;}
    public EntityOperationType getOperationType() {return operationType;}
}