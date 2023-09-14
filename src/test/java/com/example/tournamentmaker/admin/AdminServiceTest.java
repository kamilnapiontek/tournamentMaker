package com.example.tournamentmaker.admin;

import com.example.tournamentmaker.user.Role;
import com.example.tournamentmaker.user.User;
import com.example.tournamentmaker.user.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.NoSuchElementException;
import java.util.Optional;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AdminServiceTest {
    @InjectMocks
    private AdminService adminService;
    @Mock
    private UserRepository userRepository;

    @Test
    void shouldSwitchRoleUserToAdmin() {
        // given
        String email = "john@gmail.com";
        Optional<User> user = Optional.of(createUser(email, Role.USER));
        // when
        when(userRepository.findByEmail(email)).thenReturn(user);
        adminService.switchRole(new SwitchRequest(email, Role.ADMIN));
        // then
        Assertions.assertEquals(Role.ADMIN, user.get().getRole());
    }

    @Test
    void shouldContainExceptionWhenUserNotFound() {
        // given
        String email = "john@gmail.com";
        // when
        Assertions.assertThrows(NoSuchElementException.class,
                () -> adminService.switchRole(new SwitchRequest(email, Role.ADMIN)));
    }

    private User createUser(String email, Role role) {
        User user = new User();
        user.setRole(role);
        user.setEmail(email);
        return user;
    }
}