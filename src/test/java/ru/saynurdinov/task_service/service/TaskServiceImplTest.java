package ru.saynurdinov.task_service.service;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
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

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


public class TaskServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private TaskMapper taskMapper;

    @InjectMocks
    private TaskServiceImpl taskService;

    private static User authenticatedUser;

    @BeforeAll
    static void authenticate() {
        authenticatedUser = User.builder()
                .id(1L)
                .username("username")
                .password("password")
                .build();

        UserDetails userDetails = new org.springframework.security.core.userdetails.User(
                authenticatedUser.getUsername(),
                authenticatedUser.getPassword(),
                Collections.emptyList());

        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                userDetails,
                null,
                Collections.emptyList());

        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllByUserId_IdProvidedAndUserFound_ReturnsTaskDTOList() {
        Long id = 1L;
        User user = User.builder()
                        .id(id).build();

        Task task = new Task();
        List<Task> taskList = Collections.singletonList(task);
        Slice<Task> taskPage = new SliceImpl<>(taskList);
        TaskDTO taskDTO = new TaskDTO();

        when(userRepository.findById(id)).thenReturn(Optional.of(user));
        when(taskRepository.findAllByUserId(eq(id), any(TaskStatus.class), any(TaskPriority.class), any(PageRequest.class)))
                .thenReturn(taskPage);
        when(taskMapper.toDTO(task)).thenReturn(taskDTO);

        ResourceResponseDTO<List<TaskDTO>> response = taskService.getAllByUserId(id, TaskStatus.TODO, TaskPriority.HIGH, 0, 10);

        assertNotNull(response);
        assertEquals(1, response.getBody().size());
        assertEquals(taskDTO, response.getBody().getFirst());
    }

    @Test
    void testGetAllByUserId_IdProvidedAndUserNotFound_ThrowsResourceNotFoundException() {
        Long id = 1L;

        when(userRepository.findById(eq(id))).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () ->
                taskService.getAllByUserId(id, TaskStatus.TODO, TaskPriority.HIGH, 0, 10));
    }

    @Test
    void testGetAllByUserId_IdNotProvided_ReturnsTaskDTOListOfAuthenticatedUser() {
        Long id = null;

        Task task = new Task();
        List<Task> taskList = Collections.singletonList(task);
        Slice<Task> taskPage = new SliceImpl<>(taskList);
        TaskDTO taskDTO = new TaskDTO();

        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(authenticatedUser));
        when(taskRepository.findAllByUserId(eq(authenticatedUser.getId()), any(TaskStatus.class), any(TaskPriority.class), any(PageRequest.class)))
                .thenReturn(taskPage);
        when(taskMapper.toDTO(task)).thenReturn(taskDTO);

        ResourceResponseDTO<List<TaskDTO>> response = taskService.getAllByUserId(id, TaskStatus.TODO, TaskPriority.HIGH, 0, 10);

        assertNotNull(response);
        assertEquals(1, response.getBody().size());
        assertEquals(taskDTO, response.getBody().getFirst());

    }

    @Test
    void testGetById_TaskFound_ReturnsTaskDTO() {
        long taskId = 1L;
        Task task = new Task();
        TaskDTO taskDTO = new TaskDTO();

        when(taskRepository.findById(eq(taskId))).thenReturn(Optional.of(task));
        when(taskMapper.toDTO(task)).thenReturn(taskDTO);

        ResourceResponseDTO<TaskDTO> response = taskService.getById(taskId);

        assertNotNull(response);
        assertEquals(taskDTO, response.getBody());
    }

    @Test
    void testGetById_TaskNotFound_ThrowsResourceNotFoundException() {
        long taskId = 1L;

        when(taskRepository.findById(eq(taskId))).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> taskService.getById(taskId));
    }

    @Test
    void testCreate_ExecutorFound_ReturnsDtoOfCreatedTask() {
        CreateTaskDTO createTaskDTO = new CreateTaskDTO("Test", "Test", TaskPriority.HIGH, LocalDateTime.now().plusDays(1), 1L);

        User executor = new User();
        Task task = new Task();

        TaskDTO taskDTO = new TaskDTO();

        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(authenticatedUser));
        when(userRepository.findById(createTaskDTO.getExecutorId())).thenReturn(Optional.of(executor));
        when(taskRepository.save(any(Task.class))).thenReturn(task);
        when(taskMapper.toDTO(task)).thenReturn(taskDTO);

        ResourceResponseDTO<TaskDTO> response = taskService.create(createTaskDTO);

        assertNotNull(response);
        assertEquals(taskDTO, response.getBody());

    }

    @Test
    void testCreate_ExecutorNotFound_ThrowsResourceNotFoundException() {
        CreateTaskDTO createTaskDTO = new CreateTaskDTO("Test", "Test", TaskPriority.HIGH, LocalDateTime.now().plusDays(1), 2L);

        when(userRepository.findById(createTaskDTO.getExecutorId())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> taskService.create(createTaskDTO));
    }


    @Test
    void testUpdate_TaskFoundAndExecutorFoundAndTaskBelongsUser_ReturnsDtoOfUpdatedTask() {
        Long taskId = 1L;
        UpdateTaskDTO updateTaskDTO = new UpdateTaskDTO("Test", "Test", TaskPriority.HIGH, TaskStatus.TODO, LocalDateTime.now().plusDays(1), 1L);

        Task task = new Task();
        authenticatedUser.setCreatedTasks(Collections.singletonList(task));
        TaskDTO taskDTO = new TaskDTO();
        User executor = new User();

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(authenticatedUser));
        when(userRepository.findById(updateTaskDTO.getExecutorId())).thenReturn(Optional.of(executor));
        when(taskRepository.save(any(Task.class))).thenReturn(task);
        when(taskMapper.toDTO(task)).thenReturn(taskDTO);

        ResourceResponseDTO<TaskDTO> response = taskService.update(taskId, updateTaskDTO);

        assertNotNull(response);
        assertEquals(taskDTO, response.getBody());
    }

    @Test
    void testUpdate_TaskNotFound_ThrowsResourceNotFoundException() {
        Long taskId = 1L;
        UpdateTaskDTO updateTaskDTO = new UpdateTaskDTO("Test", "Test", TaskPriority.HIGH, TaskStatus.TODO, LocalDateTime.now().plusDays(1), 1L);

        when(taskRepository.findById(taskId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> taskService.update(taskId, updateTaskDTO));
    }

    @Test
    void testUpdate_TaskFoundAndTaskDoNotBelongsUser_ThrowsAccessDeniedException() {
        Long taskId = 1L;
        UpdateTaskDTO updateTaskDTO = new UpdateTaskDTO("Test", "Test", TaskPriority.HIGH, TaskStatus.TODO, LocalDateTime.now().plusDays(1), 1L);

        Task task = new Task();
        authenticatedUser.setCreatedTasks(Collections.emptyList());

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(authenticatedUser));

        assertThrows(AccessDeniedException.class, () -> taskService.update(taskId, updateTaskDTO));
    }

    @Test
    void testUpdate_TaskFoundAndTaskBelongsUserAndExecutorNotFound_ThrowsResourceNotFoundException() {
        Long taskId = 1L;
        UpdateTaskDTO updateTaskDTO = new UpdateTaskDTO("Test", "Test", TaskPriority.HIGH, TaskStatus.TODO, LocalDateTime.now().plusDays(1), 1L);

        Task task = Task.builder()
                .creator(authenticatedUser)
                .build();
        authenticatedUser.setCreatedTasks(Collections.singletonList(task));

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(authenticatedUser));
        when(userRepository.findById(updateTaskDTO.getExecutorId())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> taskService.update(taskId, updateTaskDTO));
    }


    @Test
    void testChangeStatus_TaskFoundAndTaskAssignedToUser_ReturnsDtoOfTaskWithUpdatedStatus() {
        Long taskId = 1L;
        ChangeStatusDTO changeStatusDTO = new ChangeStatusDTO(TaskStatus.TODO);

        Task task = Task.builder()
                .executor(authenticatedUser)
                .build();
        authenticatedUser.setAssignedTasks(Collections.singletonList(task));
        TaskDTO taskDTO = new TaskDTO();

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(authenticatedUser));
        when(taskRepository.save(any(Task.class))).thenReturn(task);
        when(taskMapper.toDTO(task)).thenReturn(taskDTO);

        ResourceResponseDTO<TaskDTO> response = taskService.changeStatus(taskId, changeStatusDTO);

        assertNotNull(response);
        assertEquals(taskDTO, response.getBody());
    }

    @Test
    void testChangeStatus_TaskNotFound_ThrowsResourceNotFoundException() {
        Long taskId = 1L;
        ChangeStatusDTO changeStatusDTO = new ChangeStatusDTO(TaskStatus.TODO);

        when(taskRepository.findById(taskId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> taskService.changeStatus(taskId, changeStatusDTO));
    }

    @Test
    void testChangeStatus_TaskFoundAndTaskDoNotAssignedToUser_ThrowsAccessDeniedException() {
        Long taskId = 1L;
        ChangeStatusDTO changeStatusDTO = new ChangeStatusDTO(TaskStatus.TODO);

        Task task = new Task();
        authenticatedUser.setAssignedTasks(Collections.emptyList());

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(authenticatedUser));

        assertThrows(AccessDeniedException.class, () -> taskService.changeStatus(taskId, changeStatusDTO));
    }

    @Test
    void testDelete_TaskFoundAndTaskAssignedToUser_ReturnsDtoOfDeletedTask() {
        Long taskId = 1L;

        Task task = Task.builder()
                .executor(authenticatedUser)
                .build();
        authenticatedUser.setCreatedTasks(Collections.singletonList(task));
        TaskDTO taskDTO = new TaskDTO();

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(authenticatedUser));
        when(taskMapper.toDTO(task)).thenReturn(taskDTO);

        ResourceResponseDTO<TaskDTO> response = taskService.delete(taskId);

        verify(taskRepository).delete(task);
        assertNotNull(response);
        assertEquals(taskDTO, response.getBody());
    }

    @Test
    void testDelete_TaskNotFound_ThrowsResourceNotFoundException() {
        Long taskId = 1L;

        when(taskRepository.findById(taskId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> taskService.delete(taskId));
    }

    @Test
    void testDelete_TaskFoundAndTaskDoNotAssignedToUser_ThrowsAccessDeniedException() {
        Long taskId = 1L;

        Task task = new Task();
        authenticatedUser.setCreatedTasks(Collections.emptyList());

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(authenticatedUser));

        assertThrows(AccessDeniedException.class, () -> taskService.delete(taskId));
    }






}
