package com.rhododendron.dashboardisep;


import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.Optional;
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

    @CrossOrigin(origins = "*")
    @PostMapping(path = "/add/{id}", produces = {MediaType.APPLICATION_JSON_VALUE}) // Map ONLY GET Requests
    public @ResponseBody Task addNewTask(@RequestBody Task task,@PathVariable(value = "id") String id) {
        Phase ph = phaseRepository.findById(Long.valueOf(Integer.parseInt(id))).get();
        task.setPhase(ph);
        task.setDone(false);
        taskRepository.save(task);
        ph.addTask(task);
        phaseRepository.save(ph);
        return task;
    }
    @CrossOrigin(origins = "*")
    @PostMapping(path = "/addStudentToTask/{id}", produces = {MediaType.APPLICATION_JSON_VALUE}) // Map ONLY GET Requests
    public @ResponseBody Task addStudentToTask(@RequestBody Map<String, Object> map,@PathVariable(value = "id") String id) {
        Task task = taskRepository.findById(Long.valueOf(Integer.parseInt(id))).get();
        Student student = studentRepository.findById(Long.valueOf((Integer)map.get("userId"))).get();
        task.addStudent(student);
        taskRepository.save(task);
        //student.addTask(task);
        //studentRepository.save(student);
        return task;
    }

    @CrossOrigin(origins = "*")
    @DeleteMapping(path="/removeStudentFromTask/{id}")
    public @ResponseBody Task removeStudentFromTask(@RequestBody Map<String, Object> map,@PathVariable(value="id") String id) {
        Task task = taskRepository.findById(Long.valueOf(Integer.parseInt(id))).get();
        Student student = studentRepository.findById(Long.valueOf((Integer)map.get("userId"))).get();
        task.deleteStudent(student);
        taskRepository.save(task);
        return task;
    }

    @CrossOrigin(origins = "*")
    @GetMapping(path = "/all")
    public @ResponseBody Iterable<Task> getAllTasks() {
        return taskRepository.findAll();
    }

    @CrossOrigin(origins = "*")
    @GetMapping(path = "/one/{id}")
    public @ResponseBody Optional<Task> getOneTask(@PathVariable(value = "id") String id) {
        return taskRepository.findById(Long.valueOf(Integer.parseInt(id)));
    }
    @CrossOrigin(origins = "*")
    @DeleteMapping(path="/one/{id}")
    public @ResponseBody Map<String, Object> deleteOneTask(@PathVariable(value="id") String id) {
        Task task = taskRepository.findById(Long.valueOf(Integer.parseInt(id))).get();
        task.getStudents().forEach(item->{
            item.deleteTask(task);
        });
        taskRepository.delete(task);
        Map map = new HashMap();
        map.put("status",true);
        return map;
    }
}