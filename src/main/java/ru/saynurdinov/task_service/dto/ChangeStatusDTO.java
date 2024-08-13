package ru.saynurdinov.task_service.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.saynurdinov.task_service.entity.enums.TaskStatus;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ChangeStatusDTO {

    @NotNull(message = "Status is required")
    private TaskStatus taskStatus;
}
