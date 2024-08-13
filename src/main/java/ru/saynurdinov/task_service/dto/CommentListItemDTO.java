package ru.saynurdinov.task_service.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CommentListItemDTO {

    private long id;
    private String text;
    private UserDTO owner;

}
