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
    @PostMapping(path="/add",produces = {MediaType.APPLICATION_JSON_VALUE}) // Map ONLY GET Requests
    public @ResponseBody Phase addNewPhase(@RequestBody Phase phase) {
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
    @PostMapping(path="/associatePhaseToGroup/{phase_id}",produces = {MediaType.APPLICATION_JSON_VALUE}) // Map ONLY GET Requests
    public @ResponseBody Phase associatePhaseToGroup(@RequestBody Map<String, Object> payload,@PathVariable(value="phase_id") String id) {
        Phase phase = phaseRepository.findById(Long.valueOf(Integer.parseInt(id))).get();
        int groupId = (Integer)payload.get("groupId");
        StudentGroup group = groupRepository.findById(Long.valueOf(groupId)).get();
        group.addPhase(phase);
        phase.setGroup(group);
        phaseRepository.save(phase);
        groupRepository.save(group);
        return phase;
    }
}