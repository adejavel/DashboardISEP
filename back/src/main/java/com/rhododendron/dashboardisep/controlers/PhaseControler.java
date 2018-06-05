package com.rhododendron.dashboardisep;


import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.Optional;
import org.springframework.security.core.context.SecurityContextHolder;
import java.util.*;


@RequestMapping(path="/phases")
@RestController
public class PhaseControler {
    @Autowired
    private PhaseRepository phaseRepository;
    @Autowired
    private GroupRepository groupRepository;
    @Autowired
    private StudentRepository studentRepository;
    @CrossOrigin(origins = "*")
    @PostMapping(path="/add/{id}",produces = {MediaType.APPLICATION_JSON_VALUE}) // Map ONLY GET Requests
    public @ResponseBody Phase addNewPhase(@RequestBody Phase phase,@PathVariable(value = "id") String id) {
        try {
            StudentGroup group = groupRepository.findById(Long.valueOf(Integer.parseInt(id))).get();
            Student student = studentRepository.findByEmail(SecurityContextHolder.getContext().getAuthentication()
                    .getPrincipal().toString());
            if (student.getRole()>0|| student.getGroup()==group){
                phase.setGroup(group);
                group.addPhase(phase);
                groupRepository.save(group);
                phaseRepository.save(phase);
                return phase;
            }else {
                throw new RuntimeException("not authorized");
            }
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
            Student student = studentRepository.findByEmail(SecurityContextHolder.getContext().getAuthentication()
                    .getPrincipal().toString());

            Phase phase = phaseRepository.findById(Long.valueOf(Integer.parseInt(id))).get();
            if (student.getRole()>0|| student.getGroup()==phase.getGroup()){
                phase.getTasks().forEach(item->{
                    item.setPhase(null);
                });
                phaseRepository.delete(phase);
                Map map = new HashMap();
                map.put("status",true);
                return map;
            }else {
                throw new RuntimeException("not authorized");
            }
        }
        catch (Exception e){
            throw new RuntimeException("phase not found");
        }
    }
    @CrossOrigin(origins = "*")
    @PutMapping(path="/modify/{id}")
    public @ResponseBody Phase changePhase(@RequestBody Phase phase,@PathVariable(value = "id") String id) {
        try {
            Student student = studentRepository.findByEmail(SecurityContextHolder.getContext().getAuthentication()
                    .getPrincipal().toString());

            Phase original = phaseRepository.findById(Long.valueOf(Integer.parseInt(id))).get();
            if (student.getRole()>0|| student.getGroup()==original.getGroup()){
                original.setName(phase.getName());
                original.setDescription(phase.getDescription());
                original.setStart_date(phase.getStart_date());
                original.setEnd_date(phase.getEnd_date());
                phaseRepository.save(original);
                return original;
            }else {
                throw new RuntimeException("not authorized");
            }

        }
        catch (Exception e){
            throw new RuntimeException("phase not found");
        }
    }

}