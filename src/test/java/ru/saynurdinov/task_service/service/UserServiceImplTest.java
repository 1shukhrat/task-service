package ru.saynurdinov.task_service.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.saynurdinov.task_service.dto.AuthResponseDTO;
import ru.saynurdinov.task_service.dto.LoginDTO;
import ru.saynurdinov.task_service.dto.RegisterDTO;
import ru.saynurdinov.task_service.dto.UserDTO;
import ru.saynurdinov.task_service.entity.User;
import ru.saynurdinov.task_service.exception.ResourceNotFoundException;
import ru.saynurdinov.task_service.exception.UserAlreadyExistsException;
import ru.saynurdinov.task_service.mapper.UserMapper;
import ru.saynurdinov.task_service.repository.UserRepository;
import ru.saynurdinov.task_service.util.JwtUtils;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


public class UserServiceImplTest {

    @Mock
    private  UserRepository userRepository;

    @Mock
    private  AuthenticationManager authenticationManager;

    @Mock
    private  PasswordEncoder passwordEncoder;

    @Mock
    private  JwtUtils jwtUtils;

    @Mock
    private  UserMapper userMapper;

    @InjectMocks
    private UserServiceImpl userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testLoadByUsername_UserNotFound_ThrowsResourceNotFoundException() {
        String email = "test@example.com";

        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> userService.loadUserByUsername(email));
    }

    @Test
    void testLoadByUsername_UserFound_ReturnsUser() {
        String email = "test@example.com";
        String password = "password";

        User user = User.builder()
                .email(email)
                .password(password)
                .build();

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        UserDetails userDetails = userService.loadUserByUsername(email);

        assert(userDetails.getUsername().equals(email));
        assert(userDetails.getPassword().equals(password));
    }

    @Test
    void testAddUser_UserAlreadyExistsByEmail_ThrowsUserAlreadyExistsException() {
        RegisterDTO registerDTO = new RegisterDTO();
        registerDTO.setEmail("test@example.com");

        when(userRepository.existsByEmail(registerDTO.getEmail())).thenReturn(true);

        assertThrows(UserAlreadyExistsException.class, () -> userService.addUser(registerDTO));
    }

    @Test
    void testAddUser_UserAlreadyExistsByUsername_ThrowsUserAlreadyExistsException() {
        RegisterDTO registerDTO = new RegisterDTO();
        registerDTO.setUsername("test");

        when(userRepository.existsByUsername(registerDTO.getUsername())).thenReturn(true);

        assertThrows(UserAlreadyExistsException.class, () -> userService.addUser(registerDTO));
    }

    @Test
    void testAddUser_UserDoNotExists_ReturnsDtoOfCreatedUser() {
        RegisterDTO registerDTO = new RegisterDTO();
        registerDTO.setEmail("test@example.com");
        registerDTO.setUsername("test");
        registerDTO.setPassword("password");

        UserDTO userDTO = new UserDTO();

        when(userRepository.existsByEmail(registerDTO.getEmail())).thenReturn(false);
        when(userRepository.existsByUsername(registerDTO.getUsername())).thenReturn(false);
        when(passwordEncoder.encode(registerDTO.getPassword())).thenReturn("encodedPassword");
        when(jwtUtils.generateToken(any(UserDetails.class))).thenReturn("jwt");
        when(userMapper.toDTO(any(User.class))).thenReturn(userDTO);

        AuthResponseDTO response = userService.addUser(registerDTO);

        verify(userRepository).save(any());
        assertNotNull(response);
        assertEquals(response.getJwtToken(), "jwt");
        assertEquals(response.getUserDTO(), userDTO);

    }

    @Test
    void testSignIn_ValidCredentials_ReturnsDtoOfUser() {
        LoginDTO loginDTO = new LoginDTO();
        loginDTO.setEmail("test@example.com");
        loginDTO.setPassword("password");

        Authentication authentication = mock(Authentication.class);
        UserDetails userDetails = mock(UserDetails.class);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getUsername()).thenReturn(loginDTO.getEmail());

        User user = new User();
        user.setEmail(loginDTO.getEmail());
        UserDTO userDTO = new UserDTO();

        when(jwtUtils.generateToken(any(UserDetails.class))).thenReturn("jwtToken");
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
        when(userMapper.toDTO(user)).thenReturn(userDTO);


        AuthResponseDTO response = userService.signIn(loginDTO);

        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(jwtUtils).generateToken(any(UserDetails.class));
        assertEquals("jwtToken", response.getJwtToken());
        assertEquals(userDTO, response.getUserDTO());
    }

}
