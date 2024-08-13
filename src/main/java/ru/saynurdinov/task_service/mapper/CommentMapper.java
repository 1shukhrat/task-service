package ru.saynurdinov.task_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.saynurdinov.task_service.dto.CommentDTO;
import ru.saynurdinov.task_service.dto.CommentListItemDTO;
import ru.saynurdinov.task_service.entity.Comment;

@Mapper(componentModel = "spring", uses = {UserMapper.class})
public interface CommentMapper {

    CommentListItemDTO toListItemDTO(Comment comment);

    @Mapping(target = "taskId", ignore = true)
    CommentDTO toDTO(Comment comment);
}
