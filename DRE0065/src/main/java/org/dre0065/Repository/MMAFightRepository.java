package org.dre0065.Repository;

import org.dre0065.Model.MMAFight;
import org.dre0065.Model.Fight;
import org.dre0065.Model.MMAFighter;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.*;
import java.util.*;

@Repository
public interface MMAFightRepository extends JpaRepository<MMAFight, Integer>
{
    Optional<MMAFight> findByFightAndFighter(Fight fight, MMAFighter fighter);
}