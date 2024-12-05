package org.dre0065.Repository;

import org.dre0065.Model.WeightCategory;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.*;
import java.util.*;

@Repository
public interface WeightCategoryRepository extends JpaRepository<WeightCategory, Integer>
{
    boolean existsByName(String name);
    Optional<WeightCategory> findByName(String name);
}