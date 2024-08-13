package ru.saynurdinov.task_service.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.saynurdinov.task_service.entity.enums.TaskPriority;
import ru.saynurdinov.task_service.entity.enums.TaskStatus;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UpdateTaskDTO {

    @NotBlank(message = "Title can't be blank")
    private String title;

    @NotBlank(message = "Description can't be blank")
    private String description;

    @NotNull(message = "Priority is required")
    private TaskPriority taskPriority;

    @NotNull(message = "Status is required")
    private TaskStatus taskStatus;

    @NotNull(message = "Deadline is required")
    @Future(message = "Deadline must be in the future")
    private LocalDateTime deadline;

    @NotNull(message = "Executor's ID is required")
    private Long executorId;
}
