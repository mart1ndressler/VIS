package org.dre0065.Repository;

import org.dre0065.Model.Event;
import org.dre0065.Model.Fight;
import org.dre0065.Model.WeightCategory;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.*;
import java.util.*;

@Repository
public interface FightRepository extends JpaRepository<Fight, Integer>
{
    Optional<Fight> findByDateAndWeightCategoryAndEvent(Date date, WeightCategory weightCategory, Event event);
}