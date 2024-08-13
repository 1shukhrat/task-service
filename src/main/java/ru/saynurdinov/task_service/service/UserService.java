package ru.saynurdinov.task_service.service;

import ru.saynurdinov.task_service.dto.AuthResponseDTO;
import ru.saynurdinov.task_service.dto.LoginDTO;
import ru.saynurdinov.task_service.dto.RegisterDTO;

public interface UserService {

    AuthResponseDTO addUser(RegisterDTO registerDTO);
    AuthResponseDTO signIn(LoginDTO loginDTO);

}
