package com.rhododendron.dashboardisep;


import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.Optional;
import java.util.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;


@RequestMapping(path="/users")
@RestController
public class StudentController {
    @Autowired
    private StudentRepository studentRepository;
    @Autowired
    private GroupRepository groupRepository;
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    public StudentController(BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }


    @CrossOrigin(origins = "*")
    @PostMapping(path="/add",produces = {MediaType.APPLICATION_JSON_VALUE}) // Map ONLY GET Requests
    public @ResponseBody Student addNewUser(@RequestBody Student student) {
        student.setPassword(bCryptPasswordEncoder.encode(student.getPassword()));
        studentRepository.save(student);
        return student;
    }
    @CrossOrigin(origins = "*")
    @GetMapping(path="/all")
    public @ResponseBody Iterable<Student> getAllUsers() {
        return studentRepository.findAll();
    }
    @CrossOrigin(origins = "*")
    @GetMapping(path="/one/{id}")
    public @ResponseBody Optional<Student> getOneUser(@PathVariable(value="id") String id) {
        return studentRepository.findById(Long.valueOf(Integer.parseInt(id)));
    }
    @CrossOrigin(origins = "*")
    @PostMapping(path="/associateGroupToTutor/{tutor_id}",produces = {MediaType.APPLICATION_JSON_VALUE}) // Map ONLY GET Requests
    public @ResponseBody Student associateGroupToTutor(@RequestBody Map<String, Object> payload,@PathVariable(value="tutor_id") String id) {
        Student user = studentRepository.findById(Long.valueOf(Integer.parseInt(id))).get();
        int groupId = (Integer)payload.get("groupId");
        StudentGroup group = groupRepository.findById(Long.valueOf(groupId)).get();
        user.makeTutor(group);
        studentRepository.save(user);
        group.setTutor(user);
        groupRepository.save(group);
        return user;
    }
}