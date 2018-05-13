package com.rhododendron.dashboardisep;

import org.springframework.data.repository.CrudRepository;
import java.util.*;

public interface PhaseRepository extends CrudRepository<Phase, Long> {
    List<Phase> findByGroup(StudentGroup group);
}