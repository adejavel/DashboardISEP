package com.rhododendron.dashboardisep;


import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.Optional;
import org.springframework.security.core.context.SecurityContextHolder;
import it.ozimov.springboot.mail.model.Email;
import javax.mail.internet.InternetAddress;
import static com.google.common.collect.Lists.newArrayList;
import it.ozimov.springboot.mail.model.defaultimpl.DefaultEmail;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import java.util.*;


@RequestMapping(path="/tasks")
@RestController
public class TaskControler {
    @Autowired
    private TaskRepository taskRepository;
    @Autowired
    private PhaseRepository phaseRepository;
    @Autowired
    private StudentRepository studentRepository;
    @Autowired
    private it.ozimov.springboot.mail.service.EmailService emailService;

    private ThreadPoolTaskScheduler taskScheduler;


    public TaskControler(ThreadPoolTaskScheduler sch) {
        this.taskScheduler = sch;
    }

    @CrossOrigin(origins = "*")
    @PostMapping(path = "/add/{id}", produces = {MediaType.APPLICATION_JSON_VALUE}) // Map ONLY GET Requests
    public @ResponseBody Task addNewTask(@RequestBody Task task,@PathVariable(value = "id") String id) {
        try {
            Student student = studentRepository.findByEmail(SecurityContextHolder.getContext().getAuthentication()
                    .getPrincipal().toString());
            Phase ph = phaseRepository.findById(Long.valueOf(Integer.parseInt(id))).get();
            task.setPhase(ph);
            task.setDone(false);
            taskRepository.save(task);
            ph.addTask(task);
            phaseRepository.save(ph);
            try {
                long d;
                try {
                    d = ((long)(task.getEnd_date()-60*task.getTime()))*1000;
                }
                catch (Exception e){
                    d = ((long)(task.getEnd_date()-60*60))*1000;
                }
                //long d = ((long)(task.getEnd_date()-60*task.getTime()))*1000;
                //System.out.println(d);
                //System.out.println((task.getEnd_date()-60*task.getTime())*1000);
                this.taskScheduler.schedule(
                        new EmailTask("Hi!\n\nThis is DashboardISEP team and we sende you this email to remind you that task "+task.getName()+" is due for today.\n\nGoodbye",student.getEmail(),this.emailService),
                        new Date(d)
                );
            }
            catch (Exception e){
                e.printStackTrace();
            }
            return task;
        }
        catch (Exception e){
            throw new RuntimeException("phase not found");
        }

    }
    @CrossOrigin(origins = "*")
    @PostMapping(path = "/addStudentToTask/{id}", produces = {MediaType.APPLICATION_JSON_VALUE}) // Map ONLY GET Requests
    public @ResponseBody Task addStudentToTask(@RequestBody Map<String, Object> map,@PathVariable(value = "id") String id) {
        try {
            Task task = taskRepository.findById(Long.valueOf(Integer.parseInt(id))).get();
            Student student = studentRepository.findById(Long.valueOf((Integer)map.get("userId"))).get();
            task.addStudent(student);
            taskRepository.save(task);
            //student.addTask(task);
            //studentRepository.save(student);
            return task;
        }
        catch (Exception e){
            throw new RuntimeException("task or student not found");
        }
    }

    @CrossOrigin(origins = "*")
    @DeleteMapping(path="/removeStudentFromTask/{id}")
    public @ResponseBody Task removeStudentFromTask(@RequestBody Map<String, Object> map,@PathVariable(value="id") String id) {
        try {
            Task task = taskRepository.findById(Long.valueOf(Integer.parseInt(id))).get();
            Student student = studentRepository.findById(Long.valueOf((Integer)map.get("userId"))).get();
            task.deleteStudent(student);
            taskRepository.save(task);
            return task;
        }
        catch (Exception e){
            throw new RuntimeException("task or student not found");
        }
    }

    @CrossOrigin(origins = "*")
    @GetMapping(path = "/all")
    public @ResponseBody Iterable<Task> getAllTasks() {
        return taskRepository.findAll();
    }

    @CrossOrigin(origins = "*")
    @GetMapping(path = "/one/{id}")
    public @ResponseBody Optional<Task> getOneTask(@PathVariable(value = "id") String id) {
        try {
            return taskRepository.findById(Long.valueOf(Integer.parseInt(id)));
        }
        catch (Exception e){
            throw new RuntimeException("task not found");
        }

    }
    @CrossOrigin(origins = "*")
    @DeleteMapping(path="/one/{id}")
    public @ResponseBody Map<String, Object> deleteOneTask(@PathVariable(value="id") String id) {
        try {
            Task task = taskRepository.findById(Long.valueOf(Integer.parseInt(id))).get();
            task.getStudents().forEach(item->{
                item.deleteTask(task);
            });
            taskRepository.delete(task);
            Map map = new HashMap();
            map.put("status",true);
            return map;
        }
        catch (Exception e){
            throw new RuntimeException("task not found");
        }

    }
    @CrossOrigin(origins = "*")
    @PutMapping(path="/modify/{id}")
    public @ResponseBody Task changeTask(@RequestBody Task task,@PathVariable(value = "id") String id) {
        try {
            Task original = taskRepository.findById(Long.valueOf(Integer.parseInt(id))).get();
            original.setName(task.getName());
            original.setStart_date(task.getStart_date());
            original.setEnd_date(task.getEnd_date());
            original.setDescription(task.getDescription());
            taskRepository.save(original);
            return original;
        }
        catch (Exception e){
            throw new RuntimeException("task not found");
        }
    }
    @CrossOrigin(origins = "*")
    @GetMapping(path = "/markAsDone/{id}")
    public @ResponseBody Task markAsDone(@PathVariable(value = "id") String id) {
        try {
            Student student = studentRepository.findByEmail(SecurityContextHolder.getContext().getAuthentication()
                    .getPrincipal().toString());
            Task task = taskRepository.findById(Long.valueOf(Integer.parseInt(id))).get();
            if (student.getRole()>0|| student.getGroup()==task.getPhase().getGroup()){
                task.setDone(true);
                taskRepository.save(task);
                return task;
            }else {
                throw new RuntimeException("not authorized");
            }


        }
        catch (Exception e){
            throw new RuntimeException("task not found");
        }
    }
    @CrossOrigin(origins = "*")
    @GetMapping(path = "/markAsNotDone/{id}")
    public @ResponseBody Task markAsNotDone(@PathVariable(value = "id") String id) {
        try {
            Student student = studentRepository.findByEmail(SecurityContextHolder.getContext().getAuthentication()
                    .getPrincipal().toString());
            Task task = taskRepository.findById(Long.valueOf(Integer.parseInt(id))).get();
            if (student.getRole()>0|| student.getGroup()==task.getPhase().getGroup()){
                task.setDone(false);
                taskRepository.save(task);
                return task;
            }else {
                throw new RuntimeException("not authorized");
            }


        }
        catch (Exception e){
            throw new RuntimeException("task not found");
        }
    }
    /*
    @CrossOrigin(origins = "*")
    @PutMapping(path = "/change/{id}")
    public @ResponseBody Task changePhase(@PathVariable(value = "id") String id) {
        try {
            Student student = studentRepository.findByEmail(SecurityContextHolder.getContext().getAuthentication()
                    .getPrincipal().toString());
            Task task = taskRepository.findById(Long.valueOf(Integer.parseInt(id))).get();
            Phase phase = task.getPhase();
            if (student.getRole()>0|| student.getGroup()==phase.getGroup()){
                Phase newPhase = phaseRepository.findById(Long.valueOf(Integer.parseInt(id))).get();
                phase.removeTask(task);
                newPhase.addTask(task);
                task.setPhase(newPhase);
                taskRepository.save(task);
                phaseRepository.save(phase);
                phaseRepository.save(newPhase);
                return task;
            }else {
                throw new RuntimeException("not authorized");
            }

        }
        catch (Exception e){
            throw new RuntimeException("task not found");
        }
    }*/
}