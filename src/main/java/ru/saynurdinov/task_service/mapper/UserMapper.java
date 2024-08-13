package ru.saynurdinov.task_service.mapper;

import org.mapstruct.Mapper;
import ru.saynurdinov.task_service.dto.UserDTO;
import ru.saynurdinov.task_service.entity.User;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserDTO toDTO(User user);
}
