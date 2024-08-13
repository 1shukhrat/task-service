package ru.saynurdinov.task_service.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LoginDTO {
    @NotBlank(message = "Email can't be blank")
    @Email(message = "Incorrect email format")
    private String email;

    @NotBlank(message = "Password can't be blank")
    private String password;
}
