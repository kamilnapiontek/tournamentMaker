package com.example.tournamentMaker.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
class UserService {
    private final UserRepository userRepository;

    void switchRole(SwitchRequest switchRequest) {
        Optional<User> admin = userRepository.findByEmail(switchRequest.getAdminEmail());
        admin.ifPresentOrElse(a -> {
            if (a.getRole() == Role.ADMIN) {
                Optional<User> switchUser = userRepository.findByEmail(switchRequest.getSwitchEmail());
                switchUser.ifPresentOrElse(user -> {
                    if (user.getRole() == Role.USER) {
                        user.setRole(Role.ADMIN);
                        userRepository.save(user);
                    } else {
                        user.setRole(Role.USER);
                        userRepository.save(user);
                    }
                }, () -> {
                    throw new NoSuchElementException("User to switch role not found");
                });
            }
        }, () -> {
            throw new NoSuchElementException("Admin not found");
        });
    }
}