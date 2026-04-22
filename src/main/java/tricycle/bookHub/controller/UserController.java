package tricycle.bookHub.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import tricycle.bookHub.model.User;
import tricycle.bookHub.service.UserService;

import java.util.List;

@RequiredArgsConstructor
@RestController
public class UserController {

    private final UserService service;

    @GetMapping("/api/users")
    public List<User> getAllUsers(){
        return service.getAllUsers();
    }

    @GetMapping("/api/users/{id}")
    public User getUserById(@PathVariable Long id){
        return service.getUserById(id);
    }

    @PostMapping("/api/users")
    public User addUser(@RequestBody User user){
        return service.addUser(user);
    }

    @PutMapping("api/users/{id}")
    public User updateUser(@PathVariable Long id, @RequestBody User user){
        return service.updateUser(user, id);
    }

    @DeleteMapping("api/users/{id}")
    public void deleteUser(@PathVariable Long id){
        service.deleteUser(id);
    }



}
