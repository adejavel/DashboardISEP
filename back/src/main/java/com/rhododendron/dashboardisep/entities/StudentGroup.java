package com.rhododendron.dashboardisep;

import javax.persistence.*;
import java.util.*;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
public class StudentGroup {
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long id;

    private String name;

    @OneToMany(mappedBy = "group")
    private List<Student> students = new ArrayList<Student>();

    @ManyToOne
    @JoinColumn(name="istutorof_id")
    private Student tutor;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @JsonIgnore
    public List<Student> getStudents() {
        return students;
    }

    public void addStudent(Student user){
        this.students.add(user);
    }

    public void setStudents(List<Student> students) {
        this.students = students;
    }

    public Student getTutor() {
        return tutor;
    }

    public void setTutor(Student tutor) {
        this.tutor = tutor;
    }
}