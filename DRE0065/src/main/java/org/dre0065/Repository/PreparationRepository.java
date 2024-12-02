package org.dre0065.Repository;

import org.dre0065.Model.Coach;
import org.dre0065.Model.MMAFighter;
import org.dre0065.Model.Preparation;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.*;
import java.util.*;

@Repository
public interface PreparationRepository extends JpaRepository<Preparation, Integer>
{
    Preparation findByStartOfPreparationAndEndOfPreparationAndFighterAndCoach(Date startOfPreparation, Date endOfPreparation, MMAFighter fighter, Coach coach);
}