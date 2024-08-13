package ru.saynurdinov.task_service.controller;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.saynurdinov.task_service.dto.CommentDTO;
import ru.saynurdinov.task_service.dto.CreateCommentDTO;
import ru.saynurdinov.task_service.dto.ResourceResponseDTO;
import ru.saynurdinov.task_service.dto.UpdateCommentDTO;
import ru.saynurdinov.task_service.service.CommentService;

import java.util.List;

@RestController
@RequestMapping("/api/tasks/{taskId}/comments")
@SecurityRequirement(name = "bearer-jwt")
@Tag(
        name = "Comment", description = "Operations for comments"
)
public class CommentController {

    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @Operation(
            summary = "Get all task's comments by task ID",
            description = " Retrieve all comments filtered by task ID, page number and page size. "
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved comments"),
            @ApiResponse(responseCode = "400", description = "Invalid request parameters", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized access", content = @Content),
            @ApiResponse(responseCode = "404", description = "Task not found", content = @Content)
    })
    @GetMapping(produces = "application/json")
    public ResponseEntity<ResourceResponseDTO<List<CommentDTO>>> getAllByTaskId(@PathVariable("taskId") long taskId,
                                                                                @RequestParam(value = "page", defaultValue = "0") int page,
                                                                                @RequestParam(value = "size", defaultValue = "10") int size) {
        return new ResponseEntity<>(commentService.getAllByTaskId(taskId, page, size), HttpStatus.OK);
    }



    @Operation(
            summary = "Create comment",
            description = "Create a new comment for task by task ID"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Comment successfully created"),
            @ApiResponse(responseCode = "400", description = "Invalid comment data", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized access", content = @Content),
            @ApiResponse(responseCode = "404", description = "Task not found", content = @Content)
    })
    @PostMapping(consumes = "application/json", produces = "application/json")
    public ResponseEntity<ResourceResponseDTO<CommentDTO>> create(@PathVariable("taskId") long taskId,
                                                                  @RequestBody @Valid CreateCommentDTO createCommentDTO) {
        return new ResponseEntity<>(commentService.create(taskId, createCommentDTO), HttpStatus.CREATED);
    }


    @Operation(
            summary = "Update comment",
            description = "Update a existing comment for task by task ID"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Comment successfully updated"),
            @ApiResponse(responseCode = "400", description = "Invalid comment data", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized access", content = @Content),
            @ApiResponse(responseCode = "404", description = "Task or comment not found", content = @Content),
            @ApiResponse(responseCode = "403", description = "Access to comment denied", content = @Content)
    })
    @PutMapping(value = "/{commentId}", consumes = "application/json", produces = "application/json")
    public ResponseEntity<ResourceResponseDTO<CommentDTO>> update(@PathVariable("taskId") long taskId,
                                                                  @PathVariable("commentId") long commentId,
                                                                  @RequestBody @Valid UpdateCommentDTO updateCommentDTO) {
        return new ResponseEntity<>(commentService.update(taskId, commentId, updateCommentDTO), HttpStatus.OK);
    }


    @Operation(
            summary = "Delete comment",
            description = "Delete a existing comment for task by task ID"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Comment successfully deleted"),
            @ApiResponse(responseCode = "400", description = "Invalid request data", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized access", content = @Content),
            @ApiResponse(responseCode = "404", description = "Task or comment not found", content = @Content),
            @ApiResponse(responseCode = "403", description = "Access to comment denied", content = @Content)
    })
    @DeleteMapping(value = "/{commentId}", produces = "application/json")
    public ResponseEntity<ResourceResponseDTO<CommentDTO>> delete(@PathVariable("taskId") long taskId,
                                                                  @PathVariable("commentId") long commentId) {
        return new ResponseEntity<>(commentService.delete(taskId, commentId), HttpStatus.OK);
    }
}
