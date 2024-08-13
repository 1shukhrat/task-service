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

public class CommentServiceImplTest {

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private CommentMapper commentMapper;

    @InjectMocks
    private CommentServiceImpl commentService;

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
    void testGetAllByTaskId_TaskFound_ReturnsCommentDTOList() {
        Long id = 1L;

        Task task = new Task();
        Comment comment = new Comment();
        List<Comment> commentList = Collections.singletonList(comment);
        Slice<Comment> commentPage = new SliceImpl<>(commentList);
        CommentDTO commentDTO = new CommentDTO();

        when(taskRepository.findById(id)).thenReturn(Optional.of(task));
        when(commentRepository.findByTaskId(eq(id), any(PageRequest.class))).thenReturn(commentPage);
        when(commentMapper.toDTO(comment)).thenReturn(commentDTO);

        ResourceResponseDTO<List<CommentDTO>> response = commentService.getAllByTaskId(id, 0, 10);

        assertNotNull(response);
        assertEquals(1, response.getBody().size());
        assertEquals(commentDTO, response.getBody().getFirst());
    }

    @Test
    void testGetAllByTaskId_TaskNotFound_ThrowsResourceNotFoundException() {
        Long id = 1L;

        when(taskRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> commentService.getAllByTaskId(id, 0, 10));
    }

    @Test
    void testCreate_TaskFound_ReturnsDtoOfCreatedComment() {
        Long taskId = 1L;
        CreateCommentDTO createCommentDTO = new CreateCommentDTO("text");

        Task task = new Task();
        task.setId(taskId);
        Comment comment = new Comment();
        CommentDTO commentDTO = new CommentDTO();

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));
        when(commentRepository.save(any(Comment.class))).thenReturn(comment);
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(authenticatedUser));
        when(commentMapper.toDTO(comment)).thenReturn(commentDTO);

        ResourceResponseDTO<CommentDTO> response = commentService.create(taskId, createCommentDTO);

        assertNotNull(response);
        assertEquals(commentDTO, response.getBody());
        assertEquals(taskId, response.getBody().getTaskId());
    }

    @Test
    void testCreate_TaskNotFound_ThrowsResourceNotFoundException() {
        Long taskId = 1L;
        CreateCommentDTO createCommentDTO = new CreateCommentDTO("text");

        when(taskRepository.findById(taskId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> commentService.create(taskId, createCommentDTO));
    }

    @Test
    void testUpdate_TaskNotFound_ThrowsResourceNotFoundException() {
        Long taskId = 1L;
        Long commentId = 2L;
        UpdateCommentDTO updateCommentDTO = new UpdateCommentDTO("text");

        when(taskRepository.findById(taskId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> commentService.update(taskId, commentId, updateCommentDTO));
    }

    @Test
    void testUpdate_TaskFoundAndCommentNotFound_ThrowsResourceNotFoundException() {
        Long taskId = 1L;
        Long commentId = 2L;
        UpdateCommentDTO updateCommentDTO = new UpdateCommentDTO("text");

        Task task = new Task();

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));
        when(commentRepository.findById(commentId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> commentService.update(taskId, commentId, updateCommentDTO));
    }

    @Test
    void testUpdate_TaskFoundAndCommentFoundAndCommentDoNotBelongsToUser_ThrowsAccessDeniedException() {
        Long taskId = 1L;
        Long commentId = 2L;
        UpdateCommentDTO updateCommentDTO = new UpdateCommentDTO("text");

        Task task = new Task();
        Comment comment = new Comment();
        User other = new User();
        comment.setOwner(other);
        other.setComments(Collections.singletonList(comment));
        authenticatedUser.setComments(Collections.emptyList());

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));
        when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(authenticatedUser));

        assertThrows(AccessDeniedException.class, () -> commentService.update(taskId, commentId, updateCommentDTO));
    }

    @Test
    void testUpdate_TaskFoundAndCommentFoundAndCommentBelongsToUser_ReturnsDtoOfUpdatedComment() {
        Long taskId = 1L;
        Long commentId = 2L;
        UpdateCommentDTO updateCommentDTO = new UpdateCommentDTO("text");

        Task task = new Task();
        Comment comment = new Comment();
        comment.setOwner(authenticatedUser);
        authenticatedUser.setComments(Collections.singletonList(comment));
        CommentDTO commentDTO = new CommentDTO();

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));
        when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(authenticatedUser));
        when(commentRepository.save(any(Comment.class))).thenReturn(comment);
        when(commentMapper.toDTO(comment)).thenReturn(commentDTO);


        ResourceResponseDTO<CommentDTO> response = commentService.update(taskId,commentId, updateCommentDTO);

        assertNotNull(response);
        assertEquals(commentDTO, response.getBody());
    }

    @Test
    void testDelete_TaskNotFound_ThrowsResourceNotFoundException() {
        Long taskId = 1L;
        Long commentId = 2L;

        when(taskRepository.findById(taskId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> commentService.delete(taskId, commentId));
    }

    @Test
    void testDelete_TaskFoundAndCommentNotFound_ThrowsResourceNotFoundException() {
        Long taskId = 1L;
        Long commentId = 2L;

        Task task = new Task();

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));
        when(commentRepository.findById(commentId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> commentService.delete(taskId, commentId));
    }

    @Test
    void testDelete_TaskFoundAndCommentFoundAndCommentDoNotBelongsToUser_ThrowsAccessDeniedException() {
        Long taskId = 1L;
        Long commentId = 2L;

        Task task = new Task();
        Comment comment = new Comment();
        User other = new User();
        comment.setOwner(other);
        other.setComments(Collections.singletonList(comment));
        authenticatedUser.setComments(Collections.emptyList());

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));
        when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(authenticatedUser));

        assertThrows(AccessDeniedException.class, () -> commentService.delete(taskId, commentId));
    }

    @Test
    void testDelete_TaskFoundAndCommentFoundAndCommentBelongsToUser_ReturnsDtoOfDeletedComment() {
        Long taskId = 1L;
        Long commentId = 2L;

        Task task = new Task();
        Comment comment = new Comment();
        comment.setOwner(authenticatedUser);
        authenticatedUser.setComments(Collections.singletonList(comment));
        CommentDTO commentDTO = new CommentDTO();

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));
        when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(authenticatedUser));
        when(commentMapper.toDTO(comment)).thenReturn(commentDTO);


        ResourceResponseDTO<CommentDTO> response = commentService.delete(taskId,commentId);

        verify(commentRepository).delete(comment);
        assertNotNull(response);
        assertEquals(commentDTO, response.getBody());
    }







}
