package com.rhododendron.dashboardisep;


import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.Optional;
import java.util.*;


@RequestMapping(path="/groups")
@RestController
public class GroupControler {
    @Autowired
    private GroupRepository groupRepository;
    @Autowired
    private StudentRepository studentRepository;
    @CrossOrigin(origins = "*")
    @PostMapping(path="/add",produces = {MediaType.APPLICATION_JSON_VALUE}) // Map ONLY GET Requests
    public @ResponseBody StudentGroup addNewGroup(@RequestBody StudentGroup group) {
        groupRepository.save(group);
        return group;
    }
    @CrossOrigin(origins = "*")
    @GetMapping(path="/all")
    public @ResponseBody Iterable<StudentGroup> getAllGroups() {
        return groupRepository.findAll();
    }
    @CrossOrigin(origins = "*")
    @GetMapping(path="/one/{id}")
    public @ResponseBody Optional<StudentGroup> getOneGroup(@PathVariable(value="id") String id) {
        return groupRepository.findById(Long.valueOf(Integer.parseInt(id)));
    }


    @CrossOrigin(origins = "*")
    @PostMapping(path="/associateStudentToGroup/{group_id}",produces = {MediaType.APPLICATION_JSON_VALUE}) // Map ONLY GET Requests
    public @ResponseBody StudentGroup associateStudentToGroup(@RequestBody Map<String, Object> payload,@PathVariable(value="group_id") String id) {
        StudentGroup group = groupRepository.findById(Long.valueOf(Integer.parseInt(id))).get();
        int userId = (Integer)payload.get("userId");
        Student user = studentRepository.findById(Long.valueOf(userId)).get();
        user.setGroup(group);
        studentRepository.save(user);
        group.addStudent(user);
        groupRepository.save(group);
        return group;
    }


}