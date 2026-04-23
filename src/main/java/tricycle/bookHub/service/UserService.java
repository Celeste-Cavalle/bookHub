package tricycle.bookHub.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tricycle.bookHub.exception.UserNotFoundException;
import tricycle.bookHub.model.User;
import tricycle.bookHub.repository.UserRepository;

import java.util.List;

@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository repository;

    public List<User> getAllUsers(){ return repository.findAll(); }

    public User addUser(User user){
        return repository.save(user);
    }

    public User getUserById(Long id){
       return repository.findById(id).
                orElseThrow(() -> new UserNotFoundException("Cette personne avec cet id: "+ id + " n'existe pas"));
    }

    public User updateUser(User user, Long id){
        User existingUser = repository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("Cette personne avec cet id: "+ id + " n'existe pas"));

        existingUser.setFirstName(user.getFirstName());
        existingUser.setLastName(user.getLastName());
        existingUser.setPassword(user.getPassword());
        existingUser.setEmail(user.getEmail());
        existingUser.setPhone(user.getPhone());
        existingUser.setRole(user.getRole());

        return repository.save(existingUser);
    }

    public void deleteUser(Long id){
        repository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("Cette personne avec cet id: "+ id + " n'existe pas"));
    }



}
