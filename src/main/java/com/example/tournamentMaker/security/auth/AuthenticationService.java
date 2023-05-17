package com.example.tournamentMaker.auth;

import com.example.tournamentMaker.address.Address;
import com.example.tournamentMaker.config.JwtService;
import com.example.tournamentMaker.token.Token;
import com.example.tournamentMaker.token.TokenRepository;
import com.example.tournamentMaker.token.TokenType;
import com.example.tournamentMaker.user.Role;
import com.example.tournamentMaker.user.User;
import com.example.tournamentMaker.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final UserRepository repository;
    private final TokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthenticationResponse register(RegisterRequest request) {
        String encodedPassword = passwordEncoder.encode(request.getPassword());
        User user = new User(request.getFirstName(), request.getLastName(), new Address(request.getCity(),
                request.getStreet()), request.getEmail(), encodedPassword, Role.USER);
        User savedUser = repository.save(user);
        String token = jwtService.generateToken(user);
        saveUserToken(savedUser,token);
        return new AuthenticationResponse(token);
    }

    private void saveUserToken(User user, String token) {
        Token tokenToSave = Token.builder()
                .token(token)
                .tokenType(TokenType.BEARER)
                .expired(false)
                .revoked(false)
                .user(user)
                .build();
        tokenRepository.save(tokenToSave);
    }

    public AuthenticationResponse authenticate(AuthenticationRequest authenticationRequest) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                authenticationRequest.getEmail(),authenticationRequest.getPassword()));

        User user = repository.findByEmail(authenticationRequest.getEmail()).orElseThrow();
        String token = jwtService.generateToken(user);
        saveUserToken(user,token);
        revokeAllUserTokens(user);
        return new AuthenticationResponse(token);
    }
    private void revokeAllUserTokens(User user){
        List<Token> validTokens = tokenRepository.findAllValidTokenByUser((int) user.getId());
        if(validTokens.isEmpty()){
            return;
        }
        validTokens.forEach(token -> {
            token.setExpired(true);
            token.setRevoked(true);
        });
        tokenRepository.saveAll(validTokens);
    }
}

