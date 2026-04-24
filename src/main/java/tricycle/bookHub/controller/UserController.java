package tricycle.bookHub.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tricycle.bookHub.model.User;
import tricycle.bookHub.service.UserService;

import java.net.URI;
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
    public ResponseEntity<User> getUserById(@PathVariable Long id){
        User user = service.getUserById(id);
        if(user == null){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(user);
    }

    @PostMapping("/api/users")
    public ResponseEntity<User> addUser(@RequestBody User user){
        User savedUser = service.addUser(user);
        URI location = URI.create("/api/userq/" + savedUser.getId());
        return ResponseEntity.created(location).body(savedUser);
    }

    @PutMapping("api/users/{id}")
    public ResponseEntity<User> updateUser(@PathVariable Long id, @RequestBody User user){
        User updatedUser;
        try {
            updatedUser = service.updateUser(user, id);
        } catch (Exception e){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(updatedUser);
    }

    @DeleteMapping("api/users/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id){
        service.deleteUser(id);
        return ResponseEntity.noContent().build();
    }



}
