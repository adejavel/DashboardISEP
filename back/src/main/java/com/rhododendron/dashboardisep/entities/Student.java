package com.rhododendron.dashboardisep;

import javax.persistence.*;
import java.util.*;
import com.fasterxml.jackson.annotation.JsonIgnore;


@Entity
public class Student {
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long id;

    private String name;

    private String lastname;

    private int role=0;

    private String email;

    @ManyToOne
    @JoinColumn(name="group_id")
    private StudentGroup group ;

    @OneToMany(mappedBy = "tutor")
    private List<StudentGroup> istutorof = new ArrayList<StudentGroup>();

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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }


    public StudentGroup getGroup() {
        if (this.istutorof.size() ==0){
            return group;
        }
        else {
            return null;
        }

    }

    public void setGroup(StudentGroup group) {
        this.group = group;
    }

    @JsonIgnore
    public List<StudentGroup> getIstutorof() {
        return istutorof;
    }
    public void makeTutor(StudentGroup group){
        this.istutorof.add(group);
    }

    public void setIstutorof(List<StudentGroup> istutorof) {
        this.istutorof = istutorof;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public int getRole() {
        return role;
    }

    public void setRole(int role) {
        this.role = role;
    }
}