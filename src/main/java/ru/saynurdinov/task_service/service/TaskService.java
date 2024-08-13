package ru.saynurdinov.task_service.service;


import ru.saynurdinov.task_service.dto.ChangeStatusDTO;
import ru.saynurdinov.task_service.dto.CreateTaskDTO;
import ru.saynurdinov.task_service.dto.ResourceResponseDTO;
import ru.saynurdinov.task_service.dto.TaskDTO;
import ru.saynurdinov.task_service.dto.UpdateTaskDTO;
import ru.saynurdinov.task_service.entity.enums.TaskPriority;
import ru.saynurdinov.task_service.entity.enums.TaskStatus;

import java.util.List;

public interface TaskService {

    ResourceResponseDTO<List<TaskDTO>> getAllCreatedByAuthorId(Long id, TaskStatus status, TaskPriority priority, int page, int size);
    ResourceResponseDTO<List<TaskDTO>> getAllAssignedByAssigneeId(Long id, TaskStatus status, TaskPriority priority,  int page, int size);
    ResourceResponseDTO<List<TaskDTO>> getAllByUserId(Long id, TaskStatus status, TaskPriority priority,  int page, int size);
    ResourceResponseDTO<TaskDTO> getById(long id);
    ResourceResponseDTO<TaskDTO> create(CreateTaskDTO createTaskDTO);
    ResourceResponseDTO<TaskDTO> update(long id, UpdateTaskDTO updateTaskDTO);
    ResourceResponseDTO<TaskDTO> changeStatus(long id, ChangeStatusDTO changeStatusDTO);
    ResourceResponseDTO<TaskDTO> delete(long id);
}
