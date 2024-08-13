package ru.saynurdinov.task_service.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.saynurdinov.task_service.dto.ChangeStatusDTO;
import ru.saynurdinov.task_service.dto.CreateTaskDTO;
import ru.saynurdinov.task_service.dto.ResourceResponseDTO;
import ru.saynurdinov.task_service.dto.TaskDTO;
import ru.saynurdinov.task_service.dto.UpdateTaskDTO;
import ru.saynurdinov.task_service.entity.Task;
import ru.saynurdinov.task_service.entity.User;
import ru.saynurdinov.task_service.entity.enums.TaskPriority;
import ru.saynurdinov.task_service.entity.enums.TaskStatus;
import ru.saynurdinov.task_service.exception.ResourceNotFoundException;
import ru.saynurdinov.task_service.mapper.TaskMapper;
import ru.saynurdinov.task_service.repository.TaskRepository;
import ru.saynurdinov.task_service.repository.UserRepository;

import java.util.List;
import java.util.Optional;

@Service
public class TaskServiceImpl implements TaskService{


    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final TaskMapper taskMapper;

    @Autowired
    public TaskServiceImpl(TaskRepository taskRepository, UserRepository userRepository, TaskMapper taskMapper) {
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
        this.taskMapper = taskMapper;
    }

    @Override
    @Transactional
    public ResourceResponseDTO<List<TaskDTO>> getAllCreatedByAuthorId(Long id, TaskStatus status, TaskPriority priority,  int page, int size) {
        Long userId = null;
        if (id == null) {
            UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            User authenticated = userRepository.findByEmail(userDetails.getUsername()).get();
            userId = authenticated.getId();
        } else {
            Optional<User> creatorOptional = userRepository.findById(id);
            if (creatorOptional.isPresent()) {
                userId = creatorOptional.get().getId();
            } else {
                throw new ResourceNotFoundException("user", "id", String.valueOf(id));
            }
        }
        List<Task> tasks = taskRepository.findByCreatorId(userId, status, priority, PageRequest.of(page, size)).getContent();
        List<TaskDTO> taskDTOList = tasks.stream().map(taskMapper::toDTO).toList();
        return new ResourceResponseDTO<>(String.format("Request for a list of tasks created by the user with id = %d", userId), taskDTOList);
    }

    @Override
    @Transactional
    public ResourceResponseDTO<List<TaskDTO>>  getAllAssignedByAssigneeId(Long id, TaskStatus status, TaskPriority priority,  int page, int size) {
        Long userId = null;
        if (id == null) {
            UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            User authenticated = userRepository.findByEmail(userDetails.getUsername()).get();
            userId = authenticated.getId();
        } else {
            Optional<User> executorOptional = userRepository.findById(id);
            if (executorOptional.isPresent()) {
                userId = executorOptional.get().getId();
            } else {
                throw new ResourceNotFoundException("user", "id", String.valueOf(id));
            }
        }
        List<Task> tasks = taskRepository.findByExecutorId(userId, status, priority, PageRequest.of(page, size)).getContent();
        List<TaskDTO> taskDTOList = tasks.stream().map(taskMapper::toDTO).toList();
        return new ResourceResponseDTO<>(String.format("Request for a list of tasks assigned to the user with id = %d", userId), taskDTOList);
    }

    @Override
    @Transactional
    public ResourceResponseDTO<List<TaskDTO>>  getAllByUserId(Long id, TaskStatus status, TaskPriority priority,  int page, int size) {
        Long userId = null;
        if (id == null) {
            UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            User authenticated = userRepository.findByEmail(userDetails.getUsername()).get();
            userId = authenticated.getId();
        } else {
            Optional<User> userOptional = userRepository.findById(id);
            if (userOptional.isPresent()) {
                userId = userOptional.get().getId();
            } else {
                throw new ResourceNotFoundException("user", "id", String.valueOf(id));
            }
        }
        List<Task> tasks = taskRepository.findAllByUserId(userId, status, priority, PageRequest.of(page, size)).getContent();
        List<TaskDTO> taskDTOList = tasks.stream().map(taskMapper::toDTO).toList();
        return new ResourceResponseDTO<>(String.format("Request for a list of user tasks with id = %d", userId), taskDTOList);
    }

    @Override
    @Transactional
    public ResourceResponseDTO<TaskDTO> getById(long id) {
        Optional<Task> taskOptional = taskRepository.findById(id);
        if (taskOptional.isPresent()){
            TaskDTO taskDTO = taskMapper.toDTO(taskOptional.get());
            return new ResourceResponseDTO<>(String.format("Request for a task with id = %d", id), taskDTO);
        } else {
            throw new ResourceNotFoundException("task", "id", String.valueOf(id));
        }
    }

    @Transactional
    @Override
    public ResourceResponseDTO<TaskDTO> create(CreateTaskDTO createTaskDTO) {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Optional<User> executorOptional = userRepository.findById(createTaskDTO.getExecutorId());
        if (executorOptional.isPresent()) {
            Task task = Task.builder()
                    .title(createTaskDTO.getTitle())
                    .description(createTaskDTO.getDescription())
                    .deadline(createTaskDTO.getDeadline())
                    .priority(createTaskDTO.getTaskPriority())
                    .status(TaskStatus.TODO)
                    .executor(executorOptional.get())
                    .creator(userRepository.findByEmail(userDetails.getUsername()).get())
                    .build();
            TaskDTO taskDTO = taskMapper.toDTO(taskRepository.save(task));
            return new ResourceResponseDTO<>("Task was successfully created", taskDTO);
        } else {
            throw new ResourceNotFoundException("user", "id", String.valueOf(createTaskDTO.getExecutorId()));
        }

    }

    @Transactional
    @Override
    public ResourceResponseDTO<TaskDTO> update(long id, UpdateTaskDTO updateTaskDTO) {
        Optional<Task> taskOptional = taskRepository.findById(id);
        if (taskOptional.isPresent()) {
            Task task = taskOptional.get();
            UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            User authenticated = userRepository.findByEmail(userDetails.getUsername()).get();
            if (authenticated.getCreatedTasks().contains(task)) {
                Optional<User> executorOptional = userRepository.findById(updateTaskDTO.getExecutorId());
                if (executorOptional.isPresent()) {
                    task.setTitle(updateTaskDTO.getTitle());
                    task.setDeadline(updateTaskDTO.getDeadline());
                    task.setDescription(updateTaskDTO.getDescription());
                    task.setExecutor(executorOptional.get());
                    task.setPriority(updateTaskDTO.getTaskPriority());
                    task.setStatus(updateTaskDTO.getTaskStatus());
                    TaskDTO taskDTO = taskMapper.toDTO(taskRepository.save(task));
                    return new ResourceResponseDTO<>("Task was successfully updated", taskDTO);
                } else {
                    throw new ResourceNotFoundException("user", "id", String.valueOf(updateTaskDTO.getExecutorId()));
                }
            } else {
                throw new AccessDeniedException("No access to this task");
            }
        } else {
            throw new ResourceNotFoundException("task", "id", String.valueOf(id));

        }

    }

    @Transactional
    @Override
    public ResourceResponseDTO<TaskDTO> changeStatus(long id, ChangeStatusDTO changeStatusDTO) {
        Optional<Task> taskOptional = taskRepository.findById(id);
        if (taskOptional.isPresent()) {
            Task task = taskOptional.get();
            UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            User authenticated = userRepository.findByEmail(userDetails.getUsername()).get();
            if (authenticated.getAssignedTasks().contains(task)) {
                task.setStatus(changeStatusDTO.getTaskStatus());
                TaskDTO taskDTO = taskMapper.toDTO( taskRepository.save(task));
                return new ResourceResponseDTO<>("Task's status was successfully updated", taskDTO);
            } else {
                throw new AccessDeniedException("No access to this task");
            }
        } else {
            throw new ResourceNotFoundException("task", "id", String.valueOf(id));
        }
    }

    @Transactional
    @Override
    public ResourceResponseDTO<TaskDTO> delete(long id) {
        Optional<Task> taskOptional = taskRepository.findById(id);
        if (taskOptional.isPresent()) {
            Task task = taskOptional.get();
            UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            User authenticated = userRepository.findByEmail(userDetails.getUsername()).get();
            if (authenticated.getCreatedTasks().contains(task)) {
                taskRepository.delete(task);
                TaskDTO taskDTO = taskMapper.toDTO(task);
                return new ResourceResponseDTO<>("Task was successfully deleted", taskDTO);
            } else {
                throw new AccessDeniedException("No access to this task");
            }
        } else {
            throw new ResourceNotFoundException("task", "id", String.valueOf(id));
        }
    }


}
