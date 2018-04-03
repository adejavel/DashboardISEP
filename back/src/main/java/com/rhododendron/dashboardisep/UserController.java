package com.rhododendron.dashboardisep;


import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.Optional;


@RequestMapping(path="/api")
@RestController
public class UserController {
    @Autowired
    private UserRepository userRepository;

    @PostMapping(path="/add",produces = {MediaType.APPLICATION_JSON_VALUE}) // Map ONLY GET Requests
    public @ResponseBody User addNewUser(@RequestBody User user) {
        userRepository.save(user);
        return user;
    }
    @GetMapping(path="/all")
    public @ResponseBody Iterable<User> getAllUsers() {
        return userRepository.findAll();
    }
    @GetMapping(path="/{id}")
    public @ResponseBody Optional<User> getAllUsers(@PathVariable(value="id") String id) {
        return userRepository.findById(Long.valueOf(Integer.parseInt(id)));
    }
}