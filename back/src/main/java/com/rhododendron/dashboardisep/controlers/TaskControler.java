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

    @CrossOrigin(origins = "*")
    @PostMapping(path = "/add/{id}", produces = {MediaType.APPLICATION_JSON_VALUE}) // Map ONLY GET Requests
    public @ResponseBody Task addNewTask(@RequestBody Task task,@PathVariable(value = "id") String id) {
        Phase ph = phaseRepository.findById(Long.valueOf(Integer.parseInt(id))).get();
        task.setPhase(ph);
        taskRepository.save(task);
        ph.addTask(task);
        phaseRepository.save(ph);
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
}