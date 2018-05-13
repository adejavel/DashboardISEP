package com.rhododendron.dashboardisep;

import org.springframework.data.repository.CrudRepository;
import java.util.*;

public interface StudentRepository extends CrudRepository<Student, Long> {
    List<Student> findByGroup(StudentGroup group);
    Student findByEmail(String email);
    Student findByUsername(String username);
}
