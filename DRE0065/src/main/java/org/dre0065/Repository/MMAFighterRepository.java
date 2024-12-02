package org.dre0065.Repository;

import org.dre0065.Model.MMAFighter;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.*;
import java.util.*;

@Repository
public interface MMAFighterRepository extends JpaRepository<MMAFighter, Integer>
{
    boolean existsByFirstNameAndLastName(String firstName, String lastName);
    Optional<MMAFighter> findByFirstNameAndLastName(String firstName, String lastName);
}