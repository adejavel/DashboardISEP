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
        StudentGroup group = groupRepository.findById(Long.valueOf(Integer.parseInt(id))).get();
        phase.setGroup(group);
        group.addPhase(phase);
        groupRepository.save(group);
        phaseRepository.save(phase);
        return phase;
    }
    @CrossOrigin(origins = "*")
    @GetMapping(path="/all")
    public @ResponseBody Iterable<Phase> getAllPhases() {
        return phaseRepository.findAll();
    }
    @CrossOrigin(origins = "*")
    @GetMapping(path="/one/{id}")
    public @ResponseBody Optional<Phase> getOnePhase(@PathVariable(value="id") String id) {
        return phaseRepository.findById(Long.valueOf(Integer.parseInt(id)));
    }

    @CrossOrigin(origins = "*")
    @DeleteMapping(path="/one/{id}")
    public @ResponseBody Map<String, Object> deleteOnePhase(@PathVariable(value="id") String id) {
        Phase phase = phaseRepository.findById(Long.valueOf(Integer.parseInt(id))).get();
        phase.getTasks().forEach(item->{
            item.setPhase(null);
        });
        phaseRepository.delete(phase);
        Map map = new HashMap();
        map.put("status",true);
        return map;
    }

}