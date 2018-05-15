package com.rhododendron.dashboardisep;

import javax.persistence.*;
import java.util.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;


@Entity
public class Student {
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long id;

    private String name;

    private String lastname;

    private int role=0;

    private String email;

    private String password;

    private String username;

    @ManyToOne
    @JoinColumn(name="group_id")
    private StudentGroup group ;

    @OneToMany(mappedBy = "tutor")
    private List<StudentGroup> istutorof = new ArrayList<StudentGroup>();

    @ManyToMany
    private List<Task> tasks = new ArrayList<Task>();

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

    @JsonIgnore
    public List<Task> getTasks() {
        return tasks;
    }

    public void setTasks(List<Task> tasks) {
        this.tasks = tasks;
    }

    public void addTask(Task task){
        this.tasks.add(task);
    }

    @JsonIgnore
    public String getPassword() {
        return password;
    }
    @JsonProperty
    public Student setPassword(String password) {
        this.password = password;
        return this;
    }

    public String getUsername(){
        return this.email;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void deleteTask(Task task){
        this.tasks.remove(task);
    }
}