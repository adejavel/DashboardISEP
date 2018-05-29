package com.rhododendron.dashboardisep;


import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.Optional;
import java.util.*;


@RequestMapping(path="/phases")
@RestController
public class PhaseControler {
    @Autowired
    private PhaseRepository phaseRepository;
    @Autowired
    private GroupRepository groupRepository;
    @CrossOrigin(origins = "*")
    @PostMapping(path="/add/{id}",produces = {MediaType.APPLICATION_JSON_VALUE}) // Map ONLY GET Requests
    public @ResponseBody Phase addNewPhase(@RequestBody Phase phase,@PathVariable(value = "id") String id) {
        try {
            StudentGroup group = groupRepository.findById(Long.valueOf(Integer.parseInt(id))).get();
            phase.setGroup(group);
            group.addPhase(phase);
            groupRepository.save(group);
            phaseRepository.save(phase);
            return phase;
        }
        catch (Exception e){
            throw new RuntimeException("group not found");
        }

    }
    @CrossOrigin(origins = "*")
    @GetMapping(path="/all")
    public @ResponseBody Iterable<Phase> getAllPhases() {
        return phaseRepository.findAll();
    }
    @CrossOrigin(origins = "*")
    @GetMapping(path="/one/{id}")
    public @ResponseBody Optional<Phase> getOnePhase(@PathVariable(value="id") String id) {
        try {
            return phaseRepository.findById(Long.valueOf(Integer.parseInt(id)));
        }
        catch (Exception e){
            throw new RuntimeException("phase not found");
        }
    }

    @CrossOrigin(origins = "*")
    @DeleteMapping(path="/one/{id}")
    public @ResponseBody Map<String, Object> deleteOnePhase(@PathVariable(value="id") String id) {
        try {
            Phase phase = phaseRepository.findById(Long.valueOf(Integer.parseInt(id))).get();
            phase.getTasks().forEach(item->{
                item.setPhase(null);
            });
            phaseRepository.delete(phase);
            Map map = new HashMap();
            map.put("status",true);
            return map;
        }
        catch (Exception e){
            throw new RuntimeException("phase not found");
        }
    }
    @CrossOrigin(origins = "*")
    @PutMapping(path="/modify/{id}")
    public @ResponseBody Phase changePhase(@RequestBody Phase phase,@PathVariable(value = "id") String id) {
        try {
            Phase original = phaseRepository.findById(Long.valueOf(Integer.parseInt(id))).get();
            original.setName(phase.getName());
            original.setDescription(phase.getDescription());
            original.setStart_date(phase.getStart_date());
            original.setEnd_date(phase.getEnd_date());
            phaseRepository.save(original);
            return phase;
        }
        catch (Exception e){
            throw new RuntimeException("phase not found");
        }
    }
    /*
    @CrossOrigin(origins = "*")
    @GetMapping(path = "/markAsDone/{id}")
    public @ResponseBody Phase markAsDone(@PathVariable(value = "id") String id) {
        try {
            Phase phase = phaseRepository.findById(Long.valueOf(Integer.parseInt(id))).get();
            phase.setDone(true);
            phaseRepository.save(phase);
            return task;
        }
        catch (Exception e){
            throw new RuntimeException("phase not found");
        }
    }

    @CrossOrigin(origins = "*")
    @GetMapping(path = "/markAsNotDone/{id}")
    public @ResponseBody Phase markAsNotDone(@PathVariable(value = "id") String id) {
        try {
            Phase phase = phaseRepository.findById(Long.valueOf(Integer.parseInt(id)));
            phase.setDone(false);
            phaseRepository.save(phase);
            return task;
        }
        catch (Exception e){
            throw new RuntimeException("phase not found");
        }
    }
    */

}