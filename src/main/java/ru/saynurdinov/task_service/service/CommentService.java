package ru.saynurdinov.task_service.service;

import ru.saynurdinov.task_service.dto.CommentDTO;
import ru.saynurdinov.task_service.dto.CreateCommentDTO;
import ru.saynurdinov.task_service.dto.ResourceResponseDTO;
import ru.saynurdinov.task_service.dto.UpdateCommentDTO;

import java.util.List;

public interface CommentService {

    ResourceResponseDTO<List<CommentDTO>> getAllByTaskId(long taskId, int page, int size);
    ResourceResponseDTO<CommentDTO> create(long taskId, CreateCommentDTO createCommentDTO);
    ResourceResponseDTO<CommentDTO> update(long taskId, long commentId, UpdateCommentDTO updateCommentDTO);
    ResourceResponseDTO<CommentDTO> delete(long taskId, long commentId);
}
