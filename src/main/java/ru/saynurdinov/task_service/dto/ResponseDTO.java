package ru.saynurdinov.task_service.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class ResponseDTO {

    private LocalDateTime date;
    private String message;

    public ResponseDTO(String message) {
        this.message = message;
        date = LocalDateTime.now();
    }

}
