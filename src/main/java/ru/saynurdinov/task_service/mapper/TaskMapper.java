package ru.saynurdinov.task_service.mapper;

import org.mapstruct.Mapper;
import ru.saynurdinov.task_service.dto.TaskDTO;
import ru.saynurdinov.task_service.entity.Task;

@Mapper(componentModel = "spring", uses = {UserMapper.class, CommentMapper.class})
public interface TaskMapper {

    TaskDTO toDTO(Task task);
}
