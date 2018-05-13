package com.rhododendron.dashboardisep;

import javax.persistence.*;
import java.util.*;
import com.fasterxml.jackson.annotation.JsonIgnore;


@Entity
public class Task {
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long id;

    private String name;

    private int start_date;

    private int end_date;

    @ManyToOne
    @JoinColumn(name="phase_id")
    private Phase phase;

    @ManyToMany
    @JoinColumn(name="students")
    private List<Student> students = new ArrayList<Student>();

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

    public int getStart_date() {
        return start_date;
    }

    public void setStart_date(int start_date) {
        this.start_date = start_date;
    }

    public int getEnd_date() {
        return end_date;
    }

    public void setEnd_date(int end_date) {
        this.end_date = end_date;
    }

    public Phase getPhase() {
        return phase;
    }

    public void setPhase(Phase phase) {
        this.phase = phase;
    }

    public List<Student> getStudents() {
        return students;
    }

    public void setStudents(List<Student> students) {
        this.students = students;
    }
}