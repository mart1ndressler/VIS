package org.dre0065.Repository;

import org.dre0065.Model.WeightCategory;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.*;

@Repository
public interface WeightCategoryRepository extends JpaRepository<WeightCategory, Integer>
{
    boolean existsByName(String name);
    WeightCategory findByName(String name);
}