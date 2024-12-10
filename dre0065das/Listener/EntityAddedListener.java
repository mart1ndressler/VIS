package org.dre0065.Listener;

import org.dre0065.Event.EntityAddedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import jakarta.persistence.Id;
import java.lang.reflect.Field;

@Component
public class EntityAddedListener {

    @EventListener
    public void handleEntityAddedEvent(EntityAddedEvent event) {
        Object entity = event.getEntity();
        Class<?> entityClass = entity.getClass();
        String entityName = entityClass.getSimpleName();

        Integer entityId = null;

        for (Field field : entityClass.getDeclaredFields()) {
            if (field.isAnnotationPresent(Id.class)) {
                field.setAccessible(true);
                try {
                    Object idValue = field.get(entity);
                    if (idValue instanceof Integer) {
                        entityId = (Integer) idValue;
                    }
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
                break;
            }
        }

        if (entityId != null) {
            System.out.println("New " + entityName + " added with ID " + entityId);
        } else {
            System.out.println("New " + entityName + " added, but ID could not be determined");
        }
    }
}