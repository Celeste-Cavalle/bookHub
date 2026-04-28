package tricycle.bookHub.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tricycle.bookHub.exception.UserNotFoundException;
import tricycle.bookHub.model.Role;
import tricycle.bookHub.model.User;
import tricycle.bookHub.repository.UserRepository;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock private UserRepository userRepository;

    @InjectMocks private UserService userService;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setEmail("john@test.com");
        user.setPassword("hashed");
        user.setRole(Role.USER);
    }

    // ===================== getAllUsers =====================

    @Test
    void getAllUsers_shouldReturnAllUsers() {
        when(userRepository.findAll()).thenReturn(List.of(user));

        List<User> result = userService.getAllUsers();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getEmail()).isEqualTo("john@test.com");
    }

    // ===================== getUserById =====================

    @Test
    void getUserById_shouldReturnUser_whenExists() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        User result = userService.getUserById(1L);

        assertThat(result.getFirstName()).isEqualTo("John");
    }

    @Test
    void getUserById_shouldThrow_whenNotFound() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.getUserById(99L))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessageContaining("99");
    }

    // ===================== addUser =====================

    @Test
    void addUser_shouldSaveAndReturnUser() {
        when(userRepository.save(any())).thenReturn(user);

        User result = userService.addUser(user);

        assertThat(result.getEmail()).isEqualTo("john@test.com");
        verify(userRepository).save(user);
    }

    // ===================== updateUser =====================

    @Test
    void updateUser_shouldUpdateOnlyProvidedFields() {
        User update = new User();
        update.setFirstName("Jane");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        User result = userService.updateUser(update, 1L);

        assertThat(result.getFirstName()).isEqualTo("Jane");
        assertThat(result.getLastName()).isEqualTo("Doe"); // inchangé
        assertThat(result.getEmail()).isEqualTo("john@test.com"); // inchangé
    }

    @Test
    void updateUser_shouldUpdateAllFields_whenAllProvided() {
        User update = new User();
        update.setFirstName("Jane");
        update.setLastName("Smith");
        update.setEmail("jane@test.com");
        update.setPassword("newpassword");
        update.setPhone("0612345678");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        User result = userService.updateUser(update, 1L);

        assertThat(result.getFirstName()).isEqualTo("Jane");
        assertThat(result.getLastName()).isEqualTo("Smith");
        assertThat(result.getEmail()).isEqualTo("jane@test.com");
        assertThat(result.getPassword()).isEqualTo("newpassword");
        assertThat(result.getPhone()).isEqualTo("0612345678");
    }

    @Test
    void updateUser_shouldThrow_whenNotFound() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.updateUser(user, 99L))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessageContaining("99");
    }

    // ===================== deleteUser =====================


    @Test
    void deleteUser_shouldCallDeleteById_whenUserExists() {
        lenient().when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        userService.deleteUser(1L);

        verify(userRepository).deleteById(1L);
    }

    @Test
    void deleteUser_shouldThrow_whenUserNotFound() {
        lenient().when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.deleteUser(99L))
                .isInstanceOf(UserNotFoundException.class);
    }
}
