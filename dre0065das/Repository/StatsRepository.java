package org.dre0065.Repository;

import org.dre0065.Model.MMAFighter;
import org.dre0065.Model.Stats;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.*;
import java.util.*;

@Repository
public interface StatsRepository extends JpaRepository<Stats, Integer>
{
    Optional<Stats> findByFighter(MMAFighter fighter);
}