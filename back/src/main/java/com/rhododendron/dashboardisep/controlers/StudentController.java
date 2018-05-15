package com.rhododendron.dashboardisep;


import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.Optional;
import java.util.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.core.context.SecurityContextHolder;
import java.lang.*;


@RequestMapping(path="/users")
@RestController
public class StudentController {
    @Autowired
    private StudentRepository studentRepository;
    @Autowired
    private GroupRepository groupRepository;
    @Autowired
    private PhaseRepository phaseRepository;
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    public StudentController(BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }


    @CrossOrigin(origins = "*")
    @PostMapping(path="/add",produces = {MediaType.APPLICATION_JSON_VALUE}) // Map ONLY GET Requests
    public @ResponseBody Student addNewUser(@RequestBody Student student) {
        try {
            Student registrar = studentRepository.findByEmail(SecurityContextHolder.getContext().getAuthentication()
                    .getPrincipal().toString()).setPassword(null);
        }
        catch (Exception e){
            student.setRole(0);
        }
        if (!student.getEmail().endsWith("@isep.fr")){
            throw new RuntimeException("bad email");
        }
        Student existingSt = studentRepository.findByEmail(student.getEmail());
        if (existingSt!=null){
            throw new RuntimeException("already existing user");
        }
        student.setPassword(bCryptPasswordEncoder.encode(student.getPassword()));
        student.setUsername(student.getEmail());
        studentRepository.save(student);
        return student;
    }

    @CrossOrigin(origins = "*")
    @GetMapping(path="/me")
    public @ResponseBody Student getMeUser() {
        Student student = studentRepository.findByEmail(SecurityContextHolder.getContext().getAuthentication()
                .getPrincipal().toString());
        return student;
    }
    @CrossOrigin(origins = "*")
    @GetMapping(path="/myGroupPhases")
    public @ResponseBody Map<String, Object> getMyPhases() {
        Student student = studentRepository.findByEmail(SecurityContextHolder.getContext().getAuthentication()
                .getPrincipal().toString());
        StudentGroup group = student.getGroup();
        List<Phase> phases = phaseRepository.findByGroup(group);
        Map map = new HashMap();
        map.put("status",true);
        map.put("phases",phases);
        map.put("groupId",group.getId());
        map.put("groupName",group.getName());
        return map;
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
    @CrossOrigin(origins = "*")
    @DeleteMapping(path="/one/{id}")
    public @ResponseBody Map<String, Object> deleteOneUser(@PathVariable(value="id") String id) {
        Student student = studentRepository.findById(Long.valueOf(Integer.parseInt(id))).get();
        student.getIstutorof().forEach(item->{
            item.setTutor(null);
        });
        studentRepository.delete(student);
        Map map = new HashMap();
        map.put("status",true);
        return map;
    }
}