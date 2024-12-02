package org.dre0065.Repository;

import org.dre0065.Model.Coach;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.*;
import java.util.*;

@Repository
public interface CoachRepository extends JpaRepository<Coach, Integer>
{
    boolean existsByFirstNameAndLastName(String firstName, String lastName);
    Optional<Coach> findByFirstNameAndLastName(String firstName, String lastName);
}