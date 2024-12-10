package org.dre0065.Repository;

import org.dre0065.Model.Event;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.*;
import java.util.*;

@Repository
public interface EventRepository extends JpaRepository<Event, Integer>
{
    boolean existsByEventNameAndStartOfEvent(String eventName, Date startOfEvent);
    List<Event> findByEventName(String eventName);
}