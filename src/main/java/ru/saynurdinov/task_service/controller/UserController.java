package ru.saynurdinov.task_service.controller;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.saynurdinov.task_service.dto.AuthResponseDTO;
import ru.saynurdinov.task_service.dto.LoginDTO;
import ru.saynurdinov.task_service.dto.RegisterDTO;
import ru.saynurdinov.task_service.service.UserService;

@RestController
@RequestMapping("/api/users")
@Tag(
        name = "User", description = "Operations for users"
)
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }



    @Operation(
            summary = "Sign in",
            description = "Authenticate a user with email and password and return a JWT token."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully authenticated"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - invalid credentials",
                    content = @Content),
            @ApiResponse(responseCode = "400", description = "Bad Request - validation errors",
                    content = @Content)
    })
    @PostMapping(value = "/sign-in", produces = "application/json", consumes = "application/json")
    public ResponseEntity<AuthResponseDTO> signIn(@RequestBody @Valid LoginDTO loginDTO) {
        return new ResponseEntity<>(userService.signIn(loginDTO), HttpStatus.OK);
    }

    @Operation(
            summary = "Sign up",
            description = "Register a new user and return a JWT token."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully registered"),
            @ApiResponse(responseCode = "409", description = "Conflict - user already exists",
                    content = @Content),
            @ApiResponse(responseCode = "400", description = "Bad Request - validation errors",
                    content = @Content)
    })
    @PostMapping(value ="/sign-up", produces = "application/json", consumes = "application/json")
    public ResponseEntity<AuthResponseDTO> signUp(@RequestBody @Valid RegisterDTO registerDTO) {
        return new ResponseEntity<>(userService.addUser(registerDTO), HttpStatus.OK);
    }







}
