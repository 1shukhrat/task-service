package ru.saynurdinov.task_service.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.saynurdinov.task_service.entity.enums.TaskPriority;
import ru.saynurdinov.task_service.entity.enums.TaskStatus;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TaskDTO {

    private long id;
    private String title;
    private String description;
    private TaskPriority priority;
    private TaskStatus status;
    private UserDTO creator;
    private UserDTO executor;
    private List<CommentListItemDTO> comments;
}
