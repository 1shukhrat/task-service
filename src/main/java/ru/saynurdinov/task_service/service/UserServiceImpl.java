package ru.saynurdinov.task_service.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.saynurdinov.task_service.dto.AuthResponseDTO;
import ru.saynurdinov.task_service.dto.LoginDTO;
import ru.saynurdinov.task_service.dto.RegisterDTO;
import ru.saynurdinov.task_service.entity.User;
import ru.saynurdinov.task_service.exception.ResourceNotFoundException;
import ru.saynurdinov.task_service.exception.UserAlreadyExistsException;
import ru.saynurdinov.task_service.mapper.UserMapper;
import ru.saynurdinov.task_service.repository.UserRepository;
import ru.saynurdinov.task_service.util.JwtUtils;

import java.util.Collections;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserDetailsService, UserService {

    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;
    private final UserMapper userMapper;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, @Lazy AuthenticationManager authenticationManager, @Lazy PasswordEncoder passwordEncoder, JwtUtils jwtUtils, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.authenticationManager = authenticationManager;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtils = jwtUtils;
        this.userMapper = userMapper;
    }

    @Transactional(readOnly = true)
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Optional<User> optionalUser = userRepository.findByEmail(email);
        if (optionalUser.isPresent()) {
            User existedUser = optionalUser.get();
            return new org.springframework.security.core.userdetails.User(existedUser.getEmail(),
                    existedUser.getPassword(), Collections.emptyList());
        } else {
            throw new ResourceNotFoundException("user", "email", email);
        }
    }

    @Transactional
    @Override
    public AuthResponseDTO addUser(RegisterDTO registerDTO) {
        if (userRepository.existsByEmail(registerDTO.getEmail())
                || userRepository.existsByUsername(registerDTO.getUsername())) {
            throw new UserAlreadyExistsException("User is already registered");
        }
        User user = User.builder().username(registerDTO.getUsername())
                .email(registerDTO.getEmail())
                .password(passwordEncoder.encode(registerDTO.getPassword()))
                .build();
        userRepository.save(user);
        String token = jwtUtils.generateToken(
                new org.springframework.security.core.userdetails.User(user.getEmail(), user.getPassword(), Collections.emptyList()));
        return new AuthResponseDTO("User was successfully registered", token, userMapper.toDTO(user));
    }

    @Transactional(readOnly = true)
    @Override
    public AuthResponseDTO signIn(LoginDTO loginDTO) {
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginDTO.getEmail(), loginDTO.getPassword())
        );
        String token = jwtUtils.generateToken((UserDetails) auth.getPrincipal());
        User user = userRepository.findByEmail(((UserDetails) auth.getPrincipal()).getUsername()).get();
        return new AuthResponseDTO("User was successfully authenticated", token, userMapper.toDTO(user));
    }
}
