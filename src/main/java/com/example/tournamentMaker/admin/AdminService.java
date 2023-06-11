package com.example.tournamentMaker.admin;

import com.example.tournamentMaker.user.User;
import com.example.tournamentMaker.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
class AdminService {
    private final UserRepository userRepository;

    public void switchRole(SwitchRequest switchRequest) {
        Optional<User> userToSwitch = userRepository.findByEmail(switchRequest.getEmail());
        userToSwitch.ifPresentOrElse(user -> {
            user.setRole(switchRequest.getNewRole());
            userRepository.save(user);
        }, () -> {
            throw new NoSuchElementException("User to switch role not found");
        });
    }
}
