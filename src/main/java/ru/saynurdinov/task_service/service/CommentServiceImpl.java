package ru.saynurdinov.task_service.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.saynurdinov.task_service.dto.CommentDTO;
import ru.saynurdinov.task_service.dto.CreateCommentDTO;
import ru.saynurdinov.task_service.dto.ResourceResponseDTO;
import ru.saynurdinov.task_service.dto.UpdateCommentDTO;
import ru.saynurdinov.task_service.entity.Comment;
import ru.saynurdinov.task_service.entity.Task;
import ru.saynurdinov.task_service.entity.User;
import ru.saynurdinov.task_service.exception.ResourceNotFoundException;
import ru.saynurdinov.task_service.mapper.CommentMapper;
import ru.saynurdinov.task_service.repository.CommentRepository;
import ru.saynurdinov.task_service.repository.TaskRepository;
import ru.saynurdinov.task_service.repository.UserRepository;

import java.util.List;
import java.util.Optional;

@Service
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final TaskRepository taskRepository;
    private final CommentMapper commentMapper;


    @Autowired
    public CommentServiceImpl(CommentRepository commentRepository, UserRepository userRepository, TaskRepository taskRepository, CommentMapper commentMapper) {
        this.commentRepository = commentRepository;
        this.userRepository = userRepository;
        this.taskRepository = taskRepository;
        this.commentMapper = commentMapper;
    }

    @Transactional(readOnly = true)
    @Override
    public ResourceResponseDTO<List<CommentDTO>> getAllByTaskId(long taskId, int page, int size) {
        Optional<Task> taskOptional = taskRepository.findById(taskId);
        if (taskOptional.isPresent()) {
            Task task = taskOptional.get();
            List<Comment> comments = commentRepository.findByTaskId(taskId, PageRequest.of(page, size)).getContent();
            List<CommentDTO> commentDTOList = comments.stream().map(comment -> {
                CommentDTO commentDTO = commentMapper.toDTO(comment);
                commentDTO.setTaskId(task.getId());
                return commentDTO;
            }).toList();
            return new ResourceResponseDTO<>(String.format("Request for a list of comments of task with id = %d", task.getId()), commentDTOList);
        } else {
            throw new ResourceNotFoundException("task", "id", String.valueOf(taskId));
        }
    }

    @Transactional
    @Override
    public ResourceResponseDTO<CommentDTO> create(long taskId, CreateCommentDTO createCommentDTO) {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Optional<Task> taskOptional = taskRepository.findById(taskId);
        if (taskOptional.isPresent()) {
            Task task = taskOptional.get();
            Comment comment = Comment.builder()
                    .text(createCommentDTO.getText())
                    .owner(userRepository.findByEmail(userDetails.getUsername()).get())
                    .task(task)
                    .build();
            CommentDTO commentDTO = commentMapper.toDTO(commentRepository.save(comment));
            commentDTO.setTaskId(task.getId());
            return new ResourceResponseDTO<>("Comment was successfully created", commentDTO);

        } else {
            throw new ResourceNotFoundException("task", "id", String.valueOf(taskId));
        }
    }

    @Transactional
    @Override
    public ResourceResponseDTO<CommentDTO> update(long taskId, long commentId, UpdateCommentDTO updateCommentDTO) {
        Optional<Task> taskOptional = taskRepository.findById(taskId);
        if (taskOptional.isPresent()) {
            Task task = taskOptional.get();
            Optional<Comment> commentOptional = commentRepository.findById(commentId);
            if (commentOptional.isPresent()) {
                Comment comment = commentOptional.get();
                UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
                User authenticated = userRepository.findByEmail(userDetails.getUsername()).get();
                if (comment.getOwner().equals(authenticated)) {
                    comment.setText(updateCommentDTO.getText());
                    CommentDTO commentDTO = commentMapper.toDTO(commentRepository.save(comment));
                    commentDTO.setTaskId(task.getId());
                    return new ResourceResponseDTO<>("Comment was successfully updated", commentDTO);

                } else {
                    throw new AccessDeniedException("No access to this comment");
                }
            } else {
                throw new ResourceNotFoundException("comment", "id", String.valueOf(commentId));

            }
        } else {
            throw new ResourceNotFoundException("task", "id", String.valueOf(taskId));
        }
    }

    @Transactional
    @Override
    public ResourceResponseDTO<CommentDTO>  delete(long taskId, long commentId) {
        Optional<Task> taskOptional = taskRepository.findById(taskId);
        if (taskOptional.isPresent()) {
            Optional<Comment> commentOptional = commentRepository.findById(commentId);
            if (commentOptional.isPresent()) {
                Comment comment = commentOptional.get();
                UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
                User authenticated = userRepository.findByEmail(userDetails.getUsername()).get();
                if (comment.getOwner().equals(authenticated)) {
                    commentRepository.delete(comment);
                    CommentDTO commentDTO = commentMapper.toDTO(comment);
                    commentDTO.setTaskId(taskOptional.get().getId());
                    return new ResourceResponseDTO<>("Comment was successfully deleted", commentDTO);
                } else {
                    throw new AccessDeniedException("No access to this comment");
                }
            } else {
                throw new ResourceNotFoundException("comment", "id", String.valueOf(commentId));

            }
        } else {
            throw new ResourceNotFoundException("task", "id", String.valueOf(taskId));
        }
    }

}
