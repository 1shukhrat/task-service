package ru.saynurdinov.task_service.dto;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
public class AuthResponseDTO extends ResponseDTO {
    private String jwtToken;
    private UserDTO userDTO;

    public AuthResponseDTO(String message, String jwtToken, UserDTO userDTO) {
        super(message);
        this.jwtToken = jwtToken;
        this.userDTO = userDTO;
    }
}
