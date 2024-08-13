package ru.saynurdinov.task_service.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.saynurdinov.task_service.dto.ChangeStatusDTO;
import ru.saynurdinov.task_service.dto.CreateTaskDTO;
import ru.saynurdinov.task_service.dto.ResourceResponseDTO;
import ru.saynurdinov.task_service.dto.TaskDTO;
import ru.saynurdinov.task_service.dto.UpdateTaskDTO;
import ru.saynurdinov.task_service.entity.enums.TaskPriority;
import ru.saynurdinov.task_service.entity.enums.TaskStatus;
import ru.saynurdinov.task_service.service.TaskService;

import java.util.List;

@RestController
@RequestMapping("/api/tasks")
@SecurityRequirement(name = "bearer-jwt")
@Tag(
        name = "Task", description = "Operations for tasks"
)
public class TaskController {

    private final TaskService taskService;

    @Autowired
    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }



    @Operation(
            summary = "Get all tasks by user ID",
            description = """
                        Retrieve all tasks filtered by user ID, status, priority, page number and page size.
                        If user ID is not provided, tasks of authenticated user are returned.
                        """
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved tasks"),
            @ApiResponse(responseCode = "400", description = "Invalid request parameters", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized access", content = @Content)
    })
    @GetMapping(produces = "application/json")
    public ResponseEntity<ResourceResponseDTO<List<TaskDTO>>> getAll(
            @RequestParam(value = "userId", required = false) Long userId,
            @RequestParam(value = "status", required = false) TaskStatus status,
            @RequestParam(value = "priority", required = false) TaskPriority priority,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size) {
        return new ResponseEntity<>(taskService.getAllByUserId(userId, status, priority, page, size), HttpStatus.OK);
    }

    @Operation(
            summary = "Get all created tasks by user ID",
            description = """
                        Retrieve all tasks created by a user, filtered by status, priority, page number and page size.
                        If user ID is not provided, tasks of authenticated user are returned.
                        """
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved created tasks"),
            @ApiResponse(responseCode = "400", description = "Invalid request parameters", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized access", content = @Content)
    })
    @GetMapping(value ="/created", produces = "application/json")
    public ResponseEntity<ResourceResponseDTO<List<TaskDTO>>> getAllCreated(
            @RequestParam(value = "userId", required = false) Long userId,
            @RequestParam(value = "status", required = false) TaskStatus status,
            @RequestParam(value = "priority", required = false) TaskPriority priority,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size) {
        return new ResponseEntity<>(taskService.getAllCreatedByAuthorId(userId, status, priority, page, size), HttpStatus.OK);
    }

    @Operation(
            summary = "Get all assigned tasks by user ID",
            description = """
                        Retrieve all tasks assigned to a user, filtered by status, priority, page number and page size.
                        If user ID is not provided, tasks of authenticated user are returned.
                        """
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved assigned tasks"),
            @ApiResponse(responseCode = "400", description = "Invalid request parameters", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized access",content = @Content )
    })
    @GetMapping(value = "/assigned", produces = "application/json")
    public ResponseEntity<ResourceResponseDTO<List<TaskDTO>>> getAllAssigned(
            @RequestParam(value = "userId", required = false) Long userId,
            @RequestParam(value = "status", required = false) TaskStatus status,
            @RequestParam(value = "priority", required = false) TaskPriority priority,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size) {
        return new ResponseEntity<>(taskService.getAllAssignedByAssigneeId(userId, status, priority, page, size), HttpStatus.OK);
    }


    @Operation(
            summary = "Get task by ID",
            description = "Retrieve a task by its ID."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved task"),
            @ApiResponse(responseCode = "404", description = "Task not found", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized access",content = @Content)
    })
    @GetMapping(value = "/{id}", produces = "application/json")
    public ResponseEntity<ResourceResponseDTO<TaskDTO>> getById(@PathVariable("id") long id) {
        return new ResponseEntity<>(taskService.getById(id), HttpStatus.OK);
    }


    @Operation(
            summary = "Create task",
            description = "Create a new task."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Task successfully created"),
            @ApiResponse(responseCode = "400", description = "Invalid task data", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized access", content = @Content)
    })
    @PostMapping(consumes = "application/json", produces = "application/json")
    public ResponseEntity<ResourceResponseDTO<TaskDTO>> create(@RequestBody @Valid CreateTaskDTO createTaskDTO) {
        return new ResponseEntity<>(taskService.create(createTaskDTO), HttpStatus.CREATED);
    }

    @Operation(
            summary = "Update task",
            description = "Update an existing task by its ID."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Task successfully updated"),
            @ApiResponse(responseCode = "400", description = "Invalid task data", content = @Content),
            @ApiResponse(responseCode = "404", description = "Task not found", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized access", content = @Content),
            @ApiResponse(responseCode = "403", description = "Access to task is denied", content = @Content),
    })
    @PutMapping(value ="/{id}", consumes = "application/json", produces = "application/json")
    public ResponseEntity<ResourceResponseDTO<TaskDTO>> update(@PathVariable("id") long id, @RequestBody @Valid UpdateTaskDTO updateTaskDTO) {
        return new ResponseEntity<>(taskService.update(id, updateTaskDTO), HttpStatus.OK);
    }


    @Operation(
            summary = "Change task status",
            description = """
                         Change the status of an existing task by its ID.
                         Only users, who are assigned a task, can change its status through this endpoint.
                         """
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Task status successfully changed"),
            @ApiResponse(responseCode = "400", description = "Invalid status data", content = @Content),
            @ApiResponse(responseCode = "404", description = "Task not found", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized access", content = @Content),
            @ApiResponse(responseCode = "403", description = "Access to task is denied", content = @Content),
    })
    @PatchMapping(value = "/{id}", consumes = "application/json", produces = "application/json" )
    public ResponseEntity<ResourceResponseDTO<TaskDTO>> changeStatus(@PathVariable("id") long id, @RequestBody @Valid ChangeStatusDTO changeStatusDTO) {
        return new ResponseEntity<>(taskService.changeStatus(id, changeStatusDTO), HttpStatus.OK);
    }

    @Operation(
            summary = "Delete task",
            description = "Delete an existing task by its ID."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Task successfully deleted"),
            @ApiResponse(responseCode = "404", description = "Task not found", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized access", content = @Content),
            @ApiResponse(responseCode = "403", description = "Access to task is denied", content = @Content),
    })
    @DeleteMapping(value ="/{id}", produces = "application/json")
    public ResponseEntity<ResourceResponseDTO<TaskDTO>> delete(@PathVariable("id") long id) {
        return new ResponseEntity<>(taskService.delete(id), HttpStatus.OK);
    }


}
