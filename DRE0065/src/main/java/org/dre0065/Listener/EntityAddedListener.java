package org.dre0065.Listener;

import org.dre0065.Event.EntityAddedEvent;
import org.dre0065.Event.EntityOperationType;
import org.springframework.context.event.*;
import org.springframework.stereotype.*;
import jakarta.persistence.*;
import java.lang.reflect.*;

@Component
public class EntityAddedListener
{
    @EventListener
    public void handleEntityAddedEvent(EntityAddedEvent event)
    {
        Object entity = event.getEntity();
        EntityOperationType operationType = event.getOperationType();
        Class<?> entityClass = entity.getClass();
        String entityName = entityClass.getSimpleName();
        Integer entityId = null;

        for(Field field : entityClass.getDeclaredFields())
        {
            if(field.isAnnotationPresent(Id.class))
            {
                field.setAccessible(true);
                try
                {
                    Object idValue = field.get(entity);
                    if(idValue instanceof Integer) entityId = (Integer) idValue;
                    else if(idValue instanceof Long) entityId = ((Long) idValue).intValue();
                }
                catch(IllegalAccessException e) {e.printStackTrace();}
                break;
            }
        }

        String operationMessage;
        switch(operationType)
        {
            case CREATE:
                operationMessage = "created";
                break;
            case UPDATE:
                operationMessage = "updated";
                break;
            case DELETE:
                operationMessage = "deleted";
                break;
            default:
                operationMessage = "performed an unknown operation on";
        }

        if(entityId != null) System.out.println("Entity " + entityName + " with ID " + entityId + " has been " + operationMessage + ".");
        else System.out.println("Entity " + entityName + " has been " + operationMessage + ", but ID could not be determined.");
    }
}