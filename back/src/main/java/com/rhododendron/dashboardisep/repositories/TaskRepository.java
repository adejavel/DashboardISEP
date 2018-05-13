package com.rhododendron.dashboardisep;

import org.springframework.data.repository.CrudRepository;
import java.util.*;

public interface TaskRepository extends CrudRepository<Task, Long> {
    //List<Student> findByGroup(StudentGroup group);
}
