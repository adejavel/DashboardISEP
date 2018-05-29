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
    @Autowired
    private PhaseRepository phaseRepository;
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
        try {
            return groupRepository.findById(Long.valueOf(Integer.parseInt(id)));
        }
        catch (Exception e){
            throw new RuntimeException("group not found");
        }

    }


    @CrossOrigin(origins = "*")
    @PostMapping(path="/associateStudentToGroup/{group_id}",produces = {MediaType.APPLICATION_JSON_VALUE}) // Map ONLY GET Requests
    public @ResponseBody StudentGroup associateStudentToGroup(@RequestBody Map<String, Object> payload,@PathVariable(value="group_id") String id) {

        try {
            StudentGroup group = groupRepository.findById(Long.valueOf(Integer.parseInt(id))).get();
            int userId = (Integer)payload.get("userId");
            Student user = studentRepository.findById(Long.valueOf(userId)).get();
            user.setGroup(group);
            studentRepository.save(user);
            group.addStudent(user);
            groupRepository.save(group);
            return group;
        }
        catch (Exception e){
            throw new RuntimeException("group or student not found");
        }
    }

    @CrossOrigin(origins = "*")
    @GetMapping(path="/removeStudentFromGroup/{id}",produces = {MediaType.APPLICATION_JSON_VALUE}) // Map ONLY GET Requests
    public @ResponseBody Student removeStudentFromGroup(@PathVariable(value="id") String id) {
        try {
            Student st = studentRepository.findById(Long.valueOf(Integer.parseInt(id))).get();
            st.setGroup(null);
            studentRepository.save(st);
            return st;
        }
        catch (Exception e){
            throw new RuntimeException("student not found");
        }
    }

    @CrossOrigin(origins = "*")
    @GetMapping(path="/getStudents/{group_id}",produces = {MediaType.APPLICATION_JSON_VALUE}) // Map ONLY GET Requests
    public @ResponseBody Map<String, Object> getStudentsByGroup(@PathVariable(value="group_id") String id) {
        try {
            StudentGroup group = groupRepository.findById(Long.valueOf(Integer.parseInt(id))).get();
            List<Student> students = studentRepository.findByGroup(group);
            Map map = new HashMap();
            map.put("students",students);
            map.put("groupId", group.getId());
            map.put("groupName",group.getName());
            return map;
        }
        catch (Exception e){
            throw new RuntimeException("group not found");
        }
    }

    @CrossOrigin(origins = "*")
    @GetMapping(path="/getPhases/{group_id}",produces = {MediaType.APPLICATION_JSON_VALUE}) // Map ONLY GET Requests
    public @ResponseBody Map<String, Object> getPhasesByGroup(@PathVariable(value="group_id") String id) {

        try {
            StudentGroup group = groupRepository.findById(Long.valueOf(Integer.parseInt(id))).get();
            List<Phase> phases = phaseRepository.findByGroup(group);
            Map map = new HashMap();
            map.put("phases",phases);
            map.put("groupId", group.getId());
            map.put("groupName",group.getName());
            return map;
        }
        catch (Exception e){
            throw new RuntimeException("group not found");
        }
    }

    @CrossOrigin(origins = "*")
    @DeleteMapping(path="/one/{id}")
    public @ResponseBody Map<String, Object> deleteOneGroup(@PathVariable(value="id") String id) {

        try {
            StudentGroup group = groupRepository.findById(Long.valueOf(Integer.parseInt(id))).get();
            group.getPhases().forEach(item->{
                item.setGroup(null);
            });
            groupRepository.delete(group);
            Map map = new HashMap();
            map.put("status",true);
            return map;
        }
        catch (Exception e){
            throw new RuntimeException("group not found");
        }
    }
    @CrossOrigin(origins = "*")
    @PutMapping(path="/modify/{id}")
    public @ResponseBody StudentGroup changeGroup(@RequestBody StudentGroup group,@PathVariable(value = "id") String id) {
        try {
            StudentGroup original = groupRepository.findById(Long.valueOf(Integer.parseInt(id))).get();
            original.setName(group.getName());
            groupRepository.save(original);
            return original;
        }
        catch (Exception e){
            throw new RuntimeException("group not found");
        }
    }

}