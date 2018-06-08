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
import it.ozimov.springboot.mail.model.Email;
import javax.mail.internet.InternetAddress;
import static com.google.common.collect.Lists.newArrayList;
import it.ozimov.springboot.mail.model.defaultimpl.DefaultEmail;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import java.time.*;

@RequestMapping(path="/users")
@RestController
public class StudentController {
    @Autowired
    private StudentRepository studentRepository;
    @Autowired
    private GroupRepository groupRepository;
    @Autowired
    private PhaseRepository phaseRepository;
    @Autowired
    private TaskRepository taskRepository;
    @Autowired
    private it.ozimov.springboot.mail.service.EmailService emailService;
    private ThreadPoolTaskScheduler taskScheduler;

    private BCryptPasswordEncoder bCryptPasswordEncoder;

    public StudentController(BCryptPasswordEncoder bCryptPasswordEncoder,ThreadPoolTaskScheduler sch) {
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.taskScheduler = sch;
    }

    /*
    @Autowired
    public StudentController(TaskScheduler taskExecutor) {
        this.executor = taskExecutor;
    }*/



    @CrossOrigin(origins = "*")
    @PostMapping(path="/sendEmail",produces = {MediaType.APPLICATION_JSON_VALUE})
    public @ResponseBody Map<String, Object> sendEmail(@RequestBody Map<String, Object> map) {


        try {
            this.taskScheduler.schedule(
                    new EmailTask("message!!!!","aymeric@dejavel.fr",this.emailService),
                    new Date(System.currentTimeMillis() + 3000)
            );
            Map mapd = new HashMap();
            mapd.put("status",false);
            return mapd;

        }
        catch (Exception e){
            throw new RuntimeException("error");
        }
    }


    @CrossOrigin(origins = "*")
    @PostMapping(path="/add",produces = {MediaType.APPLICATION_JSON_VALUE}) // Map ONLY GET Requests
    public @ResponseBody Student addNewUser(@RequestBody Student student) {
        try {
            Student registrar = studentRepository.findByEmail(SecurityContextHolder.getContext().getAuthentication()
                    .getPrincipal().toString());
            if (registrar.getRole()<student.getRole()){
                student.setRole(0);
            }
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
        if (student.getPassword().equals(student.getPasswordRepeat()) && student.getPassword().length()>7){
            student.setPasswordRepeat("");
            student.setPassword(bCryptPasswordEncoder.encode(student.getPassword()));
            student.setUsername(student.getEmail());
            studentRepository.save(student);
            return student;
        }else {
            throw new RuntimeException("password error");
        }

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
        try {
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
        catch (Exception e){
            throw new RuntimeException("failed to get phases");
        }
    }
    @CrossOrigin(origins = "*")
    @GetMapping(path="/myTasks")
    public @ResponseBody List<Task> getMyTasks() {
        try {
            Student student = studentRepository.findByEmail(SecurityContextHolder.getContext().getAuthentication()
                    .getPrincipal().toString());
            Iterable<Task> allTasks = taskRepository.findAll();
            List<Task> userTask = new ArrayList<Task>();
            allTasks.forEach((task)->{
                for (int i=0;i<task.getStudents().size();i++){
                    if (task.getStudents().get(i).getId()==student.getId()){
                        userTask.add(task);
                    }
                }
            });
            return userTask;
        }
        catch (Exception e){
            throw new RuntimeException("failed to get phases");
        }
    }
    @CrossOrigin(origins = "*")
    @GetMapping(path="/all")
    public @ResponseBody List<Student> getAllUsers() {
        Student student = studentRepository.findByEmail(SecurityContextHolder.getContext().getAuthentication()
                .getPrincipal().toString());
        List<Student> students = new ArrayList<Student>();

        studentRepository.findAll().forEach((item)->{
            try {
                if (item.getRole()<=student.getRole()){
                    students.add(item);
                }
            }
            catch (Exception e){
                System.out.println("item without role");
            }
        });
        return students;
    }
    @CrossOrigin(origins = "*")
    @GetMapping(path="/one/{id}")
    public @ResponseBody Optional<Student> getOneUser(@PathVariable(value="id") String id) {
        try {
            return studentRepository.findById(Long.valueOf(Integer.parseInt(id)));
        }
        catch (Exception e){
            throw new RuntimeException("user not found");
        }

    }
    @CrossOrigin(origins = "*")
    @PostMapping(path="/associateGroupToTutor/{tutor_id}",produces = {MediaType.APPLICATION_JSON_VALUE}) // Map ONLY GET Requests
    public @ResponseBody Student associateGroupToTutor(@RequestBody Map<String, Object> payload,@PathVariable(value="tutor_id") String id) {
        try {
            Student st = studentRepository.findByEmail(SecurityContextHolder.getContext().getAuthentication()
                    .getPrincipal().toString());
            if (st.getRole()>0){
                Student user = studentRepository.findById(Long.valueOf(Integer.parseInt(id))).get();
                int groupId = (Integer)payload.get("groupId");
                StudentGroup group = groupRepository.findById(Long.valueOf(groupId)).get();
                if (group.getTutor()!=null){
                    Student tutor = group.getTutor();
                    tutor.removeGroupFromTutor(group);
                }
                user.makeTutor(group);
                studentRepository.save(user);
                group.setTutor(user);
                groupRepository.save(group);
                return user;
            }else {
                throw new RuntimeException("not authorized");
            }

        }
        catch (Exception e){
            throw new RuntimeException("failed to link group and tutor");
        }

    }
    @CrossOrigin(origins = "*")
    @DeleteMapping(path="/one/{id}")
    public @ResponseBody Map<String, Object> deleteOneUser(@PathVariable(value="id") String id) {
        try {
            Student student = studentRepository.findById(Long.valueOf(Integer.parseInt(id))).get();
            Student st = studentRepository.findByEmail(SecurityContextHolder.getContext().getAuthentication()
                    .getPrincipal().toString());
            if (st.getRole()>student.getRole()){
                student.getIstutorof().forEach(item->{
                    item.setTutor(null);
                });
                studentRepository.delete(student);
                Map map = new HashMap();
                map.put("status",true);
                return map;
            }else {
                throw new RuntimeException("not authorized");
            }

        }
        catch (Exception e){
            throw new RuntimeException("user not found");
        }
    }
    @CrossOrigin(origins = "*")
    @PostMapping(path="/changeRole/{user_id}")
    public @ResponseBody Student changeRole(@RequestBody Map<String, Object> payload,@PathVariable(value="user_id") String id) {
        try {
            Student student = studentRepository.findByEmail(SecurityContextHolder.getContext().getAuthentication()
                    .getPrincipal().toString());
            Student user = studentRepository.findById(Long.valueOf(Integer.parseInt(id))).get();
            int role = (Integer)payload.get("role");
            if (student.getRole()>=role && student.getRole()>=user.getRole()){
                user.setRole(role);
                studentRepository.save(user);
            }
            return user;
        }
        catch (Exception e){
            throw new RuntimeException("user not found");
        }
    }
    @CrossOrigin(origins = "*")
    @PutMapping(path="/modify")
    public @ResponseBody Student changeStudent(@RequestBody Map<String, Object> payload) {
        try {
            Student student = studentRepository.findByEmail(SecurityContextHolder.getContext().getAuthentication()
                .getPrincipal().toString());
            System.out.println(student.getName());
            student.setName(payload.get("name").toString());
            student.setLastname((String)payload.get("lastname"));
            studentRepository.save(student);
            return student;
        }
        catch (Exception e){
            throw new RuntimeException("student not found");
        }
    }
    @CrossOrigin(origins = "*")
    @PutMapping(path="/changePassword")
    public @ResponseBody Map<String, Object> changePassword(@RequestBody Map<String, Object> payload) {
        try {
            String password = (String)payload.get("password");
            String passwordRepeat = (String)payload.get("passwordRepeat");
            Student student = studentRepository.findByEmail(SecurityContextHolder.getContext().getAuthentication()
                    .getPrincipal().toString());
            if (password.equals(passwordRepeat) && password.length()>7){
                student.setPassword(bCryptPasswordEncoder.encode(password));
                studentRepository.save(student);
                Map map = new HashMap();
                map.put("status",true);
                return map;
            }else {
                Map map = new HashMap();
                map.put("status",false);
                return map;
            }
        }
        catch (Exception e){
            throw new RuntimeException("error while changing password");
        }
    }
}